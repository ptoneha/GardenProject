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
import com.ruoyi.agriculture.domain.BusCropConfig;
import com.ruoyi.agriculture.service.IBusCropConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 作物指标配置Controller
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/agriculture/config")
public class BusCropConfigController extends BaseController
{
    @Autowired
    private IBusCropConfigService busCropConfigService;

    /**
     * 查询作物指标配置列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusCropConfig busCropConfig)
    {
        startPage();
        List<BusCropConfig> list = busCropConfigService.selectBusCropConfigList(busCropConfig);
        return getDataTable(list);
    }

    /**
     * 导出作物指标配置列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:config:export')")
    @Log(title = "作物指标配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusCropConfig busCropConfig)
    {
        List<BusCropConfig> list = busCropConfigService.selectBusCropConfigList(busCropConfig);
        ExcelUtil<BusCropConfig> util = new ExcelUtil<BusCropConfig>(BusCropConfig.class);
        util.exportExcel(response, list, "作物指标配置数据");
    }

    /**
     * 获取作物指标配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:config:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable("configId") Long configId)
    {
        return success(busCropConfigService.selectBusCropConfigByConfigId(configId));
    }

    /**
     * 新增作物指标配置
     */
    @PreAuthorize("@ss.hasPermi('agriculture:config:add')")
    @Log(title = "作物指标配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusCropConfig busCropConfig)
    {
        return toAjax(busCropConfigService.insertBusCropConfig(busCropConfig));
    }

    /**
     * 修改作物指标配置
     */
    @PreAuthorize("@ss.hasPermi('agriculture:config:edit')")
    @Log(title = "作物指标配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusCropConfig busCropConfig)
    {
        return toAjax(busCropConfigService.updateBusCropConfig(busCropConfig));
    }

    /**
     * 删除作物指标配置
     */
    @PreAuthorize("@ss.hasPermi('agriculture:config:remove')")
    @Log(title = "作物指标配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        return toAjax(busCropConfigService.deleteBusCropConfigByConfigIds(configIds));
    }
}
