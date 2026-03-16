package com.ruoyi.agriculture.service;

import java.util.List;
import com.ruoyi.agriculture.domain.BusPlantingResult;

/**
 * 种植决策方案Service接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface IBusPlantingResultService 
{
    /**
     * 查询种植决策方案
     * 
     * @param resultId 种植决策方案主键
     * @return 种植决策方案
     */
    public BusPlantingResult selectBusPlantingResultByResultId(Long resultId);

    /**
     * 查询种植决策方案列表
     * 
     * @param busPlantingResult 种植决策方案
     * @return 种植决策方案集合
     */
    public List<BusPlantingResult> selectBusPlantingResultList(BusPlantingResult busPlantingResult);

    /**
     * 新增种植决策方案
     * 
     * @param busPlantingResult 种植决策方案
     * @return 结果
     */
    public int insertBusPlantingResult(BusPlantingResult busPlantingResult);

    /**
     * 修改种植决策方案
     * 
     * @param busPlantingResult 种植决策方案
     * @return 结果
     */
    public int updateBusPlantingResult(BusPlantingResult busPlantingResult);

    /**
     * 批量删除种植决策方案
     * 
     * @param resultIds 需要删除的种植决策方案主键集合
     * @return 结果
     */
    public int deleteBusPlantingResultByResultIds(Long[] resultIds);

    /**
     * 删除种植决策方案信息
     * 
     * @param resultId 种植决策方案主键
     * @return 结果
     */
    public int deleteBusPlantingResultByResultId(Long resultId);
}
