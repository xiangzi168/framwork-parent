package com.amg.fulfillment.cloud.logistics.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.amg.framework.boot.utils.id.SnowflakeIdUtils;
import com.amg.fulfillment.cloud.logistics.api.enumeration.LogisticsStatusFor1688Enum;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageMergeStatusDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsStatusDO;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsStatusMapper;
import com.amg.fulfillment.cloud.logistics.model.bo.OrderLogisticsTracingModel;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsStatusService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzx
 * @since 2021-07-29
 */
@Slf4j
@Service
public class LogisticsStatusServiceImpl extends ServiceImpl<LogisticsStatusMapper, LogisticsStatusDO> implements ILogisticsStatusService {
    @Override
    public Boolean saveOrUpdateBasedOnMailNo(OrderLogisticsTracingModel orderLogisticsTracingModel) {
        boolean b = false;
        if (orderLogisticsTracingModel != null) {
            String logisticsId = orderLogisticsTracingModel.getLogisticsId();
            String mailNo = orderLogisticsTracingModel.getMailNo();
            Date changeTime = orderLogisticsTracingModel.getChangeTime();
            String statusChanged = orderLogisticsTracingModel.getStatusChanged();
            //关键字段不能为空
            if (StringUtils.isNotEmpty(logisticsId) && StringUtils.isNotEmpty(mailNo) && changeTime != null && StringUtils.isNotEmpty(statusChanged)) {
                LambdaQueryWrapper<LogisticsStatusDO> logisticsStatusDOLambdaQueryWrapper = Wrappers.lambdaQuery();
                logisticsStatusDOLambdaQueryWrapper.eq(LogisticsStatusDO::getMailNo, mailNo);
                //查询logistics_id是否存在表中
                LogisticsStatusDO one = getOne(logisticsStatusDOLambdaQueryWrapper);
                LogisticsStatusDO logisticsStatusDO = UpdateLogisticsStatusDO(one, orderLogisticsTracingModel);
                try {
                    b = saveOrUpdate(logisticsStatusDO);
                } catch (Exception e) {
                    if (e.getClass() == DuplicateKeyException.class) {
                        log.info("违反唯一约束冲突：" + JSONObject.toJSONString(logisticsStatusDO));
                    }
                    throw e;
                }
            } else {
                //输出必要信息，提示关键字段不存在
                log.info("更新或插入物流状态信息失败：参数中缺少必要字段" + JSONObject.toJSONString(orderLogisticsTracingModel));
            }
        } else {
            //输出必要信息，提示关键字段不存在
            log.info("更新或插入物流状态信息失败：参数中orderLogisticsTracingModel对象为空");
        }
        return b;
    }

