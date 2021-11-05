package com.amg.fulfillment.cloud.logistics.mapper;

import com.amg.fulfillment.cloud.logistics.entity.TestCurrentDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzx
 * @since 2021-10-19
 */
public interface TestCurrentMapper extends BaseMapper<TestCurrentDO> {

    TestCurrentDO selectByIdForUpdate(@Param("id") int id);
}
