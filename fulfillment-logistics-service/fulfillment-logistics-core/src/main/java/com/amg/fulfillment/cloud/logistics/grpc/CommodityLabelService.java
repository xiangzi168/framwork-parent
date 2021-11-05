package com.amg.fulfillment.cloud.logistics.grpc;

import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.fulfillment.cloud.logistics.api.enumeration.LogisticLabelEnum;
import com.amg.fulfillment.cloud.logistics.api.grpc.CommodityLabelGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsLabelProductVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsLabelVO;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsLabelService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.protobuf.ProtocolStringList;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * @ClassName CommodityLabelService
 * @Description TODO
 * @Author 35112
 * @Date 2021/8/30 18:11
 **/
@GrpcService
public class CommodityLabelService extends CommodityLabelGrpc.CommodityLabelImplBase {
    @Autowired
    private ILogisticsLabelService logisticsLabelService;

    @Override
    public com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelInfoList getCommodityLabel(com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest request,
                                                                                                                    io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult> responseObserver) {

        if (request != null && request.getSkuList() != null && request.getSkuList().size() > 0) {
            ProtocolStringList skuList = request.getSkuList();
            List<LogisticsLabelProductVO> commodityLabel = logisticsLabelService.getCommodityLabel(skuList);
            if (commodityLabel != null) {
                CommodityLabelGto.CommodityLabelInfoList.Builder builder = CommodityLabelGto.CommodityLabelInfoList.newBuilder();
                for (LogisticsLabelProductVO logisticsLabelProductVO : commodityLabel) {
                    List<LogisticsLabelVO> logisticsLabelVOList = logisticsLabelProductVO.getLogisticsLabelVOList();
                    if (logisticsLabelVOList != null && logisticsLabelVOList.size() > 0) {
                        ArrayList<String> integers = new ArrayList<String>();
                        for (LogisticsLabelVO logisticsLabelVO : logisticsLabelVOList) {
                            if (logisticsLabelVO != null && StringUtils.isNotBlank(logisticsLabelVO.getName()) ) {
                                String name = logisticsLabelVO.getName();
                                if (name.equals(LogisticLabelEnum.activity1.getMsg()) || name.equals(LogisticLabelEnum.activity2.getMsg()) || name.equals(LogisticLabelEnum.activity3.getMsg())) {
                                    integers.add(logisticsLabelVO.getName());
                                }
                            }
                        }
                        CommodityLabelGto.CommodityLabelInfo.Builder builder1 = CommodityLabelGto.CommodityLabelInfo.newBuilder().setSku(logisticsLabelProductVO.getSku()).addAllMsg(integers);
                        builder.addCommodityLabelInfo(builder1);
                    }
                }
                CommodityLabelGto.CommodityLabelInfoList build = builder.build();
                return build;
            }
            return null;
        } else {
            new GlobalException("100700", "请求参数为空");
        }
        return null;
    }
}
