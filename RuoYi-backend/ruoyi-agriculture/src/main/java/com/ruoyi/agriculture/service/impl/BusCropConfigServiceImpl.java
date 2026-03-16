package com.ruoyi.agriculture.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.agriculture.mapper.BusCropConfigMapper;
import com.ruoyi.agriculture.domain.BusCropConfig;
import com.ruoyi.agriculture.service.IBusCropConfigService;

/**
 * 作物指标配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@Service
public class BusCropConfigServiceImpl implements IBusCropConfigService 
{
    @Autowired
    private BusCropConfigMapper busCropConfigMapper;

    /**
     * 查询作物指标配置
     * 
     * @param configId 作物指标配置主键
     * @return 作物指标配置
     */
    @Override
    public BusCropConfig selectBusCropConfigByConfigId(Long configId)
    {
        return busCropConfigMapper.selectBusCropConfigByConfigId(configId);
    }

    /**
     * 查询作物指标配置列表
     * 
     * @param busCropConfig 作物指标配置
     * @return 作物指标配置
     */
    @Override
    public List<BusCropConfig> selectBusCropConfigList(BusCropConfig busCropConfig)
    {
        return busCropConfigMapper.selectBusCropConfigList(busCropConfig);
    }

    /**
     * 新增作物指标配置
     * 
     * @param busCropConfig 作物指标配置
     * @return 结果
     */
    @Override
    public int insertBusCropConfig(BusCropConfig busCropConfig)
    {
        return busCropConfigMapper.insertBusCropConfig(busCropConfig);
    }

    /**
     * 修改作物指标配置
     * 
     * @param busCropConfig 作物指标配置
     * @return 结果
     */
    @Override
    public int updateBusCropConfig(BusCropConfig busCropConfig)
    {
        return busCropConfigMapper.updateBusCropConfig(busCropConfig);
    }

    /**
     * 批量删除作物指标配置
     * 
     * @param configIds 需要删除的作物指标配置主键
     * @return 结果
     */
    @Override
    public int deleteBusCropConfigByConfigIds(Long[] configIds)
    {
        return busCropConfigMapper.deleteBusCropConfigByConfigIds(configIds);
    }

    /**
     * 删除作物指标配置信息
     * 
     * @param configId 作物指标配置主键
     * @return 结果
     */
    @Override
    public int deleteBusCropConfigByConfigId(Long configId)
    {
        return busCropConfigMapper.deleteBusCropConfigByConfigId(configId);
    }
}
