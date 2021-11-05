package com.amg.fulfillment.cloud.logistics.mapper;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsLabelProductDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Seraph on 2021/5/24
 */
public interface LogisticsLabelProductMapper extends BaseMapper<LogisticsLabelProductDO> {

    List<Map<String, Object>> queryLogisticsLabelProductListBySkuList(List<String> list);
}
