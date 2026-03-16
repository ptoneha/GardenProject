package com.ruoyi.agriculture.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.agriculture.mapper.BusPlantingResultMapper;
import com.ruoyi.agriculture.domain.BusPlantingResult;
import com.ruoyi.agriculture.service.IBusPlantingResultService;

/**
 * 种植决策方案Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@Service
public class BusPlantingResultServiceImpl implements IBusPlantingResultService 
{
    @Autowired
    private BusPlantingResultMapper busPlantingResultMapper;

    /**
     * 查询种植决策方案
     * 
     * @param resultId 种植决策方案主键
     * @return 种植决策方案
     */
    @Override
    public BusPlantingResult selectBusPlantingResultByResultId(Long resultId)
    {
        return busPlantingResultMapper.selectBusPlantingResultByResultId(resultId);
    }

    /**
     * 查询种植决策方案列表
     * 
     * @param busPlantingResult 种植决策方案
     * @return 种植决策方案
     */
    @Override
    public List<BusPlantingResult> selectBusPlantingResultList(BusPlantingResult busPlantingResult)
    {
        return busPlantingResultMapper.selectBusPlantingResultList(busPlantingResult);
    }

    /**
     * 新增种植决策方案
     * 
     * @param busPlantingResult 种植决策方案
     * @return 结果
     */
    @Override
    public int insertBusPlantingResult(BusPlantingResult busPlantingResult)
    {
        busPlantingResult.setCreateTime(DateUtils.getNowDate());
        return busPlantingResultMapper.insertBusPlantingResult(busPlantingResult);
    }

    /**
     * 修改种植决策方案
     * 
     * @param busPlantingResult 种植决策方案
     * @return 结果
     */
    @Override
    public int updateBusPlantingResult(BusPlantingResult busPlantingResult)
    {
        return busPlantingResultMapper.updateBusPlantingResult(busPlantingResult);
    }

    /**
     * 批量删除种植决策方案
     * 
     * @param resultIds 需要删除的种植决策方案主键
     * @return 结果
     */
    @Override
    public int deleteBusPlantingResultByResultIds(Long[] resultIds)
    {
        return busPlantingResultMapper.deleteBusPlantingResultByResultIds(resultIds);
    }

    /**
     * 删除种植决策方案信息
     * 
     * @param resultId 种植决策方案主键
     * @return 结果
     */
    @Override
    public int deleteBusPlantingResultByResultId(Long resultId)
    {
        return busPlantingResultMapper.deleteBusPlantingResultByResultId(resultId);
    }
}
