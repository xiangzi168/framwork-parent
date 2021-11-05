package com.amg.fulfillment.cloud.logistics.mapper;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsRulesFreightDO;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsRulesFreightReq;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Seraph on 2021/5/25
 */
public interface LogisticsRulesFreightMapper extends BaseMapper<LogisticsRulesFreightDO> {

    List<LogisticsRulesFreightDO> queryLogisticsRulesFreightList(IPage<LogisticsRulesFreightDO> page, @Param("req") LogisticsRulesFreightReq logisticsRulesFreightReq);

    LogisticsRulesFreightDO queryLogisticsRulesFreightById(Long id);

    int insertLogisticsRulesFreightBatch(List<LogisticsRulesFreightDO> list);
}
