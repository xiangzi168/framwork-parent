package com.amg.fulfillment.cloud.logistics.util;

import com.alibaba.fastjson.JSON;
import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.boot.utils.thread.ThreadPoolUtil;
import com.amg.fulfillment.cloud.order.api.client.MetadataClient;
import com.amg.fulfillment.cloud.order.api.proto.MetadatapbMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by Seraph on 2021/5/27
 */

@Slf4j
@Component
public class MetadataPlusUtils {


    @Autowired
    private MetadataClient metadataClient;
//    private static ExecutorService executorService = LogisticsThreadPoolUtil.getInstance();

    public List<MetadatapbMetadata.CategoryMeta> getCategoryList(Set<String> categoryCodeSet) {
        return this.getCategoryList(new ArrayList<>(categoryCodeSet));
    }

    public List<MetadatapbMetadata.CategoryMeta> getCategoryList(List<String> categoryCodeList) {
        ExecutorService executorService = null;
        try {
            executorService = ThreadPoolUtil.getNewInstance((categoryCodeList.size() / 2) + 1, "logistics-metadata");
            CompletionService<MetadatapbMetadata.CategoryMeta> completionService = new ExecutorCompletionService(executorService);
            categoryCodeList.forEach(categoryCode -> {
                completionService.submit(this.getCategoryCallable(categoryCode));
            });
            List<MetadatapbMetadata.CategoryMeta> categoryMetaList = new ArrayList<>();
            for (int i = 0; i < categoryCodeList.size(); i++) {
                Future<MetadatapbMetadata.CategoryMeta> future = completionService.take();
                MetadatapbMetadata.CategoryMeta categoryMeta = future.get();
                if (categoryMeta != null) {
                    categoryMetaList.add(categoryMeta);
                }
            }
            return categoryMetaList;
        } catch (Exception err) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "getCategoryList err", err);
        } finally {
            if (executorService != null)
                executorService.shutdown();
        }
    }


    public List<MetadatapbMetadata.CategoryMeta> getCategoryParentList(List<String> categoryCodeList) {
        List<MetadatapbMetadata.CategoryMeta> categoryMetaList = new ArrayList<>();
        return this.getCategoryParentList(categoryCodeList, categoryMetaList);
    }

    public List<MetadatapbMetadata.CategoryMeta> getCategoryParentList(List<String> categoryCodeList, List<MetadatapbMetadata.CategoryMeta> categoryMetaList) {
        List<MetadatapbMetadata.CategoryMeta> tempCategoryMetaList = getCategoryList(categoryCodeList);
        categoryMetaList.addAll(tempCategoryMetaList);      //首先把查询的数据集合到一起

        List<String> parentCategoryCodeList = new ArrayList<>();
        tempCategoryMetaList.forEach(categoryMeta -> {
            if (categoryMeta.getLevel() != 1) {
                parentCategoryCodeList.add(categoryMeta.getParentId());
            }
        });

        //已经是顶级节点了，直接返回
        if (parentCategoryCodeList.size() == 0) {
            return categoryMetaList;
        }

        //继续执行查询父节点
        return getCategoryParentList(parentCategoryCodeList, categoryMetaList);
    }


    public List<MetadatapbMetadata.CategoryMeta> getCategoryChildrenList(String categoryCode) {
        List<MetadatapbMetadata.CategoryMeta> categoryMetaList = new ArrayList<>();
        this.getCategoryChildrenList(Collections.singletonList(categoryCode), categoryMetaList);
        return categoryMetaList;
    }

    public List<MetadatapbMetadata.CategoryMeta> getCategoryChildrenList(List<String> categoryCodeList, List<MetadatapbMetadata.CategoryMeta> categoryMetaList) {
        ExecutorService executorService = null;
        List<String> tempCategoryCodeList = new ArrayList<>();
        try {
            if (categoryCodeList.size() == 0)        //如果没有子类了，就直接返回
            {
                return categoryMetaList;
            }
            executorService = ThreadPoolUtil.getNewInstance((categoryCodeList.size() / 2) + 1, "logistics-metadata");
            CompletionService<MetadatapbMetadata.GetFrontCategoryChildrenReply> getFrontCategoryChildrenReplyCompletionService = new ExecutorCompletionService(executorService);
            categoryCodeList.forEach(categoryCode -> {
                getFrontCategoryChildrenReplyCompletionService.submit(this.getFrontCategoryChildrenCallable((categoryCode)));
            });


            List<MetadatapbMetadata.GetFrontCategoryChildrenReply> frontCategoryChildrenReplyList = new ArrayList<>();
            for (int i = 0; i < categoryCodeList.size(); i++) {
                Future<MetadatapbMetadata.GetFrontCategoryChildrenReply> future = getFrontCategoryChildrenReplyCompletionService.take();
                MetadatapbMetadata.GetFrontCategoryChildrenReply getFrontCategoryChildrenReply = future.get();
                frontCategoryChildrenReplyList.add(getFrontCategoryChildrenReply);

            }

            frontCategoryChildrenReplyList.forEach(frontCategoryChildrenReply -> {
                List<MetadatapbMetadata.FrontCategoryMeta> frontCategoryMetaList = frontCategoryChildrenReply.getMetaList();
                frontCategoryMetaList.forEach(frontCategoryMeta -> {
                    MetadatapbMetadata.CategoryMeta categoryMeta = MetadatapbMetadata.CategoryMeta.newBuilder()
                            .setId(frontCategoryMeta.getId())
                            .setName(frontCategoryMeta.getName())
                            .setLevel(frontCategoryMeta.getLevel())
                            .setParentId(frontCategoryMeta.getParentId())
                            .build();

                    categoryMetaList.add(categoryMeta);
                    tempCategoryCodeList.add(frontCategoryMeta.getId());
                });
            });
        } catch (Exception err) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "getCategoryList err", err);
        } finally {
            if (executorService != null)
                executorService.shutdown();
        }

        return getCategoryChildrenList(tempCategoryCodeList, categoryMetaList);
    }

    public Callable<MetadatapbMetadata.CategoryMeta> getCategoryCallable(String categoryCode) {
        return () -> this.getCategory(categoryCode);
    }


    public MetadatapbMetadata.CategoryMeta getCategory(String categoryCode) {
        try {
            log.debug("验证线程名称正在执行的名称----》{}", Thread.currentThread().getName());
            return metadataClient.getCategory(categoryCode);
        } catch (Exception err) {
            log.error("GRPC remote getCategory err,categoryCode:【{}】，cause：{}", categoryCode, err);
            return null;
        }
    }

    public Callable<MetadatapbMetadata.GetFrontCategoryChildrenReply> getFrontCategoryChildrenCallable(String categoryCode) {
        return () -> this.getFrontCategoryChildren(categoryCode);
    }

    public MetadatapbMetadata.GetFrontCategoryChildrenReply getFrontCategoryChildren(String categoryCode) {
        log.info("GetFrontCategoryChildrenReply:metaData请求参数：{}", categoryCode);
        MetadatapbMetadata.GetFrontCategoryChildrenRequest request = MetadatapbMetadata.GetFrontCategoryChildrenRequest.newBuilder()
                .setCateId(categoryCode)
                .build();
        MetadatapbMetadata.GetFrontCategoryChildrenReply frontCategoryChildren = metadataClient.getFrontCategoryChildren(request);
        List<MetadatapbMetadata.FrontCategoryMeta> metaList = frontCategoryChildren.getMetaList();
        log.info("GetFrontCategoryChildrenReply:metaData返回参数：{}", JSON.toJSONString(metaList));
        return frontCategoryChildren;
    }
}
