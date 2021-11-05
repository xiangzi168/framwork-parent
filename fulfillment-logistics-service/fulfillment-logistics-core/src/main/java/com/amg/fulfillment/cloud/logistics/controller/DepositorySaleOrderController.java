package com.amg.fulfillment.cloud.logistics.controller;


import com.amg.fulfillment.cloud.logistics.entity.DepositorySaleOrderDO;
import com.amg.fulfillment.cloud.logistics.model.req.DepositorySaleOrderReq;
import com.amg.fulfillment.cloud.logistics.model.req.DepositorySaleSearchReq;
import com.amg.fulfillment.cloud.logistics.model.req.PredictionSaleReq;
import com.amg.fulfillment.cloud.logistics.model.vo.DepositorySaleOrderPredictionVO;
import com.amg.fulfillment.cloud.logistics.model.vo.DepositorySaleOrderVO;
import com.amg.fulfillment.cloud.logistics.service.IDepositorySaleOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zzx
 * @since 2021-05-14
 */
@Slf4j
@RestController
@RequestMapping("/depository/saleOrder")
@Api(tags = {"推送仓库订购单接口"})
public class DepositorySaleOrderController {


    @Autowired
    private IDepositorySaleOrderService depositorySaleOrderService;

    /**
     * @author Tom
     * @date 2021/5/14  13:56
     * 查询
     */
    @GetMapping ("list")
    @ApiOperation(value = "查询销售订单列表")
    public Page<DepositorySaleOrderVO> listSaleOrder(DepositorySaleSearchReq depositorySaleSearchReq) {
        log.info("接收到查询销售订单数据是：{}", depositorySaleSearchReq);
        return  depositorySaleOrderService.list(depositorySaleSearchReq);
    }

    /**
     * @author Tom
     * @date 2021/5/14  13:56
     * 添加
     */
    @PostMapping("add")
    @ApiOperation(value = "增加销售订单并预报到仓库")
    public DepositorySaleOrderPredictionVO addSaleOrder(@RequestBody @Validated DepositorySaleOrderReq saleOrderReq) {
        log.info("接收到销售订单数据是：{}", saleOrderReq);
        Long id = depositorySaleOrderService.addSale(saleOrderReq);
        PredictionSaleReq predictionSaleReq = new PredictionSaleReq();
        predictionSaleReq.setIds(Arrays.asList(id));
        return depositorySaleOrderService.prediction(predictionSaleReq);
    }

    /**
     * @author Tom
     * @date 2021/5/14  13:56
     * 修改
     */
    @PostMapping("update")
    @ApiOperation(value = "修改销售订单并预报到仓库")
    public DepositorySaleOrderPredictionVO updateSaleOrder(@RequestBody @Validated DepositorySaleOrderReq saleOrderReq) {
        log.info("接收到销售订单数据是：{}", saleOrderReq);
        Long id = depositorySaleOrderService.updateSale(saleOrderReq);
        PredictionSaleReq predictionSaleReq = new PredictionSaleReq();
        predictionSaleReq.setIds(Arrays.asList(id));
        return depositorySaleOrderService.prediction(predictionSaleReq);
    }

    /**
     * @author Tom
     * @date 2021/5/14  13:56
     * 预报
     */
    @PostMapping("prediction")
    @ApiOperation(value = "预报销售订单")
    public DepositorySaleOrderPredictionVO predictionSaleOrder(@RequestBody PredictionSaleReq predictionSaleReq) {
        log.info("接收到销售订单数据是：{}", predictionSaleReq);
        return depositorySaleOrderService.prediction(predictionSaleReq);
    }


}
