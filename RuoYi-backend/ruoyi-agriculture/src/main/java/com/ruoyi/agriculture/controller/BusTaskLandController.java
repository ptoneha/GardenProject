package com.ruoyi.agriculture.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.agriculture.domain.BusTaskLand;
import com.ruoyi.agriculture.service.IBusTaskLandService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 任务资源关联Controller
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/agriculture/taskLand")
public class BusTaskLandController extends BaseController
{
    @Autowired
    private IBusTaskLandService busTaskLandService;

    /**
     * 查询任务资源关联列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:taskLand:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusTaskLand busTaskLand)
    {
        startPage();
        List<BusTaskLand> list = busTaskLandService.selectBusTaskLandList(busTaskLand);
        return getDataTable(list);
    }

    /**
     * 导出任务资源关联列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:taskLand:export')")
    @Log(title = "任务资源关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusTaskLand busTaskLand)
    {
        List<BusTaskLand> list = busTaskLandService.selectBusTaskLandList(busTaskLand);
        ExcelUtil<BusTaskLand> util = new ExcelUtil<BusTaskLand>(BusTaskLand.class);
        util.exportExcel(response, list, "任务资源关联数据");
    }

    /**
     * 获取任务资源关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:taskLand:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId)
    {
        return success(busTaskLandService.selectBusTaskLandByTaskId(taskId));
    }

    /**
     * 新增任务资源关联
     */
    @PreAuthorize("@ss.hasPermi('agriculture:taskLand:add')")
    @Log(title = "任务资源关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusTaskLand busTaskLand)
    {
        return toAjax(busTaskLandService.insertBusTaskLand(busTaskLand));
    }

    /**
     * 修改任务资源关联
     */
    @PreAuthorize("@ss.hasPermi('agriculture:taskLand:edit')")
    @Log(title = "任务资源关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusTaskLand busTaskLand)
    {
        return toAjax(busTaskLandService.updateBusTaskLand(busTaskLand));
    }

    /**
     * 删除任务资源关联
     */
    @PreAuthorize("@ss.hasPermi('agriculture:taskLand:remove')")
    @Log(title = "任务资源关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(busTaskLandService.deleteBusTaskLandByTaskIds(taskIds));
    }
}
