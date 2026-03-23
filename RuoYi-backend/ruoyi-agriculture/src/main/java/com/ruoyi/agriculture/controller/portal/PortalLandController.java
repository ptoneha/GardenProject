package com.ruoyi.agriculture.controller.portal;

import java.util.List;
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
import com.ruoyi.agriculture.domain.BusLand;
import com.ruoyi.agriculture.service.IBusLandService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

@RestController
@RequestMapping("/portal/agri/land")
@PreAuthorize("@ss.isPortalUser()")
public class PortalLandController extends BaseController
{
    @Autowired
    private IBusLandService busLandService;

    @GetMapping("/list")
    public TableDataInfo list(BusLand busLand)
    {
        startPage();
        List<BusLand> list = busLandService.selectBusLandList(busLand);
        return getDataTable(list);
    }

    @GetMapping("/{landId}")
    public AjaxResult getInfo(@PathVariable("landId") Long landId)
    {
        return success(busLandService.selectBusLandByLandId(landId));
    }

    @Log(title = "门户地块", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusLand busLand)
    {
        return toAjax(busLandService.insertBusLand(busLand));
    }

    @Log(title = "门户地块", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusLand busLand)
    {
        return toAjax(busLandService.updateBusLand(busLand));
    }

    @Log(title = "门户地块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{landIds}")
    public AjaxResult remove(@PathVariable Long[] landIds)
    {
        return toAjax(busLandService.deleteBusLandByLandIds(landIds));
    }
}
