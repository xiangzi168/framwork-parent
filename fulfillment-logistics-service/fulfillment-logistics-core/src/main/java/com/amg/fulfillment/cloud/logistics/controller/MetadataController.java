package com.amg.fulfillment.cloud.logistics.controller;

import com.amg.fulfillment.cloud.logistics.model.req.MetadataReq;
import com.amg.fulfillment.cloud.logistics.model.vo.MetadataVO;
import com.amg.fulfillment.cloud.logistics.service.IMetadataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Seraph on 2021/5/26
 */

@Api(tags={"类目管理接口"})
@Slf4j
@RestController
@RequestMapping("/metadata")
public class MetadataController {

    @Autowired
    private IMetadataService metadataService;

    @ApiOperation(value = "类目管理搜索接口", response = MetadataVO.class)
    @PostMapping("/search")
    public List<MetadataVO> search(@RequestBody MetadataReq metadataReq)
    {
        return metadataService.search(metadataReq);
    }

    @ApiOperation(value = "类目管理搜索接口", response = MetadataVO.class)
    @GetMapping("/detail/{categoryCode}")
    public MetadataVO detail(@ApiParam(value = "类目 code ", name ="categoryCode", required = true) @PathVariable String categoryCode)
    {
        return metadataService.detail(categoryCode);
    }

    @ApiOperation(value = "类目管理所有父节点查询接口", response = MetadataVO.class)
    @GetMapping("/parentList/{categoryCode}")
    public List<MetadataVO> parentList(@ApiParam(value = "类目 code ", name ="categoryCode", required = true) @PathVariable String categoryCode)
    {
        return metadataService.parentList(categoryCode);
    }
}
