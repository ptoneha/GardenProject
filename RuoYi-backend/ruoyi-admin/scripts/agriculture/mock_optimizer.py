import json
import sys
import traceback

try:
    import pulp
except ImportError:
    pulp = None


MU_TO_SQM = 666.67
EPSILON = 1e-6

ERR_TASK_NO_LANDS = "ERR_NO_LANDS"
ERR_TASK_NO_CROPS = "ERR_NO_CROPS"
ERR_TASK_NO_CROP_CONFIGS = "ERR_NO_CROP_CONFIGS"
ERR_NO_MATCHING_CONFIG = "ERR_NO_MATCHING_CONFIG"
ERR_BUDGET_TOO_LOW = "ERR_BUDGET_TOO_LOW"
ERR_PULSE_AREA_TOO_SMALL = "ERR_PULSE_AREA_TOO_SMALL"
ERR_CONSTRAINT_CONFLICT = "ERR_CONSTRAINT_CONFLICT"
ERR_NO_CONTAINERS = "ERR_NO_CONTAINERS"
ERR_NO_GARDEN_CANDIDATES = "ERR_NO_GARDEN_CANDIDATES"
ERR_CONFLICTING_CROP_RULES = "ERR_CONFLICTING_CROP_RULES"
ERR_SEASON_MISMATCH = "ERR_SEASON_MISMATCH"
ERR_NO_PULSE_CROP_AVAILABLE = "ERR_NO_PULSE_CROP_AVAILABLE"
ERR_UNSUPPORTED_MODE = "ERR_UNSUPPORTED_MODE"
ERR_INVALID_INPUT = "ERR_INVALID_INPUT"
ERR_PULP_MISSING = "ERR_PULP_MISSING"
ERR_INTERNAL = "ERR_INTERNAL"

SEASON_MONTHS = {
    "SPRING": {3, 4, 5},
    "SUMMER": {6, 7, 8},
    "AUTUMN": {9, 10, 11},
    "FALL": {9, 10, 11},
    "WINTER": {12, 1, 2},
    "春季": {3, 4, 5},
    "夏季": {6, 7, 8},
    "秋季": {9, 10, 11},
    "冬季": {12, 1, 2},
}


class OptimizerError(Exception):
    def __init__(self, code, status=3):
        super().__init__(code)
        self.code = code
        self.status = status


def configure_stdio():
    for stream in (sys.stdout, sys.stderr):
        reconfigure = getattr(stream, "reconfigure", None)
        if callable(reconfigure):
            reconfigure(encoding="utf-8")


def parse_input():
    if len(sys.argv) > 1:
        arg = sys.argv[1]
        if arg.endswith(".json"):
            with open(arg, "r", encoding="utf-8") as file:
                return json.load(file)
        return json.loads(arg)

    raw = sys.stdin.read()
    if not raw.strip():
        raise ValueError("Input JSON is empty")
    return json.loads(raw)


def build_success_response(results):
    return {
        "success": True,
        "status": 1,
        "message": "ok",
        "infeasibleReason": "",
        "results": results,
    }


def build_infeasible_response(reason_code):
    return {
        "success": False,
        "status": 2,
        "message": "infeasible",
        "infeasibleReason": reason_code or ERR_CONSTRAINT_CONFLICT,
        "results": [],
    }


def build_error_response(reason_code):
    return {
        "success": False,
        "status": 3,
        "message": "error",
        "infeasibleReason": reason_code or ERR_INTERNAL,
        "results": [],
    }


def normalize_number(value, default=0.0):
    if value is None:
        return float(default)
    return float(value)


def normalize_int(value, default=None):
    if value is None:
        return default
    return int(value)


def is_pulse_crop(crop):
    value = str(crop.get("isPulse", "")).strip().upper()
    return value in {"Y", "1", "TRUE", "T", "YES"}


def build_land_map(payload):
    lands = payload.get("lands") or []
    return {land["landId"]: land for land in lands}


def build_crop_map(payload):
    crops = payload.get("crops") or []
    return {crop["cropId"]: crop for crop in crops}


def build_config_index(payload):
    config_index = {}
    for config in payload.get("cropConfigs") or []:
        key = (config.get("cropId"), config.get("landType"))
        config_index[key] = config
    return config_index


def parse_crop_id_set(values):
    result = set()
    for value in values or []:
        try:
            result.add(int(value))
        except (TypeError, ValueError):
            raise OptimizerError(ERR_INVALID_INPUT, 3)
    return result


