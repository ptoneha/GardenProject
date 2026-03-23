package com.ruoyi.agriculture.controller.portal;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.agriculture.domain.BusPlantingResult;
import com.ruoyi.agriculture.service.IBusPlantingResultService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/portal/agri/result")
@PreAuthorize("@ss.isPortalUser()")
public class PortalResultController extends BaseController
{
    @Autowired
    private IBusPlantingResultService busPlantingResultService;

    @GetMapping("/list")
    public TableDataInfo list(BusPlantingResult busPlantingResult)
    {
        startPage();
        List<BusPlantingResult> list = busPlantingResultService.selectBusPlantingResultList(busPlantingResult);
        return getDataTable(list);
    }

    @GetMapping("/{resultId}")
    public AjaxResult getInfo(@PathVariable("resultId") Long resultId)
    {
        return success(busPlantingResultService.selectBusPlantingResultByResultId(resultId));
    }
}
