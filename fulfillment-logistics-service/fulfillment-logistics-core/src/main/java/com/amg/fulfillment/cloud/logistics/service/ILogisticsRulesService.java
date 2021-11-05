package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsRulesDO;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsRuleAddReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsRuleSearchReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsRuleDetailVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsRuleVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 物流规则表 服务类
 * </p>
 *
 * @author zzx
 * @since 2021-05-24
 */
public interface ILogisticsRulesService extends IService<LogisticsRulesDO> {

    Integer save(LogisticsRuleAddReq logisticsRuleAddReq);

    Page<LogisticsRuleVO> list(LogisticsRuleSearchReq logisticsRuleSearchReq);

    Boolean update(LogisticsRuleAddReq logisticsRuleAddReq);

    LogisticsRuleDetailVO getLogisticsRuleDetailById(Long id);
}
