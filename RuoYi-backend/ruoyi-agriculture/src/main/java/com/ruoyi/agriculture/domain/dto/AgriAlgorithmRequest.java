package com.ruoyi.agriculture.domain.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 传给 Python 算法的输入对象
 */
public class AgriAlgorithmRequest
{
    private Long taskId;

    private Integer mode;

    private BigDecimal budget;

    private String taskSeason;

    private BigDecimal minPulseRatio;

    private Long ownerUserId;

    private List<Long> cropWhitelist;

    private List<Long> cropBlacklist;

    private List<LandItem> lands;

    private List<ContainerItem> containers;

    private List<CropItem> crops;

    private List<CropConfigItem> cropConfigs;

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public Integer getMode()
    {
        return mode;
    }

    public void setMode(Integer mode)
    {
        this.mode = mode;
    }

    public BigDecimal getBudget()
    {
        return budget;
    }

    public void setBudget(BigDecimal budget)
    {
        this.budget = budget;
    }

    public String getTaskSeason()
    {
        return taskSeason;
    }

    public void setTaskSeason(String taskSeason)
    {
        this.taskSeason = taskSeason;
    }

    public BigDecimal getMinPulseRatio()
    {
        return minPulseRatio;
    }

    public void setMinPulseRatio(BigDecimal minPulseRatio)
    {
        this.minPulseRatio = minPulseRatio;
    }

    public Long getOwnerUserId()
    {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId)
    {
        this.ownerUserId = ownerUserId;
    }

    public List<Long> getCropWhitelist()
    {
        return cropWhitelist;
    }

    public void setCropWhitelist(List<Long> cropWhitelist)
    {
        this.cropWhitelist = cropWhitelist;
    }

    public List<Long> getCropBlacklist()
    {
        return cropBlacklist;
    }

    public void setCropBlacklist(List<Long> cropBlacklist)
    {
        this.cropBlacklist = cropBlacklist;
    }

    public List<LandItem> getLands()
    {
        return lands;
    }

    public void setLands(List<LandItem> lands)
    {
        this.lands = lands;
    }

    public List<ContainerItem> getContainers()
    {
        return containers;
    }

    public void setContainers(List<ContainerItem> containers)
    {
        this.containers = containers;
    }

    public List<CropItem> getCrops()
    {
        return crops;
    }

    public void setCrops(List<CropItem> crops)
    {
        this.crops = crops;
    }

    public List<CropConfigItem> getCropConfigs()
    {
        return cropConfigs;
    }

    public void setCropConfigs(List<CropConfigItem> cropConfigs)
    {
        this.cropConfigs = cropConfigs;
    }

    public static class LandItem
    {
        private Long landId;
        private String landCode;
        private Integer landType;
        private BigDecimal areaSqm;
        private Integer lightLevel;

        public Long getLandId()
        {
            return landId;
        }

        public void setLandId(Long landId)
        {
            this.landId = landId;
        }

        public String getLandCode()
        {
            return landCode;
        }

        public void setLandCode(String landCode)
        {
            this.landCode = landCode;
        }

        public Integer getLandType()
        {
            return landType;
        }

        public void setLandType(Integer landType)
        {
            this.landType = landType;
        }

        public BigDecimal getAreaSqm()
        {
            return areaSqm;
        }

        public void setAreaSqm(BigDecimal areaSqm)
        {
            this.areaSqm = areaSqm;
        }

        public Integer getLightLevel()
        {
            return lightLevel;
        }

        public void setLightLevel(Integer lightLevel)
        {
            this.lightLevel = lightLevel;
        }
    }

    public static class ContainerItem
    {
        private Long containerId;
        private Long landId;
        private Integer containerType;
        private Integer plantingSites;
        private BigDecimal depthCm;

        public Long getContainerId()
        {
            return containerId;
        }

        public void setContainerId(Long containerId)
        {
            this.containerId = containerId;
        }

        public Long getLandId()
        {
            return landId;
        }

        public void setLandId(Long landId)
        {
            this.landId = landId;
        }

        public Integer getContainerType()
        {
            return containerType;
        }

        public void setContainerType(Integer containerType)
        {
            this.containerType = containerType;
        }

        public Integer getPlantingSites()
        {
            return plantingSites;
        }

        public void setPlantingSites(Integer plantingSites)
        {
            this.plantingSites = plantingSites;
        }

        public BigDecimal getDepthCm()
        {
            return depthCm;
        }

        public void setDepthCm(BigDecimal depthCm)
        {
            this.depthCm = depthCm;
        }
    }

    public static class CropItem
    {
        private Long cropId;
        private String cropName;
        private String isPulse;

        public Long getCropId()
        {
            return cropId;
        }

        public void setCropId(Long cropId)
        {
            this.cropId = cropId;
        }

        public String getCropName()
        {
            return cropName;
        }

        public void setCropName(String cropName)
        {
            this.cropName = cropName;
        }

        public String getIsPulse()
        {
            return isPulse;
        }

        public void setIsPulse(String isPulse)
        {
            this.isPulse = isPulse;
        }
    }

    public static class CropConfigItem
    {
        private Long cropId;
        private Integer landType;
        private Integer startMonth;
        private Integer endMonth;
        private BigDecimal yieldVal;
        private BigDecimal costVal;
        private BigDecimal marketPrice;
        private Integer utilityScore;
        private Integer sunlightReq;
        private BigDecimal minDepthReq;

        public Long getCropId()
        {
            return cropId;
        }

        public void setCropId(Long cropId)
        {
            this.cropId = cropId;
        }

        public Integer getLandType()
        {
            return landType;
        }

        public void setLandType(Integer landType)
        {
            this.landType = landType;
        }

        public Integer getStartMonth()
        {
            return startMonth;
        }

        public void setStartMonth(Integer startMonth)
        {
            this.startMonth = startMonth;
        }

        public Integer getEndMonth()
        {
            return endMonth;
        }

        public void setEndMonth(Integer endMonth)
        {
            this.endMonth = endMonth;
        }

        public BigDecimal getYieldVal()
        {
            return yieldVal;
        }

        public void setYieldVal(BigDecimal yieldVal)
        {
            this.yieldVal = yieldVal;
        }

        public BigDecimal getCostVal()
        {
            return costVal;
        }

        public void setCostVal(BigDecimal costVal)
        {
            this.costVal = costVal;
        }

        public BigDecimal getMarketPrice()
        {
            return marketPrice;
        }

        public void setMarketPrice(BigDecimal marketPrice)
        {
            this.marketPrice = marketPrice;
        }

        public Integer getUtilityScore()
        {
            return utilityScore;
        }

        public void setUtilityScore(Integer utilityScore)
        {
            this.utilityScore = utilityScore;
        }

        public Integer getSunlightReq()
        {
            return sunlightReq;
        }

        public void setSunlightReq(Integer sunlightReq)
        {
            this.sunlightReq = sunlightReq;
        }

        public BigDecimal getMinDepthReq()
        {
            return minDepthReq;
        }

        public void setMinDepthReq(BigDecimal minDepthReq)
        {
            this.minDepthReq = minDepthReq;
        }
    }
}
