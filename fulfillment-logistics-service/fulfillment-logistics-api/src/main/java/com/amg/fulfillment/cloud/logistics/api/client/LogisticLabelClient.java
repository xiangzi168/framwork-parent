package com.amg.fulfillment.cloud.logistics.api.client;

import com.amg.fulfillment.cloud.logistics.api.grpc.*;
import com.amg.fulfillment.cloud.logistics.api.proto.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;


/**
 * 物流标签grpcClient
 * @author qiuhao
 * @date 2021-08-30-17:22
 */
@Service
public class LogisticLabelClient {
   @GrpcClient(value = "fulfillment-logistics-service")
   CommodityLabelGrpc.CommodityLabelBlockingStub commodityLabelBlockingStub;
   public CommodityLabelGto.CommodityLabelResult getCommodityLabel(CommodityLabelGto.SkuListRequest request) {
      return commodityLabelBlockingStub.getCommodityLabel(request);
   }
}
