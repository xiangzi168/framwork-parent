package com.amg.fulfillment.cloud.logistics.controller;

import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.fulfillment.cloud.basic.common.filehandle.annotation.FileHandle;
import com.amg.fulfillment.cloud.basic.common.filehandle.enums.FileHandleTypeEnums;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.ImportExcelVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsLabelProductVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsRulesFreightVO;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsRulesFreightService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;

/**
 * Created by Seraph on 2021/5/25
 */

@RestController
@RequestMapping("/rules/freight")
@Api(tags={"物流运费规则"})
public class LogisticsRulesFreightController {

    @Autowired
    private ILogisticsRulesFreightService logisticsRulesFreightService;

    @ApiOperation(value = "物流运费规则列表", response = LogisticsRulesFreightVO.class)
    @GetMapping("/list")
    public Page<LogisticsRulesFreightVO> list(LogisticsRulesFreightReq logisticsRulesFreightReq)
    {
        return logisticsRulesFreightService.list(logisticsRulesFreightReq);
    }

    @ApiOperation(value = "物流运费规则详情", response = LogisticsRulesFreightVO.class)
    @GetMapping("/detail/{rulesFreightId}")
    public LogisticsRulesFreightVO detail(@ApiParam(value = "物流运费 id", name ="rulesFreightId", required = true) @PathVariable Long rulesFreightId)
    {
        return logisticsRulesFreightService.detail(rulesFreightId);
    }

    @ApiOperation(value = "物流运费规则修改", response = Boolean.class)
    @PostMapping("/update")
    public Boolean update(@RequestBody @Valid LogisticsRulesFreightUpdateReq logisticsRulesFreightUpdateReq)
    {
        return logisticsRulesFreightService.update(logisticsRulesFreightUpdateReq);
    }

    @ApiOperation(value = "物流运费规则添加", response = Boolean.class)
    @PostMapping("/add")
    public Boolean add(@RequestBody @Valid LogisticsRulesFreightAddReq logisticsRulesFreightAddReq)
    {
        return logisticsRulesFreightService.add(logisticsRulesFreightAddReq);
    }

    @PostMapping("/exportLogisticsRulesFreightExcel")
    @ApiOperation("物流运费规则导出")
    @FileHandle(fileHandleType = FileHandleTypeEnums.DOWNLOAD)
    public File exportLogisticsRulesFreightExcel(String fileName, @RequestBody ExportExcelReq<LogisticsRulesFreightReq> exportExcelReq){
        if(StringUtils.isEmpty(fileName) || !fileName.endsWith(".zip"))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "文件名不能为空, 格式为zip");
        exportExcelReq.setFileName(fileName);
        return logisticsRulesFreightService.exportLogisticsRulesFreightExcel(exportExcelReq);
    }

    @PostMapping("/importLogisticsRulesFreightExcel")
    @ApiOperation(value = "物流运费规则导入", response = ImportExcelVO.class)
    @FileHandle(fileHandleType = FileHandleTypeEnums.UPLOAD)
    public File importtLogisticsRulesFreightExcel(@RequestParam String fileUrl){
        return logisticsRulesFreightService.importLogisticsRulesFreightExcel(fileUrl);
    }
}
