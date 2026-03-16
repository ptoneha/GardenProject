package com.ruoyi.agriculture.mapper;

import java.util.List;
import com.ruoyi.agriculture.domain.BusTaskLand;

/**
 * 任务资源关联Mapper接口
 */
public interface BusTaskLandMapper
{
    /**
     * 查询任务资源关联
     *
     * @param taskId 任务ID
     * @return 任务资源关联
     */
    public BusTaskLand selectBusTaskLandByTaskId(Long taskId);

    /**
     * 根据任务ID查询任务资源关联列表
     *
     * @param taskId 任务ID
     * @return 任务资源关联集合
     */
    public List<BusTaskLand> selectBusTaskLandListByTaskId(Long taskId);

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
     * 删除任务资源关联
     *
     * @param taskId 任务ID
     * @return 结果
     */
    public int deleteBusTaskLandByTaskId(Long taskId);

    /**
     * 批量删除任务资源关联
     *
     * @param taskIds 任务ID集合
     * @return 结果
     */
    public int deleteBusTaskLandByTaskIds(Long[] taskIds);
}
