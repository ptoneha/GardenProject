package com.ruoyi.agriculture.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 种植容器管理对象 bus_container
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public class BusContainer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 容器ID */
    private Long containerId;

    /** 所属位置ID */
    @Excel(name = "所属位置ID")
    private Long landId;

    /** 容器名称 */
    @Excel(name = "容器名称")
    private String containerName;

    /** 容器类型 */
    @Excel(name = "容器类型")
    private Integer containerType;

    /** 可种植位置数 */
    @Excel(name = "可种植位置数")
    private Integer plantingSites;

    /** 容器深度 */
    @Excel(name = "容器深度")
    private BigDecimal depthCm;

    /** 宽度 */
    private BigDecimal widthCm;

    /** 高度 */
    private BigDecimal heightCm;

    /** 状态 */
    @Excel(name = "状态")
    private String isActive;

    public void setContainerId(Long containerId) 
    {
        this.containerId = containerId;
    }

    public Long getContainerId() 
    {
        return containerId;
    }

    public void setLandId(Long landId) 
    {
        this.landId = landId;
    }

    public Long getLandId() 
    {
        return landId;
    }

    public void setContainerName(String containerName) 
    {
        this.containerName = containerName;
    }

    public String getContainerName() 
    {
        return containerName;
    }

    public void setContainerType(Integer containerType) 
    {
        this.containerType = containerType;
    }

    public Integer getContainerType() 
    {
        return containerType;
    }

    public void setPlantingSites(Integer plantingSites) 
    {
        this.plantingSites = plantingSites;
    }

    public Integer getPlantingSites() 
    {
        return plantingSites;
    }

    public void setDepthCm(BigDecimal depthCm) 
    {
        this.depthCm = depthCm;
    }

    public BigDecimal getDepthCm() 
    {
        return depthCm;
    }

    public void setWidthCm(BigDecimal widthCm) 
    {
        this.widthCm = widthCm;
    }

    public BigDecimal getWidthCm() 
    {
        return widthCm;
    }

    public void setHeightCm(BigDecimal heightCm) 
    {
        this.heightCm = heightCm;
    }

    public BigDecimal getHeightCm() 
    {
        return heightCm;
    }

    public void setIsActive(String isActive) 
    {
        this.isActive = isActive;
    }

    public String getIsActive() 
    {
        return isActive;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("containerId", getContainerId())
            .append("landId", getLandId())
            .append("containerName", getContainerName())
            .append("containerType", getContainerType())
            .append("plantingSites", getPlantingSites())
            .append("depthCm", getDepthCm())
            .append("widthCm", getWidthCm())
            .append("heightCm", getHeightCm())
            .append("isActive", getIsActive())
            .toString();
    }
}
