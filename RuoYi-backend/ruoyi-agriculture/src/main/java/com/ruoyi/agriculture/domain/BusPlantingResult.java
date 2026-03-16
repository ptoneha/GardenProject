package com.ruoyi.agriculture.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 种植决策方案对象 bus_planting_result
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public class BusPlantingResult extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 结果ID */
    private Long resultId;

    /** 任务ID */
    private Long taskId;

    /** 位置ID */
    @Excel(name = "位置ID")
    private Long landId;

    /** 容器ID */
    @Excel(name = "容器ID")
    private Long containerId;

    /** 作物ID */
    @Excel(name = "作物ID")
    private Long cropId;

    /** 分配面积 */
    @Excel(name = "分配面积")
    private BigDecimal allocatedAreaSqm;

    /** 显示数值 */
    @Excel(name = "显示数值")
    private BigDecimal displayAreaVal;

    /** 建议株树 */
    @Excel(name = "建议株树")
    private Integer plantCount;

    /** 收益（元） */
    @Excel(name = "收益", readConverterExp = "元=")
    private BigDecimal profitAmount;

    /** 体验评分 */
    @Excel(name = "体验评分")
    private Long experienceScore;

    public void setResultId(Long resultId) 
    {
        this.resultId = resultId;
    }

    public Long getResultId() 
    {
        return resultId;
    }

    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }

    public void setLandId(Long landId) 
    {
        this.landId = landId;
    }

    public Long getLandId() 
    {
        return landId;
    }

    public void setContainerId(Long containerId) 
    {
        this.containerId = containerId;
    }

    public Long getContainerId() 
    {
        return containerId;
    }

    public void setCropId(Long cropId) 
    {
        this.cropId = cropId;
    }

    public Long getCropId() 
    {
        return cropId;
    }

    public void setAllocatedAreaSqm(BigDecimal allocatedAreaSqm) 
    {
        this.allocatedAreaSqm = allocatedAreaSqm;
    }

    public BigDecimal getAllocatedAreaSqm() 
    {
        return allocatedAreaSqm;
    }

    public void setDisplayAreaVal(BigDecimal displayAreaVal) 
    {
        this.displayAreaVal = displayAreaVal;
    }

    public BigDecimal getDisplayAreaVal() 
    {
        return displayAreaVal;
    }

    public void setPlantCount(Integer plantCount) 
    {
        this.plantCount = plantCount;
    }

    public Integer getPlantCount() 
    {
        return plantCount;
    }

    public void setProfitAmount(BigDecimal profitAmount) 
    {
        this.profitAmount = profitAmount;
    }

    public BigDecimal getProfitAmount() 
    {
        return profitAmount;
    }

    public void setExperienceScore(Long experienceScore) 
    {
        this.experienceScore = experienceScore;
    }

    public Long getExperienceScore() 
    {
        return experienceScore;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("resultId", getResultId())
            .append("taskId", getTaskId())
            .append("landId", getLandId())
            .append("containerId", getContainerId())
            .append("cropId", getCropId())
            .append("allocatedAreaSqm", getAllocatedAreaSqm())
            .append("displayAreaVal", getDisplayAreaVal())
            .append("plantCount", getPlantCount())
            .append("profitAmount", getProfitAmount())
            .append("experienceScore", getExperienceScore())
            .append("createTime", getCreateTime())
            .toString();
    }
}
