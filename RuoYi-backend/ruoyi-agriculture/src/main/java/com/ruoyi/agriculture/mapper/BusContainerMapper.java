package com.ruoyi.agriculture.mapper;

import java.util.List;
import com.ruoyi.agriculture.domain.BusContainer;

/**
 * 种植容器管理Mapper接口
 */
public interface BusContainerMapper
{
    /**
     * 查询种植容器管理
     *
     * @param containerId 容器ID
     * @return 种植容器管理
     */
    public BusContainer selectBusContainerByContainerId(Long containerId);

    /**
     * 根据地块ID批量查询种植容器
     *
     * @param landIds 地块ID集合
     * @return 种植容器集合
     */
    public List<BusContainer> selectBusContainerByLandIds(List<Long> landIds);

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
     * 删除种植容器管理
     *
     * @param containerId 容器ID
     * @return 结果
     */
    public int deleteBusContainerByContainerId(Long containerId);

    /**
     * 批量删除种植容器管理
     *
     * @param containerIds 容器ID集合
     * @return 结果
     */
    public int deleteBusContainerByContainerIds(Long[] containerIds);
}
