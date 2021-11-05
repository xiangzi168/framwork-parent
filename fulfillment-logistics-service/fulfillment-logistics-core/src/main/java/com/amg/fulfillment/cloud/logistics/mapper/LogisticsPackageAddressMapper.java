package com.amg.fulfillment.cloud.logistics.mapper;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageAddressDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * Created by Seraph on 2021/5/27
 */
public interface LogisticsPackageAddressMapper extends BaseMapper<LogisticsPackageAddressDO> {

    int insertLogisticsPackageAddressBatch(List<LogisticsPackageAddressDO> list);

}
