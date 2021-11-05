package com.amg.fulfillment.cloud.logistics.manager;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.GoodSku;
import com.amg.fulfillment.cloud.logistics.dto.depository.*;

import java.util.List;

/**
 * @author Tom
 * @date 2021/4/12  10:03
 * 仓库抽象类
 */
public interface IDepositoryManager {

    /**
     * @author Tom
     * @date 2021/4/13  12:03
     * 得的仓库标识
     */
    String getDepositorySign();

    /**
     * @author Tom
     * @date 2021/4/12  17:13
     * 仓库采购订单
     */
    PurchaseResultDto addPurchaseOrder(DepositoryPurchaseOrderDto depositoryPurchaseOrderDto);

     /**
     * @author Tom
     * @date 2021/4/12  17:13
     *  推送出库订单
     */
     OutDepositoryResultDto addOutDepositoryOrder(OutDepositoryOrderDto outDepositoryOrderDto);


    /**
     * @author Tom
     * @date 2021/4/13  12:03
     * 取消出库订单
     */
    CancelDepositoryOrderResponse cancelOutDepositoryOrder(String dispatchOrderId);


     /**
     * @author Tom
     * @date 2021/4/13  12:03
     * 查询出库订单
     */
     OutDepositoryOrderResultDto getOutDepositoryOrder(OutDepositoryOrderDto outDepositoryOrderDto);


    /**
     * @author Tom
     * @date 2021/4/13  12:03
     * 查询产品库存
     */
    List<InventoryDto> getDepositoryCount(DepositorySearchDto depositorySearchDto);

    /**
     * @author Tom
     * @date 2021/4/13  12:03
     * 推送产品资料接
     */
    OutDepositoryResultDto addMaterialToDepository(GoodSku goodSku);

    /**
     * @author Tom
     * @date 2021/4/13  12:03
     * 查询产品资料接
     */
    GoodSku getMaterialFromDepository(GoodSku goodSku);

    /**
     * @author Tom
     * @date 2021/4/29  19:00
     * 推送销售订单到仓库
     */
    SaleOrderResultDto addSaleOrder(DepositorySaleOrderDto depositorySaleOrderDto);
}
