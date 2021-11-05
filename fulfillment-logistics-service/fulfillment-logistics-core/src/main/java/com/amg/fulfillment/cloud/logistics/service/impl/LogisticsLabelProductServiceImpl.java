package com.amg.fulfillment.cloud.logistics.service.impl;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsLabelProductDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsLabelProductItemDO;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsLabelProductMapper;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsLabelProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 物流标签商品表 服务实现类
 * </p>
 *
 * @author zzx
 * @since 2021-08-31
 */
@Service
public class LogisticsLabelProductServiceImpl extends ServiceImpl<LogisticsLabelProductMapper, LogisticsLabelProductDO> implements ILogisticsLabelProductService {

    @Autowired
    private LogisticsLabelProductServiceImpl logisticsLabelProductServiceImpl;
    @Autowired
    private LogisticsLabelProductItemServiceImpl logisticsLabelProductItemServiceImpl;

    @Override
    @Transactional
    public boolean updateCommodityLabel(List<LogisticsLabelProductDO> logisticsLabelProductDOS, List<LogisticsLabelProductItemDO> logisticsLabelProductItemDOS, List<Long> longs) {

        if (longs != null && longs.size() > 0) {
            LambdaQueryWrapper<LogisticsLabelProductDO> logisticsLabelProductDOLambdaQueryWrapper = Wrappers.lambdaQuery();
            logisticsLabelProductDOLambdaQueryWrapper.in(LogisticsLabelProductDO::getSku, longs);
            logisticsLabelProductServiceImpl.remove(logisticsLabelProductDOLambdaQueryWrapper);
        }
        if (logisticsLabelProductDOS != null && logisticsLabelProductDOS.size() > 0) {
            logisticsLabelProductServiceImpl.saveBatch(logisticsLabelProductDOS);
        }
        if (logisticsLabelProductItemDOS != null && logisticsLabelProductItemDOS.size() > 0) {
            logisticsLabelProductItemServiceImpl.saveBatch(logisticsLabelProductItemDOS);
        }
        return true;
    }
}
