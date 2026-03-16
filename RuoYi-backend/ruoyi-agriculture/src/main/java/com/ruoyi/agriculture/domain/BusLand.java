package com.ruoyi.agriculture.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 地块资源管理对象 bus_land
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public class BusLand extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 地块ID */
    private Long landId;

    /** 位置名称 */
    @Excel(name = "位置名称")
    private String landCode;

    /** 地块类型 */
    @Excel(name = "地块类型")
    private Integer landType;

    /** 用户输入数值 */
    @Excel(name = "用户输入数值")
    private BigDecimal inputValue;

    /** 输入单位 */
    @Excel(name = "输入单位")
    private String unit;

    /** 算法基准面积(平方米) */
    @Excel(name = "算法基准面积(平方米)")
    private BigDecimal areaSqm;

    /** 光照等级 */
    @Excel(name = "光照等级")
    private Integer lightLevel;

    public void setLandId(Long landId) 
    {
        this.landId = landId;
    }

    public Long getLandId() 
    {
        return landId;
    }

    public void setLandCode(String landCode) 
    {
        this.landCode = landCode;
    }

    public String getLandCode() 
    {
        return landCode;
    }

    public void setLandType(Integer landType) 
    {
        this.landType = landType;
    }

    public Integer getLandType() 
    {
        return landType;
    }

    public void setInputValue(BigDecimal inputValue) 
    {
        this.inputValue = inputValue;
    }

    public BigDecimal getInputValue() 
    {
        return inputValue;
    }

    public void setUnit(String unit) 
    {
        this.unit = unit;
    }

    public String getUnit() 
    {
        return unit;
    }

    public void setAreaSqm(BigDecimal areaSqm) 
    {
        this.areaSqm = areaSqm;
    }

    public BigDecimal getAreaSqm() 
    {
        return areaSqm;
    }

    public void setLightLevel(Integer lightLevel) 
    {
        this.lightLevel = lightLevel;
    }

    public Integer getLightLevel() 
    {
        return lightLevel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("landId", getLandId())
            .append("landCode", getLandCode())
            .append("landType", getLandType())
            .append("inputValue", getInputValue())
            .append("unit", getUnit())
            .append("areaSqm", getAreaSqm())
            .append("lightLevel", getLightLevel())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
