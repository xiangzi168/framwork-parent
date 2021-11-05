package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.model.req.ExportExcelReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsChannelAddReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsChannelReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsChannelUpdateReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsChannelVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.File;

/**
 * Created by Seraph on 2021/5/24
 */
public interface ILogisticsChannelService {

    Page<LogisticsChannelVO> list(LogisticsChannelReq logisticsChannelReq);

    LogisticsChannelVO detail(Long logisticsChannelId);

     Boolean update(LogisticsChannelUpdateReq logisticsChannelUpdateReq);

     Boolean add(LogisticsChannelAddReq logisticsChannelAddReq);

     Boolean checkExists(LogisticsChannelReq logisticsChannelReq);

     File exportLogisticsChannelExcel(ExportExcelReq<LogisticsChannelReq> exportExcelReq);
}
