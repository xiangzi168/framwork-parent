package com.amg.fulfillment.cloud.logistics.controller;

import com.alibaba.fastjson.JSONObject;
import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.fulfillment.cloud.basic.common.filehandle.annotation.FileHandle;
import com.amg.fulfillment.cloud.basic.common.filehandle.enums.FileHandleTypeEnums;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.DeliveryPackageCannelVO;
import com.amg.fulfillment.cloud.logistics.model.vo.DeliveryPackageVO;
import com.amg.fulfillment.cloud.logistics.model.vo.ImportExcelVO;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryPackageService;
import com.amg.fulfillment.cloud.logistics.util.ExcelUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.List;

/**
 * Created by Seraph on 2021/5/13
 */

@Slf4j
@RestController
@RequestMapping("/delivery/package")
@Validated
@Api(tags = {"物流管理-境外包裹单"})
public class DeliveryPackageController {

    @Autowired
    private IDeliveryPackageService deliveryPackageService;

    @ApiOperation(value = "物流发货单列表", response = DeliveryPackageVO.class)
    @GetMapping("/list")
    public Page<DeliveryPackageVO> list(DeliveryPackageReq deliveryPackageReq) {
        return deliveryPackageService.list(deliveryPackageReq);
    }

    @ApiOperation(value = "物流发货单详情", response = DeliveryPackageVO.class)
    @GetMapping("/detail/{type}/{packageId}")
    public DeliveryPackageVO detail(@ApiParam(value = "包裹发货类型  1 境外发货单  2 AE 发货单 4 CJ包裹 ", name = "type", required = true) @PathVariable Integer type,
                                    @ApiParam(value = "包裹单号", name = "packageId", required = true) @PathVariable Long packageId) {
        return deliveryPackageService.detail(type, packageId);
    }

    @ApiOperation(value = "根据销售订单 id 查询物流发货包裹", response = DeliveryPackageVO.class)
    @GetMapping("/salesOrderPackage/{salesOrderId}")
    public List<DeliveryPackageVO> salesOrderPackage(@PathVariable String salesOrderId) {
        return deliveryPackageService.salesOrderPackage(salesOrderId);
    }

    @ApiOperation(value = "推送仓库接口(参数 id -> 包裹单号 )", response = Boolean.class)
    @PostMapping("/pushWarehouse")
    public Boolean pushWarehouse(@RequestBody DeliveryPackageOperationReq deliveryPackageOperationReq) {
        return deliveryPackageService.pushWarehouse(deliveryPackageOperationReq);
    }


    @ApiOperation(value = "推送仓库接口(参数 idList -> 包裹单号)", response = Boolean.class)
    @PostMapping("/pushWarehouseBatch")
    public Boolean pushWarehouseBatch(@RequestBody DeliveryPackageOperationReq deliveryPackageOperationReq) {
        return deliveryPackageService.pushWarehouseBatch(deliveryPackageOperationReq);
    }

    @ApiOperation(value = "取消发货(参数 id -> 包裹单号 )", response = DeliveryPackageCannelVO.class)
    @PostMapping("/cancel")
    public DeliveryPackageCannelVO cancel(@RequestBody DeliveryPackageOperationReq deliveryPackageOperationReq) {
        log.info("取消发货接口调用,接口JSON是{}", JSONObject.toJSONString(deliveryPackageOperationReq));
        return deliveryPackageService.cancel(deliveryPackageOperationReq);
    }

    @ApiOperation(value = "取消发货批量(参数 idList -> 包裹单号 )", response = DeliveryPackageCannelVO.class)
    @PostMapping("/cancelBatch")
    public List<DeliveryPackageCannelVO> cancelBatch(@RequestBody DeliveryPackageOperationReq deliveryPackageOperationReq) {
        return deliveryPackageService.cancelBatch(deliveryPackageOperationReq);
    }

