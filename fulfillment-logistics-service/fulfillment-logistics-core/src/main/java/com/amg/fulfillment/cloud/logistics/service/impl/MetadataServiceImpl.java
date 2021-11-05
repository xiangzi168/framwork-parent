package com.amg.fulfillment.cloud.logistics.service.impl;

import com.amg.fulfillment.cloud.logistics.model.req.MetadataReq;
import com.amg.fulfillment.cloud.logistics.model.vo.MetadataVO;
import com.amg.fulfillment.cloud.logistics.service.IMetadataService;
import com.amg.fulfillment.cloud.logistics.util.MetadataPlusUtils;
import com.amg.fulfillment.cloud.order.api.proto.MetadatapbMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Seraph on 2021/5/26
 */

@Slf4j
@Service
public class MetadataServiceImpl implements IMetadataService {

    @Autowired
    private MetadataPlusUtils metadataPlusUtils;

    @Override
    public List<MetadataVO> search(MetadataReq metadataReq) {
        List<MetadataVO> metadataVOList = new ArrayList<>();
        List<String> categoryCodeList = metadataReq.getCategoryCodeList();
        Set<String> categoryCodeSet = new HashSet<>(categoryCodeList);      //请求数据的时候去重
        List<MetadatapbMetadata.CategoryMeta> categoryMetaList = Optional.ofNullable(metadataPlusUtils.getCategoryList(categoryCodeSet)).orElse(new ArrayList<>());
        categoryCodeList.forEach(categoryCode -> {
            AtomicReference<MetadatapbMetadata.CategoryMeta> atomicReferenceCategoryMeta = new AtomicReference(null);
            categoryMetaList.forEach(categoryMeta -> {
                if(categoryMeta != null && categoryMeta.getId().equals(categoryCode))
                atomicReferenceCategoryMeta.set(categoryMeta);
            });

            MetadatapbMetadata.CategoryMeta categoryMeta = atomicReferenceCategoryMeta.get();
            MetadataVO metadataVO = new MetadataVO();
            metadataVO.setCategoryCode(categoryCode);
            if(categoryMeta == null)
            {
                metadataVO.setErrorMsg("该类目ID不存在");
            } else
            {
                metadataVO = this.convertCategoryMetaToMetadataVO(categoryCode, categoryMeta);
            }
            metadataVOList.add(metadataVO);

        });
        return metadataVOList;
    }

    @Override
    public MetadataVO detail(String categoryCode) {
        MetadatapbMetadata.CategoryMeta categoryMeta = metadataPlusUtils.getCategory(categoryCode);
        return this.convertCategoryMetaToMetadataVO(categoryCode, categoryMeta);
    }

    @Override
    public List<MetadataVO> parentList(String categoryCode) {

        List<MetadataVO> metadataVOList = new ArrayList<>();
        List<MetadatapbMetadata.CategoryMeta> categoryMetaList = metadataPlusUtils.getCategoryParentList(Collections.singletonList(categoryCode));
        categoryMetaList.forEach(categoryMeta -> {
            MetadataVO metadataVO = this.convertCategoryMetaToMetadataVO(categoryCode, categoryMeta);
            metadataVOList.add(metadataVO);
        });
        return metadataVOList;
    }

    private MetadataVO convertCategoryMetaToMetadataVO(String categoryCode, MetadatapbMetadata.CategoryMeta categoryMeta)
    {
        MetadataVO metadataVO = new MetadataVO();
        metadataVO.setCategoryCode(categoryCode);
        if(categoryMeta != null)
        {
            metadataVO.setCategoryName(categoryMeta.getName());
            metadataVO.setCategoryLevel(String.valueOf(categoryMeta.getLevel()));
        } else
        {
            metadataVO.setErrorMsg("该类目ID不存在");
        }
        return metadataVO;
    }

}
