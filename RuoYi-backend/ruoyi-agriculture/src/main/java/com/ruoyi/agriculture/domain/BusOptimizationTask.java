package com.ruoyi.agriculture.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 种植优化任务对象 bus_optimization_task
 */
public class BusOptimizationTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long taskId;

    /** 任务名称 */
    @Excel(name = "任务名称")
    private String taskName;

    /** 优化模式 */
    @Excel(name = "优化模式")
    private Integer mode;

    /** 预算上限 */
    @Excel(name = "预算上限")
    private BigDecimal totalBudget;

    /** 美感权重 */
    private BigDecimal weightAesthetic;

    /** 收获感权重 */
    private BigDecimal weightYield;

    /** 易种性权重 */
    private BigDecimal weightDifficulty;

    /** 任务状态 */
    @Excel(name = "任务状态")
    private String status;

    /** 作物白名单，逗号分隔的 crop_id 列表 */
    private String cropWhitelist;

    /** 作物黑名单，逗号分隔的 crop_id 列表 */
    private String cropBlacklist;

    /** 豆类最小占比，取值范围 [0,1] */
    private BigDecimal minPulseRatio;

    /** 任务季节 */
    private String taskSeason;

    /** 任务归属用户ID */
    private Long ownerUserId;

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public Integer getMode()
    {
        return mode;
    }

    public void setMode(Integer mode)
    {
        this.mode = mode;
    }

    public BigDecimal getTotalBudget()
    {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget)
    {
        this.totalBudget = totalBudget;
    }

    public BigDecimal getWeightAesthetic()
    {
        return weightAesthetic;
    }

    public void setWeightAesthetic(BigDecimal weightAesthetic)
    {
        this.weightAesthetic = weightAesthetic;
    }

    public BigDecimal getWeightYield()
    {
        return weightYield;
    }

    public void setWeightYield(BigDecimal weightYield)
    {
        this.weightYield = weightYield;
    }

    public BigDecimal getWeightDifficulty()
    {
        return weightDifficulty;
    }

    public void setWeightDifficulty(BigDecimal weightDifficulty)
    {
        this.weightDifficulty = weightDifficulty;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getCropWhitelist()
    {
        return cropWhitelist;
    }

    public void setCropWhitelist(String cropWhitelist)
    {
        this.cropWhitelist = cropWhitelist;
    }

    public String getCropBlacklist()
    {
        return cropBlacklist;
    }

    public void setCropBlacklist(String cropBlacklist)
    {
        this.cropBlacklist = cropBlacklist;
    }

    public BigDecimal getMinPulseRatio()
    {
        return minPulseRatio;
    }

    public void setMinPulseRatio(BigDecimal minPulseRatio)
    {
        this.minPulseRatio = minPulseRatio;
    }

    public String getTaskSeason()
    {
        return taskSeason;
    }

    public void setTaskSeason(String taskSeason)
    {
        this.taskSeason = taskSeason;
    }

    public Long getOwnerUserId()
    {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId)
    {
        this.ownerUserId = ownerUserId;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("taskName", getTaskName())
            .append("mode", getMode())
            .append("totalBudget", getTotalBudget())
            .append("weightAesthetic", getWeightAesthetic())
            .append("weightYield", getWeightYield())
            .append("weightDifficulty", getWeightDifficulty())
            .append("status", getStatus())
            .append("cropWhitelist", getCropWhitelist())
            .append("cropBlacklist", getCropBlacklist())
            .append("minPulseRatio", getMinPulseRatio())
            .append("taskSeason", getTaskSeason())
            .append("ownerUserId", getOwnerUserId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
