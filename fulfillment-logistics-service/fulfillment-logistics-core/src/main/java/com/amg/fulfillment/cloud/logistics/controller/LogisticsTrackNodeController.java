package com.amg.fulfillment.cloud.logistics.controller;

import com.amg.fulfillment.cloud.logistics.model.req.LogisticsRuleSearchReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsTrackNodeReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsRuleVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsTrackNodeVO;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsTrackNodeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Seraph on 2021/6/1
 */

@Slf4j
@RestController
@RequestMapping("/node")
@Api(tags={"物流节点查询"})
public class LogisticsTrackNodeController {

    @Autowired
    private ILogisticsTrackNodeService logisticsTrackNodeService;

    @GetMapping("/list")
    @ApiOperation("查询物流配置节点")
    public List<LogisticsTrackNodeVO> list(LogisticsTrackNodeReq logisticsTrackNodeReq) {
        return logisticsTrackNodeService.list(logisticsTrackNodeReq);
    }

}
