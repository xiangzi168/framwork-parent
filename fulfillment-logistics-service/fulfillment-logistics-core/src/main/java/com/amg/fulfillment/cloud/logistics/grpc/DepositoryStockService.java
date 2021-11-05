package com.amg.fulfillment.cloud.logistics.grpc;

import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.grpc.DepositoryStockQueryGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO;

import com.amg.fulfillment.cloud.logistics.dto.depository.DepositorySearchDto;
import com.amg.fulfillment.cloud.logistics.dto.depository.InventoryDto;
import com.amg.fulfillment.cloud.logistics.service.IDepositoryService;
import com.google.protobuf.ProtocolStringList;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@GrpcService
public class DepositoryStockService extends DepositoryStockQueryGrpc.DepositoryStockQueryImplBase {

    @Autowired
    private IDepositoryService depositoryServiceImpl;

    @Override
    public List<DepositoryStockGTO.DepositoryStockDetailResponse> querySaleOrderStauts(DepositoryStockGTO.DepositoryStockDetailRequest request, StreamObserver<DepositoryStockGTO.DepositoryStockDetailResponseResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用查询库数量请求参数转成JSON是：{}", requestStr);
        ProtocolStringList skusList = request.getSkusList();
        ArrayList<Object> resultList = new ArrayList<>();
        skusList.stream().forEach(item ->{
            DepositorySearchDto depositorySearchDto = new DepositorySearchDto();
            depositorySearchDto.setSku(item);
            List<InventoryDto> depositoryCount = depositoryServiceImpl.getDepositoryCount(depositorySearchDto);
            resultList.addAll(depositoryCount);
        });
        List<DepositoryStockGTO.DepositoryStockDetailResponse> messages = GrpcJsonFormatUtils.javaToGrpc(resultList, DepositoryStockGTO.DepositoryStockDetailResponse.class);
        log.info("GRPC--远程调用查询库数量返回体转成JSON是：{}", requestStr);
        return messages;
    }
}
