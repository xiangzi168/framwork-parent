package com.amg.fulfillment.cloud.logistics.manager;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.Address;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.GoodSku;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.*;
import com.amg.fulfillment.cloud.logistics.api.enumeration.LogisticItemEnum;
import com.amg.fulfillment.cloud.logistics.api.util.WanBDepistoryUtil;
import com.amg.fulfillment.cloud.logistics.dto.depository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName TestDepositoryManagerImpl
 * @Description 仅供测试使用
 * @Author 35112
 * @Date 2021/8/2 10:55
 **/
@Slf4j
@Service("testDepositoryManagerImpl")
public class TestDepositoryManagerImpl implements IDepositoryManager{
    @Autowired
    private WanBDepistoryUtil wanBDepistoryUtil;


    private static final String PURCHASE_SUCCESS = "采购成功";
    private static final String PURCHASE_FAIL = "采购失败";


    @Override
    public String getDepositorySign() {
        return Constant.DEPOSITORY_WB;
    }

    @Override
    public SaleOrderResultDto addSaleOrder(DepositorySaleOrderDto depositorySaleOrderDto) {
        //材料详情
        List<SalesOrderForWanB.SalesOrderProductEditRequest> products = depositorySaleOrderDto.getProductDtos().stream().map(item -> {
            return SalesOrderForWanB.SalesOrderProductEditRequest.builder()
                    .SKU(item.getSku())
                    .Name(StringUtils.isNoneBlank(item.getProductName()) ? item.getProductName() : Constant.DEFAULT_SALEORDER_PRODUCT_NAME_CN)
                    .Quantity(item.getQuantity())
                    .Variants(item.getVariants())
                    .ImageUrls(item.getImageUrls())
                    .WeightInKg(item.getWeightInKg())
                    .build();
        }).collect(Collectors.toList());
        SalesOrderForWanB salesOrderForWanB = SalesOrderForWanB.builder()
                .Notes(depositorySaleOrderDto.getRemark())
                .Products(products)
                .build();

        SaleOrderResponseMsgForWanB saleOrderResponseMsgForWanB = wanBDepistoryUtil.salesorders(depositorySaleOrderDto.getSaleOrderId(), salesOrderForWanB);
        SaleOrderResultDto saleOrderResultDto = new SaleOrderResultDto();
        saleOrderResultDto.setSourceData(saleOrderResponseMsgForWanB);
        saleOrderResultDto.setDepositoryCode(getDepositorySign());
        saleOrderResultDto.setSuccessSign(saleOrderResponseMsgForWanB.isSucceeded());
        saleOrderResultDto.setAccountNo(saleOrderResponseMsgForWanB.getData().getAccountNo());
        saleOrderResultDto.setErrorCode(saleOrderResponseMsgForWanB.getError().getCode());
        saleOrderResultDto.setErrorMsg(saleOrderResponseMsgForWanB.getError().getMessage());
        saleOrderResultDto.setSystemErrorMsg(saleOrderResponseMsgForWanB.getSystemError());
        saleOrderResultDto.setCancelOn(StrUtil.isNotBlank(saleOrderResponseMsgForWanB.getData().getCancelOn()) ? DateUtil.parse(saleOrderResponseMsgForWanB.getData().getCancelOn(), DatePattern.UTC_FORMAT).toJdkDate() : null);
        saleOrderResultDto.setCreateOn(StrUtil.isNotBlank(saleOrderResponseMsgForWanB.getData().getCreateOn()) ? DateUtil.parse(saleOrderResponseMsgForWanB.getData().getCreateOn(), DatePattern.UTC_FORMAT).toJdkDate() : null);
        saleOrderResultDto.setRemark(saleOrderResponseMsgForWanB.getData().getNotes());
        saleOrderResultDto.setProduct(saleOrderResponseMsgForWanB.getData().getProducts());
        return saleOrderResultDto;
    }

