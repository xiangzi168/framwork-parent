package com.amg.fulfillment.cloud.logistics.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amg.fulfillment.cloud.logistics.annotation.RetryAnnotation;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4.*;
import com.amg.fulfillment.cloud.logistics.api.enumeration.LogisticItemEnum;
import com.amg.fulfillment.cloud.logistics.api.util.LogisticsCommonUtils;
import com.amg.fulfillment.cloud.logistics.api.util.PX4LogisticUtil;
import com.amg.fulfillment.cloud.logistics.dto.depository.AddressDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-04-20-15:46
 */
@Service("pX4LogisticManagerImpl")
public class PX4LogisticManagerImpl implements ILogisticManager {

    @Autowired
    private PX4LogisticUtil px4LogisticUtil;

    @Override
    public List<String> getLogisticTrackEndStatusList() {
        return Arrays.asList("FPX_S_OK","FPX_S_OKGP","FPX_S_OKVP","FPX_S_OKPO","FPX_S_OKCC","FPX_S_OKSC");
    }

    @Override
    public List<String> getLogisticReceivedStatusList() {
        return new ArrayList<>(Arrays.asList("FPX_C_SPLS","FPX_C_AAF"));
    }
    @Override
    public String getLogisticCode() {
        return Constant.LOGISTIC_PX4;
    }

    @Override
    public LogisticTrackResponseDto getLogisticTrack(LogisticOrderSearchDto logisticOrderSearchDto) {
        String track = logisticOrderSearchDto.getTrackingNumber();
        LogisticTrackSearchFor4PX logisticTrackSearchFor4PX = new LogisticTrackSearchFor4PX();
        logisticTrackSearchFor4PX.setDeliveryOrderNo(track);
        TrackResponseFor4PX trackResponseFor4PX = px4LogisticUtil.trOrderTrackingGet(logisticTrackSearchFor4PX);
        LogisticTrackResponseDto responseDto = new LogisticTrackResponseDto();
        this.convertResponse(trackResponseFor4PX, responseDto);
        List<TrackPointDto> trackPointDtoList = new ArrayList<>();
        TrackResponseFor4PX.TrackDetail data = trackResponseFor4PX.getData();
        if(data != null)
        {
            List<TrackResponseFor4PX.Tracking> trackingList = Optional.ofNullable(data.getTrackingList()).orElse(new ArrayList<>());
            trackPointDtoList.addAll(trackingList.stream().map(item -> {
                return TrackPointDto.builder()
                        .content(item.getTrackingContent())
                        .location(item.getOccurLocation())
                        .status(item.getBusinessLinkCode())
                        .eventTime(item.getOccurDatetime())
                        .build();
            }).collect(Collectors.toList()));
        }
        responseDto.setTrackPointDtos(trackPointDtoList);
        return responseDto;
    }

    @Override
    public LogisticOrderResponseDto addLogisticOrder(LogisticOrderDto logisticOrderDto) {

        LogisticOrderFor4PX.LogisticsServiceInfo serviceInfo = this.getLogisticsServiceInfo(logisticOrderDto);      //物流服务信息-物流渠道数据
        LogisticOrderFor4PX.ReturnInfo returnInfo = this.getReturnInfo(logisticOrderDto);               //  退件信息
        LogisticOrderFor4PX.Parcel parcel = this.getParcel(logisticOrderDto);       //运单商品详情
        LogisticOrderFor4PX.Sender sender = this.getSender();       //发件人信息、保险信息（投保时必须填写）、是否投保(Y、N)
        LogisticOrderFor4PX.RecipientInfo recipientInfo = this.getRecipientInfo(logisticOrderDto);              //	收件人信息
        LogisticOrderFor4PX.DeliverTypeInfo deliverTypeInfo = this.getDeliverTypeInfo();        //货物到仓方式信息
        LogisticOrderFor4PX.DeliverToRecipientInfo deliverToRecipientInfo = this.getDeliverToRecipientInfo();       //投递信息

        LogisticOrderFor4PX orderFor4PX = LogisticOrderFor4PX.builder()
                .ref_no(logisticOrderDto.getLogisticOrderNo())      //物流单号，系统生成的
                .business_type("BDS")       //默认 BDS
                .duty_type("U")     // 默认 U
                .logistics_service_info(serviceInfo)
                .return_info(returnInfo)
                .parcel_list(Collections.singletonList(parcel))
                .is_insure("N")
                .sender(sender)
                .recipient_info(recipientInfo)
                .deliver_type_info(deliverTypeInfo)
                .deliver_to_recipient_info(deliverToRecipientInfo)
                .build();

        LogisticOrderResponseFor4PX logisticOrderResponseFor4PX = px4LogisticUtil.dsXmsOrderCreate(orderFor4PX);
        LogisticOrderResponseFor4PX.OrderDetail orderDetail = logisticOrderResponseFor4PX.getData();
        LogisticOrderResponseDto responseDto = new LogisticOrderResponseDto();
        this.convertResponse(logisticOrderResponseFor4PX, responseDto);
//        responseDto.setTrackingNumber(orderDetail.getTracking_no());
        responseDto.setChannelTrackingNum(orderDetail.getLogistics_channel_no());
        responseDto.setLogisticsOrderNo(orderDetail.getRef_no());
        responseDto.setWaybillNo(orderDetail.getTracking_no());  //实践发现：单号是4px追踪号
        responseDto.setProcessCode(orderDetail.getRef_no());
        responseDto.setIndexNumber(orderDetail.getLogistics_channel_no());

        this.getTrackingNumber(responseDto);        //物流渠道订单号
        return responseDto;
    }

