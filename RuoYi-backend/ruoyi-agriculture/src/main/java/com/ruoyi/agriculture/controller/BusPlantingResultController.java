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
import com.ruoyi.agriculture.domain.BusPlantingResult;
import com.ruoyi.agriculture.service.IBusPlantingResultService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 种植决策方案Controller
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/agriculture/result")
public class BusPlantingResultController extends BaseController
{
    @Autowired
    private IBusPlantingResultService busPlantingResultService;

    /**
     * 查询种植决策方案列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:result:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusPlantingResult busPlantingResult)
    {
        startPage();
        List<BusPlantingResult> list = busPlantingResultService.selectBusPlantingResultList(busPlantingResult);
        return getDataTable(list);
    }

    /**
     * 导出种植决策方案列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:result:export')")
    @Log(title = "种植决策方案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusPlantingResult busPlantingResult)
    {
        List<BusPlantingResult> list = busPlantingResultService.selectBusPlantingResultList(busPlantingResult);
        ExcelUtil<BusPlantingResult> util = new ExcelUtil<BusPlantingResult>(BusPlantingResult.class);
        util.exportExcel(response, list, "种植决策方案数据");
    }

    /**
     * 获取种植决策方案详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:result:query')")
    @GetMapping(value = "/{resultId}")
    public AjaxResult getInfo(@PathVariable("resultId") Long resultId)
    {
        return success(busPlantingResultService.selectBusPlantingResultByResultId(resultId));
    }

    /**
     * 新增种植决策方案
     */
    @PreAuthorize("@ss.hasPermi('agriculture:result:add')")
    @Log(title = "种植决策方案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusPlantingResult busPlantingResult)
    {
        return toAjax(busPlantingResultService.insertBusPlantingResult(busPlantingResult));
    }

    /**
     * 修改种植决策方案
     */
    @PreAuthorize("@ss.hasPermi('agriculture:result:edit')")
    @Log(title = "种植决策方案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusPlantingResult busPlantingResult)
    {
        return toAjax(busPlantingResultService.updateBusPlantingResult(busPlantingResult));
    }

    /**
     * 删除种植决策方案
     */
    @PreAuthorize("@ss.hasPermi('agriculture:result:remove')")
    @Log(title = "种植决策方案", businessType = BusinessType.DELETE)
	@DeleteMapping("/{resultIds}")
    public AjaxResult remove(@PathVariable Long[] resultIds)
    {
        return toAjax(busPlantingResultService.deleteBusPlantingResultByResultIds(resultIds));
    }
}
