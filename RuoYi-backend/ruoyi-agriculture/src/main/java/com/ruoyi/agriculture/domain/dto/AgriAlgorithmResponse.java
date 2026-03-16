package com.ruoyi.agriculture.domain.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Python算法返回对象
 */
public class AgriAlgorithmResponse
{
    private Boolean success;

    private Integer status;

    private String message;

    private String infeasibleReason;

    private List<ResultItem> results;

    public Boolean getSuccess()
    {
        return success;
    }

    public void setSuccess(Boolean success)
    {
        this.success = success;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getInfeasibleReason()
    {
        return infeasibleReason;
    }

    public void setInfeasibleReason(String infeasibleReason)
    {
        this.infeasibleReason = infeasibleReason;
    }

    public List<ResultItem> getResults()
    {
        return results;
    }

    public void setResults(List<ResultItem> results)
    {
        this.results = results;
    }

    public static class ResultItem
    {
        private Long landId;
        private Long containerId;
        private Long cropId;
        private BigDecimal allocatedAreaSqm;
        private BigDecimal displayAreaVal;
        private Integer plantCount;
        private BigDecimal profitAmount;
        private Long experienceScore;

        public Long getLandId()
        {
            return landId;
        }

        public void setLandId(Long landId)
        {
            this.landId = landId;
        }

        public Long getContainerId()
        {
            return containerId;
        }

        public void setContainerId(Long containerId)
        {
            this.containerId = containerId;
        }

        public Long getCropId()
        {
            return cropId;
        }

        public void setCropId(Long cropId)
        {
            this.cropId = cropId;
        }

        public BigDecimal getAllocatedAreaSqm()
        {
            return allocatedAreaSqm;
        }

        public void setAllocatedAreaSqm(BigDecimal allocatedAreaSqm)
        {
            this.allocatedAreaSqm = allocatedAreaSqm;
        }

        public BigDecimal getDisplayAreaVal()
        {
            return displayAreaVal;
        }

        public void setDisplayAreaVal(BigDecimal displayAreaVal)
        {
            this.displayAreaVal = displayAreaVal;
        }

        public Integer getPlantCount()
        {
            return plantCount;
        }

        public void setPlantCount(Integer plantCount)
        {
            this.plantCount = plantCount;
        }

        public BigDecimal getProfitAmount()
        {
            return profitAmount;
        }

        public void setProfitAmount(BigDecimal profitAmount)
        {
            this.profitAmount = profitAmount;
        }

        public Long getExperienceScore()
        {
            return experienceScore;
        }

        public void setExperienceScore(Long experienceScore)
        {
            this.experienceScore = experienceScore;
        }
    }
}
