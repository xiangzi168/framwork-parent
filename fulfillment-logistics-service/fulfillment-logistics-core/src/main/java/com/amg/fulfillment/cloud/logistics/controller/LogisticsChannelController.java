package com.amg.fulfillment.cloud.logistics.controller;

import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.fulfillment.cloud.basic.common.filehandle.annotation.FileHandle;
import com.amg.fulfillment.cloud.basic.common.filehandle.enums.FileHandleTypeEnums;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsChannelVO;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsChannelService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;

/**
 * Created by Seraph on 2021/5/24
 */

@Api(tags={"物流渠道接口"})
@RestController
@RequestMapping("/channel")
public class LogisticsChannelController {

    @Autowired
    private ILogisticsChannelService logisticsChannelService;


    @ApiOperation(value = "物流渠道列表", response = LogisticsChannelVO.class)
    @GetMapping("/list")
    public Page<LogisticsChannelVO> list(LogisticsChannelReq logisticsChannelReq)
    {
        return logisticsChannelService.list(logisticsChannelReq);
    }

    @ApiOperation(value = "物流渠道详情", response = LogisticsChannelVO.class)
    @GetMapping("/detail/{logisticsChannelId}")
    public LogisticsChannelVO detail(@ApiParam(value = "物流渠道 id", name ="labelId", required = true) @PathVariable Long logisticsChannelId)
    {
        return logisticsChannelService.detail(logisticsChannelId);
    }

    @ApiOperation(value = "物流渠道修改", response = Boolean.class)
    @PostMapping("/update")
    public Boolean update(@RequestBody @Valid LogisticsChannelUpdateReq logisticsChannelUpdateReq)
    {
        return logisticsChannelService.update(logisticsChannelUpdateReq);
    }

    @ApiOperation(value = "物流渠道添加", response = Boolean.class)
    @PostMapping("/add")
    public Boolean add(@RequestBody @Valid LogisticsChannelAddReq logisticsChannelAddReq)
    {
        return logisticsChannelService.add(logisticsChannelAddReq);
    }

    @ApiOperation(value = "物流渠道判断是否存在", response = Boolean.class)
    @PostMapping("/checkExists")
    public Boolean checkExists(@RequestBody LogisticsChannelReq logisticsChannelReq)
    {
        return logisticsChannelService.checkExists(logisticsChannelReq);
    }

    @PostMapping("/exportLogisticsChannelExcel")
    @ApiOperation("物流渠道列表导出")
    @FileHandle(fileHandleType = FileHandleTypeEnums.DOWNLOAD)
    public File exportLogisticsChannelExcel(String fileName, @RequestBody ExportExcelReq<LogisticsChannelReq> exportExcelReq){
        if(StringUtils.isEmpty(fileName) || !fileName.endsWith(".zip"))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "文件名不能为空, 格式为zip");
        exportExcelReq.setFileName(fileName);
        return logisticsChannelService.exportLogisticsChannelExcel(exportExcelReq);
    }
}
