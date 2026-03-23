package com.ruoyi.agriculture.controller.portal;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.agriculture.domain.BusCrop;
import com.ruoyi.agriculture.service.IBusCropService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/portal/agri/crop")
@PreAuthorize("@ss.isPortalUser()")
public class PortalCropController extends BaseController
{
    @Autowired
    private IBusCropService busCropService;

    @GetMapping("/list")
    public TableDataInfo list(BusCrop busCrop)
    {
        startPage();
        List<BusCrop> list = busCropService.selectBusCropList(busCrop);
        return getDataTable(list);
    }

    @GetMapping("/{cropId}")
    public AjaxResult getInfo(@PathVariable("cropId") Long cropId)
    {
        return success(busCropService.selectBusCropByCropId(cropId));
    }
}
