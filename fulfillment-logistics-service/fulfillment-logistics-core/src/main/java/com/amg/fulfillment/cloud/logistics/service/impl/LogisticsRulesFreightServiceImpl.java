package com.amg.fulfillment.cloud.logistics.service.impl;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.boot.utils.excel.ExcelUtil;
import com.amg.framework.boot.utils.id.SnowflakeIdUtils;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.util.BeanConvertUtils;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsChannelDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsRulesFreightDO;
import com.amg.fulfillment.cloud.logistics.enumeration.BaseLogisticsResponseCodeEnum;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsChannelMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsProviderMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsRulesFreightMapper;
import com.amg.fulfillment.cloud.logistics.model.excel.LogisticsRulesFreightExcel;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.ImportExcelVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsRulesFreightVO;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsRulesFreightService;
import com.amg.fulfillment.cloud.logistics.util.FilePlusUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by Seraph on 2021/5/25
 */

@Slf4j
@Service
public class LogisticsRulesFreightServiceImpl implements ILogisticsRulesFreightService {

    @Autowired
    private LogisticsRulesFreightMapper logisticsRulesFreightMapper;
    @Autowired
    private LogisticsChannelMapper logisticsChannelMapper;


    @Override
    public Page<LogisticsRulesFreightVO> list(LogisticsRulesFreightReq logisticsRulesFreightReq) {
        //查询运费规则数据
        IPage page = new Page(logisticsRulesFreightReq.getPage(), logisticsRulesFreightReq.getRow());
        List<LogisticsRulesFreightDO> logisticsRulesFreightDOList = logisticsRulesFreightMapper.queryLogisticsRulesFreightList(page, logisticsRulesFreightReq);
        List<LogisticsRulesFreightVO> logisticsRulesFreightVOList = BeanConvertUtils.copyProperties(logisticsRulesFreightDOList, LogisticsRulesFreightVO.class);
        Page<LogisticsRulesFreightVO> resultPage = BeanConvertUtils.copyProperties(page, Page.class);
        resultPage.setRecords(logisticsRulesFreightVOList);
        return resultPage;
    }

