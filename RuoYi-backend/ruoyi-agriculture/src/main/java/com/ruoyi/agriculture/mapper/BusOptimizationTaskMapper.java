package com.ruoyi.agriculture.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.agriculture.domain.BusOptimizationTask;

/**
 * 种植优化任务 Mapper 接口
 */
public interface BusOptimizationTaskMapper
{
    BusOptimizationTask selectBusOptimizationTaskByTaskId(Long taskId);

    List<BusOptimizationTask> selectBusOptimizationTaskList(BusOptimizationTask busOptimizationTask);

    int insertBusOptimizationTask(BusOptimizationTask busOptimizationTask);

    int updateBusOptimizationTask(BusOptimizationTask busOptimizationTask);

    int deleteBusOptimizationTaskByTaskId(Long taskId);

    int deleteBusOptimizationTaskByTaskIds(Long[] taskIds);

    int updateTaskStatusWithLock(@Param("taskId") Long taskId,
        @Param("fromStatus") String fromStatus,
        @Param("toStatus") String toStatus);
}
