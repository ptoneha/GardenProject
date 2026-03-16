package com.ruoyi.agriculture.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 任务资源关联对象 bus_task_land
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public class BusTaskLand extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    @Excel(name = "任务ID")
    private Long taskId;

    /** 地块ID */
    @Excel(name = "地块ID")
    private Long landId;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("landId", getLandId())
            .toString();
    }
}
