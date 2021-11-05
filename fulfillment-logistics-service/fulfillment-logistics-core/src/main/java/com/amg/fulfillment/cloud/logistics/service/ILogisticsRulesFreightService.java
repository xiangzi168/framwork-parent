package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.model.req.ExportExcelReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsRulesFreightAddReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsRulesFreightReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsRulesFreightUpdateReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsRulesFreightVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seraph on 2021/5/25
 */
public interface ILogisticsRulesFreightService {

    Page<LogisticsRulesFreightVO> list(LogisticsRulesFreightReq logisticsRulesFreightReq);

    LogisticsRulesFreightVO detail(Long rulesFreightId);

    Boolean update(LogisticsRulesFreightUpdateReq logisticsRulesFreightUpdateReq);

    Boolean add(LogisticsRulesFreightAddReq logisticsRulesFreightAddReq);

    File exportLogisticsRulesFreightExcel(ExportExcelReq<LogisticsRulesFreightReq> exportExcelReq);

    File importLogisticsRulesFreightExcel(String fileUrl);

    /**
     * 查询是否存在渠道运费
     * @param channelCode 渠道code
     * @return 是否存在渠道运费
     */
    Boolean selectLogisticsRulesFreight(String channelCode, List<String> country);
}
