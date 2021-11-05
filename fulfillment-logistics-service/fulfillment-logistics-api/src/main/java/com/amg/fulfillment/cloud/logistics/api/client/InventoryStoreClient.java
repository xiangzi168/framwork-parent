package com.amg.fulfillment.cloud.logistics.api.client;

import com.amg.fulfillment.cloud.logistics.api.grpc.InventoryStoreSrvGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.InventoryStoreGTO;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * @author Tom
 * @date 2021-05-18-11:52
 */
@Service
public class InventoryStoreClient {

    @GrpcClient(value = "inventory-store")
    private InventoryStoreSrvGrpc.InventoryStoreSrvBlockingStub inventoryStoreSrvBlockingStub;

    public InventoryStoreGTO.GetSPUSizeTableReply getSpuSizeTable(InventoryStoreGTO.GetSPUSizeTableReq request) {
         return inventoryStoreSrvBlockingStub.getSPUSizeTable(request);
    }

    public InventoryStoreGTO.GetCateIDSizeTableReply GetCateIdSizeTable(InventoryStoreGTO.GetCateIDSizeTableReq request) {
        return  inventoryStoreSrvBlockingStub.getCateIDSizeTable(request);
    }

}
