package com.ruoyi.agriculture.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 作物百科管理对象 bus_crop
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public class BusCrop extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 作物ID */
    private Long cropId;

    /** 国赛C题编号 */
    @Excel(name = "国赛C题编号")
    private Long cropCode;

    /** 作物名称 */
    @Excel(name = "作物名称")
    private String cropName;

    /** 作物分类 */
    @Excel(name = "作物分类")
    private String category;

    /** 主要种植目的 */
    @Excel(name = "主要种植目的")
    private Integer plantPurpose;

    /** 是否为豆类 */
    @Excel(name = "是否为豆类")
    private String isPulse;

    /** 作物图片 */
    @Excel(name = "作物图片")
    private String imageUrl;

    public void setCropId(Long cropId) 
    {
        this.cropId = cropId;
    }

    public Long getCropId() 
    {
        return cropId;
    }

    public void setCropCode(Long cropCode) 
    {
        this.cropCode = cropCode;
    }

    public Long getCropCode() 
    {
        return cropCode;
    }

    public void setCropName(String cropName) 
    {
        this.cropName = cropName;
    }

    public String getCropName() 
    {
        return cropName;
    }

    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setPlantPurpose(Integer plantPurpose) 
    {
        this.plantPurpose = plantPurpose;
    }

    public Integer getPlantPurpose() 
    {
        return plantPurpose;
    }

    public void setIsPulse(String isPulse) 
    {
        this.isPulse = isPulse;
    }

    public String getIsPulse() 
    {
        return isPulse;
    }

    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("cropId", getCropId())
            .append("cropCode", getCropCode())
            .append("cropName", getCropName())
            .append("category", getCategory())
            .append("plantPurpose", getPlantPurpose())
            .append("isPulse", getIsPulse())
            .append("imageUrl", getImageUrl())
            .toString();
    }
}
