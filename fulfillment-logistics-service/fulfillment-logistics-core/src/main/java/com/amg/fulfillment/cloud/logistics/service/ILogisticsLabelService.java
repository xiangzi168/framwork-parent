package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.ImportExcelVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsLabelProductVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsLabelVO;
import com.amg.fulfillment.cloud.logistics.model.vo.ProductDetailVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.File;
import java.util.List;

/**
 * Created by Seraph on 2021/5/24
 */
public interface ILogisticsLabelService {
     List<LogisticsLabelProductVO> getCommodityLabel(List<String> sku);

    Page<LogisticsLabelVO> list(LogisticsLabelReq logisticsLabelReq);

    LogisticsLabelVO detail(Long labelId);

    Boolean add(LogisticsLabelAddReq logisticsLabelAddReq);

    Boolean update(LogisticsLabelUpdateReq logisticsLabelUpdateReq);

    Boolean del(LogisticsLabelDelReq logisticsLabelDelReq);

    Page<LogisticsLabelProductVO> productList(LogisticsLabelProductReq logisticsLabelProductReq);

    ProductDetailVO productSearch(Long sku);

    Boolean productAdd(LogisticsLabelProductAddReq logisticsLabelProductAddReq);

    Boolean productUpdate(LogisticsLabelProductUpdateReq logisticsLabelProductUpdateReq);

    Boolean productDel(LogisticsLabelProductDelReq logisticsLabelProductDelReq);

    /**
     * 导入物流标签
     * @param fileUrl 导入URL
     * @return 返回文件
     */
    File importCommodityLabelExcel(String fileUrl);
}
