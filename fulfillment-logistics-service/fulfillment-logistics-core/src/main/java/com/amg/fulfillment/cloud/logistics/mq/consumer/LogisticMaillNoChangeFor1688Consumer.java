package com.amg.fulfillment.cloud.logistics.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageDO;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchasePackageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Tom
 * @date 2021-05-22-11:34
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.message.message-1688logisticsMailNoChange-logistics.topic}"
        , selectorExpression = "*"
        , consumerGroup = "${mq.message.message-1688logisticsMailNoChange-logistics.topic}")
public class LogisticMaillNoChangeFor1688Consumer implements RocketMQListener<String> {

    @Autowired
    private LogisticsPurchasePackageMapper logisticsPurchasePackageMapper;

    @Override
    public void onMessage(String message) {
        log.info("监听到1688物流单号发生变化消息：{}", message);
        purchase(message);
    }

    /**
     * {
     * 	"MailNoChangeModel": {
     * 		"logisticsId": "123456",
     * 		"oldCpCode": "1234",
     * 		"newCpCode": "12345",
     * 		"oldMailNo": "123",
     * 		"newMailNo": "1234",
     * 		"eventTime": "",
     * 		"orderLogsItems": {
     * 			"orderId": 12345,
     * 			"orderEntryId": 12345678
     *      }
     *   }
     * }
     */
    public void purchase(String message) {
        JSONObject jsonObject = JSON.parseObject(message);
        String oldCpCode = jsonObject.get("oldCpCode").toString();
        String newCpCode = jsonObject.get("newCpCode").toString();
        String oldMailNo = jsonObject.get("oldMailNo").toString();
        String newMailNo = jsonObject.get("newMailNo").toString();
        LambdaQueryWrapper<LogisticsPurchasePackageDO> queryWrapper = Wrappers.<LogisticsPurchasePackageDO>lambdaQuery()
                .eq(LogisticsPurchasePackageDO::getExpressCode, oldCpCode)
                .eq(LogisticsPurchasePackageDO::getExpressBillNo, oldMailNo);
        LogisticsPurchasePackageDO logisticsPurchasePackageDO = logisticsPurchasePackageMapper.selectOne(queryWrapper);
        if (Objects.isNull(logisticsPurchasePackageDO)) {
            log.error("监听到1688物流单号发生变化消息无查询到消息：{}", message);
            return;
        }
        LogisticsPurchasePackageDO logisticsPurchasePackage = new LogisticsPurchasePackageDO();
        logisticsPurchasePackage.setId(logisticsPurchasePackage.getId());
        logisticsPurchasePackage.setExpressCompanyCode(newCpCode);
        logisticsPurchasePackage.setExpressBillNo(newMailNo);
        logisticsPurchasePackage.setOldExpressBillNo(oldMailNo);
        logisticsPurchasePackageMapper.updateById(logisticsPurchasePackage);
    }

}
