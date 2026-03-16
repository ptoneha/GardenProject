package com.ruoyi.agriculture.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.agriculture.mapper.BusCropMapper;
import com.ruoyi.agriculture.domain.BusCrop;
import com.ruoyi.agriculture.service.IBusCropService;

/**
 * 作物百科管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@Service
public class BusCropServiceImpl implements IBusCropService 
{
    @Autowired
    private BusCropMapper busCropMapper;

    /**
     * 查询作物百科管理
     * 
     * @param cropId 作物百科管理主键
     * @return 作物百科管理
     */
    @Override
    public BusCrop selectBusCropByCropId(Long cropId)
    {
        return busCropMapper.selectBusCropByCropId(cropId);
    }

    /**
     * 查询作物百科管理列表
     * 
     * @param busCrop 作物百科管理
     * @return 作物百科管理
     */
    @Override
    public List<BusCrop> selectBusCropList(BusCrop busCrop)
    {
        return busCropMapper.selectBusCropList(busCrop);
    }

    /**
     * 新增作物百科管理
     * 
     * @param busCrop 作物百科管理
     * @return 结果
     */
    @Override
    public int insertBusCrop(BusCrop busCrop)
    {
        return busCropMapper.insertBusCrop(busCrop);
    }

    /**
     * 修改作物百科管理
     * 
     * @param busCrop 作物百科管理
     * @return 结果
     */
    @Override
    public int updateBusCrop(BusCrop busCrop)
    {
        return busCropMapper.updateBusCrop(busCrop);
    }

    /**
     * 批量删除作物百科管理
     * 
     * @param cropIds 需要删除的作物百科管理主键
     * @return 结果
     */
    @Override
    public int deleteBusCropByCropIds(Long[] cropIds)
    {
        return busCropMapper.deleteBusCropByCropIds(cropIds);
    }

    /**
     * 删除作物百科管理信息
     * 
     * @param cropId 作物百科管理主键
     * @return 结果
     */
    @Override
    public int deleteBusCropByCropId(Long cropId)
    {
        return busCropMapper.deleteBusCropByCropId(cropId);
    }
}
