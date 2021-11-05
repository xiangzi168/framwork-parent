package com.amg.fulfillment.cloud.logistics.mapper;


import com.amg.fulfillment.cloud.logistics.entity.LogisticsProductDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * Created by Seraph on 2021/5/13
 */
public interface LogisticsProductMapper extends BaseMapper<LogisticsProductDO> {

    int insertLogisticsProductBatch(List<LogisticsProductDO> logisticsProductDOList);
}
