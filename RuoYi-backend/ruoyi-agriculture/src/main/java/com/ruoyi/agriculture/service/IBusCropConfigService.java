package com.ruoyi.agriculture.service;

import java.util.List;
import com.ruoyi.agriculture.domain.BusCropConfig;

/**
 * 作物指标配置Service接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface IBusCropConfigService 
{
    /**
     * 查询作物指标配置
     * 
     * @param configId 作物指标配置主键
     * @return 作物指标配置
     */
    public BusCropConfig selectBusCropConfigByConfigId(Long configId);

    /**
     * 查询作物指标配置列表
     * 
     * @param busCropConfig 作物指标配置
     * @return 作物指标配置集合
     */
    public List<BusCropConfig> selectBusCropConfigList(BusCropConfig busCropConfig);

    /**
     * 新增作物指标配置
     * 
     * @param busCropConfig 作物指标配置
     * @return 结果
     */
    public int insertBusCropConfig(BusCropConfig busCropConfig);

    /**
     * 修改作物指标配置
     * 
     * @param busCropConfig 作物指标配置
     * @return 结果
     */
    public int updateBusCropConfig(BusCropConfig busCropConfig);

    /**
     * 批量删除作物指标配置
     * 
     * @param configIds 需要删除的作物指标配置主键集合
     * @return 结果
     */
    public int deleteBusCropConfigByConfigIds(Long[] configIds);

    /**
     * 删除作物指标配置信息
     * 
     * @param configId 作物指标配置主键
     * @return 结果
     */
    public int deleteBusCropConfigByConfigId(Long configId);
}
