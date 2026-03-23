package com.ruoyi.agriculture.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.agriculture.domain.BusContainer;
import com.ruoyi.agriculture.domain.BusLand;
import com.ruoyi.agriculture.mapper.BusContainerMapper;
import com.ruoyi.agriculture.mapper.BusLandMapper;
import com.ruoyi.agriculture.service.IBusContainerService;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 种植容器管理Service业务层处理
 */
@Service
public class BusContainerServiceImpl implements IBusContainerService
{
    @Autowired
    private BusContainerMapper busContainerMapper;

    @Autowired
    private BusLandMapper busLandMapper;

    @Override
    public BusContainer selectBusContainerByContainerId(Long containerId)
    {
        BusContainer busContainer = busContainerMapper.selectBusContainerByContainerId(containerId);
        return canAccessContainer(busContainer) ? busContainer : null;
    }

    @Override
    public List<BusContainer> selectBusContainerList(BusContainer busContainer)
    {
        if (isCurrentAdmin())
        {
            return busContainerMapper.selectBusContainerList(busContainer);
        }
        if (busContainer != null && busContainer.getLandId() != null)
        {
            if (!canAccessLand(busContainer.getLandId()))
            {
                return Collections.emptyList();
            }
            return busContainerMapper.selectBusContainerList(busContainer);
        }

        List<Long> landIds = resolveCurrentLandIds();
        if (landIds.isEmpty())
        {
            return Collections.emptyList();
        }
        List<BusContainer> containers = busContainerMapper.selectBusContainerByLandIds(landIds);
        if (busContainer != null && StringUtils.isNotEmpty(busContainer.getContainerName()))
        {
            return containers.stream()
                .filter(item -> StringUtils.containsIgnoreCase(item.getContainerName(), busContainer.getContainerName()))
                .collect(Collectors.toList());
        }
        return containers;
    }

    @Override
    public int insertBusContainer(BusContainer busContainer)
    {
        if (!canAccessLand(busContainer.getLandId()))
        {
            return 0;
        }
        return busContainerMapper.insertBusContainer(busContainer);
    }

    @Override
    public int updateBusContainer(BusContainer busContainer)
    {
        if (!canAccessContainer(busContainer.getContainerId()) || !canAccessLand(busContainer.getLandId()))
        {
            return 0;
        }
        return busContainerMapper.updateBusContainer(busContainer);
    }

    @Override
    public int deleteBusContainerByContainerIds(Long[] containerIds)
    {
        int rows = 0;
        for (Long containerId : containerIds)
        {
            rows += deleteBusContainerByContainerId(containerId);
        }
        return rows;
    }

    @Override
    public int deleteBusContainerByContainerId(Long containerId)
    {
        if (!canAccessContainer(containerId))
        {
            return 0;
        }
        return busContainerMapper.deleteBusContainerByContainerId(containerId);
    }

    private List<Long> resolveCurrentLandIds()
    {
        BusLand query = new BusLand();
        query.setCreateBy(resolveCurrentUsername());
        return busLandMapper.selectBusLandList(query).stream()
            .map(BusLand::getLandId)
            .collect(Collectors.toList());
    }

    private boolean canAccessContainer(Long containerId)
    {
        return canAccessContainer(busContainerMapper.selectBusContainerByContainerId(containerId));
    }

    private boolean canAccessContainer(BusContainer busContainer)
    {
        return busContainer != null && canAccessLand(busContainer.getLandId());
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
