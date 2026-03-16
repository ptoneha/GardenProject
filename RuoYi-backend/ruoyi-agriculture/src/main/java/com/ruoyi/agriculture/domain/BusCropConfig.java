package com.ruoyi.agriculture.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 作物指标配置对象 bus_crop_config
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public class BusCropConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long configId;

    /** 关联作物ID */
    @Excel(name = "关联作物ID")
    private Long cropId;

    /** 适用环境 */
    @Excel(name = "适用环境")
    private Integer landType;

    /** 建议播种月份 */
    @Excel(name = "建议播种月份")
    private Integer startMonth;

    /** 预计收获月份 */
    @Excel(name = "预计收获月份")
    private Integer endMonth;

    /** 生长周期/天 */
    @Excel(name = "生长周期/天")
    private Integer growthDays;

    /** 建议株距 */
    @Excel(name = "建议株距")
    private BigDecimal plantSpacingCm;

    /** 建议行距 */
    @Excel(name = "建议行距")
    private BigDecimal rowSpacingCm;

    /** 原始数据单位 */
    @Excel(name = "原始数据单位")
    private String dataUnit;

    /** 产量数值 */
    @Excel(name = "产量数值")
    private BigDecimal yieldVal;

    /** 成本数值 */
    private BigDecimal costVal;

    /** 市场单价 */
    @Excel(name = "市场单价")
    private BigDecimal marketPrice;

    /** 综合体验评分 */
    @Excel(name = "综合体验评分")
    private Integer utilityScore;

    /** 光照需求等级 */
    @Excel(name = "光照需求等级")
    private Integer sunlightReq;

    /** 根系最低深度 */
    private BigDecimal minDepthReq;

    public void setConfigId(Long configId) 
    {
        this.configId = configId;
    }

    public Long getConfigId() 
    {
        return configId;
    }

    public void setCropId(Long cropId) 
    {
        this.cropId = cropId;
    }

    public Long getCropId() 
    {
        return cropId;
    }

    public void setLandType(Integer landType) 
    {
        this.landType = landType;
    }

    public Integer getLandType() 
    {
        return landType;
    }

    public void setStartMonth(Integer startMonth) 
    {
        this.startMonth = startMonth;
    }

    public Integer getStartMonth() 
    {
        return startMonth;
    }

    public void setEndMonth(Integer endMonth) 
    {
        this.endMonth = endMonth;
    }

    public Integer getEndMonth() 
    {
        return endMonth;
    }

    public void setGrowthDays(Integer growthDays) 
    {
        this.growthDays = growthDays;
    }

    public Integer getGrowthDays() 
    {
        return growthDays;
    }

    public void setPlantSpacingCm(BigDecimal plantSpacingCm) 
    {
        this.plantSpacingCm = plantSpacingCm;
    }

    public BigDecimal getPlantSpacingCm() 
    {
        return plantSpacingCm;
    }

    public void setRowSpacingCm(BigDecimal rowSpacingCm) 
    {
        this.rowSpacingCm = rowSpacingCm;
    }

    public BigDecimal getRowSpacingCm() 
    {
        return rowSpacingCm;
    }

    public void setDataUnit(String dataUnit) 
    {
        this.dataUnit = dataUnit;
    }

    public String getDataUnit() 
    {
        return dataUnit;
    }

    public void setYieldVal(BigDecimal yieldVal) 
    {
        this.yieldVal = yieldVal;
    }

    public BigDecimal getYieldVal() 
    {
        return yieldVal;
    }

    public void setCostVal(BigDecimal costVal) 
    {
        this.costVal = costVal;
    }

    public BigDecimal getCostVal() 
    {
        return costVal;
    }

    public void setMarketPrice(BigDecimal marketPrice) 
    {
        this.marketPrice = marketPrice;
    }

    public BigDecimal getMarketPrice() 
    {
        return marketPrice;
    }

    public void setUtilityScore(Integer utilityScore) 
    {
        this.utilityScore = utilityScore;
    }

    public Integer getUtilityScore() 
    {
        return utilityScore;
    }

    public void setSunlightReq(Integer sunlightReq) 
    {
        this.sunlightReq = sunlightReq;
    }

    public Integer getSunlightReq() 
    {
        return sunlightReq;
    }

    public void setMinDepthReq(BigDecimal minDepthReq) 
    {
        this.minDepthReq = minDepthReq;
    }

    public BigDecimal getMinDepthReq() 
    {
        return minDepthReq;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("configId", getConfigId())
            .append("cropId", getCropId())
            .append("landType", getLandType())
            .append("startMonth", getStartMonth())
            .append("endMonth", getEndMonth())
            .append("growthDays", getGrowthDays())
            .append("plantSpacingCm", getPlantSpacingCm())
            .append("rowSpacingCm", getRowSpacingCm())
            .append("dataUnit", getDataUnit())
            .append("yieldVal", getYieldVal())
            .append("costVal", getCostVal())
            .append("marketPrice", getMarketPrice())
            .append("utilityScore", getUtilityScore())
            .append("sunlightReq", getSunlightReq())
            .append("minDepthReq", getMinDepthReq())
            .toString();
    }
}
