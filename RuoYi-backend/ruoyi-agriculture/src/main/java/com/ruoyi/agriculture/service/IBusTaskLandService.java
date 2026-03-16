package com.ruoyi.agriculture.service;

import java.util.List;
import com.ruoyi.agriculture.domain.BusTaskLand;

/**
 * 任务资源关联Service接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface IBusTaskLandService 
{
    /**
     * 查询任务资源关联
     * 
     * @param taskId 任务资源关联主键
     * @return 任务资源关联
     */
    public BusTaskLand selectBusTaskLandByTaskId(Long taskId);

    /**
     * 查询任务资源关联列表
     * 
     * @param busTaskLand 任务资源关联
     * @return 任务资源关联集合
     */
    public List<BusTaskLand> selectBusTaskLandList(BusTaskLand busTaskLand);

    /**
     * 新增任务资源关联
     * 
     * @param busTaskLand 任务资源关联
     * @return 结果
     */
    public int insertBusTaskLand(BusTaskLand busTaskLand);

    /**
     * 修改任务资源关联
     * 
     * @param busTaskLand 任务资源关联
     * @return 结果
     */
    public int updateBusTaskLand(BusTaskLand busTaskLand);

    /**
     * 批量删除任务资源关联
     * 
     * @param taskIds 需要删除的任务资源关联主键集合
     * @return 结果
     */
    public int deleteBusTaskLandByTaskIds(Long[] taskIds);

    /**
     * 删除任务资源关联信息
     * 
     * @param taskId 任务资源关联主键
     * @return 结果
     */
    public int deleteBusTaskLandByTaskId(Long taskId);
}