    @Override
    public PurchaseResultDto addPurchaseOrder(DepositoryPurchaseOrderDto depositoryPurchaseOrderDto) {
        PurchaseOrderForWanB purchaseOrderForWanB = translateDepositoryOrderDtoToSpecialOrder(depositoryPurchaseOrderDto);
        DepositoryResponseMsgForWanB depositoryResponseMsgForWanB = wanBDepistoryUtil.purchaseorders(depositoryPurchaseOrderDto.getPurchaseOrderId(), purchaseOrderForWanB);
        PurchaseResultDto purchaseResultDto = new PurchaseResultDto();
        purchaseResultDto.setDeposityCode(getDepositorySign());
        purchaseResultDto.setSuccessSign(depositoryResponseMsgForWanB.isSucceeded());
        purchaseResultDto.setPurchaseNo(depositoryResponseMsgForWanB.getData().getRefId());
        purchaseResultDto.setStatus(depositoryResponseMsgForWanB.getData().getStatus());
        purchaseResultDto.setAccountNo(depositoryResponseMsgForWanB.getData().getAccountNo());
        purchaseResultDto.setCreateDate(depositoryResponseMsgForWanB.getData().getCreateOn());
        purchaseResultDto.setPurchaseReason(depositoryResponseMsgForWanB.getData().getPurchaseReason());
        purchaseResultDto.setPurchaseMsg(depositoryResponseMsgForWanB.isSucceeded() ? PURCHASE_SUCCESS : PURCHASE_FAIL);
        purchaseResultDto.setErrorCode(depositoryResponseMsgForWanB.getError().getCode());
        purchaseResultDto.setErrorMsg(depositoryResponseMsgForWanB.getError().getMessage());
        purchaseResultDto.setSystemErrorMsg(depositoryResponseMsgForWanB.getSystemError());
        return purchaseResultDto;
    }

    @Override
    public OutDepositoryResultDto addOutDepositoryOrder(OutDepositoryOrderDto outDepositoryOrderDto) {

        //OutDepositoryOrderForWanB outDepositoryOrderForWanB = translateOutDepositoryOrderDtoToSpecialOrder(outDepositoryOrderDto);
       // DepositoryResponseMsgForWanB depositoryResponseMsgForWanB = wanBDepistoryUtil.dispatchorders(outDepositoryOrderDto.getDispatchOrderId(), outDepositoryOrderForWanB);
        DepositoryResponseMsgForWanB depositoryResponseMsgForWanB=new  DepositoryResponseMsgForWanB();
        depositoryResponseMsgForWanB.setSucceeded(true);
        DepositoryResponseMsgForWanB.DetailData detailData = new DepositoryResponseMsgForWanB.DetailData();
        detailData.setRefId(outDepositoryOrderDto.getDispatchOrderId());
        detailData.setStatus("Shipped");
        depositoryResponseMsgForWanB.setData(detailData);
        return getDepositoryResultDto(depositoryResponseMsgForWanB);
        //return outDepositoryResultDto
    }


    @Override
    public CancelDepositoryOrderResponse cancelOutDepositoryOrder(String dispatchOrderId) {

        CancelDepositoryOrderResponseForWanB cancelDepositoryOrderResponseForWanB = wanBDepistoryUtil.dispatchordersCancellation(dispatchOrderId);
        CancelDepositoryOrderResponse cancelDepositoryOrderResponse = new CancelDepositoryOrderResponse();
        this.convertResponse(cancelDepositoryOrderResponseForWanB, cancelDepositoryOrderResponse);
        if (cancelDepositoryOrderResponseForWanB.isSucceeded()) {
            CancelDepositoryOrderResponseForWanB.CancelDepositoryOrderResponseDataForWanB data = cancelDepositoryOrderResponseForWanB.getData();
            cancelDepositoryOrderResponse.setStatus(data.getStatus());
            cancelDepositoryOrderResponse.setRequestOn(data.getRequestOn());
            cancelDepositoryOrderResponse.setResponseOn(data.getResponseOn());
        } else {
            if (StringUtils.isNoneBlank(cancelDepositoryOrderResponseForWanB.getError().getMessage()) && cancelDepositoryOrderResponseForWanB.getError().getMessage().contains("Packed")) {
                cancelDepositoryOrderResponse.setStatus(LogisticItemEnum.LogisticItemEnumWanB.DepositoryChannelOrderStatusEnum.REJECTED.getStatus());
            }
        }
        return cancelDepositoryOrderResponse;
    }

    @Override
    public OutDepositoryOrderResultDto getOutDepositoryOrder(OutDepositoryOrderDto outDepositoryOrderDto) {
        OutDepositoryOrderResponseForWanB outDepositoryOrderResponseForWanB = wanBDepistoryUtil.dispatchorders(outDepositoryOrderDto.getDispatchOrderId());
        OutDepositoryOrderResultDto outDepositoryOrderResultDto = new OutDepositoryOrderResultDto();
        if (outDepositoryOrderResponseForWanB.isSucceeded()) {
            outDepositoryOrderResultDto.setDepositoryCode(Constant.DEPOSITORY_WB);
            outDepositoryOrderResultDto.setWbData(outDepositoryOrderResponseForWanB.getData());
            return outDepositoryOrderResultDto;
        }
        throw new RuntimeException(outDepositoryOrderResponseForWanB.getError().getMessage());
    }

