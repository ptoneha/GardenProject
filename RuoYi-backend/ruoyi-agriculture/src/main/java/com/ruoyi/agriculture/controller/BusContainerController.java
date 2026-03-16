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
import com.ruoyi.agriculture.domain.BusContainer;
import com.ruoyi.agriculture.service.IBusContainerService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 种植容器管理Controller
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/agriculture/container")
public class BusContainerController extends BaseController
{
    @Autowired
    private IBusContainerService busContainerService;

    /**
     * 查询种植容器管理列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:container:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusContainer busContainer)
    {
        startPage();
        List<BusContainer> list = busContainerService.selectBusContainerList(busContainer);
        return getDataTable(list);
    }

    /**
     * 导出种植容器管理列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:container:export')")
    @Log(title = "种植容器管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusContainer busContainer)
    {
        List<BusContainer> list = busContainerService.selectBusContainerList(busContainer);
        ExcelUtil<BusContainer> util = new ExcelUtil<BusContainer>(BusContainer.class);
        util.exportExcel(response, list, "种植容器管理数据");
    }

    /**
     * 获取种植容器管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:container:query')")
    @GetMapping(value = "/{containerId}")
    public AjaxResult getInfo(@PathVariable("containerId") Long containerId)
    {
        return success(busContainerService.selectBusContainerByContainerId(containerId));
    }

    /**
     * 新增种植容器管理
     */
    @PreAuthorize("@ss.hasPermi('agriculture:container:add')")
    @Log(title = "种植容器管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusContainer busContainer)
    {
        return toAjax(busContainerService.insertBusContainer(busContainer));
    }

    /**
     * 修改种植容器管理
     */
    @PreAuthorize("@ss.hasPermi('agriculture:container:edit')")
    @Log(title = "种植容器管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusContainer busContainer)
    {
        return toAjax(busContainerService.updateBusContainer(busContainer));
    }

    /**
     * 删除种植容器管理
     */
    @PreAuthorize("@ss.hasPermi('agriculture:container:remove')")
    @Log(title = "种植容器管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{containerIds}")
    public AjaxResult remove(@PathVariable Long[] containerIds)
    {
        return toAjax(busContainerService.deleteBusContainerByContainerIds(containerIds));
    }
}