    @ApiOperation(value = "新建包裹单", response = Boolean.class)
    @PostMapping("/create")
    public Boolean create(@RequestBody @Validated(LogisticsPackageAddReq.Add.class) LogisticsPackageAddReq logisticsPackageAddReq) {
        log.info("创建包裹请求是：{}", logisticsPackageAddReq);
        return deliveryPackageService.save(logisticsPackageAddReq);
    }


    @ApiOperation(value = "已发货仅制单", response = Boolean.class)
    @PostMapping("/deliveredCreatePackage")
    public Boolean deliveredCreatePackage(@RequestBody @Validated DeliveryPackageDeliveredCreateReq deliveryPackageDeliveredCreateReq) {
        Boolean aBoolean = deliveryPackageService.deliveredCreatePackage(deliveryPackageDeliveredCreateReq);
        if (!aBoolean) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "已发货仅制单失败");
        }
        return aBoolean;
    }

    @ApiOperation(value = "更新物流信息", response = Boolean.class)
    @PostMapping("/refresh")
    public Boolean refresh(@RequestBody DeliveryPackageOperationReq deliveryPackageOperationReq) {
        return deliveryPackageService.refresh(deliveryPackageOperationReq);
    }

    @ApiOperation(value = "设置为失效", response = Boolean.class)
    @PostMapping("/disable")
    public Boolean disable(@RequestBody DeliveryPackageOperationReq deliveryPackageOperationReq) {
        return deliveryPackageService.disable(deliveryPackageOperationReq);
    }

    @PostMapping("/exportDeliveryPackageExcel")
    @ApiOperation("发货包裹列表导出")
    @FileHandle(fileHandleType = FileHandleTypeEnums.DOWNLOAD)
    public File exportDeliveryPackageExcel(String fileName, @RequestBody ExportExcelReq<DeliveryPackageReq> exportExcelReq) {
        log.info("接收到的fileName参数是：{}", fileName);
        log.info("接收到的exportExcelReq参数是：{}", exportExcelReq);
        if (StringUtils.isEmpty(fileName) || !fileName.endsWith(".zip"))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "文件名不能为空, 格式为zip");
        exportExcelReq.setFileName(ExcelUtils.getNormativeFileName(fileName));
        return deliveryPackageService.exportDeliveryPackageExcel(exportExcelReq);
    }

    @ApiOperation(value = "添加到包裹单", response = Boolean.class)
    @PostMapping("/updatePackage")
    public Boolean updatePackage(@RequestBody @Validated(LogisticsPackageAddReq.Update.class) LogisticsPackageAddReq logisticsPackageAddReq) {
        log.info("添加到包裹单请求是：{}", logisticsPackageAddReq);
        return deliveryPackageService.updatePackage(logisticsPackageAddReq);
    }

    @ApiOperation(value = "修改成通知用户", response = Boolean.class)
    @PostMapping("/noticeUser")
    public Boolean noticeUser(@RequestBody DeliveryPackageOperationReq deliveryPackageOperationReq) {
        log.info("修改成通知用户请求参数：{}", deliveryPackageOperationReq);
        List<Long> idList = deliveryPackageOperationReq.getIdList();
        return deliveryPackageService.noticeUser(idList);
    }

    @ApiOperation(value = "更新物流跟踪号", response = Boolean.class)
    @PostMapping("/updateLogisticsTrackingCode")
    public Boolean updateLogisticsTrackingCode(@RequestBody @Valid List<LogisticsTrackingCodeUpdateReq> logisticsTrackingCodeUpdateReqList) {
       return deliveryPackageService.updateLogisticsTrackingCode(logisticsTrackingCodeUpdateReqList);
    }

    @PostMapping("/importLogisticsTrackingCodeExcel")
    @ApiOperation(value = "批量导入更新物流跟踪号", response = ImportExcelVO.class)
    @FileHandle(fileHandleType = FileHandleTypeEnums.UPLOAD)
    public File importLogisticsTrackingCodeExcel(@RequestParam String fileUrl,@RequestParam Integer type){
        log.info("进入批量导入更新物流跟踪号下载地址：{}-type:{}",fileUrl,type);
        return deliveryPackageService.importLogisticsTrackingCodeExcel(fileUrl,type);
    }
}
