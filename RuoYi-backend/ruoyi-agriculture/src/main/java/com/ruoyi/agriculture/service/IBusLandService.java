package com.ruoyi.agriculture.service;

import java.util.List;
import com.ruoyi.agriculture.domain.BusLand;

/**
 * 地块资源管理Service接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface IBusLandService 
{
    /**
     * 查询地块资源管理
     * 
     * @param landId 地块资源管理主键
     * @return 地块资源管理
     */
    public BusLand selectBusLandByLandId(Long landId);

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
     * 批量删除地块资源管理
     * 
     * @param landIds 需要删除的地块资源管理主键集合
     * @return 结果
     */
    public int deleteBusLandByLandIds(Long[] landIds);

    /**
     * 删除地块资源管理信息
     * 
     * @param landId 地块资源管理主键
     * @return 结果
     */
    public int deleteBusLandByLandId(Long landId);
}
