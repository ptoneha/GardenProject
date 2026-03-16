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
import com.ruoyi.agriculture.domain.BusCrop;
import com.ruoyi.agriculture.service.IBusCropService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 作物百科管理Controller
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/agriculture/crop")
public class BusCropController extends BaseController
{
    @Autowired
    private IBusCropService busCropService;

    /**
     * 查询作物百科管理列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:crop:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusCrop busCrop)
    {
        startPage();
        List<BusCrop> list = busCropService.selectBusCropList(busCrop);
        return getDataTable(list);
    }

    /**
     * 导出作物百科管理列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:crop:export')")
    @Log(title = "作物百科管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusCrop busCrop)
    {
        List<BusCrop> list = busCropService.selectBusCropList(busCrop);
        ExcelUtil<BusCrop> util = new ExcelUtil<BusCrop>(BusCrop.class);
        util.exportExcel(response, list, "作物百科管理数据");
    }

    /**
     * 获取作物百科管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:crop:query')")
    @GetMapping(value = "/{cropId}")
    public AjaxResult getInfo(@PathVariable("cropId") Long cropId)
    {
        return success(busCropService.selectBusCropByCropId(cropId));
    }

    /**
     * 新增作物百科管理
     */
    @PreAuthorize("@ss.hasPermi('agriculture:crop:add')")
    @Log(title = "作物百科管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusCrop busCrop)
    {
        return toAjax(busCropService.insertBusCrop(busCrop));
    }

    /**
     * 修改作物百科管理
     */
    @PreAuthorize("@ss.hasPermi('agriculture:crop:edit')")
    @Log(title = "作物百科管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusCrop busCrop)
    {
        return toAjax(busCropService.updateBusCrop(busCrop));
    }

    /**
     * 删除作物百科管理
     */
    @PreAuthorize("@ss.hasPermi('agriculture:crop:remove')")
    @Log(title = "作物百科管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{cropIds}")
    public AjaxResult remove(@PathVariable Long[] cropIds)
    {
        return toAjax(busCropService.deleteBusCropByCropIds(cropIds));
    }
}
