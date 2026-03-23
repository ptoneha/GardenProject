package com.ruoyi.agriculture.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.agriculture.domain.BusOptimizationTask;
import com.ruoyi.agriculture.domain.dto.AgriTaskExecuteResult;
import com.ruoyi.agriculture.service.IBusOptimizationTaskService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 种植优化任务Controller
 */
@RestController
@RequestMapping("/agriculture/task")
public class BusOptimizationTaskController extends BaseController
{
    @Autowired
    private IBusOptimizationTaskService busOptimizationTaskService;

    /**
     * 查询种植优化任务列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:task:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusOptimizationTask busOptimizationTask)
    {
        startPage();
        List<BusOptimizationTask> list = busOptimizationTaskService.selectBusOptimizationTaskList(busOptimizationTask);
        return getDataTable(list);
    }

    /**
     * 导出种植优化任务列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:task:export')")
    @Log(title = "种植优化任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusOptimizationTask busOptimizationTask)
    {
        List<BusOptimizationTask> list = busOptimizationTaskService.selectBusOptimizationTaskList(busOptimizationTask);
        ExcelUtil<BusOptimizationTask> util = new ExcelUtil<BusOptimizationTask>(BusOptimizationTask.class);
        util.exportExcel(response, list, "种植优化任务数据");
    }

    /**
     * 获取种植优化任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:task:query')")
    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId)
    {
        return success(busOptimizationTaskService.selectBusOptimizationTaskByTaskId(taskId));
    }

    /**
     * 新增种植优化任务
     */
    @PreAuthorize("@ss.hasPermi('agriculture:task:add')")
    @Log(title = "种植优化任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusOptimizationTask busOptimizationTask)
    {
        return busOptimizationTaskService.insertBusOptimizationTask(busOptimizationTask) > 0
            ? AjaxResult.success("success", busOptimizationTask)
            : AjaxResult.error();
    }

    /**
     * 修改种植优化任务
     */
    @PreAuthorize("@ss.hasPermi('agriculture:task:edit')")
    @Log(title = "种植优化任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusOptimizationTask busOptimizationTask)
    {
        return busOptimizationTaskService.updateBusOptimizationTask(busOptimizationTask) > 0
            ? AjaxResult.success("success", busOptimizationTask)
            : AjaxResult.error();
    }

    /**
     * 执行种植优化任务
     */
    @PreAuthorize("@ss.hasPermi('agriculture:task:edit')")
    @Log(title = "种植优化任务", businessType = BusinessType.UPDATE)
    @PostMapping("/execute/{taskId}")
    public AjaxResult execute(@PathVariable("taskId") Long taskId)
    {
        AgriTaskExecuteResult result = busOptimizationTaskService.executeTask(taskId);
        if (Integer.valueOf(1).equals(result.getStatus()))
        {
            return AjaxResult.success(result.getMessage(), result);
        }
        if (Integer.valueOf(2).equals(result.getStatus()))
        {
            return AjaxResult.warn(result.getMessage(), result);
        }
        return AjaxResult.error(result.getMessage(), result);
    }

    /**
     * 删除种植优化任务
     */
    @PreAuthorize("@ss.hasPermi('agriculture:task:remove')")
    @Log(title = "种植优化任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(busOptimizationTaskService.deleteBusOptimizationTaskByTaskIds(taskIds));
    }
}
