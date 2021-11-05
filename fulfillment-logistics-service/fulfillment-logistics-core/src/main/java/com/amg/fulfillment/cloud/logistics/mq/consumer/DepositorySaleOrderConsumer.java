package com.amg.fulfillment.cloud.logistics.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.SaleOrderMsgDto;
import com.amg.fulfillment.cloud.logistics.entity.DepositorySaleOrderDO;
import com.amg.fulfillment.cloud.logistics.model.req.DepositorySaleOrderReq;
import com.amg.fulfillment.cloud.logistics.model.req.PredictionSaleReq;
import com.amg.fulfillment.cloud.logistics.service.IDepositorySaleOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-05-13-15:03
 */

@Slf4j
@Component
//@RocketMQMessageListener(topic = "${mq.order.order-predictWarehouseSaleOrder-logistic.topic}",
//        selectorExpression = "*",
//        consumerGroup = "${mq.order.order-predictWarehouseSaleOrder-logistic.topic}")
public class DepositorySaleOrderConsumer implements RocketMQListener<SaleOrderMsgDto> {

    @Autowired
    private IDepositorySaleOrderService depositorySaleOrderService;

    @Override
    public void onMessage(SaleOrderMsgDto message) {
        log.info("监听到推送销售订单到仓库消息，{}", message);
        processMessage(message);
    }

    private void processMessage(SaleOrderMsgDto saleOrderMsgDto) {
        if (StringUtils.isBlank(saleOrderMsgDto.getSaleOrderId()) || Objects.isNull(saleOrderMsgDto.getProducts()) || saleOrderMsgDto.getProducts().size() < 1) {
            return;
        }
        String originalContent = JSON.toJSONString(saleOrderMsgDto.getProducts());
        // 将消息以采购id维度转成以sku维度
        Map<String, List<SaleOrderMsgDto.Product>> skuGroups = saleOrderMsgDto.getProducts().stream().collect(Collectors.groupingBy(SaleOrderMsgDto.Product::getSku));
        HashMap<String, SaleOrderMsgDto.Product> realMap = new HashMap<>();
        for (Map.Entry<String, List<SaleOrderMsgDto.Product>> entry : skuGroups.entrySet()) {
            // 统计数量
            entry.getValue().get(0).setQuantity(entry.getValue().size());
            // 转化G ->KG (历史原因)
            entry.getValue().get(0).setWeightInKg(!Objects.isNull(entry.getValue().get(0).getWeightInKg())
                    ? new BigDecimal(entry.getValue().get(0).getWeightInKg().toString()).divide(new BigDecimal(1000)).doubleValue()
                    : Constant.DEFAULT_SALEORDER_PRODUCT_WEIGHT_KG);
            realMap.putIfAbsent(entry.getKey(), entry.getValue().get(0));
            // 对没有商品名称的赋默认值
            entry.getValue().get(0).setProductName(StringUtils.isNotBlank(entry.getValue().get(0).getProductName()) ? entry.getValue().get(0).getProductName() : Constant.DEFAULT_SALEORDER_PRODUCT_NAME_CN);
        }
        saleOrderMsgDto.setProducts(new ArrayList<>(realMap.values()));
        //将字符串属性转成仓库需要的数据格式
        saleOrderMsgDto.getProducts().stream().forEach(v -> v.setVariants(v.tranVariantstrToObj(v.getVariantStr())));
        String jsonString = JSON.toJSONString(saleOrderMsgDto);
        DepositorySaleOrderReq depositorySaleOrderReq = JSON.parseObject(jsonString, DepositorySaleOrderReq.class);
        // 只去第一张图片
        depositorySaleOrderReq.getProducts().stream().forEach(item -> {
            item.setImageUrls(!Objects.isNull(item.getImageUrls()) ? Collections.singletonList(item.getImageUrls().get(0)) : Collections.emptyList());
        });
        depositorySaleOrderReq.setOriginalContent(originalContent);
        DepositorySaleOrderDO depositorySaleOrderDO = new DepositorySaleOrderDO();
        depositorySaleOrderDO.setSaleOrder(depositorySaleOrderReq.getSaleOrderId());
        depositorySaleOrderDO.setDepositoryCode(Optional.ofNullable(depositorySaleOrderReq.getDepositoryCode()).orElse(Constant.DEPOSITORY_WB));
        depositorySaleOrderDO.setDepositoryName(Optional.ofNullable(depositorySaleOrderReq.getDepositoryName()).orElse(Constant.DEPOSITORY_WB_NAME));

        LambdaQueryWrapper<DepositorySaleOrderDO> lambdaQueryWrapper = Wrappers.<DepositorySaleOrderDO>lambdaQuery()
                .eq(DepositorySaleOrderDO::getSaleOrder, depositorySaleOrderDO.getSaleOrder())
                .eq(DepositorySaleOrderDO::getDepositoryCode, depositorySaleOrderDO.getDepositoryCode());
        DepositorySaleOrderDO saleOrderDO = depositorySaleOrderService.getOne(lambdaQueryWrapper);

        if (Objects.isNull(saleOrderDO)) {
            Long aLong = depositorySaleOrderService.addSale(depositorySaleOrderReq);
            PredictionSaleReq predictionSaleReq = new PredictionSaleReq();
            predictionSaleReq.setIds(Arrays.asList(aLong));
            depositorySaleOrderService.prediction(predictionSaleReq);
        } else if (!Objects.isNull(saleOrderDO) && saleOrderDO.getSaleNotice()==Constant.NO_0) {
            PredictionSaleReq predictionSaleReq = new PredictionSaleReq();
            predictionSaleReq.setIds(Arrays.asList(saleOrderDO.getId()));
            depositorySaleOrderService.prediction(predictionSaleReq);
        }
    }

}
