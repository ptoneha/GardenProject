package com.ruoyi.agriculture.mapper;

import java.util.List;
import com.ruoyi.agriculture.domain.BusOptimizationTask;

/**
 * 种植优化任务Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface BusOptimizationTaskMapper 
{
    /**
     * 查询种植优化任务
     * 
     * @param taskId 种植优化任务主键
     * @return 种植优化任务
     */
    public BusOptimizationTask selectBusOptimizationTaskByTaskId(Long taskId);

    /**
     * 查询种植优化任务列表
     * 
     * @param busOptimizationTask 种植优化任务
     * @return 种植优化任务集合
     */
    public List<BusOptimizationTask> selectBusOptimizationTaskList(BusOptimizationTask busOptimizationTask);

    /**
     * 新增种植优化任务
     * 
     * @param busOptimizationTask 种植优化任务
     * @return 结果
     */
    public int insertBusOptimizationTask(BusOptimizationTask busOptimizationTask);

    /**
     * 修改种植优化任务
     * 
     * @param busOptimizationTask 种植优化任务
     * @return 结果
     */
    public int updateBusOptimizationTask(BusOptimizationTask busOptimizationTask);

    /**
     * 删除种植优化任务
     * 
     * @param taskId 种植优化任务主键
     * @return 结果
     */
    public int deleteBusOptimizationTaskByTaskId(Long taskId);

    /**
     * 批量删除种植优化任务
     * 
     * @param taskIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusOptimizationTaskByTaskIds(Long[] taskIds);
}
