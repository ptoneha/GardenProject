package com.ruoyi.agriculture.mapper;

import java.util.List;
import com.ruoyi.agriculture.domain.BusLand;

/**
 * 地块资源管理Mapper接口
 */
public interface BusLandMapper
{
    /**
     * 查询地块资源管理
     *
     * @param landId 地块ID
     * @return 地块资源管理
     */
    public BusLand selectBusLandByLandId(Long landId);

    /**
     * 批量查询地块资源管理
     *
     * @param landIds 地块ID集合
     * @return 地块资源管理集合
     */
    public List<BusLand> selectBusLandByLandIds(List<Long> landIds);

    /**
     * 查询地块资源管理列表
     *
     * @param busLand 地块资源管理
     * @return 地块资源管理集合
     */
    public List<BusLand> selectBusLandList(BusLand busLand);

    /**
     * 新增地块资源管理
     *
     * @param busLand 地块资源管理
     * @return 结果
     */
    public int insertBusLand(BusLand busLand);

    /**
     * 修改地块资源管理
     *
     * @param busLand 地块资源管理
     * @return 结果
     */
    public int updateBusLand(BusLand busLand);

    /**
     * 删除地块资源管理
     *
     * @param landId 地块ID
     * @return 结果
     */
    public int deleteBusLandByLandId(Long landId);

    /**
     * 批量删除地块资源管理
     *
     * @param landIds 地块ID集合
     * @return 结果
     */
    public int deleteBusLandByLandIds(Long[] landIds);
}