def normalize_season(value):
    if value is None:
        return None
    season = str(value).strip().upper()
    if not season:
        return None
    if season not in SEASON_MONTHS:
        raise OptimizerError(ERR_INVALID_INPUT, 3)
    return season


def month_range(start_month, end_month):
    if start_month is None or end_month is None:
        return None
    start_month = normalize_int(start_month)
    end_month = normalize_int(end_month)
    if start_month < 1 or start_month > 12 or end_month < 1 or end_month > 12:
        raise OptimizerError(ERR_INVALID_INPUT, 3)
    if start_month <= end_month:
        return set(range(start_month, end_month + 1))
    return set(range(start_month, 13)) | set(range(1, end_month + 1))


def season_matches(config, task_season):
    if not task_season:
        return True
    active_months = month_range(config.get("startMonth"), config.get("endMonth"))
    if active_months is None:
        return True
    return bool(active_months & SEASON_MONTHS[task_season])


def prepare_task_rules(payload):
    whitelist = parse_crop_id_set(payload.get("cropWhitelist") or [])
    blacklist = parse_crop_id_set(payload.get("cropBlacklist") or [])
    if whitelist & blacklist:
        raise OptimizerError(ERR_CONFLICTING_CROP_RULES, 2)

    min_pulse_ratio = normalize_number(payload.get("minPulseRatio"), 0.0)
    if min_pulse_ratio < -EPSILON or min_pulse_ratio > 1 + EPSILON:
        raise OptimizerError(ERR_INVALID_INPUT, 3)
    min_pulse_ratio = max(0.0, min(1.0, min_pulse_ratio))

    task_season = normalize_season(payload.get("taskSeason"))
    return {
        "whitelist": whitelist,
        "blacklist": blacklist,
        "minPulseRatio": min_pulse_ratio,
        "taskSeason": task_season,
    }


def crop_allowed(crop_id, rules):
    whitelist = rules["whitelist"]
    blacklist = rules["blacklist"]
    if whitelist and crop_id not in whitelist:
        return False
    return crop_id not in blacklist


def analyze_farmer_infeasible_reason(payload, candidates, context):
    lands = payload.get("lands") or []
    crops = payload.get("crops") or []
    configs = payload.get("cropConfigs") or []
    budget = normalize_number(payload.get("budget"))
    rules = context["rules"]

    if not lands:
        return ERR_TASK_NO_LANDS
    if not crops:
        return ERR_TASK_NO_CROPS
    if not configs:
        return ERR_TASK_NO_CROP_CONFIGS
    if not context["has_config_match"]:
        return ERR_NO_MATCHING_CONFIG
    if rules["taskSeason"] and not context["has_season_match"]:
        return ERR_SEASON_MISMATCH
    if rules["minPulseRatio"] > EPSILON and not context["has_pulse_candidate"]:
        return ERR_NO_PULSE_CROP_AVAILABLE
    if budget <= EPSILON and any(item["costVal"] > EPSILON for item in candidates):
        return ERR_BUDGET_TOO_LOW

    max_area = sum(item["landAreaSqm"] for item in candidates)
    if max_area <= EPSILON:
        return ERR_PULSE_AREA_TOO_SMALL

    return ERR_CONSTRAINT_CONFLICT


def analyze_garden_infeasible_reason(payload, candidates, context):
    rules = context["rules"]
    containers = payload.get("containers") or []
    if not containers:
        return ERR_NO_CONTAINERS
    if rules["taskSeason"] and not context["has_season_match"]:
        return ERR_SEASON_MISMATCH
    if rules["minPulseRatio"] > EPSILON and not context["has_pulse_candidate"]:
        return ERR_NO_PULSE_CROP_AVAILABLE
    if not candidates:
        return ERR_NO_GARDEN_CANDIDATES
    return ERR_CONSTRAINT_CONFLICT


