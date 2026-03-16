package com.ruoyi.agriculture.service;

import java.util.List;
import com.ruoyi.agriculture.domain.BusOptimizationTask;
import com.ruoyi.agriculture.domain.dto.AgriTaskExecuteResult;

/**
 * 种植优化任务Service接口
 */
public interface IBusOptimizationTaskService
{
    /**
     * 查询种植优化任务
     *
     * @param taskId 任务ID
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
     * 批量删除种植优化任务
     *
     * @param taskIds 任务ID集合
     * @return 结果
     */
    public int deleteBusOptimizationTaskByTaskIds(Long[] taskIds);

    /**
     * 删除种植优化任务
     *
     * @param taskId 任务ID
     * @return 结果
     */
    public int deleteBusOptimizationTaskByTaskId(Long taskId);

    /**
     * 执行优化任务
     *
     * @param taskId 任务ID
     * @return 执行结果
     */
    public AgriTaskExecuteResult executeTask(Long taskId);
}
