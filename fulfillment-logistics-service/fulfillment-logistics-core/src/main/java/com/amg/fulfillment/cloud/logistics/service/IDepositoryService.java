package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.GoodSku;
import com.amg.fulfillment.cloud.logistics.dto.depository.DepositorySearchDto;
import com.amg.fulfillment.cloud.logistics.dto.depository.InventoryDto;
import com.amg.fulfillment.cloud.logistics.dto.depository.OutDepositoryResultDto;
import com.amg.fulfillment.cloud.logistics.model.vo.DepositoryDataStatisticsVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsDataStatisticsForAEVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsDataStatisticsForAbroadVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsPackageOperationerStatisticsVO;


import java.util.List;

/**
 * @author Tom
 * @date 2021/4/12  10:03
 * 仓库抽象类
 */
public interface IDepositoryService {

    /**
     * @author Tom
     * @date 2021/4/13  12:03
     * 查询产品库存
     */
    List<InventoryDto> getDepositoryCount(DepositorySearchDto depositorySearchDto);

    /**
     * @author Tom
     * @date 2021/4/13  12:03
     * 推送产品资料接
     */
    OutDepositoryResultDto addMaterialToDepository(GoodSku goodSku);

    /**
     * @author Tom
     * @date 2021/4/13  12:03
     * 查询产品资料接
     */
    GoodSku getMaterialFromDepository(GoodSku goodSku);

    /**
     * 获取仓库首页统计
     * @return
     */
    DepositoryDataStatisticsVO getDepositoryDataStatistics();

    /**
     * 获取物流首页国外统计
     * @return
     */
    LogisticsDataStatisticsForAbroadVO getLogisticsAboardStatistics();

    /**
     * 获取物流首页AE统计
     * @return
     */
    LogisticsDataStatisticsForAEVO getLogisticsAEStatistics();

    /**
     * 获取物流首页---今日物流数据统计
     * @return
     */
    LogisticsPackageOperationerStatisticsVO getLogisticsPackageStatistics();
}
