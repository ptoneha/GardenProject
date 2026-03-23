package com.ruoyi.agriculture.constant;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import com.ruoyi.common.utils.StringUtils;

/**
 * Stable error codes returned by the agriculture optimizer.
 * Frontend code should render by code instead of relying on backend prose.
 */
public enum AgriErrorCode
{
    NONE("", ""),
    ERR_TASK_NOT_FOUND("ERR_TASK_NOT_FOUND", "任务不存在"),
    ERR_TASK_RUNNING("ERR_TASK_RUNNING", "任务正在执行中，请勿重复触发"),
    ERR_TASK_FORBIDDEN("ERR_TASK_FORBIDDEN", "当前用户无权执行该任务"),
    ERR_NO_LANDS("ERR_NO_LANDS", "任务未关联可用地块"),
    ERR_NO_CROPS("ERR_NO_CROPS", "系统中没有可用作物数据"),
    ERR_NO_CROP_CONFIGS("ERR_NO_CROP_CONFIGS", "系统中没有可用作物配置"),
    ERR_NO_MATCHING_CONFIG("ERR_NO_MATCHING_CONFIG", "没有匹配当前地块类型的作物配置"),
    ERR_BUDGET_TOO_LOW("ERR_BUDGET_TOO_LOW", "预算不足，无法满足当前约束"),
    ERR_PULSE_AREA_TOO_SMALL("ERR_PULSE_AREA_TOO_SMALL", "豆类约束要求至少 1 平方米，但当前可用面积不足"),
    ERR_CONSTRAINT_CONFLICT("ERR_CONSTRAINT_CONFLICT", "约束冲突，当前任务无可行解"),
    ERR_NO_CONTAINERS("ERR_NO_CONTAINERS", "任务关联地块下没有可用容器"),
    ERR_NO_GARDEN_CANDIDATES("ERR_NO_GARDEN_CANDIDATES", "没有满足容器、光照和深度要求的作物配置"),
    ERR_CONFLICTING_CROP_RULES("ERR_CONFLICTING_CROP_RULES", "作物黑白名单存在冲突"),
    ERR_SEASON_MISMATCH("ERR_SEASON_MISMATCH", "当前季节下没有可行作物"),
    ERR_NO_PULSE_CROP_AVAILABLE("ERR_NO_PULSE_CROP_AVAILABLE", "设置了豆类约束，但候选作物中没有豆类"),
    ERR_UNSUPPORTED_MODE("ERR_UNSUPPORTED_MODE", "当前任务模式不受支持"),
    ERR_INVALID_INPUT("ERR_INVALID_INPUT", "算法输入无效"),
    ERR_PULP_MISSING("ERR_PULP_MISSING", "Python 环境缺少 PuLP 依赖"),
    ERR_PYTHON_UNAVAILABLE("ERR_PYTHON_UNAVAILABLE", "算法服务不可用"),
    ERR_PYTHON_SCRIPT_MISSING("ERR_PYTHON_SCRIPT_MISSING", "算法脚本不存在"),
    ERR_PYTHON_TIMEOUT("ERR_PYTHON_TIMEOUT", "算法执行超时"),
    ERR_PYTHON_BAD_RESPONSE("ERR_PYTHON_BAD_RESPONSE", "算法返回了非法响应"),
    ERR_PYTHON_EXECUTION_FAILED("ERR_PYTHON_EXECUTION_FAILED", "算法进程执行失败"),
    ERR_INTERNAL("ERR_INTERNAL", "算法执行异常");

    private static final Map<String, AgriErrorCode> CODE_INDEX;

    static
    {
        Map<String, AgriErrorCode> codeIndex = new LinkedHashMap<String, AgriErrorCode>();
        for (AgriErrorCode value : values())
        {
            codeIndex.put(value.getCode(), value);
        }
        CODE_INDEX = Collections.unmodifiableMap(codeIndex);
    }

    private final String code;

    private final String description;

    AgriErrorCode(String code, String description)
    {
        this.code = code;
        this.description = description;
    }

    public String getCode()
    {
        return code;
    }

    public String getDescription()
    {
        return description;
    }

    public static AgriErrorCode fromCode(String code)
    {
        if (StringUtils.isEmpty(code))
        {
            return NONE;
        }
        AgriErrorCode errorCode = CODE_INDEX.get(code);
        return errorCode == null ? ERR_INTERNAL : errorCode;
    }

    public static String normalizeCode(String code)
    {
        if (StringUtils.isEmpty(code))
        {
            return NONE.getCode();
        }
        return fromCode(code).getCode();
    }

    public static String resolveDescription(String code)
    {
        if (StringUtils.isEmpty(code))
        {
            return "";
        }
        return fromCode(code).getDescription();
    }
}
