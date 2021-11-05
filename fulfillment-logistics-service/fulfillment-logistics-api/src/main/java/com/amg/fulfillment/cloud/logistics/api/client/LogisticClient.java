package com.amg.fulfillment.cloud.logistics.api.client;

import com.amg.fulfillment.cloud.logistics.api.grpc.*;
import com.amg.fulfillment.cloud.logistics.api.proto.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;


/**
 * @author Tom
 * @date 2021-04-29-17:22
 */
@Service
public class LogisticClient {

    @GrpcClient(value = "fulfillment-logistics-service")
    ChannelOrderLogisticsGrpc.ChannelOrderLogisticsBlockingStub channelOrderLogisticsBlockingStub;

    @GrpcClient(value = "fulfillment-logistics-service")
    SalesOrderLogisticsGrpc.SalesOrderLogisticsBlockingStub salesOrderLogisticsBlockingStub;

    @GrpcClient(value = "fulfillment-logistics-service")
    LogisticsTrackGrpc.LogisticsTrackBlockingStub logisticsTrackBlockingStub;

    @GrpcClient(value = "fulfillment-logistics-service")
    ManualOutDepositoryGrpc.ManualOutDepositoryBlockingStub manualOutDepositoryBlockingStub;

    @GrpcClient(value = "fulfillment-logistics-service")
    PurchasePackageLogisticsInfoGrpc.PurchasePackageLogisticsInfoBlockingStub purchasePackageLogisticsInfoBlockingStub;

    @GrpcClient(value = "fulfillment-logistics-service")
    LogisticsProviderGrpc.LogisticsProviderBlockingStub logisticsProviderBlockingStub;

    @GrpcClient(value = "fulfillment-logistics-service")
    SaleOrderDetailGrpc.SaleOrderDetailBlockingStub saleOrderDetailBlockingStub;

    @GrpcClient(value = "fulfillment-logistics-service")
    SaleOrderStatusQueryGrpc.SaleOrderStatusQueryBlockingStub saleOrderStatusQueryBlockingStub;

    @GrpcClient(value = "fulfillment-logistics-service")
    DeliveryPackageGrpc.DeliveryPackageBlockingStub deliveryPackageBlockingStub;


    @GrpcClient(value = "fulfillment-logistics-service")
    DepositoryStockQueryGrpc.DepositoryStockQueryBlockingStub depositoryStockQueryBlockingStub;

    @GrpcClient(value = "fulfillment-logistics-service")
    LogisticsTrackDetailGrpc.LogisticsTrackDetailBlockingStub logisticsTrackDetailBlockingStub;

    @GrpcClient(value = "fulfillment-logistics-service")
    SaleOrderAssignmentPurchaseIdGrpc.SaleOrderAssignmentPurchaseIdBlockingStub saleOrderAssignmentPurchaseIdBlockingStub;


    @GrpcClient(value = "fulfillment-logistics-service")
    ChannelOrderForDirectSendGrpc.ChannelOrderForDirectSendBlockingStub channelOrderForDirectSendBlockingStub;

    public ChannelOrderLogisticsGTO.updateChannelOrderIdResult updateChannelOrderId(ChannelOrderLogisticsGTO.updateChannelOrderIdRequest request) {
        return channelOrderLogisticsBlockingStub.updateChannelOrderId(request);

    }
    public ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult getChannelOrderLogistics(ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest request) {
        return channelOrderLogisticsBlockingStub.getChannelOrderLogistics(request);
    }

    public LogisticsTrackGTO.LogisticsTrackResponseResult getLogisticsTrack(LogisticsTrackGTO.LogisticsTrackRequest logisticsTrackRequest) {
        return logisticsTrackBlockingStub.getLogisticsTrack(logisticsTrackRequest);
    }

    public SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult getSalesOrderLogistics(SalesOrderLogisticsGTO.SalesOrderLogisticsRequest request) {

        return salesOrderLogisticsBlockingStub.getSalesOrderLogistics(request);
    }

    public SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult getSalesOrderLogisticsList(SalesOrderLogisticsGTO.SalesOrderLogisticsRequest request) {

        return salesOrderLogisticsBlockingStub.getSalesOrderLogisticsList(request);
    }


