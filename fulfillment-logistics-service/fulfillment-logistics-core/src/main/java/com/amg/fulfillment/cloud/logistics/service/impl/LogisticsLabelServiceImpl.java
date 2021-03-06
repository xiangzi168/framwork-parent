package com.amg.fulfillment.cloud.logistics.service.impl;

import com.alibaba.fastjson.JSON;
import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.boot.utils.excel.ExcelUtil;
import com.amg.framework.boot.utils.id.SnowflakeIdUtils;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.enumeration.OperationalTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.util.BeanConvertUtils;
import com.amg.fulfillment.cloud.logistics.api.util.LogisticsCommonUtils;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsLabelCategoryDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsLabelDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsLabelProductDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsLabelProductItemDO;
import com.amg.fulfillment.cloud.logistics.enumeration.BaseLogisticsResponseCodeEnum;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsLabelCategoryMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsLabelMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsLabelProductItemMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsLabelProductMapper;
import com.amg.fulfillment.cloud.logistics.model.excel.CommodityLabelExcel;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.*;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsLabelService;
import com.amg.fulfillment.cloud.logistics.util.StringUtil;
import com.amg.fulfillment.cloud.order.api.client.MetadataClient;
import com.amg.fulfillment.cloud.purchase.api.client.ItemsSrvGrpcClient;
import com.amg.fulfillment.cloud.purchase.api.dto.RpcResult;
import com.amg.fulfillment.cloud.purchase.api.enums.ErrorCodeEnum;
import com.amg.fulfillment.cloud.purchase.api.proto.ItemsGTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Seraph on 2021/5/24
 */
@Slf4j
@Service
public class LogisticsLabelServiceImpl extends ServiceImpl<LogisticsLabelMapper, LogisticsLabelDO> implements ILogisticsLabelService {

    @Autowired
    private MetadataClient metadataClient;
    @Autowired
    private ItemsSrvGrpcClient itemsSrvGrpcClient;
    @Autowired
    private LogisticsLabelMapper logisticsLabelMapper;
    @Autowired
    private LogisticsLabelCategoryMapper logisticsLabelCategoryMapper;
    @Autowired
    private LogisticsLabelProductMapper logisticsLabelProductMapper;
    @Autowired
    private LogisticsLabelProductItemMapper logisticsLabelProductItemMapper;
    @Autowired
    private LogisticsLabelProductServiceImpl logisticsLabelProductServiceImpl;

    @Override
    public Page<LogisticsLabelVO> list(LogisticsLabelReq logisticsLabelReq) {
        LambdaQueryWrapper<LogisticsLabelDO> logisticsLabelDOLambdaQueryWrapper = this.doConditionHandler(logisticsLabelReq);
        Page<LogisticsLabelDO> selectPage = logisticsLabelMapper.selectPage(new Page(logisticsLabelReq.getPage(), logisticsLabelReq.getRow()), logisticsLabelDOLambdaQueryWrapper);
        List<LogisticsLabelDO> logisticsLabelDOList = selectPage.getRecords();
        List<Long> logisticsLabelIdList = logisticsLabelDOList.stream().map(LogisticsLabelDO::getId).collect(Collectors.toList());
        Map<Long, List<LogisticsLabelCategoryDO>> logisticsLabelCategoryDOMap = new HashMap<>();
        if (logisticsLabelIdList.size() != 0) {
            //????????????????????????
            LambdaQueryWrapper<LogisticsLabelCategoryDO> logisticsLabelCategoryDOLambdaQueryWrapper = Wrappers.<LogisticsLabelCategoryDO>lambdaQuery()
                    .in(LogisticsLabelCategoryDO::getLabelId, logisticsLabelIdList);
            List<LogisticsLabelCategoryDO> logisticsLabelCategoryDOList = logisticsLabelCategoryMapper.selectList(logisticsLabelCategoryDOLambdaQueryWrapper);
            logisticsLabelCategoryDOMap.putAll(logisticsLabelCategoryDOList.stream().collect(Collectors.groupingBy(LogisticsLabelCategoryDO::getLabelId)));
        }

        List<LogisticsLabelVO> logisticsLabelVOList = new ArrayList<>();
        logisticsLabelDOList.forEach(logisticsLabelDO -> {
            LogisticsLabelVO logisticsLabelVO = BeanConvertUtils.copyProperties(logisticsLabelDO, LogisticsLabelVO.class);

            //????????????????????????
            List<LogisticsLabelCategoryDO> tempLogisticsLabelCategoryDOList = Optional.ofNullable(logisticsLabelCategoryDOMap.get(logisticsLabelDO.getId())).orElse(new ArrayList<>());
            String categoryCodeList = tempLogisticsLabelCategoryDOList.stream().map(LogisticsLabelCategoryDO::getCategoryCode).collect(Collectors.joining(","));
            logisticsLabelVO.setCategoryCodeList(categoryCodeList);
            logisticsLabelVO.setOperationalBehavior(OperationalTypeEnum.getOperationalTypeEnumByType(logisticsLabelDO.getOperationalBehavior()).getMsg());
            logisticsLabelVOList.add(logisticsLabelVO);
        });

        Page<LogisticsLabelVO> resultPage = BeanConvertUtils.copyProperties(selectPage, Page.class);
        resultPage.setRecords(logisticsLabelVOList);
        return resultPage;
    }

