package com.amg.fulfillment.cloud.logistics.controller;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.GoodSku;
import com.amg.fulfillment.cloud.logistics.dto.depository.DepositorySearchDto;
import com.amg.fulfillment.cloud.logistics.dto.depository.InventoryDto;
import com.amg.fulfillment.cloud.logistics.dto.depository.OutDepositoryResultDto;
import com.amg.fulfillment.cloud.logistics.model.req.GoodSkuAddReq;
import com.amg.fulfillment.cloud.logistics.model.req.GoodSkuSearchReq;
import com.amg.fulfillment.cloud.logistics.model.req.InventorySearchReq;
import com.amg.fulfillment.cloud.logistics.model.vo.DepositoryDataStatisticsVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsDataStatisticsForAEVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsDataStatisticsForAbroadVO;

import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsPackageOperationerStatisticsVO;
import com.amg.fulfillment.cloud.logistics.service.IDepositoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-14-13:59
 */
@RestController
@RequestMapping("/depository")
@Slf4j
@Api(tags = {"首页-物流待办"})
public class DepositoryController {

    @Autowired
    private IDepositoryService depositoryService;


    @ApiOperation(value = "推送产品资料", response = OutDepositoryResultDto.class)
    @PostMapping("addMaterialToDepository")
    public OutDepositoryResultDto addMaterialToDepository(@RequestBody GoodSkuAddReq goodSkuAddReq) {
        GoodSku goodSku = new GoodSku();
        goodSku.setDepositoryCode(goodSkuAddReq.getDepositoryCode());
        goodSku.setSku(goodSkuAddReq.getSku());
        goodSku.setName(goodSkuAddReq.getName());
        goodSku.setWeightInKg(goodSkuAddReq.getWeight() / 1000);
        goodSku.setVariants(Objects.isNull(goodSkuAddReq.getVariants()) ? Collections.emptyList() : goodSkuAddReq.getVariants());
        OutDepositoryResultDto outDepositoryResultDto = depositoryService.addMaterialToDepository(goodSku);
        return outDepositoryResultDto;
    }

    @ApiOperation(value = "查询产品资料", response = GoodSku.class)
    @GetMapping("getMaterialFromDepository")
    public GoodSku getMaterialFromDepository(GoodSkuSearchReq goodSkuSearchReq) {
        GoodSku goodSku = new GoodSku();
        goodSku.setDepositoryCode(goodSkuSearchReq.getDepositoryCode());
        goodSku.setSku(goodSkuSearchReq.getSku());
        return depositoryService.getMaterialFromDepository(goodSku);
    }

    @ApiOperation(value = "查询产品库存", response = InventoryDto.class)
    @GetMapping("getDepositoryCount")
    public List<InventoryDto> getDepositoryCount(InventorySearchReq inventorySearchReq) {
        DepositorySearchDto depositorySearchDto = new DepositorySearchDto();
        depositorySearchDto.setDepositoryCode(inventorySearchReq.getDepositoryCode());
        depositorySearchDto.setWarehouseCode(inventorySearchReq.getWarehouseCode());
        depositorySearchDto.setSku(inventorySearchReq.getSku());
        depositorySearchDto.setType(inventorySearchReq.getType());
        return depositoryService.getDepositoryCount(depositorySearchDto);
    }

    @ApiOperation(value = "首页--物流待办数据", response = DepositoryDataStatisticsVO.class)
    @GetMapping("getDepositoryDataStatistics")
    public DepositoryDataStatisticsVO getDepositoryDataStatistics() {
        return depositoryService.getDepositoryDataStatistics();
    }

    @ApiOperation(value = "首页--物流境外异常数据", response = LogisticsDataStatisticsForAbroadVO.class)
    @GetMapping("getLogisticsAboardStatistics")
    public LogisticsDataStatisticsForAbroadVO getLogisticsAboardStatistics() {
        return depositoryService.getLogisticsAboardStatistics();
    }

    @ApiOperation(value = "首页--物流AE异常数据", response = LogisticsDataStatisticsForAEVO.class)
    @GetMapping("getLogisticsAEStatistics")
    public LogisticsDataStatisticsForAEVO getLogisticsAEStatistics() {
        return depositoryService.getLogisticsAEStatistics();
    }

    @ApiOperation(value = "首页--今日物流数据统计", response = LogisticsPackageOperationerStatisticsVO.class)
    @GetMapping("getLogisticsPackageStatistics")
    public LogisticsPackageOperationerStatisticsVO getLogisticsPackageStatistics() {
        return depositoryService.getLogisticsPackageStatistics();
    }


}
