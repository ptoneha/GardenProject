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
import com.ruoyi.agriculture.domain.BusContainer;
import com.ruoyi.agriculture.service.IBusContainerService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

@RestController
@RequestMapping("/portal/agri/container")
@PreAuthorize("@ss.isPortalUser()")
public class PortalContainerController extends BaseController
{
    @Autowired
    private IBusContainerService busContainerService;

    @GetMapping("/list")
    public TableDataInfo list(BusContainer busContainer)
    {
        startPage();
        List<BusContainer> list = busContainerService.selectBusContainerList(busContainer);
        return getDataTable(list);
    }

    @GetMapping("/{containerId}")
    public AjaxResult getInfo(@PathVariable("containerId") Long containerId)
    {
        return success(busContainerService.selectBusContainerByContainerId(containerId));
    }

    @Log(title = "门户容器", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusContainer busContainer)
    {
        return toAjax(busContainerService.insertBusContainer(busContainer));
    }

    @Log(title = "门户容器", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusContainer busContainer)
    {
        return toAjax(busContainerService.updateBusContainer(busContainer));
    }

    @Log(title = "门户容器", businessType = BusinessType.DELETE)
    @DeleteMapping("/{containerIds}")
    public AjaxResult remove(@PathVariable Long[] containerIds)
    {
        return toAjax(busContainerService.deleteBusContainerByContainerIds(containerIds));
    }
}
