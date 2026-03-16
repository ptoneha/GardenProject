package com.ruoyi.agriculture.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.agriculture.mapper.BusTaskLandMapper;
import com.ruoyi.agriculture.domain.BusTaskLand;
import com.ruoyi.agriculture.service.IBusTaskLandService;

/**
 * 任务资源关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@Service
public class BusTaskLandServiceImpl implements IBusTaskLandService 
{
    @Autowired
    private BusTaskLandMapper busTaskLandMapper;

    /**
     * 查询任务资源关联
     * 
     * @param taskId 任务资源关联主键
     * @return 任务资源关联
     */
    @Override
    public BusTaskLand selectBusTaskLandByTaskId(Long taskId)
    {
        return busTaskLandMapper.selectBusTaskLandByTaskId(taskId);
    }

    /**
     * 查询任务资源关联列表
     * 
     * @param busTaskLand 任务资源关联
     * @return 任务资源关联
     */
    @Override
    public List<BusTaskLand> selectBusTaskLandList(BusTaskLand busTaskLand)
    {
        return busTaskLandMapper.selectBusTaskLandList(busTaskLand);
    }

    /**
     * 新增任务资源关联
     * 
     * @param busTaskLand 任务资源关联
     * @return 结果
     */
    @Override
    public int insertBusTaskLand(BusTaskLand busTaskLand)
    {
        return busTaskLandMapper.insertBusTaskLand(busTaskLand);
    }

    /**
     * 修改任务资源关联
     * 
     * @param busTaskLand 任务资源关联
     * @return 结果
     */
    @Override
    public int updateBusTaskLand(BusTaskLand busTaskLand)
    {
        return busTaskLandMapper.updateBusTaskLand(busTaskLand);
    }

    /**
     * 批量删除任务资源关联
     * 
     * @param taskIds 需要删除的任务资源关联主键
     * @return 结果
     */
    @Override
    public int deleteBusTaskLandByTaskIds(Long[] taskIds)
    {
        return busTaskLandMapper.deleteBusTaskLandByTaskIds(taskIds);
    }

    /**
     * 删除任务资源关联信息
     * 
     * @param taskId 任务资源关联主键
     * @return 结果
     */
    @Override
    public int deleteBusTaskLandByTaskId(Long taskId)
    {
        return busTaskLandMapper.deleteBusTaskLandByTaskId(taskId);
    }
}
