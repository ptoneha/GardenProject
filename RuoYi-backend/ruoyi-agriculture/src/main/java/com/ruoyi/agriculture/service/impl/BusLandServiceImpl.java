package com.ruoyi.agriculture.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.agriculture.domain.BusLand;
import com.ruoyi.agriculture.mapper.BusLandMapper;
import com.ruoyi.agriculture.service.IBusLandService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 地块资源管理Service业务层处理
 */
@Service
public class BusLandServiceImpl implements IBusLandService
{
    @Autowired
    private BusLandMapper busLandMapper;

    private static final BigDecimal MU_TO_SQM_RATIO = new BigDecimal("666.67");

    @Override
    public BusLand selectBusLandByLandId(Long landId)
    {
        BusLand busLand = busLandMapper.selectBusLandByLandId(landId);
        return canAccessLand(busLand) ? busLand : null;
    }

    @Override
    public List<BusLand> selectBusLandList(BusLand busLand)
    {
        applyCurrentUserFilter(busLand);
        return busLandMapper.selectBusLandList(busLand);
    }

    @Override
    public int insertBusLand(BusLand busLand)
    {
        calculateAreaSqm(busLand);
        busLand.setCreateTime(DateUtils.getNowDate());
        if (busLand.getCreateBy() == null)
        {
            busLand.setCreateBy(resolveCurrentUsername());
        }
        return busLandMapper.insertBusLand(busLand);
    }

    @Override
    public int updateBusLand(BusLand busLand)
    {
        if (!canAccessLand(busLand.getLandId()))
        {
            return 0;
        }
        calculateAreaSqm(busLand);
        return busLandMapper.updateBusLand(busLand);
    }

    @Override
    public int deleteBusLandByLandIds(Long[] landIds)
    {
        int rows = 0;
        for (Long landId : landIds)
        {
            rows += deleteBusLandByLandId(landId);
        }
        return rows;
    }

    @Override
    public int deleteBusLandByLandId(Long landId)
    {
        if (!canAccessLand(landId))
        {
            return 0;
        }
        return busLandMapper.deleteBusLandByLandId(landId);
    }

    private void calculateAreaSqm(BusLand busLand)
    {
        if (busLand.getInputValue() != null && busLand.getUnit() != null)
        {
            if ("mu".equals(busLand.getUnit()))
            {
                busLand.setAreaSqm(busLand.getInputValue().multiply(MU_TO_SQM_RATIO));
            }
            else
            {
                busLand.setAreaSqm(busLand.getInputValue());
            }
        }
    }

    private void applyCurrentUserFilter(BusLand busLand)
    {
        if (busLand == null || isCurrentAdmin())
        {
            return;
        }
        busLand.setCreateBy(resolveCurrentUsername());
    }

    private boolean canAccessLand(Long landId)
    {
        return canAccessLand(busLandMapper.selectBusLandByLandId(landId));
    }

    private boolean canAccessLand(BusLand busLand)
    {
        if (busLand == null)
        {
            return false;
        }
        if (isCurrentAdmin())
        {
            return true;
        }
        return Objects.equals(resolveCurrentUsername(), busLand.getCreateBy());
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
