package com.amg.fulfillment.cloud.logistics.mapper;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsLabelCategoryDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Seraph on 2021/5/24
 */
public interface LogisticsLabelCategoryMapper extends BaseMapper<LogisticsLabelCategoryDO> {

    int insertLogisticsLabelCategoryBatch(List<LogisticsLabelCategoryDO> list);

    int updateLogisticsLabelCategoryBatch(List<LogisticsLabelCategoryDO> list);

    List<Map<String, Object>> queryLogisticsLabelCategoryList(List<String> list);
}
