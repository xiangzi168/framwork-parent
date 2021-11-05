package com.amg.fulfillment.cloud.logistics.grpc;

import com.alibaba.fastjson.JSON;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.grpc.LogisticsProviderGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO;
import com.amg.fulfillment.cloud.logistics.api.util.BeanConvertUtils;
import com.amg.fulfillment.cloud.logistics.dto.depository.AddressDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.WaybillGoodDetailDto;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsChannelDO;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsRecommendReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsChannelVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsProviderVO;
import com.amg.fulfillment.cloud.logistics.module.rule.LogisticMatchRuleHandler;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryProductService;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsRecommendService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Seraph on 2021/6/9
 */

@Slf4j
@GrpcService
@Component
public class LogisticsProviderService extends LogisticsProviderGrpc.LogisticsProviderImplBase {

    @Autowired
    private ILogisticsRecommendService logisticsRecommendService;
    @Autowired
    private IDeliveryProductService deliveryProductService;
    @Autowired
    private LogisticMatchRuleHandler logisticMatchRuleHandler;

    @Override
    public List<LogisticsProviderGTO.LogisticsProviderResponse> getLogisticsProvider(LogisticsProviderGTO.nullRequest request,
                                                                                     StreamObserver<LogisticsProviderGTO.LogisticsProviderResponseResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用 查询物流商信息 接口请求参数转成JSON是：{}", requestStr);

        List<LogisticsProviderVO> logisticsProviderVOList = logisticsRecommendService.provider();
        List<LogisticsProviderGTO.LogisticsProviderResponse> logisticsProviderReponseList = this.getLogisticsProviderReponse(logisticsProviderVOList);
        return logisticsProviderReponseList;
    }

    @Override
    public List<LogisticsProviderGTO.LogisticsChannelResponse> getLogisticsChannel(LogisticsProviderGTO.LogisticsRequest request,
                                                                                   StreamObserver<LogisticsProviderGTO.LogisticsChannelResponseResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用 查询物流商渠道信息 接口请求参数转成JSON是：{}", requestStr);

        LogisticsRecommendReq logisticsRecommendReq = JSON.parseObject(requestStr, LogisticsRecommendReq.class);
        LogisticOrderDto logisticOrderDto = new LogisticOrderDto();
        logisticOrderDto.setLogisticCode(logisticsRecommendReq.getLogisticsCode());
        List<LogisticsChannelVO> logisticsChannelVOList = logisticsRecommendService.channel(logisticOrderDto);
        List<LogisticsProviderGTO.LogisticsChannelResponse> logisticsChannelReponseList = this.getLogisticsChannelReponse(logisticsChannelVOList);
        return logisticsChannelReponseList;
    }

    @Override
    public List<LogisticsProviderGTO.LogisticsProviderResponse> getLogisticsRecommendProvider(LogisticsProviderGTO.LogisticsRecommendRequest request,
                                                                                              StreamObserver<LogisticsProviderGTO.LogisticsProviderResponseResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用 查询推荐物流商信息 接口请求参数转成JSON是：{}", requestStr);
        List<LogisticsProviderGTO.LogisticsProviderResponse> logisticsProviderReponseList = null;
        if (request != null) {
            LogisticsRecommendReq logisticsRecommendReq = JSON.parseObject(requestStr, LogisticsRecommendReq.class);
            LogisticOrderDto logisticOrderDto = this.getLogisticOrderDto(logisticsRecommendReq);
            //获取所有的物流商和渠道
            List<LogisticsProviderVO> logisticsProviderVOList = logisticsRecommendService.provider();
/*            //需要删除
            List<WaybillGoodDetailDto> waybillGoodDetailDtos = logisticOrderDto.getWaybillGoodDetailDtos();
            ArrayList<Long> longs = new ArrayList<>();
            longs.add(1415918703502422018L);
            waybillGoodDetailDtos.get(0).setLabelIdList(longs);*/
            //需要删除
            //    "1415918703502422018";
            log.info("GRPC--远程调用 查询推荐物流商信息 获取物流标签后参数转成JSON是：{}", logisticOrderDto.toString());
            logisticsProviderReponseList = this.getLogisticsProviderReponse(logisticOrderDto, logisticsProviderVOList);
            //    returnGrpcResult(logisticsProviderReponseList);
        } else {
            log.error("GRPC--远程调用 查询推荐物流商信息 请求参数为空request: {}", requestStr);
        }
        return logisticsProviderReponseList;
    }