    @RetryAnnotation
    @Override
    public LogisticPrintLabelResponseDto getLogisticPrintLabel(LogisticOrderSearchDto logisticOrderSearchDto) {

        LogisticLabelSearchFor4PX logisticLabelSearchFor4PX = LogisticLabelSearchFor4PX.builder()
                .requestNo(logisticOrderSearchDto.getOrderNo())
                .build();
        LogisticLabelResponseFor4PX logisticLabelResponseFor4PX = px4LogisticUtil.dsXmsLabelGet(logisticLabelSearchFor4PX);
        LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = new LogisticPrintLabelResponseDto();
        this.convertResponse(logisticLabelResponseFor4PX, logisticPrintLabelResponseDto);
        LogisticLabelResponseFor4PX.LabelDetail labelDetail = logisticLabelResponseFor4PX.getData();
        if(labelDetail != null)
        {
            logisticPrintLabelResponseDto.setLabelBarcode(labelDetail.getLabelBarcode());
            logisticPrintLabelResponseDto.setLogisticsLabel(labelDetail.getLabelUrlInfo().getLogisticsLabel());
        }
        return logisticPrintLabelResponseDto;
    }

    @Override
    public LogisticOrderDetailResponseDto getLogisticDetail(LogisticOrderSearchDto logisticOrderSearchDto) {
        LogisticOrderInfoSearchFor4PX logisticOrderInfoSearchFor4PX = LogisticOrderInfoSearchFor4PX.builder()
                .requestNo(logisticOrderSearchDto.getLogisticsOrderNo())
                .build();

        LogisticOrderInfoResponseFor4PX logisticOrderInfoResponseFor4PX = px4LogisticUtil.dsXmsOrderGet(logisticOrderInfoSearchFor4PX);
        LogisticOrderDetailResponseDto logisticOrderDetailResponseDto = new LogisticOrderDetailResponseDto();
        this.convertResponse(logisticOrderInfoResponseFor4PX, logisticOrderDetailResponseDto);

        List<LogisticOrderInfoResponseFor4PX.OrderInfoDetail> orderInfoDetailList = logisticOrderInfoResponseFor4PX.getDataDetail();
        for(LogisticOrderInfoResponseFor4PX.OrderInfoDetail orderInfoDetail : orderInfoDetailList)
        {
            LogisticOrderInfoResponseFor4PX.ConsignmentInfo consignmentInfo = orderInfoDetail.getConsignmentInfo();
            String trackingNumber = consignmentInfo.getLogisticsChannelNo();
            if(StringUtils.isBlank(trackingNumber))
            {
                if(LogisticItemEnum.LogisticItemEnumPX4.OrderModelEnum.U.getCode().equals(consignmentInfo.getGetNoMode()))      //仓库作业时取号
                {
                    trackingNumber = consignmentInfo.getLogisticsChannelNo();
                }
            }
            logisticOrderDetailResponseDto.setTrackingNumber(trackingNumber);
            logisticOrderDetailResponseDto.setChannelTrackingNumber(trackingNumber);
        }

        return logisticOrderDetailResponseDto;
    }

    //物流服务信息
    private LogisticOrderFor4PX.LogisticsServiceInfo getLogisticsServiceInfo(LogisticOrderDto logisticOrderDto)
    {
        return LogisticOrderFor4PX.LogisticsServiceInfo.builder()
                .logistics_product_code(logisticOrderDto.getChannel())
                .build();
    }