    @Override
    public LogisticsStatusDO UpdateLogisticsStatusDO(LogisticsStatusDO one, OrderLogisticsTracingModel orderLogisticsTracingModel) {
//更新字段
        if (orderLogisticsTracingModel != null && orderLogisticsTracingModel.getStatusChanged() != null) {
            if (one == null) {
                one = new LogisticsStatusDO();
                one.setId(SnowflakeIdUtils.getId());
                one.setMailNo(orderLogisticsTracingModel.getMailNo());
                one.setLogisticsId(orderLogisticsTracingModel.getLogisticsId());
            }
            String statusChanged1 = orderLogisticsTracingModel.getStatusChanged();
                              /*  CONSIGN("CONSIGN", "发货"),
                                        ACCEPT("ACCEPT", "揽收"),
                                        TRANSPORT("TRANSPORT", "运输"),
                                        DELIVERING("DELIVERING", "派送"),
                                        SIGN("SIGN", "签收");*/
            //如果发货状态  发货状态更新
            if (statusChanged1.equals(LogisticsStatusFor1688Enum.CONSIGN.getType())) {
                one.setConsign(true);
                one.setConsignTime(orderLogisticsTracingModel.getChangeTime());
                if ((one.getSign() == null || !one.getSign()) && (one.getDelivering() == null || !one.getDelivering()) && (one.getTransport() == null || !one.getTransport()) && (one.getAccept() == null || !one.getAccept())) {
                    one.setLogisticsStatus(LogisticsStatusFor1688Enum.CONSIGN.getCode());
                }
            }
            //如果揽收状态  揽收状态更新
            if (statusChanged1.equals(LogisticsStatusFor1688Enum.ACCEPT.getType())) {
                one.setAccept(true);
                one.setAcceptTime(orderLogisticsTracingModel.getChangeTime());
                if ((one.getSign() == null || !one.getSign()) && (one.getDelivering() == null || !one.getDelivering()) && (one.getTransport() == null || !one.getTransport())) {
                    one.setLogisticsStatus(LogisticsStatusFor1688Enum.ACCEPT.getCode());
                }
            }
            //如果运输状态  运输状态更新
            if (statusChanged1.equals(LogisticsStatusFor1688Enum.TRANSPORT.getType())) {
                one.setTransport(true);
                one.setTransportTime(orderLogisticsTracingModel.getChangeTime());
                if ((one.getSign() == null || !one.getSign()) && (one.getDelivering() == null || !one.getDelivering())) {
                    one.setLogisticsStatus(LogisticsStatusFor1688Enum.TRANSPORT.getCode());
                }
            }
            //如果派送状态  派送状态更新
            if (statusChanged1.equals(LogisticsStatusFor1688Enum.DELIVERING.getType())) {
                one.setDelivering(true);
                one.setDeliveringTime(orderLogisticsTracingModel.getChangeTime());
                if (one.getSign() == null || !one.getSign()) {
                    one.setLogisticsStatus(LogisticsStatusFor1688Enum.DELIVERING.getCode());
                }
            }
            //如果签收状态  签收状态更新
            if (statusChanged1.equals(LogisticsStatusFor1688Enum.SIGN.getType())) {
                one.setSign(true);
                one.setSignTime(orderLogisticsTracingModel.getChangeTime());
                one.setLogisticsStatus(LogisticsStatusFor1688Enum.SIGN.getCode());
            }
        }
        return one;
    }

    @Override
    public List<LogisticsStatusDO> selectLogisticsStatusDOBasedOnMailNo(List<String> mailNoList) {
        if (mailNoList != null && mailNoList.size() > 0) {
            LambdaQueryWrapper<LogisticsStatusDO> logisticsStatusDOLambdaQueryWrapper = Wrappers.lambdaQuery();
            logisticsStatusDOLambdaQueryWrapper.in(LogisticsStatusDO::getMailNo, mailNoList);
            List<LogisticsStatusDO> list = list(logisticsStatusDOLambdaQueryWrapper);
            return list;
        } else {
            return null;
        }
    }

    @Override
    public Integer getLatestLogisticsStatus(LogisticsPackageMergeStatusDO logisticsPackageMergeStatusDO) {
        if (logisticsPackageMergeStatusDO != null) {
            if (logisticsPackageMergeStatusDO.getSign() != null && logisticsPackageMergeStatusDO.getSign()) {
                return LogisticsStatusFor1688Enum.SIGN.getCode();
            }
            if (logisticsPackageMergeStatusDO.getDelivering() != null && logisticsPackageMergeStatusDO.getDelivering()) {
                return LogisticsStatusFor1688Enum.DELIVERING.getCode();
            }
            if (logisticsPackageMergeStatusDO.getTransport() != null && logisticsPackageMergeStatusDO.getTransport()) {
                return LogisticsStatusFor1688Enum.TRANSPORT.getCode();
            }
            if (logisticsPackageMergeStatusDO.getAccept() != null && logisticsPackageMergeStatusDO.getAccept()) {
                return LogisticsStatusFor1688Enum.ACCEPT.getCode();
            }
            if (logisticsPackageMergeStatusDO.getConsign() != null && logisticsPackageMergeStatusDO.getConsign()) {
                return LogisticsStatusFor1688Enum.CONSIGN.getCode();
            }
        }
        return null;
    }
}
