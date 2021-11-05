package com.amg.fulfillment.cloud.logistics.controller;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.boot.utils.excel.ExcelUtil;
import com.amg.framework.boot.utils.id.SnowflakeIdUtils;
import com.amg.fulfillment.cloud.basic.common.filehandle.annotation.FileHandle;
import com.amg.fulfillment.cloud.basic.common.filehandle.enums.FileHandleTypeEnums;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsChannelDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsRulesFreightDO;
import com.amg.fulfillment.cloud.logistics.model.excel.CommodityLabelExcel;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.ImportExcelVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsLabelProductVO;
import com.amg.fulfillment.cloud.logistics.model.vo.ProductDetailVO;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsLabelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by Seraph on 2021/5/24
 */

@Api(tags={"物流标签接口"})
@RestController
@RequestMapping("/label/product")
public class LogisticsLabelProductController {

    @Autowired
    private ILogisticsLabelService logisticsLabelService;

    @ApiOperation(value = "物流标签商品列表", response = LogisticsLabelProductVO.class)
    @GetMapping("/list")
    public Page<LogisticsLabelProductVO> list(LogisticsLabelProductReq logisticsLabelProductReq)
    {
        return logisticsLabelService.productList(logisticsLabelProductReq);
    }

    @ApiOperation(value = "物流标签商品搜索", response = ProductDetailVO.class)
    @GetMapping("/sku/{sku}")
    public ProductDetailVO productSearch(@ApiParam(value = "sku", name ="sku", required = true)@PathVariable Long sku)
    {
        return logisticsLabelService.productSearch(sku);

    }
    @ApiOperation(value = "物流标签商品添加", response = Boolean.class)
    @PostMapping("/add")
    public Boolean add(@RequestBody @Valid LogisticsLabelProductAddReq logisticsLabelProductAddReq)
    {
        return logisticsLabelService.productAdd(logisticsLabelProductAddReq);
    }

    @ApiOperation(value = "物流标签商品修改", response = Boolean.class)
    @PostMapping("/update")
    public Boolean update(@RequestBody @Valid LogisticsLabelProductUpdateReq logisticsLabelProductUpdateReq)
    {
        return logisticsLabelService.productUpdate(logisticsLabelProductUpdateReq);
    }

    @ApiOperation(value = "物流标签商品删除", response = Boolean.class)
    @PostMapping("/del")
    public Boolean del(@RequestBody @Valid LogisticsLabelProductDelReq logisticsLabelProductDelReq)
    {
        return logisticsLabelService.productDel(logisticsLabelProductDelReq);
    }

    @PostMapping("/importCommodityLabelExcel")
    @ApiOperation(value = "物流标签导入", response = ImportExcelVO.class)
    @FileHandle(fileHandleType = FileHandleTypeEnums.UPLOAD)
    public File importCommodityLabelExcel(@RequestParam String fileUrl){
        return logisticsLabelService.importCommodityLabelExcel(fileUrl);
    }
}