    @Override
    public LogisticsRulesFreightVO detail(Long rulesFreightId) {
        //查询运费规则数据
        LogisticsRulesFreightDO logisticsRulesFreightDO = logisticsRulesFreightMapper.queryLogisticsRulesFreightById(rulesFreightId);
        if (logisticsRulesFreightDO == null) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "查询物流运费规则不存在");
        }

        LogisticsRulesFreightVO logisticsRulesFreightVO = BeanConvertUtils.copyProperties(logisticsRulesFreightDO, LogisticsRulesFreightVO.class);
        return logisticsRulesFreightVO;
    }

    @Override
    @CacheEvict(value = Constant.LOGISTICS_LOGISTIC_FREIGHT_RULE, allEntries = true)
    public Boolean update(LogisticsRulesFreightUpdateReq logisticsRulesFreightUpdateReq) {
        LogisticsRulesFreightDO logisticsRulesFreightDO = logisticsRulesFreightMapper.selectById(logisticsRulesFreightUpdateReq.getId());
        if (logisticsRulesFreightDO == null) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "查询物流运费规则不存在");
        }

        LogisticsRulesFreightDO tempLogisticsRulesFreightDO = new LogisticsRulesFreightDO();
        tempLogisticsRulesFreightDO.setId(logisticsRulesFreightUpdateReq.getId());
        tempLogisticsRulesFreightDO.setName(logisticsRulesFreightUpdateReq.getName());
        tempLogisticsRulesFreightDO.setLogisticsCode(logisticsRulesFreightUpdateReq.getLogisticsCode());
        tempLogisticsRulesFreightDO.setChannelCode(logisticsRulesFreightUpdateReq.getChannelCode());
        tempLogisticsRulesFreightDO.setCountry(logisticsRulesFreightUpdateReq.getCountry());
        tempLogisticsRulesFreightDO.setCountryAbbre(logisticsRulesFreightUpdateReq.getCountryAbbre());
        tempLogisticsRulesFreightDO.setStartWeight(logisticsRulesFreightUpdateReq.getStartWeight());
        tempLogisticsRulesFreightDO.setEndWeight(logisticsRulesFreightUpdateReq.getEndWeight());
        tempLogisticsRulesFreightDO.setPegistrationMoney(logisticsRulesFreightUpdateReq.getPegistrationMoney());
        tempLogisticsRulesFreightDO.setUnitPrice(logisticsRulesFreightUpdateReq.getUnitPrice());
        tempLogisticsRulesFreightDO.setEarliestPrescriptionDays(logisticsRulesFreightUpdateReq.getEarliestPrescriptionDays());
        tempLogisticsRulesFreightDO.setLatestPrescriptionDays(logisticsRulesFreightUpdateReq.getLatestPrescriptionDays());
        tempLogisticsRulesFreightDO.setIsDisable(logisticsRulesFreightUpdateReq.getIsDisable());
        logisticsRulesFreightMapper.updateById(tempLogisticsRulesFreightDO);
        return true;
    }

    @Override
    @CacheEvict(value = Constant.LOGISTICS_LOGISTIC_FREIGHT_RULE, allEntries = true)
    public Boolean add(LogisticsRulesFreightAddReq logisticsRulesFreightAddReq) {
        LogisticsRulesFreightDO logisticsRulesFreightDO = new LogisticsRulesFreightDO();
        logisticsRulesFreightDO.setName(logisticsRulesFreightAddReq.getName());
        logisticsRulesFreightDO.setLogisticsCode(logisticsRulesFreightAddReq.getLogisticsCode());
        logisticsRulesFreightDO.setChannelCode(logisticsRulesFreightAddReq.getChannelCode());
        logisticsRulesFreightDO.setCountry(logisticsRulesFreightAddReq.getCountry());
        logisticsRulesFreightDO.setCountryAbbre(logisticsRulesFreightAddReq.getCountryAbbre());
        logisticsRulesFreightDO.setStartWeight(logisticsRulesFreightAddReq.getStartWeight());
        logisticsRulesFreightDO.setEndWeight(logisticsRulesFreightAddReq.getEndWeight());
        logisticsRulesFreightDO.setPegistrationMoney(logisticsRulesFreightAddReq.getPegistrationMoney());
        logisticsRulesFreightDO.setUnitPrice(logisticsRulesFreightAddReq.getUnitPrice());
        logisticsRulesFreightDO.setEarliestPrescriptionDays(logisticsRulesFreightAddReq.getEarliestPrescriptionDays());
        logisticsRulesFreightDO.setLatestPrescriptionDays(logisticsRulesFreightAddReq.getLatestPrescriptionDays());
        logisticsRulesFreightDO.setIsDisable(logisticsRulesFreightAddReq.getIsDisable());
        logisticsRulesFreightMapper.insert(logisticsRulesFreightDO);
        return true;
    }

    @Override
    public File exportLogisticsRulesFreightExcel(ExportExcelReq<LogisticsRulesFreightReq> exportExcelReq) {
        List<LogisticsRulesFreightExcel> logisticsRulesFreightExcelList = this.listLogisticsRulesFreight(exportExcelReq);
        return FilePlusUtils.exportExcel(exportExcelReq, logisticsRulesFreightExcelList, LogisticsRulesFreightExcel.class);
    }

    @Override
    @CacheEvict(value = Constant.LOGISTICS_LOGISTIC_FREIGHT_RULE, allEntries = true)
    public File importLogisticsRulesFreightExcel(String fileUrl) {
        ImportExcelVO purchaseBlackProductConfigImportVO = new ImportExcelVO();
        try {
            String name = FilenameUtils.getName(fileUrl);
            String localFileName = "/data/tmp/" + name;
            File localFile = new File(localFileName);
            FileUtils.copyURLToFile(new URL(fileUrl), localFile);
            List<LogisticsRulesFreightImportReq> logisticsRulesFreightImportReqList = ExcelUtil.importExcel(localFileName, 0, 1, LogisticsRulesFreightImportReq.class);
            if (logisticsRulesFreightImportReqList.size() > 1000) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "单次导入应小于等于1000行");
            }
            if (logisticsRulesFreightImportReqList.size() == 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "单次导入应大于等于0行");
            }
            Set<String> logisticsNameSet = logisticsRulesFreightImportReqList.stream().map(LogisticsRulesFreightImportReq::getLogistics).collect(Collectors.toSet());
            Map<String, LogisticsChannelDO> logisticsChannelDOMap = new HashMap<>();
            if (logisticsNameSet.size() != 0) {
                LambdaQueryWrapper<LogisticsChannelDO> logisticsChannelDOLambdaQueryWrapper = Wrappers.<LogisticsChannelDO>lambdaQuery()
                        .in(LogisticsChannelDO::getLogisticsName, logisticsNameSet);
                List<LogisticsChannelDO> logisticsChannelDOList = logisticsChannelMapper.selectList(logisticsChannelDOLambdaQueryWrapper);
                logisticsChannelDOMap.putAll(logisticsChannelDOList.stream().collect(Collectors.toMap(logisticsChannelDO -> (logisticsChannelDO.getLogisticsName() + logisticsChannelDO.getChannelName()), logisticsChannelDO -> logisticsChannelDO)));
            }
            //为空数据校验
            AtomicReference<Boolean> atomicReferenceErrorFlag = new AtomicReference<>(false);       //是否有异常
            logisticsRulesFreightImportReqList.forEach(logisticsRulesFreightImportReq -> {
                List<Field> fieldList = Arrays.asList(logisticsRulesFreightImportReq.getClass().getDeclaredFields());
                List<Field> sortedFieldList = fieldList.stream().sorted(Comparator.comparing(field -> Integer.valueOf(field.getAnnotation(Excel.class).orderNum()))).collect(Collectors.toList());
                for (Field field : sortedFieldList) {
                    try {
                        field.setAccessible(true);
                        Excel excelAnnotation = field.getAnnotation(Excel.class);
                        NotNull notNullAnnotation = field.getAnnotation(NotNull.class);
                        Object obj = field.get(logisticsRulesFreightImportReq);
                        if (notNullAnnotation != null && obj == null) {
                            if (obj == null) {
                                logisticsRulesFreightImportReq.setErrorMsg(excelAnnotation.name() + " 不能为空");
                                atomicReferenceErrorFlag.set(true);
                                break;
                            }
                        }
                    } catch (IllegalAccessException err) {
                        log.error("importLogisticsRulesFreightExcel err", err);
                    }
                }

                if (StringUtils.isBlank(logisticsRulesFreightImportReq.getErrorMsg())) {
                    LogisticsChannelDO logisticsChannelDO = logisticsChannelDOMap.get(logisticsRulesFreightImportReq.getLogistics() + logisticsRulesFreightImportReq.getChannel());
                    if (logisticsChannelDO == null) {
                        logisticsRulesFreightImportReq.setErrorMsg("物流信息不存在");
                        atomicReferenceErrorFlag.set(true);
                    }
                }
            });

            //有异常数据
            purchaseBlackProductConfigImportVO.setSuccess(atomicReferenceErrorFlag.get());
            if (atomicReferenceErrorFlag.get()) {
                String uuid = UUID.randomUUID().toString();
                String errorLocalFile = "/data/tmp/" + uuid + ".xls";
                File file = new File(errorLocalFile);
                ExcelUtil.getExport(logisticsRulesFreightImportReqList, null, "", LogisticsRulesFreightImportReq.class, new FileOutputStream(file));
                return file;
            }

            List<LogisticsRulesFreightDO> logisticsRulesFreightDOList = new ArrayList<>();
            logisticsRulesFreightImportReqList.forEach(logisticsRulesFreightImportReq -> {
                logisticsRulesFreightImportReq.setIsDisable(StringUtils.isBlank(logisticsRulesFreightImportReq.getIsDisable()) ? "否" : logisticsRulesFreightImportReq.getIsDisable());

                LogisticsChannelDO logisticsChannelDO = logisticsChannelDOMap.get(logisticsRulesFreightImportReq.getLogistics() + logisticsRulesFreightImportReq.getChannel());

                LogisticsRulesFreightDO logisticsRulesFreightDO = new LogisticsRulesFreightDO();
                logisticsRulesFreightDO.setId(SnowflakeIdUtils.getId());
                logisticsRulesFreightDO.setName(logisticsRulesFreightImportReq.getName());
                logisticsRulesFreightDO.setLogisticsCode(logisticsChannelDO.getLogisticsCode());
                logisticsRulesFreightDO.setChannelCode(logisticsChannelDO.getChannelCode());
                logisticsRulesFreightDO.setCountry(logisticsRulesFreightImportReq.getCountry());
                logisticsRulesFreightDO.setCountryAbbre(logisticsRulesFreightImportReq.getCountryAbbre());
                logisticsRulesFreightDO.setStartWeight(logisticsRulesFreightImportReq.getStartWeight());
                logisticsRulesFreightDO.setEndWeight(logisticsRulesFreightImportReq.getEndWeight());
                logisticsRulesFreightDO.setPegistrationMoney(logisticsRulesFreightImportReq.getPegistrationMoney());
                logisticsRulesFreightDO.setUnitPrice(logisticsRulesFreightImportReq.getUnitPrice());
                logisticsRulesFreightDO.setEarliestPrescriptionDays(logisticsRulesFreightImportReq.getEarliestPrescriptionDays());
                logisticsRulesFreightDO.setLatestPrescriptionDays(logisticsRulesFreightImportReq.getLatestPrescriptionDays());
                logisticsRulesFreightDO.setIsDisable(logisticsRulesFreightImportReq.getIsDisable().equals("是") ? 1 : 0);
                logisticsRulesFreightDOList.add(logisticsRulesFreightDO);
            });
            logisticsRulesFreightMapper.insertLogisticsRulesFreightBatch(logisticsRulesFreightDOList);
        } catch (IOException e) {
            log.error("读取excel数据异常 fileName:" + fileUrl, e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
        return null;
    }

    @Override
    public Boolean selectLogisticsRulesFreight(String channelCode, List<String> country) {

        if (channelCode != null) {
            LambdaQueryWrapper<LogisticsRulesFreightDO> logisticsRulesFreightDOLambdaQueryWrapper = Wrappers.<LogisticsRulesFreightDO>lambdaQuery();
            logisticsRulesFreightDOLambdaQueryWrapper.eq(LogisticsRulesFreightDO::getChannelCode, channelCode)
                    .eq(LogisticsRulesFreightDO::getIsDisable, 0)
                    .eq(LogisticsRulesFreightDO::getIsDeleted, 0);

            List<LogisticsRulesFreightDO> logisticsRulesFreightDOS = logisticsRulesFreightMapper.selectList(logisticsRulesFreightDOLambdaQueryWrapper);
            if (logisticsRulesFreightDOS == null || logisticsRulesFreightDOS.size() == 0) {
                return false;
            }else{
                if (country != null && country.size() > 0) {
                    for (String s:country){
                        boolean b=false;
                        for (LogisticsRulesFreightDO logisticsRulesFreightDO:logisticsRulesFreightDOS){
                            String countryAbbre = logisticsRulesFreightDO.getCountryAbbre();
                           if (countryAbbre!=null&&countryAbbre.equals(s)){
                                b=true;
                           }
                        }
                        if (!b){
                            return b;
                        }
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private List<LogisticsRulesFreightExcel> listLogisticsRulesFreight(ExportExcelReq<LogisticsRulesFreightReq> exportExcelReq) {
        LogisticsRulesFreightReq logisticsRulesFreightReq = Optional.ofNullable(exportExcelReq.getData()).orElse(new LogisticsRulesFreightReq());
        if (exportExcelReq.getIdList() != null && exportExcelReq.getIdList().size() != 0) {
            logisticsRulesFreightReq.setIdList(exportExcelReq.getIdList());
        }

        List<LogisticsRulesFreightDO> logisticsRulesFreightDOList = logisticsRulesFreightMapper.queryLogisticsRulesFreightList(null, logisticsRulesFreightReq);
        List<LogisticsRulesFreightExcel> logisticsRulesFreightExcelList = new ArrayList<>();
        logisticsRulesFreightDOList.forEach(logisticsRulesFreightDO -> {
            LogisticsRulesFreightExcel logisticsRulesFreightExcel = new LogisticsRulesFreightExcel();
            logisticsRulesFreightExcel.setName(logisticsRulesFreightDO.getName());
            logisticsRulesFreightExcel.setLogisticsName(logisticsRulesFreightDO.getLogisticsName());
            logisticsRulesFreightExcel.setChannelName(logisticsRulesFreightDO.getChannelName());
            logisticsRulesFreightExcel.setCountry(logisticsRulesFreightDO.getCountry());
            logisticsRulesFreightExcel.setStartWeight(logisticsRulesFreightDO.getStartWeight());
            logisticsRulesFreightExcel.setEndWeight(logisticsRulesFreightDO.getEndWeight());
            logisticsRulesFreightExcel.setPegistrationMoney(logisticsRulesFreightDO.getPegistrationMoney());
            logisticsRulesFreightExcel.setUnitPrice(logisticsRulesFreightDO.getUnitPrice());
            logisticsRulesFreightExcel.setEarliestPrescriptionDays(logisticsRulesFreightDO.getEarliestPrescriptionDays());
            logisticsRulesFreightExcel.setLatestPrescriptionDays(logisticsRulesFreightDO.getLatestPrescriptionDays());
            logisticsRulesFreightExcel.setIsDisable(logisticsRulesFreightDO.getIsDisable() == Constant.YES ? "是" : "否");
            logisticsRulesFreightExcelList.add(logisticsRulesFreightExcel);
        });
        return logisticsRulesFreightExcelList;
    }
}
