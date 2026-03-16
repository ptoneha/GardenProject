package com.ruoyi.agriculture.mapper;

import java.util.List;
import com.ruoyi.agriculture.domain.BusPlantingResult;

/**
 * 种植决策方案Mapper接口
 */
public interface BusPlantingResultMapper
{
    /**
     * 查询种植决策方案
     *
     * @param resultId 结果ID
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
     * 根据任务ID删除种植决策方案
     *
     * @param taskId 任务ID
     * @return 结果
     */
    public int deleteBusPlantingResultByTaskId(Long taskId);

    /**
     * 删除种植决策方案
     *
     * @param resultId 结果ID
     * @return 结果
     */
    public int deleteBusPlantingResultByResultId(Long resultId);

    /**
     * 批量删除种植决策方案
     *
     * @param resultIds 结果ID集合
     * @return 结果
     */
    public int deleteBusPlantingResultByResultIds(Long[] resultIds);
}
