package com.amg.fulfillment.cloud.logistics.mapper;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageItemDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * Created by Seraph on 2021/5/20
 */
public interface LogisticsPackageItemMapper extends BaseMapper<LogisticsPackageItemDO> {

    int insertLogisticsPackageItemBatch(List<LogisticsPackageItemDO> list);
}