    @Override
    public LogisticsLabelVO detail(Long labelId) {
        LogisticsLabelDO logisticsLabelDO = logisticsLabelMapper.selectById(labelId);
        if (logisticsLabelDO == null) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "?????????????????????");
        }

        LambdaQueryWrapper<LogisticsLabelCategoryDO> logisticsLabelCategoryDOLambdaQueryWrapper = Wrappers.<LogisticsLabelCategoryDO>lambdaQuery()
                .eq(LogisticsLabelCategoryDO::getLabelId, labelId);
        List<LogisticsLabelCategoryDO> logisticsLabelCategoryDOList = logisticsLabelCategoryMapper.selectList(logisticsLabelCategoryDOLambdaQueryWrapper);

        List<LogisticsLabelCategoryVO> logisticsLabelCategoryVOList = new ArrayList<>();
        logisticsLabelCategoryDOList.forEach(logisticsLabelCategoryDO -> {
            LogisticsLabelCategoryVO logisticsLabelCategoryVO = BeanConvertUtils.copyProperties(logisticsLabelCategoryDO, LogisticsLabelCategoryVO.class);
            logisticsLabelCategoryVOList.add(logisticsLabelCategoryVO);
        });
        String categoryCodeList = logisticsLabelCategoryDOList.stream().map(LogisticsLabelCategoryDO::getCategoryCode).collect(Collectors.joining(","));

        LogisticsLabelVO logisticsLabelVO = BeanConvertUtils.copyProperties(logisticsLabelDO, LogisticsLabelVO.class);
        logisticsLabelVO.setCategoryCodeList(categoryCodeList);
        logisticsLabelVO.setCategoryList(logisticsLabelCategoryVOList);
        logisticsLabelVO.setOperationalBehavior(OperationalTypeEnum.getOperationalTypeEnumByType(logisticsLabelDO.getOperationalBehavior()).getMsg());
        return logisticsLabelVO;
    }

    @Override
    public Boolean add(LogisticsLabelAddReq logisticsLabelAddReq) {
        //??????????????????????????????
        LambdaQueryWrapper<LogisticsLabelDO> logisticsLabelDOLambdaQueryWrapper = Wrappers.<LogisticsLabelDO>lambdaQuery()
                .eq(LogisticsLabelDO::getName, logisticsLabelAddReq.getName());
        List<LogisticsLabelDO> logisticsLabelDOList = logisticsLabelMapper.selectList(logisticsLabelDOLambdaQueryWrapper);
        if (logisticsLabelDOList.size() != 0) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "????????????????????????");
        }

        //???????????????????????????????????????
        List<LogisticsLabelAddReq.LogisticsLabelCategoryAddReq> logisticsLabelCategoryAddReqList = logisticsLabelAddReq.getCategoryList();
        Set<String> categoryCodeSet = logisticsLabelCategoryAddReqList.stream().map(LogisticsLabelAddReq.LogisticsLabelCategoryAddReq::getCategoryCode).collect(Collectors.toSet());
        if (categoryCodeSet.size() != logisticsLabelCategoryAddReqList.size()) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "?????? id ??????");
        }
        //??????search ???????????????????????????????????????
