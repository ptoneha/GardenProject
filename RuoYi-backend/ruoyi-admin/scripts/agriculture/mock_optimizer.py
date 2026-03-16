import json
import sys


def choose_crop(crops):
    if not crops:
        return None
    return crops[0]


def build_results(payload):
    lands = payload.get("lands") or []
    containers = payload.get("containers") or []
    crops = payload.get("crops") or []

    if not lands or not crops:
        return {
            "success": False,
            "status": 2,
            "message": "no feasible solution",
            "infeasibleReason": "Missing lands or crops",
            "results": [],
        }

    crop = choose_crop(crops)
    mode = payload.get("mode")
    container_map = {}
    for container in containers:
        land_id = container.get("landId")
        container_map.setdefault(land_id, []).append(container)

    results = []
    for land in lands:
        land_id = land.get("landId")
        area_sqm = float(land.get("areaSqm") or 0)
        current_containers = container_map.get(land_id, [])
        if current_containers:
            unit_area = round(area_sqm / len(current_containers), 4) if current_containers else area_sqm
            for container in current_containers:
                results.append(
                    {
                        "landId": land_id,
                        "containerId": container.get("containerId"),
                        "cropId": crop.get("cropId"),
                        "allocatedAreaSqm": unit_area,
                        "displayAreaVal": unit_area,
                        "plantCount": container.get("plantingSites"),
                        "profitAmount": round(unit_area * 3.6, 2) if mode == 1 else 0,
                        "experienceScore": int(container.get("plantingSites") or 0) * 10 if mode == 2 else None,
                    }
                )
        else:
            results.append(
                {
                    "landId": land_id,
                    "containerId": None,
                    "cropId": crop.get("cropId"),
                    "allocatedAreaSqm": area_sqm,
                    "displayAreaVal": round(area_sqm / 666.67, 4),
                    "plantCount": None,
                    "profitAmount": round(area_sqm * 1.8, 2) if mode == 1 else 0,
                    "experienceScore": int(area_sqm) if mode == 2 else None,
                }
            )

    return {
        "success": True,
        "status": 1,
        "message": "ok",
        "infeasibleReason": "",
        "results": results,
    }


def main():
    raw = sys.stdin.read()
    payload = json.loads(raw) if raw.strip() else {}
    response = build_results(payload)
    sys.stdout.write(json.dumps(response, ensure_ascii=False))


if __name__ == "__main__":
    main()
