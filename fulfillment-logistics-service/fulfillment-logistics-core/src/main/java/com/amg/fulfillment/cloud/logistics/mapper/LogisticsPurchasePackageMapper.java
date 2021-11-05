package com.amg.fulfillment.cloud.logistics.mapper;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageMergeStatusDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageDO;
import com.amg.fulfillment.cloud.logistics.model.req.PurchasePackageReq;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * Created by Seraph on 2021/5/11
 */
public interface LogisticsPurchasePackageMapper extends BaseMapper<LogisticsPurchasePackageDO> {
    List<LogisticsPackageMergeStatusDO> selectLogisticsStatusList(PurchasePackageReq purchasePackageReq);
    List<LogisticsPackageMergeStatusDO> selectLogisticsStatusExportList(PurchasePackageReq purchasePackageReq);
    List<LogisticsPackageMergeStatusDO> selectLogisticsStatusOnIdList(List<String> ids);
    Integer selectLogisticsStatusCount(PurchasePackageReq purchasePackageReq);
    List<LogisticsPurchasePackageDO> selectListByFindInSet(List<String> channelOrderIdList);
}
