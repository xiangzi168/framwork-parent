package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.model.req.MetadataReq;
import com.amg.fulfillment.cloud.logistics.model.vo.MetadataVO;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Seraph on 2021/5/26
 */
public interface IMetadataService {

    List<MetadataVO> search(MetadataReq metadataReq);

    MetadataVO detail(String categoryCode);

    List<MetadataVO> parentList(String categoryCode);
}
