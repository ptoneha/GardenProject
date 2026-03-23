package com.ruoyi.agriculture.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.agriculture.domain.BusLand;
import com.ruoyi.agriculture.domain.BusOptimizationTask;
import com.ruoyi.agriculture.domain.BusTaskLand;
import com.ruoyi.agriculture.mapper.BusLandMapper;
import com.ruoyi.agriculture.mapper.BusOptimizationTaskMapper;
import com.ruoyi.agriculture.mapper.BusTaskLandMapper;
import com.ruoyi.agriculture.service.IBusTaskLandService;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 任务资源关联Service业务层处理
 */
@Service
public class BusTaskLandServiceImpl implements IBusTaskLandService
{
    @Autowired
    private BusTaskLandMapper busTaskLandMapper;

    @Autowired
    private BusOptimizationTaskMapper busOptimizationTaskMapper;

    @Autowired
    private BusLandMapper busLandMapper;

    @Override
    public BusTaskLand selectBusTaskLandByTaskId(Long taskId)
    {
        if (!canAccessTask(taskId))
        {
            return null;
        }
        return busTaskLandMapper.selectBusTaskLandByTaskId(taskId);
    }

    @Override
    public List<BusTaskLand> selectBusTaskLandList(BusTaskLand busTaskLand)
    {
        if (isCurrentAdmin())
        {
            return busTaskLandMapper.selectBusTaskLandList(busTaskLand);
        }
        if (busTaskLand == null || busTaskLand.getTaskId() == null || !canAccessTask(busTaskLand.getTaskId()))
        {
            return Collections.emptyList();
        }
        if (busTaskLand.getLandId() != null && !canAccessLand(busTaskLand.getLandId()))
        {
            return Collections.emptyList();
        }
        return busTaskLandMapper.selectBusTaskLandList(busTaskLand);
    }

    @Override
    public int insertBusTaskLand(BusTaskLand busTaskLand)
    {
        if (!canAccessTask(busTaskLand.getTaskId()) || !canAccessLand(busTaskLand.getLandId()))
        {
            return 0;
        }
        return busTaskLandMapper.insertBusTaskLand(busTaskLand);
    }

    @Override
    public int updateBusTaskLand(BusTaskLand busTaskLand)
    {
        if (!canAccessTask(busTaskLand.getTaskId()) || !canAccessLand(busTaskLand.getLandId()))
        {
            return 0;
        }
        return busTaskLandMapper.updateBusTaskLand(busTaskLand);
    }

    @Override
    public int deleteBusTaskLandByTaskIds(Long[] taskIds)
    {
        int rows = 0;
        for (Long taskId : taskIds)
        {
          rows += deleteBusTaskLandByTaskId(taskId);
        }
        return rows;
    }

    @Override
    public int deleteBusTaskLandByTaskId(Long taskId)
    {
        if (!canAccessTask(taskId))
        {
            return 0;
        }
        return busTaskLandMapper.deleteBusTaskLandByTaskId(taskId);
    }

    private boolean canAccessTask(Long taskId)
    {
        BusOptimizationTask task = busOptimizationTaskMapper.selectBusOptimizationTaskByTaskId(taskId);
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

    private boolean canAccessLand(Long landId)
    {
        BusLand land = busLandMapper.selectBusLandByLandId(landId);
        if (land == null)
        {
            return false;
        }
        if (isCurrentAdmin())
        {
            return true;
        }
        return Objects.equals(resolveCurrentUsername(), land.getCreateBy());
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

    private String resolveCurrentUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