    @Override
    public List<LogisticsProviderGTO.LogisticsChannelResponse> getLogisticsRecommendChannel(LogisticsProviderGTO.LogisticsRecommendRequest request,
                                                                                            StreamObserver<LogisticsProviderGTO.LogisticsChannelResponseResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用 查询推荐物流商渠道信息 接口请求参数转成JSON是：{}", requestStr);

        LogisticsRecommendReq logisticsRecommendReq = JSON.parseObject(requestStr, LogisticsRecommendReq.class);
        LogisticOrderDto logisticOrderDto = this.getLogisticOrderDto(logisticsRecommendReq);
        List<LogisticsChannelVO> logisticsChannelVOList = logisticsRecommendService.channel(logisticOrderDto);
        List<LogisticsProviderGTO.LogisticsChannelResponse> logisticsChannelReponseList = this.getLogisticsChannelReponse(logisticOrderDto, logisticsChannelVOList);
        return logisticsChannelReponseList;
    }


    private LogisticOrderDto getLogisticOrderDto(LogisticsRecommendReq logisticsRecommendReq) {
        if (logisticsRecommendReq != null) {
            List<WaybillGoodDetailDto> waybillGoodDetailDtoList = new ArrayList<>();
            List<LogisticsRecommendReq.LogisticsRecommendProductReq> productList = Optional.ofNullable(logisticsRecommendReq.getProductList()).orElse(new ArrayList<>());
            productList.forEach(product -> {
                WaybillGoodDetailDto waybillGoodDetailDto = BeanConvertUtils.copyProperties(product, WaybillGoodDetailDto.class);
                waybillGoodDetailDtoList.add(waybillGoodDetailDto);
            });
            AddressDto addressDto = BeanConvertUtils.copyProperties(logisticsRecommendReq.getAddress(), AddressDto.class);
            //商品信息集合和地址信息 如果为空返回空值
            if (waybillGoodDetailDtoList.size() == 0 || addressDto == null) return null;
            List<LogisticOrderDto> logisticOrderDtoList = new ArrayList<>();
            //组装数据
            LogisticOrderDto logisticOrderDto = new LogisticOrderDto();
            logisticOrderDto.setLogisticCode(logisticsRecommendReq.getLogisticsCode());
            logisticOrderDto.setWaybillGoodDetailDtos(waybillGoodDetailDtoList);
            logisticOrderDto.setReceiverAddress(addressDto);
            logisticOrderDtoList.add(logisticOrderDto);
            //初始化数据, 生成的 id 物流包裹单号是临时的，不会占用正式的序号
            deliveryProductService.initLogisticsPackage(logisticOrderDtoList);
            return logisticOrderDto;
        }
        return null;
    }

    private List<LogisticsProviderGTO.LogisticsProviderResponse> getLogisticsProviderReponse(List<LogisticsProviderVO> logisticsProviderVOList) {
        return this.getLogisticsProviderReponse(null, logisticsProviderVOList);
    }

