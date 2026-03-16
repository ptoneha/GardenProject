package com.ruoyi.agriculture.service;

import java.util.List;
import com.ruoyi.agriculture.domain.BusContainer;

/**
 * 种植容器管理Service接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface IBusContainerService 
{
    /**
     * 查询种植容器管理
     * 
     * @param containerId 种植容器管理主键
     * @return 种植容器管理
     */
    public BusContainer selectBusContainerByContainerId(Long containerId);

    /**
     * 查询种植容器管理列表
     * 
     * @param busContainer 种植容器管理
     * @return 种植容器管理集合
     */
    public List<BusContainer> selectBusContainerList(BusContainer busContainer);

    /**
     * 新增种植容器管理
     * 
     * @param busContainer 种植容器管理
     * @return 结果
     */
    public int insertBusContainer(BusContainer busContainer);

    /**
     * 修改种植容器管理
     * 
     * @param busContainer 种植容器管理
     * @return 结果
     */
    public int updateBusContainer(BusContainer busContainer);

    /**
     * 批量删除种植容器管理
     * 
     * @param containerIds 需要删除的种植容器管理主键集合
     * @return 结果
     */
    public int deleteBusContainerByContainerIds(Long[] containerIds);

    /**
     * 删除种植容器管理信息
     * 
     * @param containerId 种植容器管理主键
     * @return 结果
     */
    public int deleteBusContainerByContainerId(Long containerId);
}
