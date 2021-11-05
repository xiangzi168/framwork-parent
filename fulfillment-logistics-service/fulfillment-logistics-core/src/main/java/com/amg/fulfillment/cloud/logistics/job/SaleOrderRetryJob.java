package com.amg.fulfillment.cloud.logistics.job;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.entity.DepositorySaleOrderDO;
import com.amg.fulfillment.cloud.logistics.mapper.DepositorySaleOrderMapper;
import com.amg.fulfillment.cloud.logistics.model.req.PredictionSaleReq;
import com.amg.fulfillment.cloud.logistics.service.IDepositorySaleOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 重新推送失败的销售订单到仓库
 */
@Slf4j
@JobHandler(value = "saleOrder-retry-job")
@Component
public class SaleOrderRetryJob extends IJobHandler {

    @Autowired
    private IDepositorySaleOrderService depositorySaleOrderService;
    @Autowired
    private DepositorySaleOrderMapper depositorySaleOrderMapper;


    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info("--------------------------------------开始执行saleOrder-retry-job----------------------------------------------");
        LocalDateTime localDateTime3 = LocalDateTime.now().minusMonths(3L);
        LambdaQueryWrapper<DepositorySaleOrderDO> queryWrapper = Wrappers.<DepositorySaleOrderDO>lambdaQuery()
                .eq(DepositorySaleOrderDO::getSaleNotice, Constant.NO_0)
                .between(DepositorySaleOrderDO::getCreateTime, localDateTime3, LocalDateTime.now());
        List<DepositorySaleOrderDO> saleOrderDOS = depositorySaleOrderMapper.selectList(queryWrapper);
        List<Long> list = saleOrderDOS.stream().map(DepositorySaleOrderDO::getId).collect(Collectors.toList());
        if (!Objects.isNull(list) && list.size() > 0) {
            PredictionSaleReq predictionSaleReq = new PredictionSaleReq();
            predictionSaleReq.setIds(list);
            depositorySaleOrderService.prediction(predictionSaleReq);
        }
        log.info("--------------------------------------结束执行saleOrder-retry-job----------------------------------------------");
        return ReturnT.SUCCESS;
    }

}