def solve_farmer_mode(payload):
    rules = prepare_task_rules(payload)
    config_index = build_config_index(payload)

    context = {
        "rules": rules,
        "has_config_match": False,
        "has_season_match": False,
        "has_pulse_candidate": False,
    }

    candidates = []
    for land in payload.get("lands") or []:
        land_id = land.get("landId")
        land_type = land.get("landType")
        land_area = normalize_number(land.get("areaSqm"))
        for crop in payload.get("crops") or []:
            crop_id = crop.get("cropId")
            config = config_index.get((crop_id, land_type))
            if not config:
                continue
            context["has_config_match"] = True
            if crop_id is None or not crop_allowed(crop_id, rules):
                continue
            if not season_matches(config, rules["taskSeason"]):
                continue
            context["has_season_match"] = True
            unit_profit = (
                normalize_number(config.get("yieldVal"))
                * normalize_number(config.get("marketPrice"))
                - normalize_number(config.get("costVal"))
            )
            is_pulse = is_pulse_crop(crop)
            if is_pulse:
                context["has_pulse_candidate"] = True
            candidates.append(
                {
                    "landId": land_id,
                    "cropId": crop_id,
                    "landAreaSqm": land_area,
                    "costVal": normalize_number(config.get("costVal")),
                    "unitProfit": unit_profit,
                    "isPulse": is_pulse,
                }
            )

    if not candidates:
        return build_infeasible_response(
            analyze_farmer_infeasible_reason(payload, candidates, context)
        )

    model = pulp.LpProblem("farmer_mode", pulp.LpMaximize)
    area_vars = {}
    for item in candidates:
        key = (item["landId"], item["cropId"])
        area_vars[key] = pulp.LpVariable(
            "area_land_{0}_crop_{1}".format(item["landId"], item["cropId"]),
            lowBound=0,
            cat="Continuous",
        )

    model += pulp.lpSum(
        item["unitProfit"] * area_vars[(item["landId"], item["cropId"])]
        for item in candidates
    )

    for land in payload.get("lands") or []:
        land_id = land.get("landId")
        land_area = normalize_number(land.get("areaSqm"))
        land_vars = [
            area_vars[(item["landId"], item["cropId"])]
            for item in candidates
            if item["landId"] == land_id
        ]
        if land_vars:
            model += pulp.lpSum(land_vars) <= land_area

    model += pulp.lpSum(
        item["costVal"] * area_vars[(item["landId"], item["cropId"])]
        for item in candidates
    ) <= normalize_number(payload.get("budget"))

    min_pulse_ratio = rules["minPulseRatio"]
    if min_pulse_ratio > EPSILON:
        pulse_keys = [
            (item["landId"], item["cropId"])
            for item in candidates
            if item["isPulse"]
        ]
        if not pulse_keys:
            return build_infeasible_response(ERR_NO_PULSE_CROP_AVAILABLE)
        total_area_expr = pulp.lpSum(area_vars.values())
        pulse_area_expr = pulp.lpSum(area_vars[key] for key in pulse_keys)
        model += pulse_area_expr >= min_pulse_ratio * total_area_expr

    status = model.solve(pulp.PULP_CBC_CMD(msg=False))
    status_name = pulp.LpStatus.get(status, "Unknown")
    if status_name != "Optimal":
        return build_infeasible_response(
            analyze_farmer_infeasible_reason(payload, candidates, context)
        )

    results = []
    for item in candidates:
        key = (item["landId"], item["cropId"])
        allocated_area = normalize_number(pulp.value(area_vars[key]))
        if allocated_area <= EPSILON:
            continue
        results.append(
            {
                "landId": item["landId"],
                "containerId": None,
                "cropId": item["cropId"],
                "allocatedAreaSqm": round(allocated_area, 4),
                "displayAreaVal": round(allocated_area / MU_TO_SQM, 4),
                "plantCount": None,
                "profitAmount": round(item["unitProfit"] * allocated_area, 2),
                "experienceScore": None,
            }
        )

    if not results:
        return build_infeasible_response(
            analyze_farmer_infeasible_reason(payload, candidates, context)
        )

    return build_success_response(results)


def calculate_garden_score(config, land, container):
    del land
    del container
    return normalize_number(config.get("utilityScore"))


