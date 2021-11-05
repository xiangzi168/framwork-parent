package com.amg.fulfillment.cloud.logistics.grpc;

import com.alibaba.fastjson.JSON;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.enumeration.LogisticsTrackInsideNodeEnum;
import com.amg.fulfillment.cloud.logistics.api.grpc.LogisticsTrackDetailGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPackageMapper;
import com.amg.fulfillment.cloud.logistics.model.vo.DeliveryPackageVO;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryPackageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class LogisticsTrackDetailService extends LogisticsTrackDetailGrpc.LogisticsTrackDetailImplBase {

    @Autowired
    private IDeliveryPackageService deliveryPackageService;
    @Autowired
    private LogisticsPackageMapper logisticsPackageMapper;

    private final static String FAIL_CODE = "100400"; // 请求异常
    private final static int NANO_O = 1000000; // 请求异常
    private final static long DEFAUL_2000_01_01 = 946656000000000000L; // 请求异常
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat simpleDateFormatByT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public LogisticsTrackDetailGTO.LogisticsTrackDetailResponse getLogisticsTrackDetail(LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest request, StreamObserver<LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用 平台查询物流轨迹详情 接口请求参数转成JSON是：{}", requestStr);
        String logisticsTrackingCode = request.getLogisticsTrackingCode();
        if (StringUtils.isBlank(logisticsTrackingCode)) {
            throw new GlobalException(FAIL_CODE, "请按要求传送追踪号参数！！！！");
        }
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getLogisticsTrackingCode, logisticsTrackingCode);
        List<LogisticsPackageDO> packageDOList = logisticsPackageMapper.selectList(queryWrapper);
        if (packageDOList.isEmpty()) {
            throw new GlobalException(FAIL_CODE, "履约系统查询不到该数据，追踪号：【" + logisticsTrackingCode + "】");
        }
        if (packageDOList.size() > 1) {
            throw new GlobalException(FAIL_CODE, "履约系统查询到多条信息数据，追踪号：【" + logisticsTrackingCode + "】");
        }
        Integer type = packageDOList.get(0).getType();
        DeliveryPackageVO packageVO = deliveryPackageService.detail(type, packageDOList.get(0).getId());
        if (packageVO.getId() == null) {
            throw new GlobalException(FAIL_CODE, "履约系统查询不到该类型下的数据，类型是：【" + type + "】，追踪号：【" + logisticsTrackingCode + "】");
        }
        log.info("LogisticsTrackDetailService 转成Pb对象前的数据是： {}", JSON.toJSONString(packageVO));
        List<LogisticsTrackDetailGTO.TrackDetailResponse> list = packageVO.getTrackList().stream().map(item -> {
            Long nano = null;
            try {
                if (StringUtils.isNotBlank(item.getTrackTime())) {
                    if (item.getTrackTime().contains("T")) {
                        nano = simpleDateFormatByT.parse(item.getTrackTime()).getTime() * NANO_O;
                    } else {
                        nano = simpleDateFormat.parse(item.getTrackTime()).getTime() * NANO_O;
                    }
                }
            } catch (ParseException e) {
                log.error("平台查询物流轨迹详情日期转化失败！日期是--{}",item.getTrackTime());
            }
            return LogisticsTrackDetailGTO.TrackDetailResponse.newBuilder()
                    .setNodeName(Optional.ofNullable(item.getNode()).orElse(""))
                    .setNodeDesc(Optional.ofNullable(item.getNodeDesc()).orElse(""))
                    .setInsideCn(Optional.ofNullable(item.getInsideCn()).orElse(""))
                    .setInsideEn(LogisticsTrackInsideNodeEnum.getInsideNodeEnumByNodeEn(item.getInsideEn()).getPlatformEnum())
                    .setContent(Optional.ofNullable(item.getContent()).orElse(""))
                    .setTrackTime(nano == null ? DEFAUL_2000_01_01 : nano)
                    .build();
        }).collect(Collectors.toList());
        LogisticsTrackDetailGTO.LogisticsTrackDetailResponse trackDetailResponse = LogisticsTrackDetailGTO.LogisticsTrackDetailResponse.newBuilder()
                .setType(type)
                .setCreateTime(packageVO.getCreateTime() != null ? packageVO.getCreateTime().getTime() * NANO_O : DEFAUL_2000_01_01)
                .setLogisticsNode(Optional.ofNullable(packageVO.getLogisticsNode()).orElse(""))
                .addAllTrackList(list)
                .build();
        return trackDetailResponse;
    }
}
