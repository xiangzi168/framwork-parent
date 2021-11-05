package com.amg.fulfillment.cloud.logistics.controller;

import com.amg.fulfillment.cloud.logistics.model.req.LogisticsProviderReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsProviderVO;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsProviderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Seraph on 2021/5/25
 */

@Api(tags={"物流商接口"})
@RestController
@RequestMapping("/provider")
public class LogisticsProviderController {

    @Autowired
    private ILogisticsProviderService logisticsProviderService;

    @ApiOperation(value = "物流商列表", response = LogisticsProviderVO.class)
    @GetMapping("/list")
    public List<LogisticsProviderVO> list(LogisticsProviderReq logisticsProviderReq)
    {
        return logisticsProviderService.list(logisticsProviderReq);
    }
}
