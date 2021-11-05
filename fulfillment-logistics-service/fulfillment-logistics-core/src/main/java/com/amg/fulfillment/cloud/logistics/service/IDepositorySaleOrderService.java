package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.entity.DepositorySaleOrderDO;
import com.amg.fulfillment.cloud.logistics.grpc.SaleOrderAssignmentPurchaseIdService;
import com.amg.fulfillment.cloud.logistics.model.req.DepositorySaleOrderReq;
import com.amg.fulfillment.cloud.logistics.model.req.DepositorySaleSearchReq;
import com.amg.fulfillment.cloud.logistics.model.req.PredictionSaleReq;
import com.amg.fulfillment.cloud.logistics.model.vo.DepositorySaleOrderPredictionVO;
import com.amg.fulfillment.cloud.logistics.model.vo.DepositorySaleOrderVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzx
 * @since 2021-05-14
 */
public interface IDepositorySaleOrderService extends IService<DepositorySaleOrderDO> {

    Long addSale(DepositorySaleOrderReq saleOrderReq);

    DepositorySaleOrderPredictionVO prediction(PredictionSaleReq predictionSaleReq);

    Page<DepositorySaleOrderVO> list(DepositorySaleSearchReq depositorySaleSearchReq);

    Long updateSale(DepositorySaleOrderReq saleOrderReq);

    DepositorySaleOrderVO getSaleDetail(Long id);

    List<SaleOrderAssignmentPurchaseIdService.SaleOrderResponse> assignmentPurchaseId(List<SaleOrderAssignmentPurchaseIdService.SaleOrderRequest> saleOrderRequestList);
}
