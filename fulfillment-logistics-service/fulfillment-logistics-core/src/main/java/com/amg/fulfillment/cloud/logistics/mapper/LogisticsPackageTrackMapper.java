package com.amg.fulfillment.cloud.logistics.mapper;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageTrackDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Seraph on 2021/5/10
 */
public interface LogisticsPackageTrackMapper extends BaseMapper<LogisticsPackageTrackDO> {

    int deleteLogisticsPackageTrackByPackageId(Long packageId);

    int insertLogisticsPackageTrackBatch(List<LogisticsPackageTrackDO> list);

    Boolean updateDatabase(@Param("sql") String sql);
}
