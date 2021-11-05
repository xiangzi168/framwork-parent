package com.amg.fulfillment.cloud.logistics.mapper;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsLabelProductItemDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * Created by Seraph on 2021/5/26
 */
public interface LogisticsLabelProductItemMapper extends BaseMapper<LogisticsLabelProductItemDO> {

    int insertLogisticsLabelProductItemBatch(List<LogisticsLabelProductItemDO> list);

    int deleteLogisticsLabelProductItemByProductId(Long productId);

    List<LogisticsLabelProductItemDO> queryLogisticsLabelProductItemListByProductIdList(List<Long> productId);
}
