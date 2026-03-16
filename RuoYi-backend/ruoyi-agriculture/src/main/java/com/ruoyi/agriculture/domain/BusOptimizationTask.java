package com.ruoyi.agriculture.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 种植优化任务对象 bus_optimization_task
 * 
 * @author ruoyi
 * @date 2026-03-15
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

    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }

    public void setTaskName(String taskName) 
    {
        this.taskName = taskName;
    }

    public String getTaskName() 
    {
        return taskName;
    }

    public void setMode(Integer mode) 
    {
        this.mode = mode;
    }

    public Integer getMode() 
    {
        return mode;
    }

    public void setTotalBudget(BigDecimal totalBudget) 
    {
        this.totalBudget = totalBudget;
    }

    public BigDecimal getTotalBudget() 
    {
        return totalBudget;
    }

    public void setWeightAesthetic(BigDecimal weightAesthetic) 
    {
        this.weightAesthetic = weightAesthetic;
    }

    public BigDecimal getWeightAesthetic() 
    {
        return weightAesthetic;
    }

    public void setWeightYield(BigDecimal weightYield) 
    {
        this.weightYield = weightYield;
    }

    public BigDecimal getWeightYield() 
    {
        return weightYield;
    }

    public void setWeightDifficulty(BigDecimal weightDifficulty) 
    {
        this.weightDifficulty = weightDifficulty;
    }

    public BigDecimal getWeightDifficulty() 
    {
        return weightDifficulty;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("taskName", getTaskName())
            .append("mode", getMode())
            .append("totalBudget", getTotalBudget())
            .append("weightAesthetic", getWeightAesthetic())
            .append("weightYield", getWeightYield())
            .append("weightDifficulty", getWeightDifficulty())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