def solve_garden_mode(payload):
    rules = prepare_task_rules(payload)
    lands = build_land_map(payload)
    crop_map = build_crop_map(payload)
    config_index = build_config_index(payload)

    context = {
        "rules": rules,
        "has_season_match": False,
        "has_pulse_candidate": False,
    }

    candidates = []
    for container in payload.get("containers") or []:
        container_id = container.get("containerId")
        land_id = container.get("landId")
        land = lands.get(land_id)
        if not land:
            continue
        land_type = land.get("landType")
        light_level = normalize_number(land.get("lightLevel"))
        depth_cm = normalize_number(container.get("depthCm"))
        for crop_id, crop in crop_map.items():
            if not crop_allowed(crop_id, rules):
                continue
            config = config_index.get((crop_id, land_type))
            if not config:
                continue
            if not season_matches(config, rules["taskSeason"]):
                continue
            context["has_season_match"] = True
            min_depth = normalize_number(config.get("minDepthReq"))
            sunlight_req = normalize_number(config.get("sunlightReq"))
            if depth_cm + EPSILON < min_depth:
                continue
            if light_level + EPSILON < sunlight_req:
                continue
            score = calculate_garden_score(config, land, container)
            if score <= 0:
                continue
            is_pulse = is_pulse_crop(crop)
            if is_pulse:
                context["has_pulse_candidate"] = True
            candidates.append(
                {
                    "containerId": container_id,
                    "landId": land_id,
                    "cropId": crop.get("cropId"),
                    "plantingSites": int(normalize_number(container.get("plantingSites"))),
                    "score": score,
                    "isPulse": is_pulse,
                }
            )

    if not candidates:
        return build_infeasible_response(
            analyze_garden_infeasible_reason(payload, candidates, context)
        )

    model = pulp.LpProblem("garden_mode", pulp.LpMaximize)
    plant_vars = {}
    for item in candidates:
        key = (item["containerId"], item["cropId"])
        plant_vars[key] = pulp.LpVariable(
            "plant_container_{0}_crop_{1}".format(item["containerId"], item["cropId"]),
            lowBound=0,
            cat="Integer",
        )

    model += pulp.lpSum(
        item["score"] * plant_vars[(item["containerId"], item["cropId"])]
        for item in candidates
    )

    for container in payload.get("containers") or []:
        container_id = container.get("containerId")
        planting_sites = int(normalize_number(container.get("plantingSites")))
        container_vars = [
            plant_vars[(item["containerId"], item["cropId"])]
            for item in candidates
            if item["containerId"] == container_id
        ]
        if container_vars:
            model += pulp.lpSum(container_vars) <= planting_sites

    min_pulse_ratio = rules["minPulseRatio"]
    if min_pulse_ratio > EPSILON:
        pulse_keys = [
            (item["containerId"], item["cropId"])
            for item in candidates
            if item["isPulse"]
        ]
        if not pulse_keys:
            return build_infeasible_response(ERR_NO_PULSE_CROP_AVAILABLE)
        total_plant_expr = pulp.lpSum(plant_vars.values())
        pulse_plant_expr = pulp.lpSum(plant_vars[key] for key in pulse_keys)
        model += pulse_plant_expr >= min_pulse_ratio * total_plant_expr

    status = model.solve(pulp.PULP_CBC_CMD(msg=False))
    status_name = pulp.LpStatus.get(status, "Unknown")
    if status_name != "Optimal":
        return build_infeasible_response(
            analyze_garden_infeasible_reason(payload, candidates, context)
        )

    results = []
    for item in candidates:
        key = (item["containerId"], item["cropId"])
        plant_count = int(round(normalize_number(pulp.value(plant_vars[key]))))
        if plant_count <= 0:
            continue
        results.append(
            {
                "landId": item["landId"],
                "containerId": item["containerId"],
                "cropId": item["cropId"],
                "allocatedAreaSqm": None,
                "displayAreaVal": None,
                "plantCount": plant_count,
                "profitAmount": None,
                "experienceScore": int(round(item["score"] * plant_count)),
            }
        )

    if not results:
        return build_infeasible_response(
            analyze_garden_infeasible_reason(payload, candidates, context)
        )

    return build_success_response(results)


def main():
    configure_stdio()
    try:
        if pulp is None:
            response = build_error_response(ERR_PULP_MISSING)
        else:
            payload = parse_input()
            mode = payload.get("mode")
            if mode == 1:
                response = solve_farmer_mode(payload)
            elif mode == 2:
                response = solve_garden_mode(payload)
            else:
                response = build_error_response(ERR_UNSUPPORTED_MODE)
    except OptimizerError as exc:
        if exc.status == 2:
            response = build_infeasible_response(exc.code)
        else:
            response = build_error_response(exc.code)
    except (json.JSONDecodeError, ValueError):
        traceback.print_exc(file=sys.stderr)
        response = build_error_response(ERR_INVALID_INPUT)
    except Exception:
        traceback.print_exc(file=sys.stderr)
        response = build_error_response(ERR_INTERNAL)

    sys.stdout.write(json.dumps(response, ensure_ascii=True))


if __name__ == "__main__":
    main()
