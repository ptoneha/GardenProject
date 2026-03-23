package com.ruoyi.agriculture.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.agriculture.domain.BusOptimizationTask;
import com.ruoyi.agriculture.domain.BusPlantingResult;
import com.ruoyi.agriculture.mapper.BusOptimizationTaskMapper;
import com.ruoyi.agriculture.mapper.BusPlantingResultMapper;
import com.ruoyi.agriculture.service.IBusPlantingResultService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 种植决策方案Service业务层处理
 */
@Service
public class BusPlantingResultServiceImpl implements IBusPlantingResultService
{
    @Autowired
    private BusPlantingResultMapper busPlantingResultMapper;

    @Autowired
    private BusOptimizationTaskMapper busOptimizationTaskMapper;

    @Override
    public BusPlantingResult selectBusPlantingResultByResultId(Long resultId)
    {
        BusPlantingResult result = busPlantingResultMapper.selectBusPlantingResultByResultId(resultId);
        return canAccessTask(result == null ? null : result.getTaskId()) ? result : null;
    }

    @Override
    public List<BusPlantingResult> selectBusPlantingResultList(BusPlantingResult busPlantingResult)
    {
        if (isCurrentAdmin())
        {
            return busPlantingResultMapper.selectBusPlantingResultList(busPlantingResult);
        }
        if (busPlantingResult == null || busPlantingResult.getTaskId() == null)
        {
            return Collections.emptyList();
        }
        if (!canAccessTask(busPlantingResult.getTaskId()))
        {
            return Collections.emptyList();
        }
        return busPlantingResultMapper.selectBusPlantingResultList(busPlantingResult);
    }

    @Override
    public int insertBusPlantingResult(BusPlantingResult busPlantingResult)
    {
        if (!canAccessTask(busPlantingResult.getTaskId()))
        {
            return 0;
        }
        busPlantingResult.setCreateTime(DateUtils.getNowDate());
        return busPlantingResultMapper.insertBusPlantingResult(busPlantingResult);
    }

    @Override
    public int updateBusPlantingResult(BusPlantingResult busPlantingResult)
    {
        if (!canAccessTask(busPlantingResult.getTaskId()))
        {
            return 0;
        }
        return busPlantingResultMapper.updateBusPlantingResult(busPlantingResult);
    }

    @Override
    public int deleteBusPlantingResultByResultIds(Long[] resultIds)
    {
        int rows = 0;
        for (Long resultId : resultIds)
        {
            rows += deleteBusPlantingResultByResultId(resultId);
        }
        return rows;
    }

    @Override
    public int deleteBusPlantingResultByResultId(Long resultId)
    {
        BusPlantingResult result = busPlantingResultMapper.selectBusPlantingResultByResultId(resultId);
        if (result == null || !canAccessTask(result.getTaskId()))
        {
            return 0;
        }
        return busPlantingResultMapper.deleteBusPlantingResultByResultId(resultId);
    }

    private boolean canAccessTask(Long taskId)
    {
        BusOptimizationTask task = taskId == null ? null : busOptimizationTaskMapper.selectBusOptimizationTaskByTaskId(taskId);
        if (task == null)
        {
            return false;
        }
        if (isCurrentAdmin())
        {
            return true;
        }
        return Objects.equals(resolveCurrentUserId(), task.getOwnerUserId());
    }

    private boolean isCurrentAdmin()
    {
        Long userId = resolveCurrentUserId();
        return userId != null && SecurityUtils.isAdmin(userId);
    }

    private Long resolveCurrentUserId()
    {
        try
        {
            return SecurityUtils.getUserId();
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
