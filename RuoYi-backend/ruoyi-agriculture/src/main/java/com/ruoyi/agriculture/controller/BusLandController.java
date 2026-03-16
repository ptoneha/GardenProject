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
import com.ruoyi.agriculture.domain.BusLand;
import com.ruoyi.agriculture.service.IBusLandService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 地块资源管理Controller
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/agriculture/land")
public class BusLandController extends BaseController
{
    @Autowired
    private IBusLandService busLandService;

    /**
     * 查询地块资源管理列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:land:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusLand busLand)
    {
        startPage();
        List<BusLand> list = busLandService.selectBusLandList(busLand);
        return getDataTable(list);
    }

    /**
     * 导出地块资源管理列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:land:export')")
    @Log(title = "地块资源管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusLand busLand)
    {
        List<BusLand> list = busLandService.selectBusLandList(busLand);
        ExcelUtil<BusLand> util = new ExcelUtil<BusLand>(BusLand.class);
        util.exportExcel(response, list, "地块资源管理数据");
    }

    /**
     * 获取地块资源管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:land:query')")
    @GetMapping(value = "/{landId}")
    public AjaxResult getInfo(@PathVariable("landId") Long landId)
    {
        return success(busLandService.selectBusLandByLandId(landId));
    }

    /**
     * 新增地块资源管理
     */
    @PreAuthorize("@ss.hasPermi('agriculture:land:add')")
    @Log(title = "地块资源管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusLand busLand)
    {
        return toAjax(busLandService.insertBusLand(busLand));
    }

    /**
     * 修改地块资源管理
     */
    @PreAuthorize("@ss.hasPermi('agriculture:land:edit')")
    @Log(title = "地块资源管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusLand busLand)
    {
        return toAjax(busLandService.updateBusLand(busLand));
    }

    /**
     * 删除地块资源管理
     */
    @PreAuthorize("@ss.hasPermi('agriculture:land:remove')")
    @Log(title = "地块资源管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{landIds}")
    public AjaxResult remove(@PathVariable Long[] landIds)
    {
        return toAjax(busLandService.deleteBusLandByLandIds(landIds));
    }
}