    //退件信息
    private LogisticOrderFor4PX.ReturnInfo getReturnInfo(LogisticOrderDto logisticOrderDto)
    {

        //QC渠道不支持国内异常包裹退货
        // U:其它;Y:退货;N:销毁
        String logisticsProductCode = logisticOrderDto.getChannel();
        LogisticOrderFor4PX.ReturnInfo returnInfo = LogisticOrderFor4PX.ReturnInfo.builder()
                .is_return_on_oversea("U")
                .build();
        if("4PX-QC".equals(logisticsProductCode) || "QC".equals(logisticsProductCode))
        {
            returnInfo.setIs_return_on_domestic("U");
        } else
        {
            returnInfo.setIs_return_on_domestic("Y");
        }
        return returnInfo;
    }

    //包裹列表
    private LogisticOrderFor4PX.Parcel getParcel(LogisticOrderDto logisticOrderDto)
    {
        //运单商品详情
        AtomicReference<BigDecimal> atomicReferenceParcelWeight = new AtomicReference<>(new BigDecimal("0.0"));
        AtomicReference<BigDecimal> atomicReferenceParcelValue = new AtomicReference<>(new BigDecimal("0.0"));
        List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();

        Map<String, LogisticOrderFor4PX.Product> productMap = new HashMap<>();
        Map<String, LogisticOrderFor4PX.DeclareProductInfo> declareProductInfoMap = new HashMap<>();

        waybillGoodDetailDtoList.forEach(waybillGoodDetailDto -> {
            LogisticOrderFor4PX.Product product = productMap.get(waybillGoodDetailDto.getGoodsId());
            if(product == null) {
                product = LogisticOrderFor4PX.Product.builder()     //投保物品信息（投保、查验、货物丢失作为参考依据）
                        .sku_code(waybillGoodDetailDto.getGoodsId())
                        .product_name(waybillGoodDetailDto.getDeclaredNameCn())
                        .product_description(waybillGoodDetailDto.getGoodsTitle())
                        .product_unit_price(waybillGoodDetailDto.getDeclaredValue())
                        .currency("USD")
                        .qty(1)
                        .build();
                productMap.put(waybillGoodDetailDto.getGoodsId(), product);
            } else {
                product.setQty(product.getQty() + 1);       //设置数量数量
            }

            LogisticOrderFor4PX.DeclareProductInfo declareProductInfo = declareProductInfoMap.get(waybillGoodDetailDto.getGoodsId());
            if(declareProductInfo == null) {
                declareProductInfo = LogisticOrderFor4PX.DeclareProductInfo.builder()        //海关申报信息(每个包裹的申报信息，方式1：填写申报产品代码和申报数量；方式2：填写其他详细申报信息)
                        .declare_product_code_qty(1)
                        .declare_product_name_cn(waybillGoodDetailDto.getDeclaredNameCn())
                        .declare_product_name_en(waybillGoodDetailDto.getDeclaredNameEn())
                        .declare_unit_price_export(waybillGoodDetailDto.getDeclaredValue())
                        .currency_export("USD")
                        .declare_unit_price_import(waybillGoodDetailDto.getDeclaredValue())
                        .currency_import("USD")
                        .brand_export("none")
                        .brand_import("none")
                        .build();
                declareProductInfoMap.put(waybillGoodDetailDto.getGoodsId(), declareProductInfo);
            } else {
                declareProductInfo.setDeclare_product_code_qty(declareProductInfo.getDeclare_product_code_qty() + 1);
            }

            atomicReferenceParcelWeight.getAndAccumulate(waybillGoodDetailDto.getWeight(), BigDecimal::add);        //计算重量
            atomicReferenceParcelValue.getAndAccumulate(waybillGoodDetailDto.getDeclaredValue(), BigDecimal::add);      //计算包裹申报价格
        });

        return LogisticOrderFor4PX.Parcel.builder()
                .weight(atomicReferenceParcelWeight.get())      //重量
                .parcel_value(atomicReferenceParcelValue.get())     //包裹申报价格
                .currency("USD")        //币种
                .include_battery(LogisticsCommonUtils.convertJudgeIntegerToString(logisticOrderDto.getBattery()))      //是否带电
                .product_list(new ArrayList<>(productMap.values()))
                .declare_product_info(new ArrayList<>(declareProductInfoMap.values()))
                .build();
    }

    //发件人信息
    private LogisticOrderFor4PX.Sender getSender()
    {
        return LogisticOrderFor4PX.Sender.builder()
                .first_name("云摩")
                .country("CN")
                .city("Shenzhen")
                .build();
    }