    public ManualOutDepositoryGTO.ManualOutDepositoryResponseResult addOutDepository(ManualOutDepositoryGTO.ManualOutDepositoryRequest request) {
        return manualOutDepositoryBlockingStub.addOutDepository(request);
    }

    public ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult getChannerOrderIdByPackageId(ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest request) {
        return channelOrderLogisticsBlockingStub.getChannerOrderIdByPackageId(request);
    }

    public SalesOrderLogisticsGTO.SalesOrderIdResponseResult getSalesOrderIdByPackageId(SalesOrderLogisticsGTO.SalesOrderPackageIdRequest request) {
        return salesOrderLogisticsBlockingStub.getSalesOrderIdByPackageId(request);
    }

    public PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult getPurchasePackageLogisticsInfo(PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest request) {
         return purchasePackageLogisticsInfoBlockingStub.getPurchasePackageLogisticsInfo(request);
    }

    public LogisticsProviderGTO.LogisticsProviderResponseResult getLogisticsProvider(LogisticsProviderGTO.nullRequest request) {
        return logisticsProviderBlockingStub.getLogisticsProvider(request);
    }

    public LogisticsProviderGTO.LogisticsChannelResponseResult getLogisticsChannel(LogisticsProviderGTO.LogisticsRequest request) {
        return logisticsProviderBlockingStub.getLogisticsChannel(request);
    }

    public LogisticsProviderGTO.LogisticsProviderResponseResult getLogisticsRecommendProvider(LogisticsProviderGTO.LogisticsRecommendRequest request) {
        return logisticsProviderBlockingStub.getLogisticsRecommendProvider(request);
    }

    public LogisticsProviderGTO.LogisticsChannelResponseResult getLogisticsRecommendChannel(LogisticsProviderGTO.LogisticsRecommendRequest request) {
        return logisticsProviderBlockingStub.getLogisticsRecommendChannel(request);
    }

   public SaleOrderDetailGTO.SaleOrderResponseResult addSaleOrderDetail(SaleOrderDetailGTO.SaleOrderDetailRequest request){
        return saleOrderDetailBlockingStub.addSaleOrder(request);
    }

    public SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult querySaleOrderStatus(SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request){
        return saleOrderStatusQueryBlockingStub.querySaleOrderStauts(request);
    }

    public SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult querySaleOrderStautsForReport(SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request) {
        return saleOrderStatusQueryBlockingStub.querySaleOrderStautsForReport(request);
    }

    public DeliveryPackageGTO.DeliveryPackageResponseResult pushAeLogisticsOrder(DeliveryPackageGTO.DeliveryPackageRequest request) {
        return deliveryPackageBlockingStub.pushAeLogisticsOrder(request);
    }


    public DepositoryStockGTO.DepositoryStockDetailResponseResult querySaleOrderStauts(DepositoryStockGTO.DepositoryStockDetailRequest request) {
        return depositoryStockQueryBlockingStub.querySaleOrderStauts(request);
    }

     public LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply getLogisticsTrackDetail(LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest request) {
        return logisticsTrackDetailBlockingStub.getLogisticsTrackDetail(request);
    }

    public SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult assignmentPurchaseId(SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest request) {
        return saleOrderAssignmentPurchaseIdBlockingStub.assignmentPurchaseId(request);
    }

    public ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult pushDeliveryPack(ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest request){
        return channelOrderForDirectSendBlockingStub.pushDeliveryPack(request);
    }

    public ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult queryChannelOrderForDirectSend(ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest request){
        return channelOrderForDirectSendBlockingStub.queryChannelOrderForDirectSend(request);
    }

    public ChannelOrderLogisticsGTO.AddPurchaseOrderResult addPurchaseOrder(ChannelOrderLogisticsGTO.AddPurchaseOrderRequest request){
        return channelOrderLogisticsBlockingStub.addPurchaseOrder(request);
    }

    public ChannelOrderLogisticsGTO.UpdatePurchasePackageResult updatePurchasePackage(ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest request){
        return channelOrderLogisticsBlockingStub.updatePurchasePackage(request);
    }

}
