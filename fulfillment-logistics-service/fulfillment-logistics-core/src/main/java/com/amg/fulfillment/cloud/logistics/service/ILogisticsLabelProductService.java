package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsLabelProductDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsLabelProductItemDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 物流标签商品表 服务类
 * </p>
 *
 * @author zzx
 * @since 2021-08-31
 */
public interface ILogisticsLabelProductService extends IService<LogisticsLabelProductDO> {
      boolean updateCommodityLabel(List<LogisticsLabelProductDO> logisticsLabelProductDOS, List<LogisticsLabelProductItemDO> logisticsLabelProductItemDOS, List<Long> longs);
}