    //	收件人信息
    private LogisticOrderFor4PX.RecipientInfo getRecipientInfo(LogisticOrderDto logisticOrderDto)
    {
        AddressDto receiverAddress = logisticOrderDto.getReceiverAddress();
        return LogisticOrderFor4PX.RecipientInfo.builder()
                .first_name(receiverAddress.getFirstName())
                .last_name(receiverAddress.getLastName())
                .phone(receiverAddress.getTel())
                .email(receiverAddress.getEmail())
                .post_code(receiverAddress.getPostCode())
                .country(receiverAddress.getCountryCode())
                .state(receiverAddress.getProvince())
                .city(receiverAddress.getCity())
                .street(receiverAddress.getStreet1())
                .district(receiverAddress.getStreet2())
                .build();
    }

    //货物到仓方式信息
    private LogisticOrderFor4PX.DeliverTypeInfo getDeliverTypeInfo()
    {
        return LogisticOrderFor4PX.DeliverTypeInfo.builder()
                .deliver_type("1")
                .build();
    }

    //投递信息
    private LogisticOrderFor4PX.DeliverToRecipientInfo getDeliverToRecipientInfo()
    {
        return LogisticOrderFor4PX.DeliverToRecipientInfo.builder()
                .build();
    }

    /**
     * 4PX修改业务规则，原来通过接口去4px创建订单后，会接着获取到物流渠道订单号。但现在4PX的美国渠道换成了当4px收货后再更新该单号。同时面单上也不存在该物流渠道订单号。即变化如下：
     * 1、非美国的制单和获取面单不变；
     * 2、美国的制单后，实际获取跟踪号即可，并将该跟踪号以及制成的面单（url链接）推送到仓库出库；
     * 解决方案：
     * 1、调用【创建直发委托单接口】制单；
     * 2、创建后调用【查询直发委托单接口】查询字段get_no_mode的值
     * 2.1若为U，则获取logistics_channel_no，获取到则生成面单并通知仓库出库；未获取则继续查询；
     * 2.2若为C，则不获取logistics_channel_no，根据跟踪号生成面单并通知仓库出库；
     * https://4pxgroup.yuque.com/docs/share/e957c444-eb33-48ac-ab0e-ce1209424045?#
     * @param logisticOrderResponseDto
     */
    private void getTrackingNumber(LogisticOrderResponseDto logisticOrderResponseDto)
    {
        if(logisticOrderResponseDto.isSuccessSign() && StringUtils.isBlank(logisticOrderResponseDto.getTrackingNumber()))
        {
            LogisticOrderInfoSearchFor4PX logisticOrderInfoSearchFor4PX = LogisticOrderInfoSearchFor4PX.builder()
                    .requestNo(logisticOrderResponseDto.getLogisticsOrderNo())
                    .build();
            LogisticOrderInfoResponseFor4PX logisticOrderInfoResponseFor4PX = px4LogisticUtil.dsXmsOrderGet(logisticOrderInfoSearchFor4PX);
            List<LogisticOrderInfoResponseFor4PX.OrderInfoDetail> orderInfoDetailList = logisticOrderInfoResponseFor4PX.getDataDetail();
            for(LogisticOrderInfoResponseFor4PX.OrderInfoDetail orderInfoDetail : orderInfoDetailList)
            {
                LogisticOrderInfoResponseFor4PX.ConsignmentInfo consignmentInfo = orderInfoDetail.getConsignmentInfo();
                if(LogisticItemEnum.LogisticItemEnumPX4.OrderModelEnum.U.getCode().equals(consignmentInfo.getGetNoMode()))      //仓库作业时取号
                {
                    logisticOrderResponseDto.setChannelTrackingNum(consignmentInfo.getLogisticsChannelNo());
                    return;
                }
            }
        }
    }

    private void convertResponse(AbstractResponseFor4PX abstractResponseFor4PX, AbstractLogisticResponse abstractLogisticResponse)
    {
        List errors = abstractResponseFor4PX.getErrors();
        String errorString = JSON.toJSONString(errors);
        abstractLogisticResponse.setSourceDate(abstractResponseFor4PX);
        abstractLogisticResponse.setLogsiticCode(getLogisticCode());
        abstractLogisticResponse.setSuccessSign(LogisticOrderResponseFor4PX.RESULT_SUCCESS.equals(abstractResponseFor4PX.getResult()) ? Boolean.TRUE : Boolean.FALSE);
        abstractLogisticResponse.setMessage(abstractResponseFor4PX.getMsg() + (!errors.isEmpty() ? StringUtils.substring(errorString, 0, 150) : ""));
        abstractLogisticResponse.setCode(abstractResponseFor4PX.getResult());
        abstractLogisticResponse.setError(!errors.isEmpty() ? StringUtils.substring(errorString, 0, 150) : null);
    }

}