//        for (LogisticsLabelAddReq.LogisticsLabelCategoryAddReq logisticsLabelCategoryAddReq : logisticsLabelCategoryAddReqList) {
//            MetadatapbMetadata.CategoryMeta categoryMeta = metadataClient.getCategory(logisticsLabelCategoryAddReq.getCategoryCode());
//            if(categoryMeta == null)
//            {
//                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, String.format("%s ?????????????????????????????????????????????", logisticsLabelCategoryAddReq.getCategoryCode()));
//            }
//        }
        // ??????null?????????
        long codeCount = logisticsLabelCategoryAddReqList.stream().filter(item -> StringUtils.isBlank(item.getCategoryCode())).count();
        long nameCount = logisticsLabelCategoryAddReqList.stream().filter(item -> StringUtils.isBlank(item.getCategoryName())).count();
        long levelCount = logisticsLabelCategoryAddReqList.stream().filter(item -> StringUtils.isBlank(item.getCategoryLevel())).count();
        if (codeCount > 0 || nameCount > 0 || levelCount > 0) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "????????????????????????null????????????");
        }
        LogisticsLabelDO logisticsLabelDO = new LogisticsLabelDO();
        logisticsLabelDO.setName(logisticsLabelAddReq.getName());
        logisticsLabelDO.setOperationalBehavior(OperationalTypeEnum.ADD.getType());
        logisticsLabelMapper.insert(logisticsLabelDO);

        List<LogisticsLabelCategoryDO> logisticsLabelCategoryDOList = new ArrayList<>();
        logisticsLabelCategoryAddReqList.forEach(logisticsLabelCategoryAddReq -> {
            LogisticsLabelCategoryDO logisticsLabelCategoryDO = new LogisticsLabelCategoryDO();
            logisticsLabelCategoryDO.setId(SnowflakeIdUtils.getId());
            logisticsLabelCategoryDO.setLabelId(logisticsLabelDO.getId());
            logisticsLabelCategoryDO.setCategoryCode(logisticsLabelCategoryAddReq.getCategoryCode());
            logisticsLabelCategoryDO.setCategoryName(logisticsLabelCategoryAddReq.getCategoryName());
            logisticsLabelCategoryDO.setCategoryLevel(logisticsLabelCategoryAddReq.getCategoryLevel());
            logisticsLabelCategoryDOList.add(logisticsLabelCategoryDO);
        });
        logisticsLabelCategoryMapper.insertLogisticsLabelCategoryBatch(logisticsLabelCategoryDOList);
        return true;
    }

    @Override
    public Boolean update(LogisticsLabelUpdateReq logisticsLabelUpdateReq) {
        //?????? id ??????????????????????????????
        LogisticsLabelDO logisticsLabelDO = logisticsLabelMapper.selectById(logisticsLabelUpdateReq.getId());
        if (logisticsLabelDO == null) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "?????????????????????");
        }

        //?????? name ??????????????????????????????
        LambdaQueryWrapper<LogisticsLabelDO> logisticsLabelDOLambdaQueryWrapper = Wrappers.<LogisticsLabelDO>lambdaQuery()
                .eq(LogisticsLabelDO::getName, logisticsLabelUpdateReq.getName());
        List<LogisticsLabelDO> logisticsLabelDOList = logisticsLabelMapper.selectList(logisticsLabelDOLambdaQueryWrapper);
        if (logisticsLabelDOList.size() != 0) {
            if (!logisticsLabelDO.getId().equals(logisticsLabelDOList.get(0).getId())) {
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "????????????????????????");
            }
        }

        List<Long> deleteLabelCategoryIdList = Optional.ofNullable(logisticsLabelUpdateReq.getDeleteLabelCategoryIdList()).orElse(new ArrayList<>());     //???????????????????????????????????????
        List<LogisticsLabelUpdateReq.LogisticsLabelCategoryUpdateReq> updateLabelCategoryList = Optional.ofNullable(logisticsLabelUpdateReq.getUpdateLabelCategoryList()).orElse(new ArrayList<>());     //???????????????????????????????????????
        List<LogisticsLabelAddReq.LogisticsLabelCategoryAddReq> addLabelCategoryList = Optional.ofNullable(logisticsLabelUpdateReq.getAddLabelCategoryList()).orElse(new ArrayList<>());     //???????????????????????????????????????
        Set<String> addLabelCategory = addLabelCategoryList.stream().map(LogisticsLabelAddReq.LogisticsLabelCategoryAddReq::getCategoryCode).collect(Collectors.toSet());

        LambdaQueryWrapper<LogisticsLabelCategoryDO> logisticsLabelCategoryDOLambdaQueryWrapper = Wrappers.<LogisticsLabelCategoryDO>lambdaQuery()
                .eq(LogisticsLabelCategoryDO::getLabelId, logisticsLabelUpdateReq.getId());
        List<LogisticsLabelCategoryDO> logisticsLabelCategoryDOList = logisticsLabelCategoryMapper.selectList(logisticsLabelCategoryDOLambdaQueryWrapper);
        List<Long> logisticsLabelCategoryIdList = logisticsLabelCategoryDOList.stream().map(LogisticsLabelCategoryDO::getId).collect(Collectors.toList());
        for (Long deleteLabelCategoryId : deleteLabelCategoryIdList) {
            if (!logisticsLabelCategoryIdList.contains(deleteLabelCategoryId))       //??????????????? id ??????????????????????????????
            {
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "????????????????????????????????????");
            }
        }

        //?????????????????????????????????
        if (addLabelCategory.size() != addLabelCategoryList.size()) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "?????? id ??????");
        }

        //?????? - ?????? + ??????
        int remainingQuantity = logisticsLabelCategoryDOList.size() - deleteLabelCategoryIdList.size() + addLabelCategoryList.size();
        if (remainingQuantity < 1) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "???????????????????????????????????? 1");
        }

        //??????????????????
        Map<String, LogisticsLabelAddReq.LogisticsLabelCategoryAddReq> addLabelCategoryMap = addLabelCategoryList.stream().collect(Collectors.toMap(LogisticsLabelAddReq.LogisticsLabelCategoryAddReq::getCategoryCode, logisticsLabelCategoryAddReq -> logisticsLabelCategoryAddReq));
        for (LogisticsLabelCategoryDO logisticsLabelCategoryDO : logisticsLabelCategoryDOList) {
            if (deleteLabelCategoryIdList.contains(logisticsLabelDO.getId())) {
                continue;
            }

            //???????????????????????????????????????????????????????????????
            LogisticsLabelAddReq.LogisticsLabelCategoryAddReq logisticsLabelCategoryAddReq = addLabelCategoryMap.get(logisticsLabelCategoryDO.getCategoryCode());
            if (logisticsLabelCategoryAddReq != null) {
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "?????? id ??????");
            }
        }

        List<LogisticsLabelCategoryDO> updateLogisticsLabelCategoryDOList = new ArrayList<>();
        updateLabelCategoryList.forEach(logisticsLabelCategoryUpdateReq -> {
            LogisticsLabelCategoryDO logisticsLabelCategoryDO = new LogisticsLabelCategoryDO();
            logisticsLabelCategoryDO.setId(logisticsLabelCategoryUpdateReq.getId());
            logisticsLabelCategoryDO.setCategoryCode(logisticsLabelCategoryUpdateReq.getCategoryCode());
            logisticsLabelCategoryDO.setCategoryName(logisticsLabelCategoryUpdateReq.getCategoryName());
            logisticsLabelCategoryDO.setCategoryLevel(logisticsLabelCategoryUpdateReq.getCategoryLevel());
            updateLogisticsLabelCategoryDOList.add(logisticsLabelCategoryDO);
        });

        List<LogisticsLabelCategoryDO> addLogisticsLabelCategoryDOList = new ArrayList<>();
        addLabelCategoryList.forEach(logisticsLabelCategoryAddReq -> {
            LogisticsLabelCategoryDO logisticsLabelCategoryDO = new LogisticsLabelCategoryDO();
            logisticsLabelCategoryDO.setId(SnowflakeIdUtils.getId());
            logisticsLabelCategoryDO.setLabelId(logisticsLabelUpdateReq.getId());
            logisticsLabelCategoryDO.setCategoryCode(logisticsLabelCategoryAddReq.getCategoryCode());
            logisticsLabelCategoryDO.setCategoryName(logisticsLabelCategoryAddReq.getCategoryName());
            logisticsLabelCategoryDO.setCategoryLevel(logisticsLabelCategoryAddReq.getCategoryLevel());
            addLogisticsLabelCategoryDOList.add(logisticsLabelCategoryDO);
        });

        //??????????????????
        LogisticsLabelDO tempLogisticsLabelDO = new LogisticsLabelDO();
        tempLogisticsLabelDO.setId(logisticsLabelUpdateReq.getId());
        tempLogisticsLabelDO.setName(logisticsLabelUpdateReq.getName());
        tempLogisticsLabelDO.setOperationalBehavior(OperationalTypeEnum.UPDATE.getType());
        tempLogisticsLabelDO.setUpdateTime(new Date());
        logisticsLabelMapper.updateById(tempLogisticsLabelDO);

        //????????????????????????
        if (deleteLabelCategoryIdList.size() != 0)
            logisticsLabelCategoryMapper.deleteBatchIds(deleteLabelCategoryIdList);
        if (updateLogisticsLabelCategoryDOList.size() != 0)
            logisticsLabelCategoryMapper.updateLogisticsLabelCategoryBatch(updateLogisticsLabelCategoryDOList);
        if (addLogisticsLabelCategoryDOList.size() != 0)
            logisticsLabelCategoryMapper.insertLogisticsLabelCategoryBatch(addLogisticsLabelCategoryDOList);

        return true;
    }

    @Override
    public Boolean del(LogisticsLabelDelReq logisticsLabelDelReq) {
        LambdaQueryWrapper<LogisticsLabelDO> logisticsLabelDOLambdaQueryWrapper = Wrappers.<LogisticsLabelDO>lambdaQuery()
                .in(LogisticsLabelDO::getId, logisticsLabelDelReq.getIdList());

        LambdaQueryWrapper<LogisticsLabelCategoryDO> logisticsLabelCategoryDOLambdaQueryWrapper = Wrappers.<LogisticsLabelCategoryDO>lambdaQuery()
                .in(LogisticsLabelCategoryDO::getLabelId, logisticsLabelDelReq.getIdList());

        logisticsLabelMapper.delete(logisticsLabelDOLambdaQueryWrapper);
        logisticsLabelCategoryMapper.delete(logisticsLabelCategoryDOLambdaQueryWrapper);
        return true;
    }


    @Override
    public List<LogisticsLabelProductVO> getCommodityLabel(List<String> sku) {
        LambdaQueryWrapper<LogisticsLabelProductDO> logisticsLabelProductDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        logisticsLabelProductDOLambdaQueryWrapper.in(LogisticsLabelProductDO::getSku, sku);
        List<LogisticsLabelProductDO> logisticsLabelProductDOS = logisticsLabelProductMapper.selectList(logisticsLabelProductDOLambdaQueryWrapper);
        List<Long> productIdList = logisticsLabelProductDOS.stream().map(LogisticsLabelProductDO::getId).collect(Collectors.toList());
        Map<Long, List<LogisticsLabelProductItemDO>> logisticsLabelProductItemDOMap = new HashMap<>();
        Map<Long, LogisticsLabelDO> logisticsLabelDOMap = new HashMap<>();
        if (productIdList.size() != 0) {
            LambdaQueryWrapper<LogisticsLabelProductItemDO> logisticsLabelProductItemDOLambdaQueryWrapper = Wrappers.<LogisticsLabelProductItemDO>lambdaQuery()
                    .in(LogisticsLabelProductItemDO::getProductId, productIdList);
            List<LogisticsLabelProductItemDO> logisticsLabelProductItemDOList = logisticsLabelProductItemMapper.selectList(logisticsLabelProductItemDOLambdaQueryWrapper);
            Set<Long> labelIdSet = logisticsLabelProductItemDOList.stream().map(LogisticsLabelProductItemDO::getLabelId).collect(Collectors.toSet());
            logisticsLabelProductItemDOMap.putAll(logisticsLabelProductItemDOList.stream().collect(Collectors.groupingBy(LogisticsLabelProductItemDO::getProductId)));

            if (labelIdSet.size() != 0) {
                LambdaQueryWrapper<LogisticsLabelDO> logisticsLabelDOLambdaQueryWrapper = Wrappers.<LogisticsLabelDO>lambdaQuery()
                        .in(LogisticsLabelDO::getId, new ArrayList<>(labelIdSet));
                List<LogisticsLabelDO> logisticsLabelDOList = logisticsLabelMapper.selectList(logisticsLabelDOLambdaQueryWrapper);
                logisticsLabelDOMap.putAll(logisticsLabelDOList.stream().collect(Collectors.toMap(LogisticsLabelDO::getId, logisticsLabelDO -> logisticsLabelDO)));
            }
        }


        List<LogisticsLabelProductVO> logisticsLabelProductVOList = new ArrayList<>();
        logisticsLabelProductDOS.forEach(logisticsLabelProductDO -> {
            List<LogisticsLabelVO> logisticsLabelVOList = new ArrayList<>();
            List<LogisticsLabelProductItemDO> tempLogisticsLabelProductItemDOList = Optional.ofNullable(logisticsLabelProductItemDOMap.get(logisticsLabelProductDO.getId())).orElse(new ArrayList<>());
            List<Long> labelIdList = tempLogisticsLabelProductItemDOList.stream().map(LogisticsLabelProductItemDO::getLabelId).collect(Collectors.toList());
            labelIdList.forEach(labelId -> {
                LogisticsLabelDO logisticsLabelDO = logisticsLabelDOMap.get(labelId);
                LogisticsLabelVO logisticsLabelVO = BeanConvertUtils.copyProperties(logisticsLabelDO, LogisticsLabelVO.class);
                logisticsLabelVOList.add(logisticsLabelVO);
            });
            LogisticsLabelProductVO logisticsLabelProductVO = BeanConvertUtils.copyProperties(logisticsLabelProductDO, LogisticsLabelProductVO.class);
            logisticsLabelProductVO.setLogisticsLabelVOList(logisticsLabelVOList);
            logisticsLabelProductVO.setOperationalBehavior(OperationalTypeEnum.getOperationalTypeEnumByType(logisticsLabelProductDO.getOperationalBehavior()).getMsg());
            logisticsLabelProductVOList.add(logisticsLabelProductVO);
        });
        return logisticsLabelProductVOList;
    }


    @Override
    public Page<LogisticsLabelProductVO> productList(LogisticsLabelProductReq logisticsLabelProductReq) {
        LambdaQueryWrapper<LogisticsLabelProductDO> logisticsLabelProductDOLambdaQueryWrapper = this.doConditionHandler(logisticsLabelProductReq);
        Page<LogisticsLabelProductDO> selectPage = logisticsLabelProductMapper.selectPage(new Page(logisticsLabelProductReq.getPage(), logisticsLabelProductReq.getRow()), logisticsLabelProductDOLambdaQueryWrapper);
        List<LogisticsLabelProductDO> logisticsLabelProductDOList = selectPage.getRecords();
        List<Long> productIdList = logisticsLabelProductDOList.stream().map(LogisticsLabelProductDO::getId).collect(Collectors.toList());

        Map<Long, List<LogisticsLabelProductItemDO>> logisticsLabelProductItemDOMap = new HashMap<>();
        Map<Long, LogisticsLabelDO> logisticsLabelDOMap = new HashMap<>();
        if (productIdList.size() != 0) {
            LambdaQueryWrapper<LogisticsLabelProductItemDO> logisticsLabelProductItemDOLambdaQueryWrapper = Wrappers.<LogisticsLabelProductItemDO>lambdaQuery()
                    .in(LogisticsLabelProductItemDO::getProductId, productIdList);
            List<LogisticsLabelProductItemDO> logisticsLabelProductItemDOList = logisticsLabelProductItemMapper.selectList(logisticsLabelProductItemDOLambdaQueryWrapper);
            Set<Long> labelIdSet = logisticsLabelProductItemDOList.stream().map(LogisticsLabelProductItemDO::getLabelId).collect(Collectors.toSet());
            logisticsLabelProductItemDOMap.putAll(logisticsLabelProductItemDOList.stream().collect(Collectors.groupingBy(LogisticsLabelProductItemDO::getProductId)));

            if (labelIdSet.size() != 0) {
                LambdaQueryWrapper<LogisticsLabelDO> logisticsLabelDOLambdaQueryWrapper = Wrappers.<LogisticsLabelDO>lambdaQuery()
                        .in(LogisticsLabelDO::getId, new ArrayList<>(labelIdSet));
                List<LogisticsLabelDO> logisticsLabelDOList = logisticsLabelMapper.selectList(logisticsLabelDOLambdaQueryWrapper);
                logisticsLabelDOMap.putAll(logisticsLabelDOList.stream().collect(Collectors.toMap(LogisticsLabelDO::getId, logisticsLabelDO -> logisticsLabelDO)));
            }
        }


        List<LogisticsLabelProductVO> logisticsLabelProductVOList = new ArrayList<>();
        logisticsLabelProductDOList.forEach(logisticsLabelProductDO -> {
            List<LogisticsLabelVO> logisticsLabelVOList = new ArrayList<>();
            List<LogisticsLabelProductItemDO> tempLogisticsLabelProductItemDOList = Optional.ofNullable(logisticsLabelProductItemDOMap.get(logisticsLabelProductDO.getId())).orElse(new ArrayList<>());
            List<Long> labelIdList = tempLogisticsLabelProductItemDOList.stream().map(LogisticsLabelProductItemDO::getLabelId).collect(Collectors.toList());
            labelIdList.forEach(labelId -> {
                LogisticsLabelDO logisticsLabelDO = logisticsLabelDOMap.get(labelId);
                LogisticsLabelVO logisticsLabelVO = BeanConvertUtils.copyProperties(logisticsLabelDO, LogisticsLabelVO.class);
                logisticsLabelVOList.add(logisticsLabelVO);
            });
            LogisticsLabelProductVO logisticsLabelProductVO = BeanConvertUtils.copyProperties(logisticsLabelProductDO, LogisticsLabelProductVO.class);
            logisticsLabelProductVO.setLogisticsLabelVOList(logisticsLabelVOList);
            logisticsLabelProductVO.setOperationalBehavior(OperationalTypeEnum.getOperationalTypeEnumByType(logisticsLabelProductDO.getOperationalBehavior()).getMsg());
            logisticsLabelProductVOList.add(logisticsLabelProductVO);
        });

        Page<LogisticsLabelProductVO> resultPage = BeanConvertUtils.copyProperties(selectPage, Page.class);
        resultPage.setRecords(logisticsLabelProductVOList);
        return resultPage;
    }

    @Override
    public ProductDetailVO productSearch(Long sku) {
        RpcResult<ItemsGTO.GetSkuCodeReply> getSkuCodeReplyRpcResult = itemsSrvGrpcClient.getSkuCode(sku);
        if (getSkuCodeReplyRpcResult.getCode() == ErrorCodeEnum.SUCCESS.getCode() && getSkuCodeReplyRpcResult.getData() != null) {
            ItemsGTO.GetSkuCodeReply getSkuCodeReply = getSkuCodeReplyRpcResult.getData();
            RpcResult<ItemsGTO.GetSpecByCodeReply> getSpecByCodeReplyRpcResult = itemsSrvGrpcClient.getSpecByCode(getSkuCodeReply.getSkuCode(), false);
            if (getSpecByCodeReplyRpcResult.getCode() == ErrorCodeEnum.SUCCESS.getCode() && getSpecByCodeReplyRpcResult.getData() != null) {
                ItemsGTO.GetSpecByCodeReply getSpecByCodeReply = getSpecByCodeReplyRpcResult.getData();
                ItemsGTO.Spec spec = getSpecByCodeReply.getSpec();
                Long spuId = spec.getSpuId();

                String productName = "";
                RpcResult<ItemsGTO.GetProductByIDReply> getProductByIDReplyRpcResult = itemsSrvGrpcClient.getProductByID(spuId);
                if (getProductByIDReplyRpcResult.getCode() == ErrorCodeEnum.SUCCESS.getCode() && getProductByIDReplyRpcResult.getData() != null) {
                    ItemsGTO.GetProductByIDReply getProductByIDReply = getProductByIDReplyRpcResult.getData();
                    if (getProductByIDReply != null) {
                        ItemsGTO.Product product = getProductByIDReply.getProduct();
                        if (product != null) {
                            ItemsGTO.Item item = product.getItem();
                            if (item != null) {
                                productName = item.getTitle();
                            }
                        }
                    }
                }
                ProductDetailVO productDetailVO = new ProductDetailVO();
                productDetailVO.setSku(String.valueOf(sku));
                productDetailVO.setProductName(productName);     //
                productDetailVO.setProductAttribute(JSON.toJSONString(spec.getAttributesMap()));
                productDetailVO.setProductImg(LogisticsCommonUtils.convertMomosoImg(JSON.toJSONString(spec.getImagesList())));
                return productDetailVO;
            }
        }
        throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "sku ????????????");
    }

    @Override
    public Boolean productAdd(LogisticsLabelProductAddReq logisticsLabelProductAddReq) {
        ProductDetailVO productDetailVO = this.productSearch(Long.valueOf(logisticsLabelProductAddReq.getSku()));
        LambdaQueryWrapper<LogisticsLabelProductDO> logisticsLabelProductDOLambdaQueryWrapper = Wrappers.<LogisticsLabelProductDO>lambdaQuery()
                .eq(LogisticsLabelProductDO::getSku, logisticsLabelProductAddReq.getSku());
        List<LogisticsLabelProductDO> logisticsLabelProductDOList = logisticsLabelProductMapper.selectList(logisticsLabelProductDOLambdaQueryWrapper);
        if (logisticsLabelProductDOList.size() > 0) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "???SKU????????????");
        }
        LogisticsLabelProductDO logisticsLabelProductDO = new LogisticsLabelProductDO();
        logisticsLabelProductDO.setSku(logisticsLabelProductAddReq.getSku());
        logisticsLabelProductDO.setProductName(productDetailVO.getProductName());
        logisticsLabelProductDO.setProductAttribute(productDetailVO.getProductAttribute());
        logisticsLabelProductDO.setProductImg(productDetailVO.getProductImg());
        logisticsLabelProductDO.setOperationalBehavior(OperationalTypeEnum.ADD.getType());
        logisticsLabelProductMapper.insert(logisticsLabelProductDO);
        return true;
    }

    @Override
    public Boolean productUpdate(LogisticsLabelProductUpdateReq logisticsLabelProductUpdateReq) {

        LogisticsLabelProductDO logisticsLabelProductDO = logisticsLabelProductMapper.selectById(logisticsLabelProductUpdateReq.getId());
        if (logisticsLabelProductDO == null) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "??????????????????????????????????????????");
        }

        List<LogisticsLabelProductItemDO> logisticsLabelProductItemDOList = new ArrayList<>();
        List<Long> labelIdList = logisticsLabelProductUpdateReq.getLabelIdList();
        labelIdList.forEach(labelId -> {
            LogisticsLabelProductItemDO logisticsLabelProductItemDO = new LogisticsLabelProductItemDO();
            logisticsLabelProductItemDO.setId(SnowflakeIdUtils.getId());
            logisticsLabelProductItemDO.setLabelId(labelId);
            logisticsLabelProductItemDO.setProductId(logisticsLabelProductDO.getId());
            logisticsLabelProductItemDOList.add(logisticsLabelProductItemDO);
        });

        LogisticsLabelProductDO tempLogisticsLabelProductDO = new LogisticsLabelProductDO();
        tempLogisticsLabelProductDO.setId(logisticsLabelProductDO.getId());
        tempLogisticsLabelProductDO.setOperationalBehavior(OperationalTypeEnum.UPDATE.getType());
        tempLogisticsLabelProductDO.setUpdateTime(new Date());
        logisticsLabelProductMapper.updateById(tempLogisticsLabelProductDO);

        logisticsLabelProductItemMapper.deleteLogisticsLabelProductItemByProductId(logisticsLabelProductDO.getId());
        if (logisticsLabelProductItemDOList.size() != 0)
            logisticsLabelProductItemMapper.insertLogisticsLabelProductItemBatch(logisticsLabelProductItemDOList);
        return true;
    }

    @Override
    public Boolean productDel(LogisticsLabelProductDelReq logisticsLabelProductDelReq) {
        LogisticsLabelProductDO logisticsLabelProductDO = logisticsLabelProductMapper.selectById(logisticsLabelProductDelReq.getId());
        if (logisticsLabelProductDO == null) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "??????????????????????????????????????????");
        }
        logisticsLabelProductMapper.deleteById(logisticsLabelProductDO.getId());
        logisticsLabelProductItemMapper.deleteLogisticsLabelProductItemByProductId(logisticsLabelProductDO.getId());
        return true;
    }

    @Override
    @CacheEvict(value = Constant.LOGISTICS_LOGISTIC_FREIGHT_RULE, allEntries = true)
    public File importCommodityLabelExcel(String fileUrl) {
        ImportExcelVO purchaseBlackProductConfigImportVO = new ImportExcelVO();
        purchaseBlackProductConfigImportVO.setSuccess(true);
        String name = FilenameUtils.getName(fileUrl);
        String localFileName = "/data/tmp/" + name;
        File localFile = new File(localFileName);
        try {
            FileUtils.copyURLToFile(new URL(fileUrl), localFile);
        } catch (IOException e) {
            log.error("???????????????????????????{}", e.getMessage());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "????????????");
        }
        // List<LogisticsRulesFreightImportReq> logisticsRulesFreightImportReqList =
        List<CommodityLabelExcel> commodityLabelExcels = ExcelUtil.importExcel(localFileName, 0, 1, CommodityLabelExcel.class);
        if (commodityLabelExcels.size() > 1000) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "???????????????????????????1000???");
        }
        if (commodityLabelExcels.size() == 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "???????????????????????????0???");
        }
        //???????????????????????????
        //????????????
        ArrayList<LogisticsLabelProductDO> logisticsLabelProductDOS = new ArrayList<>();
        //??????????????????
        List<LogisticsLabelProductItemDO> logisticsLabelProductItemDOList = new ArrayList<>();
        ArrayList<Long> longs = new ArrayList<>();
        LambdaQueryWrapper<LogisticsLabelDO> objectLambdaQueryWrapper = Wrappers.lambdaQuery();
        List<LogisticsLabelDO> logisticsLabelDOList = logisticsLabelMapper.selectList(objectLambdaQueryWrapper);
        HashMap<String, Long> stringLongHashMap = new HashMap<>();
        if (logisticsLabelDOList != null && logisticsLabelDOList.size() > 0) {
            for (LogisticsLabelDO logisticsLabelDO : logisticsLabelDOList) {
                stringLongHashMap.put(logisticsLabelDO.getName(), logisticsLabelDO.getId());
            }
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), ResponseCodeEnum.RETURN_CODE_100400.getMsg());
        }
        //??????SKU????????????  //??????????????????
        for (CommodityLabelExcel cle : commodityLabelExcels) {
            Long sku = cle.getSku();
            if (sku == null) {
                cle.setErrorMsg("sku??????");
                purchaseBlackProductConfigImportVO.setSuccess(false);
                continue;
            }
            ProductDetailVO productDetailVO = null;
            try {
                productDetailVO = productSearch(sku);
                longs.add(sku);
            } catch (Exception e) {
                cle.setErrorMsg("sku???????????????sku?????????" + sku);
                purchaseBlackProductConfigImportVO.setSuccess(false);
                continue;
            }
            LogisticsLabelProductDO logisticsLabelProductDO = new LogisticsLabelProductDO();
            logisticsLabelProductDO.setSku(productDetailVO.getSku());
            logisticsLabelProductDO.setProductName(productDetailVO.getProductName());
            logisticsLabelProductDO.setProductAttribute(productDetailVO.getProductAttribute());
            logisticsLabelProductDO.setProductImg(productDetailVO.getProductImg());
            logisticsLabelProductDO.setOperationalBehavior(OperationalTypeEnum.ADD.getType());
            logisticsLabelProductDO.setId(SnowflakeIdUtils.getId());
            logisticsLabelProductDOS.add(logisticsLabelProductDO);
            if ("???".equals(StringUtil.cleanSpacing(cle.getElectricity()))) {
                Long el = stringLongHashMap.get("??????");
                if (el == null) {
                    cle.setErrorMsg("?????????????????????");
                    purchaseBlackProductConfigImportVO.setSuccess(false);
                    continue;
                }
                LogisticsLabelProductItemDO logisticsLabelProductItemDO = new LogisticsLabelProductItemDO();
                logisticsLabelProductItemDO.setId(SnowflakeIdUtils.getId());

                logisticsLabelProductItemDO.setLabelId(el);
                logisticsLabelProductItemDO.setProductId(logisticsLabelProductDO.getId());
                logisticsLabelProductItemDOList.add(logisticsLabelProductItemDO);
            }
            if ("???".equals(StringUtil.cleanSpacing(cle.getFluid()))) {
                Long el = stringLongHashMap.get("??????");
                if (el == null) {
                    cle.setErrorMsg("?????????????????????");
                    purchaseBlackProductConfigImportVO.setSuccess(false);
                    continue;
                }
                LogisticsLabelProductItemDO logisticsLabelProductItemDO = new LogisticsLabelProductItemDO();
                logisticsLabelProductItemDO.setId(SnowflakeIdUtils.getId());
                logisticsLabelProductItemDO.setLabelId(el);
                logisticsLabelProductItemDO.setProductId(logisticsLabelProductDO.getId());
                logisticsLabelProductItemDOList.add(logisticsLabelProductItemDO);
            }
            if ("???".equals(StringUtil.cleanSpacing(cle.getActivity3()))) {
                Long el = stringLongHashMap.get("????????????C??????");
                if (el == null) {
                    cle.setErrorMsg("????????????C???????????????????????????");
                    purchaseBlackProductConfigImportVO.setSuccess(false);
                    continue;
                }
                LogisticsLabelProductItemDO logisticsLabelProductItemDO = new LogisticsLabelProductItemDO();
                logisticsLabelProductItemDO.setId(SnowflakeIdUtils.getId());
                logisticsLabelProductItemDO.setLabelId(el);
                logisticsLabelProductItemDO.setProductId(logisticsLabelProductDO.getId());
                logisticsLabelProductItemDOList.add(logisticsLabelProductItemDO);
            }
            if ("???".equals(StringUtil.cleanSpacing(cle.getActivity2()))) {
                Long el = stringLongHashMap.get("????????????B??????");
                if (el == null) {
                    cle.setErrorMsg("????????????B???????????????????????????");
                    purchaseBlackProductConfigImportVO.setSuccess(false);
                    continue;
                }
                LogisticsLabelProductItemDO logisticsLabelProductItemDO = new LogisticsLabelProductItemDO();
                logisticsLabelProductItemDO.setId(SnowflakeIdUtils.getId());
                logisticsLabelProductItemDO.setLabelId(el);
                logisticsLabelProductItemDO.setProductId(logisticsLabelProductDO.getId());
                logisticsLabelProductItemDOList.add(logisticsLabelProductItemDO);
            }
            if ("???".equals(StringUtil.cleanSpacing(cle.getActivity1()))) {
                Long el = stringLongHashMap.get("????????????A??????");
                if (el == null) {
                    cle.setErrorMsg("????????????A???????????????????????????");
                    purchaseBlackProductConfigImportVO.setSuccess(false);
                    continue;
                }
                LogisticsLabelProductItemDO logisticsLabelProductItemDO = new LogisticsLabelProductItemDO();
                logisticsLabelProductItemDO.setId(SnowflakeIdUtils.getId());
                logisticsLabelProductItemDO.setLabelId(el);
                logisticsLabelProductItemDO.setProductId(logisticsLabelProductDO.getId());
                logisticsLabelProductItemDOList.add(logisticsLabelProductItemDO);
            }
            if ("???".equals(StringUtil.cleanSpacing(cle.getBenefits()))) {
                Long el = stringLongHashMap.get("???????????????");
                if (el == null) {
                    cle.setErrorMsg("????????????????????????????????????");
                    purchaseBlackProductConfigImportVO.setSuccess(false);
                    continue;
                }
                LogisticsLabelProductItemDO logisticsLabelProductItemDO = new LogisticsLabelProductItemDO();
                logisticsLabelProductItemDO.setId(SnowflakeIdUtils.getId());
                logisticsLabelProductItemDO.setLabelId(el);
                logisticsLabelProductItemDO.setProductId(logisticsLabelProductDO.getId());
                logisticsLabelProductItemDOList.add(logisticsLabelProductItemDO);
            }
            if ("???".equals(StringUtil.cleanSpacing(cle.getPowder()))) {
                Long el = stringLongHashMap.get("??????");
                if (el == null) {
                    cle.setErrorMsg("???????????????????????????");
                    purchaseBlackProductConfigImportVO.setSuccess(false);
                    continue;
                }
                LogisticsLabelProductItemDO logisticsLabelProductItemDO = new LogisticsLabelProductItemDO();
                logisticsLabelProductItemDO.setId(SnowflakeIdUtils.getId());
                logisticsLabelProductItemDO.setLabelId(el);
                logisticsLabelProductItemDO.setProductId(logisticsLabelProductDO.getId());
                logisticsLabelProductItemDOList.add(logisticsLabelProductItemDO);
            }
            if ("???".equals(StringUtil.cleanSpacing(cle.getPaste()))) {
                Long el = stringLongHashMap.get("??????");
                if (el == null) {
                    cle.setErrorMsg("???????????????????????????");
                    purchaseBlackProductConfigImportVO.setSuccess(false);
                    continue;
                }
                LogisticsLabelProductItemDO logisticsLabelProductItemDO = new LogisticsLabelProductItemDO();
                logisticsLabelProductItemDO.setId(SnowflakeIdUtils.getId());
                logisticsLabelProductItemDO.setLabelId(el);
                logisticsLabelProductItemDO.setProductId(logisticsLabelProductDO.getId());
                logisticsLabelProductItemDOList.add(logisticsLabelProductItemDO);
            }
            if ("???".equals(StringUtil.cleanSpacing(cle.getClothing()))) {
                Long el = stringLongHashMap.get("??????");
                if (el == null) {
                    cle.setErrorMsg("???????????????????????????");
                    purchaseBlackProductConfigImportVO.setSuccess(false);
                    continue;
                }
                LogisticsLabelProductItemDO logisticsLabelProductItemDO = new LogisticsLabelProductItemDO();
                logisticsLabelProductItemDO.setId(SnowflakeIdUtils.getId());
                logisticsLabelProductItemDO.setLabelId(el);
                logisticsLabelProductItemDO.setProductId(logisticsLabelProductDO.getId());
                logisticsLabelProductItemDOList.add(logisticsLabelProductItemDO);
            }
        }
        if (purchaseBlackProductConfigImportVO.getSuccess()) {
            try {
                //????????????
                logisticsLabelProductServiceImpl.updateCommodityLabel(logisticsLabelProductDOS, logisticsLabelProductItemDOList, longs);
            } catch (Exception e) {
                log.error("????????????????????????-- ?????????????????????{}", e.getMessage());
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "??????????????????,????????????");
            }
        }
        //???????????????
        else {
            String uuid = UUID.randomUUID().toString();
            String errorLocalFile = "/data/tmp/" + uuid + ".xls";
            File file = new File(errorLocalFile);
            try {
                ExcelUtil.getExport(commodityLabelExcels, null, "", CommodityLabelExcel.class, new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                log.error("???????????????????????????????????????{}", e.getMessage());
            }
            return file;
        }
        return null;
    }

    private LambdaQueryWrapper<LogisticsLabelDO> doConditionHandler(LogisticsLabelReq logisticsLabelReq) {
        LambdaQueryWrapper<LogisticsLabelDO> logisticsLabelDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(logisticsLabelReq.getName())) {
            logisticsLabelDOLambdaQueryWrapper.likeRight(LogisticsLabelDO::getName, logisticsLabelReq.getName());
        }

        logisticsLabelDOLambdaQueryWrapper.orderByDesc(LogisticsLabelDO::getUpdateTime);        //?????????????????????????????????????????????
        return logisticsLabelDOLambdaQueryWrapper;
    }

    private LambdaQueryWrapper<LogisticsLabelProductDO> doConditionHandler(LogisticsLabelProductReq logisticsLabelProductReq) {
        LambdaQueryWrapper<LogisticsLabelProductDO> logisticsLabelProductDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(logisticsLabelProductReq.getSku())) {
            logisticsLabelProductDOLambdaQueryWrapper.likeRight(LogisticsLabelProductDO::getSku, logisticsLabelProductReq.getSku());
        }

        logisticsLabelProductDOLambdaQueryWrapper.orderByDesc(LogisticsLabelProductDO::getUpdateTime);        //?????????????????????????????????????????????
        return logisticsLabelProductDOLambdaQueryWrapper;
    }
}
