package com.amg.fulfillment.cloud.logistics.controller;

import com.amg.fulfillment.cloud.logistics.model.req.LogisticsLabelAddReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsLabelDelReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsLabelReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsLabelUpdateReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsLabelVO;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsLabelService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by Seraph on 2021/5/24
 */

@Api(tags={"物流标签接口"})
@RestController
@RequestMapping("/label")
public class LogisticsLabelController {

    @Autowired
    private ILogisticsLabelService logisticsLabelService;

    @ApiOperation(value = "物流标签列表", response = LogisticsLabelVO.class)
    @GetMapping("/list")
    public Page<LogisticsLabelVO> list(LogisticsLabelReq logisticsLabelReq)
    {
        return logisticsLabelService.list(logisticsLabelReq);
    }

    @ApiOperation(value = "物流标签详情", response = LogisticsLabelVO.class)
    @GetMapping("/detail/{labelId}")
    public LogisticsLabelVO detail(@ApiParam(value = "物流标签 id", name ="labelId", required = true)@PathVariable Long labelId)
    {
        return logisticsLabelService.detail(labelId);
    }

    @ApiOperation(value = "物流标签添加", response = Boolean.class)
    @PostMapping("/add")
    public Boolean add(@RequestBody @Valid LogisticsLabelAddReq logisticsLabelAddReq)
    {
        return logisticsLabelService.add(logisticsLabelAddReq);
    }

    @ApiOperation(value = "物流标签修改", response = Boolean.class)
    @PostMapping("/update")
    public Boolean update(@RequestBody @Valid LogisticsLabelUpdateReq logisticsLabelUpdateReq)
    {
        return logisticsLabelService.update(logisticsLabelUpdateReq);
    }

    @ApiOperation(value = "物流标签删除", response = Boolean.class)
    @PostMapping("/del")
    public Boolean del(@RequestBody @Valid LogisticsLabelDelReq logisticsLabelDelReq)
    {
        return logisticsLabelService.del(logisticsLabelDelReq);
    }

}
