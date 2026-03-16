package com.ruoyi.agriculture.mapper;

import java.util.List;
import com.ruoyi.agriculture.domain.BusCrop;

/**
 * 作物百科管理Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface BusCropMapper 
{
    /**
     * 查询作物百科管理
     * 
     * @param cropId 作物百科管理主键
     * @return 作物百科管理
     */
    public BusCrop selectBusCropByCropId(Long cropId);

    /**
     * 查询作物百科管理列表
     * 
     * @param busCrop 作物百科管理
     * @return 作物百科管理集合
     */
    public List<BusCrop> selectBusCropList(BusCrop busCrop);

    /**
     * 新增作物百科管理
     * 
     * @param busCrop 作物百科管理
     * @return 结果
     */
    public int insertBusCrop(BusCrop busCrop);

    /**
     * 修改作物百科管理
     * 
     * @param busCrop 作物百科管理
     * @return 结果
     */
    public int updateBusCrop(BusCrop busCrop);

    /**
     * 删除作物百科管理
     * 
     * @param cropId 作物百科管理主键
     * @return 结果
     */
    public int deleteBusCropByCropId(Long cropId);

    /**
     * 批量删除作物百科管理
     * 
     * @param cropIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusCropByCropIds(Long[] cropIds);
}
