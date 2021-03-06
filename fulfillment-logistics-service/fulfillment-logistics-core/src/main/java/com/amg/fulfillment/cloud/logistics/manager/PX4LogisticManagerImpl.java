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

        LogisticOrderFor4PX.LogisticsServiceInfo serviceInfo = this.getLogisticsServiceInfo(logisticOrderDto);      //??????????????????-??????????????????
        LogisticOrderFor4PX.ReturnInfo returnInfo = this.getReturnInfo(logisticOrderDto);               //  ????????????
        LogisticOrderFor4PX.Parcel parcel = this.getParcel(logisticOrderDto);       //??????????????????
        LogisticOrderFor4PX.Sender sender = this.getSender();       //????????????????????????????????????????????????????????????????????????(Y???N)
        LogisticOrderFor4PX.RecipientInfo recipientInfo = this.getRecipientInfo(logisticOrderDto);              //	???????????????
        LogisticOrderFor4PX.DeliverTypeInfo deliverTypeInfo = this.getDeliverTypeInfo();        //????????????????????????
        LogisticOrderFor4PX.DeliverToRecipientInfo deliverToRecipientInfo = this.getDeliverToRecipientInfo();       //????????????

        LogisticOrderFor4PX orderFor4PX = LogisticOrderFor4PX.builder()
                .ref_no(logisticOrderDto.getLogisticOrderNo())      //??????????????????????????????
                .business_type("BDS")       //?????? BDS
                .duty_type("U")     // ?????? U
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
        responseDto.setWaybillNo(orderDetail.getTracking_no());  //????????????????????????4px?????????
        responseDto.setProcessCode(orderDetail.getRef_no());
        responseDto.setIndexNumber(orderDetail.getLogistics_channel_no());

        this.getTrackingNumber(responseDto);        //?????????????????????
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
                if(LogisticItemEnum.LogisticItemEnumPX4.OrderModelEnum.U.getCode().equals(consignmentInfo.getGetNoMode()))      //?????????????????????
                {
                    trackingNumber = consignmentInfo.getLogisticsChannelNo();
                }
            }
            logisticOrderDetailResponseDto.setTrackingNumber(trackingNumber);
            logisticOrderDetailResponseDto.setChannelTrackingNumber(trackingNumber);
        }

        return logisticOrderDetailResponseDto;
    }

    //??????????????????
    private LogisticOrderFor4PX.LogisticsServiceInfo getLogisticsServiceInfo(LogisticOrderDto logisticOrderDto)
    {
        return LogisticOrderFor4PX.LogisticsServiceInfo.builder()
                .logistics_product_code(logisticOrderDto.getChannel())
                .build();
    }

    //????????????
    private LogisticOrderFor4PX.ReturnInfo getReturnInfo(LogisticOrderDto logisticOrderDto)
    {

        //QC???????????????????????????????????????
        // U:??????;Y:??????;N:??????
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

    //????????????
    private LogisticOrderFor4PX.Parcel getParcel(LogisticOrderDto logisticOrderDto)
    {
        //??????????????????
        AtomicReference<BigDecimal> atomicReferenceParcelWeight = new AtomicReference<>(new BigDecimal("0.0"));
        AtomicReference<BigDecimal> atomicReferenceParcelValue = new AtomicReference<>(new BigDecimal("0.0"));
        List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();

        Map<String, LogisticOrderFor4PX.Product> productMap = new HashMap<>();
        Map<String, LogisticOrderFor4PX.DeclareProductInfo> declareProductInfoMap = new HashMap<>();

        waybillGoodDetailDtoList.forEach(waybillGoodDetailDto -> {
            LogisticOrderFor4PX.Product product = productMap.get(waybillGoodDetailDto.getGoodsId());
            if(product == null) {
                product = LogisticOrderFor4PX.Product.builder()     //????????????????????????????????????????????????????????????????????????
                        .sku_code(waybillGoodDetailDto.getGoodsId())
                        .product_name(waybillGoodDetailDto.getDeclaredNameCn())
                        .product_description(waybillGoodDetailDto.getGoodsTitle())
                        .product_unit_price(waybillGoodDetailDto.getDeclaredValue())
                        .currency("USD")
                        .qty(1)
                        .build();
                productMap.put(waybillGoodDetailDto.getGoodsId(), product);
            } else {
                product.setQty(product.getQty() + 1);       //??????????????????
            }

            LogisticOrderFor4PX.DeclareProductInfo declareProductInfo = declareProductInfoMap.get(waybillGoodDetailDto.getGoodsId());
            if(declareProductInfo == null) {
                declareProductInfo = LogisticOrderFor4PX.DeclareProductInfo.builder()        //??????????????????(????????????????????????????????????1???????????????????????????????????????????????????2?????????????????????????????????)
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

            atomicReferenceParcelWeight.getAndAccumulate(waybillGoodDetailDto.getWeight(), BigDecimal::add);        //????????????
            atomicReferenceParcelValue.getAndAccumulate(waybillGoodDetailDto.getDeclaredValue(), BigDecimal::add);      //????????????????????????
        });

        return LogisticOrderFor4PX.Parcel.builder()
                .weight(atomicReferenceParcelWeight.get())      //??????
                .parcel_value(atomicReferenceParcelValue.get())     //??????????????????
                .currency("USD")        //??????
                .include_battery(LogisticsCommonUtils.convertJudgeIntegerToString(logisticOrderDto.getBattery()))      //????????????
                .product_list(new ArrayList<>(productMap.values()))
                .declare_product_info(new ArrayList<>(declareProductInfoMap.values()))
                .build();
    }

    //???????????????
    private LogisticOrderFor4PX.Sender getSender()
    {
        return LogisticOrderFor4PX.Sender.builder()
                .first_name("??????")
                .country("CN")
                .city("Shenzhen")
                .build();
    }

    //	???????????????
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

    //????????????????????????
    private LogisticOrderFor4PX.DeliverTypeInfo getDeliverTypeInfo()
    {
        return LogisticOrderFor4PX.DeliverTypeInfo.builder()
                .deliver_type("1")
                .build();
    }

    //????????????
    private LogisticOrderFor4PX.DeliverToRecipientInfo getDeliverToRecipientInfo()
    {
        return LogisticOrderFor4PX.DeliverToRecipientInfo.builder()
                .build();
    }

    /**
     * 4PX??????????????????????????????????????????4px?????????????????????????????????????????????????????????????????????4PX???????????????????????????4px??????????????????????????????????????????????????????????????????????????????????????????????????????
     * 1?????????????????????????????????????????????
     * 2????????????????????????????????????????????????????????????????????????????????????????????????url?????????????????????????????????
     * ???????????????
     * 1???????????????????????????????????????????????????
     * 2???????????????????????????????????????????????????????????????get_no_mode??????
     * 2.1??????U????????????logistics_channel_no??????????????????????????????????????????????????????????????????????????????
     * 2.2??????C???????????????logistics_channel_no??????????????????????????????????????????????????????
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
                if(LogisticItemEnum.LogisticItemEnumPX4.OrderModelEnum.U.getCode().equals(consignmentInfo.getGetNoMode()))      //?????????????????????
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
