package com.amg.fulfillment.cloud.logistics.mapper;

import cn.hutool.core.lang.Pair;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.logistics.model.req.DeliveryPackageReq;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seraph on 2021/5/11
 */
public interface LogisticsPackageMapper extends BaseMapper<LogisticsPackageDO> {

    List<LogisticsPackageDO> queryLogisticsPackageList(IPage<LogisticsPackageDO> page, @Param("req") DeliveryPackageReq deliveryPackageReq);

    int insertLogisticsPackageBatch(List<LogisticsPackageDO> list);

    List<Pair<String, Long>> selectAboardStatistics(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate,@Param("statusList") ArrayList<String> statusList,@Param("noticeUser")Integer noticeUser);
}