    @Override
    public List<InventoryDto> getDepositoryCount(DepositorySearchDto depositorySearchDto) {
        if (!Constant.DEPOSITORY_WB.equals(depositorySearchDto.getDepositoryCode())) {
            throw new RuntimeException("查询万邦仓库接口请将【depositoryCode】属性设置正确");
        }
        InventoryResponseForWanB inventoryResponseForWanB = wanBDepistoryUtil.inventories(depositorySearchDto.getSku(), depositorySearchDto.getWarehouseCode(), depositorySearchDto.getType());
        List<InventoryResponseForWanB.InventoryItem> list = null;
        List<InventoryDto> inventoryDtoList = new ArrayList<>();
        if (inventoryResponseForWanB.isSucceeded() && (list = inventoryResponseForWanB.getData()) != null && list.size() > 0) {
            inventoryDtoList = list.stream().map(item -> {
                return InventoryDto.builder().
                        depositoryCode(Constant.DEPOSITORY_WB)
                        .sku(item.getSKU())
                        .warehouseCode(item.getWarehouseCode())
                        .type(item.getType())
                        .totalQuantity(item.getTotalQuantity())
                        .frozenQuantity(item.getFrozenQuantity())
                        .lockQuantity(item.getLockQuantity())
                        .availableQuantity(item.getAvailableQuantity())
                        .lastModifyDate(item.getLastModifyOn()).build();
            }).collect(Collectors.toList());
            return inventoryDtoList;
        }
        return inventoryDtoList;
    }

    @Override
    public OutDepositoryResultDto addMaterialToDepository(GoodSku goodSku) {
        DepositoryResponseMsgForWanB depositoryResponseMsgForWanB = wanBDepistoryUtil.tradeitemsPut(goodSku);
        OutDepositoryResultDto outDepositoryResultDto = new OutDepositoryResultDto();
        outDepositoryResultDto.setSourceData(depositoryResponseMsgForWanB);
        if (depositoryResponseMsgForWanB.isSucceeded()) {
            outDepositoryResultDto.setSuccessSign(Boolean.TRUE);
        } else {
            outDepositoryResultDto.setSuccessSign(Boolean.FALSE);
        }
        return outDepositoryResultDto;
    }

    @Override
    public GoodSku getMaterialFromDepository(GoodSku goodSku) {
        GoodSkuResponseForWanB goodSkuResponseForWanB = wanBDepistoryUtil.tradeitemsGet(goodSku);
        GoodSku gs = new GoodSku();
        gs.setSourceData(goodSkuResponseForWanB);
        GoodSkuResponseForWanB.Good good = null;
        if (goodSkuResponseForWanB.isSucceeded() && (good = goodSkuResponseForWanB.getData()) != null) {
            gs.setDepositoryCode(getDepositorySign());
            gs.setSku(good.getSKU());
            gs.setName(good.getSKU());
            gs.setWeightInKg(good.getWeightInKg());
            gs.setImageUrls(good.getImageUrls());
            gs.setVariants(good.getVariants());
            gs.setCreateDate(good.getCreateOn());
        }
        return gs;
    }


    public static PurchaseOrderForWanB translateDepositoryOrderDtoToSpecialOrder(DepositoryPurchaseOrderDto depositoryPurchaseOrderDto) {
        //产品信息
        List<ProductDto> productInfo = null;
        List<PurchaseOrderProductContentEditRequest.PurchaseOrderProductEditRequest> products = null;
        if ((productInfo = depositoryPurchaseOrderDto.getProducts()) != null) {
            products = productInfo.stream().map(item -> {
                return PurchaseOrderProductContentEditRequest.PurchaseOrderProductEditRequest.builder()
                        .SKU(item.getSku())
                        .CheckTypes(item.getCheckTypes())
                        .Quantity(item.getQuantity()).build();
            }).collect(Collectors.toList());
        }

        PurchaseOrderProductContentEditRequest ProductContent = PurchaseOrderProductContentEditRequest.builder().Products(products).build();
        //物流信息
        List<ExpressDto> expressesInfo = null;
        List<PurchaseOrderExpressEditRequest> expresses = null;
        if ((expressesInfo = depositoryPurchaseOrderDto.getExpresses()) != null) {
            expresses = expressesInfo.stream().map(item -> {
                return PurchaseOrderExpressEditRequest.builder()
                        // 设置快递单号
                        .ExpressId(item.getExpressNo())
                        .ExpressVendorId(item.getExpressVendorId())
                        .ExpectedArriveOn(item.getExpectedArriveDate())
                        .ShipFromProvince(item.getShipFromProvince()).build();
            }).collect(Collectors.toList());
        }

        PurchaseOrderForWanB purchaseOrderForWanB = PurchaseOrderForWanB.builder()
                .PurchaseReason(depositoryPurchaseOrderDto.getPurchaseWay())
                .WarehouseCode(depositoryPurchaseOrderDto.getWarehouseCode())
                .Notes(depositoryPurchaseOrderDto.getRemark())
                .ProductContent(ProductContent)
                .Expresses(expresses).build();
        return purchaseOrderForWanB;
    }


