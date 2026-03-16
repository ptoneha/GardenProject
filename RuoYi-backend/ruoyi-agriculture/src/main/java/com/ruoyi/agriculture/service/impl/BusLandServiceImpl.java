package com.ruoyi.agriculture.service.impl;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.agriculture.mapper.BusLandMapper;
import com.ruoyi.agriculture.domain.BusLand;
import com.ruoyi.agriculture.service.IBusLandService;

/**
 * 地块资源管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@Service
public class BusLandServiceImpl implements IBusLandService 
{
    @Autowired
    private BusLandMapper busLandMapper;

    // 1. 定义换算常量：1亩 = 666.67平方米
    private static final BigDecimal MU_TO_SQM_RATIO = new BigDecimal("666.67");

    /**
     * 内部私有方法：统一处理面积换算逻辑
     */
    private void calculateAreaSqm(BusLand busLand) {
        if (busLand.getInputValue() != null && busLand.getUnit() != null) {
            if ("mu".equals(busLand.getUnit())) {
                // 如果用户选的是“亩”，系统自动存计算后的平方米
                busLand.setAreaSqm(busLand.getInputValue().multiply(MU_TO_SQM_RATIO));
            } else {
                // 如果用户选的是“平方米”，系统直接对齐存入
                busLand.setAreaSqm(busLand.getInputValue());
            }
        }
    }

    /**
     * 查询地块资源管理
     * 
     * @param landId 地块资源管理主键
     * @return 地块资源管理
     */
    @Override
    public BusLand selectBusLandByLandId(Long landId)
    {
        return busLandMapper.selectBusLandByLandId(landId);
    }

    /**
     * 查询地块资源管理列表
     * 
     * @param busLand 地块资源管理
     * @return 地块资源管理
     */
    @Override
    public List<BusLand> selectBusLandList(BusLand busLand)
    {
        return busLandMapper.selectBusLandList(busLand);
    }

    /**
     * 新增地块资源管理
     * 
     * @param busLand 地块资源管理
     * @return 结果
     */
    @Override
    public int insertBusLand(BusLand busLand)
    {
        // 2. 在插入前执行换算
        calculateAreaSqm(busLand);
        busLand.setCreateTime(DateUtils.getNowDate());
        return busLandMapper.insertBusLand(busLand);
    }

    /**
     * 修改地块资源管理
     * 
     * @param busLand 地块资源管理
     * @return 结果
     */
    @Override
    public int updateBusLand(BusLand busLand)
    {
        // 3. 在更新前执行换算（防止用户修改了面积数值或单位）
        calculateAreaSqm(busLand);
        return busLandMapper.updateBusLand(busLand);
    }

    /**
     * 批量删除地块资源管理
     * 
     * @param landIds 需要删除的地块资源管理主键
     * @return 结果
     */
    @Override
    public int deleteBusLandByLandIds(Long[] landIds)
    {
        return busLandMapper.deleteBusLandByLandIds(landIds);
    }

    /**
     * 删除地块资源管理信息
     * 
     * @param landId 地块资源管理主键
     * @return 结果
     */
    @Override
    public int deleteBusLandByLandId(Long landId)
    {
        return busLandMapper.deleteBusLandByLandId(landId);
    }
}