    private List<LogisticsProviderGTO.LogisticsProviderResponse> getLogisticsProviderReponse(LogisticOrderDto logisticOrderDto, List<LogisticsProviderVO> logisticsProviderVOList) {
        List<LogisticsProviderGTO.LogisticsProviderResponse> logisticsProviderReponseList = new ArrayList<>();
        if (logisticsProviderVOList == null || logisticsProviderVOList.size() == 0) return logisticsProviderReponseList;
        LogisticsChannelDO logisticsChannelDO = logisticOrderDto == null ? null : logisticMatchRuleHandler.getLogisticsChannel(logisticOrderDto);
        logisticsProviderVOList.forEach(logisticsProviderVO -> {
            LogisticsProviderGTO.LogisticsProviderResponse logisticsProviderReponse = LogisticsProviderGTO.LogisticsProviderResponse.newBuilder()
                    .setRecommendFlag(logisticsChannelDO == null ? Boolean.FALSE : logisticsChannelDO.getLogisticsCode().equals(logisticsProviderVO.getLogisticsCode()))
                    .setId(logisticsProviderVO.getId())
                    .setLogisticsName(logisticsProviderVO.getLogisticsName())
                    .setLogisticsCode(logisticsProviderVO.getLogisticsCode())
                    .setLogisticsDesc(logisticsProviderVO.getLogisticsDesc())
                    .addAllChannelList(this.getLogisticsChannelReponse(logisticsChannelDO, logisticOrderDto, logisticsProviderVO.getChannelList()))
                    .build();
            logisticsProviderReponseList.add(logisticsProviderReponse);
        });
        return logisticsProviderReponseList;
    }

    private List<LogisticsProviderGTO.LogisticsChannelResponse> getLogisticsChannelReponse(List<LogisticsChannelVO> logisticsChannelVOList) {
        return this.getLogisticsChannelReponse(null, logisticsChannelVOList);
    }

    private List<LogisticsProviderGTO.LogisticsChannelResponse> getLogisticsChannelReponse(LogisticOrderDto logisticOrderDto, List<LogisticsChannelVO> logisticsChannelVOList) {
        return this.getLogisticsChannelReponse(null, logisticOrderDto, logisticsChannelVOList);
    }

    private List<LogisticsProviderGTO.LogisticsChannelResponse> getLogisticsChannelReponse(LogisticsChannelDO logisticsChannelDO, LogisticOrderDto logisticOrderDto, List<LogisticsChannelVO> logisticsChannelVOList) {
        List<LogisticsProviderGTO.LogisticsChannelResponse> logisticsChannelReponseList = new ArrayList<>();
        if (logisticsChannelVOList == null || logisticsChannelVOList.size() == 0) return logisticsChannelReponseList;

        //如果推荐物流渠道为空，则需要去查询
        if (logisticsChannelDO == null) {
            logisticsChannelDO = logisticOrderDto == null ? null : logisticMatchRuleHandler.getLogisticsChannel(logisticOrderDto);
        }

        LogisticsChannelDO finalLogisticsChannelDO = logisticsChannelDO;        //重新赋值
        logisticsChannelVOList.forEach(logisticsChannelVO -> {
            LogisticsProviderGTO.LogisticsChannelResponse.Builder builder = LogisticsProviderGTO.LogisticsChannelResponse.newBuilder()
                    .setRecommendFlag(finalLogisticsChannelDO == null ? Boolean.FALSE : finalLogisticsChannelDO.getChannelCode().equals(logisticsChannelVO.getChannelCode()))
                    .setId(logisticsChannelVO.getId())
                    .setLogisticsName(logisticsChannelVO.getLogisticsName())
                    .setLogisticsCode(logisticsChannelVO.getLogisticsCode())
                    .setChannelName(logisticsChannelVO.getChannelName())
                    .setChannelCode(logisticsChannelVO.getChannelCode());
            if (finalLogisticsChannelDO != null) {
                builder.setFreight(finalLogisticsChannelDO.getTotalPrice().doubleValue());
            }
            LogisticsProviderGTO.LogisticsChannelResponse logisticsChannelReponse = builder.build();
            logisticsChannelReponseList.add(logisticsChannelReponse);
        });

        return logisticsChannelReponseList;
    }
}
