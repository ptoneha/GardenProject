package com.ruoyi.agriculture.domain.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 方案解释输入对象
 *
 * 先按 mock 结构定义，后续可由真实优化结果对象组装转换。
 */
public class PlanExplanationRequest
{
    /**
     * farmer / garden
     */
    private String mode;

    private BigDecimal totalProfit;

    private BigDecimal totalCost;

    private BigDecimal totalRevenue;

    private List<String> crops = new ArrayList<String>();

    private String majorCrop;

    private Map<String, BigDecimal> cropAreas = new LinkedHashMap<String, BigDecimal>();

    private List<String> landTypes = new ArrayList<String>();

    private BigDecimal budgetUsedRatio;

    private Boolean hasPulseCrop;

    private BigDecimal yieldRate;

    private BigDecimal utilityScore;

    private BigDecimal aestheticScore;

    private BigDecimal spaceUsageRate;

    private Integer plantCount;

    private String lightMatchLevel;

    private String depthMatchLevel;

    private List<String> dominantPlants = new ArrayList<String>();

    private List<String> containerTypes = new ArrayList<String>();

    private BigDecimal weightAesthetic;

    private BigDecimal weightYield;

    private BigDecimal weightDifficulty;

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public BigDecimal getTotalProfit()
    {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit)
    {
        this.totalProfit = totalProfit;
    }

    public BigDecimal getTotalCost()
    {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost)
    {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalRevenue()
    {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue)
    {
        this.totalRevenue = totalRevenue;
    }

    public List<String> getCrops()
    {
        return crops;
    }

    public void setCrops(List<String> crops)
    {
        this.crops = crops;
    }

    public String getMajorCrop()
    {
        return majorCrop;
    }

    public void setMajorCrop(String majorCrop)
    {
        this.majorCrop = majorCrop;
    }

    public Map<String, BigDecimal> getCropAreas()
    {
        return cropAreas;
    }

    public void setCropAreas(Map<String, BigDecimal> cropAreas)
    {
        this.cropAreas = cropAreas;
    }

    public List<String> getLandTypes()
    {
        return landTypes;
    }

    public void setLandTypes(List<String> landTypes)
    {
        this.landTypes = landTypes;
    }

    public BigDecimal getBudgetUsedRatio()
    {
        return budgetUsedRatio;
    }

    public void setBudgetUsedRatio(BigDecimal budgetUsedRatio)
    {
        this.budgetUsedRatio = budgetUsedRatio;
    }

    public Boolean getHasPulseCrop()
    {
        return hasPulseCrop;
    }

    public void setHasPulseCrop(Boolean hasPulseCrop)
    {
        this.hasPulseCrop = hasPulseCrop;
    }

    public BigDecimal getYieldRate()
    {
        return yieldRate;
    }

    public void setYieldRate(BigDecimal yieldRate)
    {
        this.yieldRate = yieldRate;
    }

    public BigDecimal getUtilityScore()
    {
        return utilityScore;
    }

    public void setUtilityScore(BigDecimal utilityScore)
    {
        this.utilityScore = utilityScore;
    }

    public BigDecimal getAestheticScore()
    {
        return aestheticScore;
    }

    public void setAestheticScore(BigDecimal aestheticScore)
    {
        this.aestheticScore = aestheticScore;
    }

    public BigDecimal getSpaceUsageRate()
    {
        return spaceUsageRate;
    }

    public void setSpaceUsageRate(BigDecimal spaceUsageRate)
    {
        this.spaceUsageRate = spaceUsageRate;
    }

    public Integer getPlantCount()
    {
        return plantCount;
    }

    public void setPlantCount(Integer plantCount)
    {
        this.plantCount = plantCount;
    }

    public String getLightMatchLevel()
    {
        return lightMatchLevel;
    }

    public void setLightMatchLevel(String lightMatchLevel)
    {
        this.lightMatchLevel = lightMatchLevel;
    }

    public String getDepthMatchLevel()
    {
        return depthMatchLevel;
    }

    public void setDepthMatchLevel(String depthMatchLevel)
    {
        this.depthMatchLevel = depthMatchLevel;
    }

    public List<String> getDominantPlants()
    {
        return dominantPlants;
    }

    public void setDominantPlants(List<String> dominantPlants)
    {
        this.dominantPlants = dominantPlants;
    }

    public List<String> getContainerTypes()
    {
        return containerTypes;
    }

    public void setContainerTypes(List<String> containerTypes)
    {
        this.containerTypes = containerTypes;
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
}
