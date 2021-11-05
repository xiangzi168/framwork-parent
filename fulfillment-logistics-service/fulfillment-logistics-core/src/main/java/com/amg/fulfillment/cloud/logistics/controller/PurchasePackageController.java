package com.amg.fulfillment.cloud.logistics.controller;

import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.cloud.grpc.context.UserContext;
import com.amg.fulfillment.cloud.basic.common.filehandle.annotation.FileHandle;
import com.amg.fulfillment.cloud.basic.common.filehandle.enums.FileHandleTypeEnums;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.PurchasePackageVO;
import com.amg.fulfillment.cloud.logistics.service.IPurchasePackageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;

/**
 * Created by Seraph on 2021/5/11
 */

@Api(tags={"采购包裹接口"})
@RestController
@RequestMapping("/purchase/package")
public class PurchasePackageController {

    @Autowired
    private IPurchasePackageService purchasePackageService;

    @ApiOperation(value = "包裹状态标记", response = Boolean.class)
    @PostMapping("/packageStatusMark")
    public Boolean packageStatusMark(@RequestBody PackageStatusMarkReq packageStatusMarkReq)
    {
        return purchasePackageService.updatePackageStatus(packageStatusMarkReq);
    }
    @ApiOperation(value = "采购包裹列表", response = PurchasePackageVO.class)
    @GetMapping("/list")
    public Page<PurchasePackageVO> list(PurchasePackageReq purchasePackageReq)
    {
        return purchasePackageService.list(purchasePackageReq);
    }

    @ApiOperation(value = "采购包裹详情", response = PurchasePackageVO.class)
    @GetMapping("/detail/{purchasePackageId}-{productType}")
    public PurchasePackageVO detail(@ApiParam(value = "采购包裹单号", name ="purchasePackageId", required = true) @PathVariable Long purchasePackageId,
                                          @ApiParam(value = "商品类型  all 全部     error 异常商品", name ="productType", required = true) @PathVariable String productType)
    {
        return purchasePackageService.detail(purchasePackageId, productType);
    }

    @ApiOperation(value = "采购包裹商品处理异常", response = Boolean.class)
    @PostMapping("/errorHandle")
    public Boolean errorHandle(@RequestBody PurchasePackageErrorHandleReq purchasePackageErrorHandleReq)
    {
        return purchasePackageService.errorHandle(purchasePackageErrorHandleReq);
    }
    @ApiOperation(value = "采购包裹预报（这期只做临时采购）", response = Boolean.class)
    @PostMapping("/prediction")
    public Boolean prediction(@RequestBody PurchasePackagePredictionReq purchasePackagePredictionReq)
    {
        return purchasePackageService.prediction(purchasePackagePredictionReq);
    }
    @PostMapping("/exportPurchasePackageExcel")
    @ApiOperation("采购包裹列表导出")
    @FileHandle(fileHandleType = FileHandleTypeEnums.DOWNLOAD)
    public File exportPurchasePackageExcel(String fileName, @RequestBody ExportExcelReq<PurchasePackageReq> exportExcelReq){
        if(StringUtils.isEmpty(fileName) || !fileName.endsWith(".zip"))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "文件名不能为空, 格式为zip");
        exportExcelReq.setFileName(fileName);
        return purchasePackageService.exportPurchasePackageExcel(exportExcelReq);
    }
    @ApiOperation(value = "重新采购", response = Boolean.class)
    @PostMapping("/againPurchase")
    public Boolean againPurchase(@RequestBody PurchasePackageErrorHandleReq purchasePackageErrorHandleReq)
    {
        return purchasePackageService.againPurchase(purchasePackageErrorHandleReq);
    }
    @ApiOperation(value = "人工添加预报包裹", response = Boolean.class)
    @PostMapping("/manualPredictionPurcahseId")
    public Boolean manualPredictionPurcahseId(@RequestBody @Validated ManualPredictionReq manualPredictionReq) {
        manualPredictionReq.setOperationer(UserContext.getCurrentUserName());
        return purchasePackageService.manualPredictionPurchaseId(manualPredictionReq);
    }
}
