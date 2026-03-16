package com.ruoyi.agriculture.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.agriculture.mapper.BusContainerMapper;
import com.ruoyi.agriculture.domain.BusContainer;
import com.ruoyi.agriculture.service.IBusContainerService;

/**
 * 种植容器管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@Service
public class BusContainerServiceImpl implements IBusContainerService 
{
    @Autowired
    private BusContainerMapper busContainerMapper;

    /**
     * 查询种植容器管理
     * 
     * @param containerId 种植容器管理主键
     * @return 种植容器管理
     */
    @Override
    public BusContainer selectBusContainerByContainerId(Long containerId)
    {
        return busContainerMapper.selectBusContainerByContainerId(containerId);
    }

    /**
     * 查询种植容器管理列表
     * 
     * @param busContainer 种植容器管理
     * @return 种植容器管理
     */
    @Override
    public List<BusContainer> selectBusContainerList(BusContainer busContainer)
    {
        return busContainerMapper.selectBusContainerList(busContainer);
    }

    /**
     * 新增种植容器管理
     * 
     * @param busContainer 种植容器管理
     * @return 结果
     */
    @Override
    public int insertBusContainer(BusContainer busContainer)
    {
        return busContainerMapper.insertBusContainer(busContainer);
    }

    /**
     * 修改种植容器管理
     * 
     * @param busContainer 种植容器管理
     * @return 结果
     */
    @Override
    public int updateBusContainer(BusContainer busContainer)
    {
        return busContainerMapper.updateBusContainer(busContainer);
    }

    /**
     * 批量删除种植容器管理
     * 
     * @param containerIds 需要删除的种植容器管理主键
     * @return 结果
     */
    @Override
    public int deleteBusContainerByContainerIds(Long[] containerIds)
    {
        return busContainerMapper.deleteBusContainerByContainerIds(containerIds);
    }

    /**
     * 删除种植容器管理信息
     * 
     * @param containerId 种植容器管理主键
     * @return 结果
     */
    @Override
    public int deleteBusContainerByContainerId(Long containerId)
    {
        return busContainerMapper.deleteBusContainerByContainerId(containerId);
    }
}
