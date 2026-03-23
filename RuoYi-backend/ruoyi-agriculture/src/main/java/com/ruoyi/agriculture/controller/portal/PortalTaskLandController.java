package com.ruoyi.agriculture.controller.portal;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.agriculture.domain.BusTaskLand;
import com.ruoyi.agriculture.service.IBusTaskLandService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

@RestController
@RequestMapping("/portal/agri/taskLand")
@PreAuthorize("@ss.isPortalUser()")
public class PortalTaskLandController extends BaseController
{
    @Autowired
    private IBusTaskLandService busTaskLandService;

    @GetMapping("/list")
    public TableDataInfo list(BusTaskLand busTaskLand)
    {
        startPage();
        List<BusTaskLand> list = busTaskLandService.selectBusTaskLandList(busTaskLand);
        return getDataTable(list);
    }

    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId)
    {
        return success(busTaskLandService.selectBusTaskLandByTaskId(taskId));
    }

    @Log(title = "门户任务地块关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusTaskLand busTaskLand)
    {
        return toAjax(busTaskLandService.insertBusTaskLand(busTaskLand));
    }

    @Log(title = "门户任务地块关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(busTaskLandService.deleteBusTaskLandByTaskIds(taskIds));
    }
}
