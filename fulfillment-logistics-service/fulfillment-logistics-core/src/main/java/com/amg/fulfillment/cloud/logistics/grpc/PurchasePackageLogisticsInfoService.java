package com.amg.fulfillment.cloud.logistics.grpc;

import com.amg.fulfillment.cloud.logistics.api.grpc.PurchasePackageLogisticsInfoGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO;
import com.amg.fulfillment.cloud.logistics.model.vo.PurchasePackageLogisticsInfoVO;
import com.amg.fulfillment.cloud.logistics.service.IPurchasePackageService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-06-03-17:36
 * 获取采购包裹的信息内容
 */
@Slf4j
@GrpcService
public class PurchasePackageLogisticsInfoService extends PurchasePackageLogisticsInfoGrpc.PurchasePackageLogisticsInfoImplBase {

    @Autowired
    private IPurchasePackageService purchasePackageService;


    @Override
    public List<PurchasePackageLogisticsInfoGTO.logisticsInfo> getPurchasePackageLogisticsInfo(PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest request, StreamObserver<PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult> responseObserver) {
        List<String> purchaseIdList = request.getPurchaseIdList().stream().collect(Collectors.toList());
        List<PurchasePackageLogisticsInfoVO> info = purchasePackageService.getPurchasePackageLogisticsInfo(purchaseIdList);
        List<PurchasePackageLogisticsInfoGTO.logisticsInfo> list = info.stream().map(vo -> {
            return PurchasePackageLogisticsInfoGTO.logisticsInfo.newBuilder()
                    .setSalesOrderId(Optional.ofNullable(vo.getSalesOrderId()).orElse(""))
                    .setItemId(Optional.ofNullable(vo.getItemId()).orElse(""))
                    .setPurchaseId(Optional.ofNullable(vo.getPurchaseId()).orElse(""))
                    .setPackageNo(Optional.ofNullable(vo.getPackageNo()).orElse(""))
                    .setExpressCompanyCode(Optional.ofNullable(vo.getExpressCompanyCode()).orElse(""))
                    .setExpressCompanyName(Optional.ofNullable(vo.getExpressCompanyName()).orElse(""))
                    .setExpressBillNo(Optional.ofNullable(vo.getExpressBillNo()).orElse(""))
                    .setExpressStatus(Optional.ofNullable(vo.getExpressStatus()).orElse(""))
                    .build();
        }).collect(Collectors.toList());
        return list;
    }
}
