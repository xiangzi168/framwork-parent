package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageMergeStatusDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsStatusDO;
import com.amg.fulfillment.cloud.logistics.model.bo.OrderLogisticsTracingModel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzx
 * @since 2021-07-29
 */
public interface ILogisticsStatusService extends IService<LogisticsStatusDO> {
    /**
     * 插入或者更新一条物流记录根据运单号
     * @param orderLogisticsTracingModel  alibaba 订阅消息封装对象
     * @return 更新或插入是否执行
     */
    public Boolean saveOrUpdateBasedOnMailNo(OrderLogisticsTracingModel orderLogisticsTracingModel);
    /**
     * 更新LogisticsStatusDO对象中状态字段
     * @param one 数据库实体对象
     * @param orderLogisticsTracingModel alibaba 订阅消息封装对象
     * @return 组装好的实体对象
     */
    public LogisticsStatusDO UpdateLogisticsStatusDO(LogisticsStatusDO one,OrderLogisticsTracingModel orderLogisticsTracingModel);

    /**
     * 根据运单号集合查询 运单号集合的数据
     * @param mailNoList 运单号集合
     * @return 运单号的集合数据
     */
    public List<LogisticsStatusDO> selectLogisticsStatusDOBasedOnMailNo(List<String> mailNoList);

    /**
     * 从物流状态对象中获取最新的物流状态
     * @param one 物流状态对象
     * @return 当前物流状态
     */
    public Integer getLatestLogisticsStatus(LogisticsPackageMergeStatusDO one);
}