    public static OutDepositoryOrderForWanB translateOutDepositoryOrderDtoToSpecialOrder(OutDepositoryOrderDto outDepositoryOrderDto) {
        // 地址
        Address address = new Address();
        address.setCountryCode(outDepositoryOrderDto.getAddress().getCountryCode());
        address.setPostCode(outDepositoryOrderDto.getAddress().getPostCode());
        address.setContacter(outDepositoryOrderDto.getAddress().getFirstName() + " " + outDepositoryOrderDto.getAddress().getLastName());
        address.setProvince(outDepositoryOrderDto.getAddress().getProvince());
        address.setStreet1(outDepositoryOrderDto.getAddress().getStreet1());
        address.setTel(outDepositoryOrderDto.getAddress().getTel());
        address.setCity(outDepositoryOrderDto.getAddress().getCity());
        //收集子项
        List<OutDepositoryOrderForWanB.DispatchOrderItem> itemList = outDepositoryOrderDto.getOrderItems().stream().map(item -> OutDepositoryOrderForWanB.DispatchOrderItem.builder().SKU(item.getSku()).Quantity(item.getQuantity()).build())
                .collect(Collectors.toList());
        //发件信息
        DispatchOrderShipping shipping = DispatchOrderShipping.builder().ServiceId(outDepositoryOrderDto.getExpress().getServiceId())
                .TrackingNumber(outDepositoryOrderDto.getExpress().getTrackingNumber())
                .LabelUrl(outDepositoryOrderDto.getExpress().getLabelUrl()).build();
        //万邦对象
        OutDepositoryOrderForWanB outDepositoryOrderForWanB = OutDepositoryOrderForWanB.builder().SalesOrderId(outDepositoryOrderDto.getSalesOrderId())
                .WarehouseCode(outDepositoryOrderDto.getWarehouseCode())
                .Notes(outDepositoryOrderDto.getRemark())
                .Address(address)
                .Items(itemList)
                .Shipping(shipping)
                .EstimatedWeightInKg(outDepositoryOrderDto.getEstimatedWeight())
                .build();
        return outDepositoryOrderForWanB;
    }


    private OutDepositoryResultDto getDepositoryResultDto(DepositoryResponseMsgForWanB depositoryResponseMsgForWanB) {
        OutDepositoryResultDto outDepositoryResultDto = new OutDepositoryResultDto();
        outDepositoryResultDto.setDeposityCode(getDepositorySign());
        outDepositoryResultDto.setSourceData(depositoryResponseMsgForWanB);
        outDepositoryResultDto.setSuccessSign(depositoryResponseMsgForWanB.isSucceeded());
        if (!depositoryResponseMsgForWanB.isSucceeded()) {
            outDepositoryResultDto.setErrorCode(depositoryResponseMsgForWanB.getError().getCode());
            outDepositoryResultDto.setErrorMsg(depositoryResponseMsgForWanB.getError().getMessage());
            outDepositoryResultDto.setSystemErrorMsg(depositoryResponseMsgForWanB.getSystemError());
        } else {
            outDepositoryResultDto.setRefId(depositoryResponseMsgForWanB.getData().getRefId());
            outDepositoryResultDto.setCancellRequestOn(depositoryResponseMsgForWanB.getData().getRequestOn());
            outDepositoryResultDto.setCancellResponseOn(depositoryResponseMsgForWanB.getData().getResponseOn());
            // 出库/取消出库

               // outDepositoryResultDto.setCancellStatus(depositoryResponseMsgForWanB.getData().getStatus());

                outDepositoryResultDto.setOutStatus(depositoryResponseMsgForWanB.getData().getStatus());

        }
        return outDepositoryResultDto;
    }

    private void convertResponse(AbstractDepositoryResponseForWanB abstractDepositoryResponseForWanB, AbstractDepositoryResponse abstractDepositoryResponse) {
        AbstractDepositoryResponseForWanB.ErrorMessage errorMessage = Optional.ofNullable(abstractDepositoryResponseForWanB.getError()).orElse(new AbstractDepositoryResponseForWanB.ErrorMessage());
        abstractDepositoryResponse.setDeposityCode(getDepositorySign());
        abstractDepositoryResponse.setSuccessSign(abstractDepositoryResponseForWanB.isSucceeded());
        abstractDepositoryResponse.setErrorMsg(errorMessage.getMessage());
        abstractDepositoryResponse.setErrorCode(errorMessage.getCode());
        abstractDepositoryResponse.setSystemErrorMsg(abstractDepositoryResponseForWanB.getSystemError());
    }
}
