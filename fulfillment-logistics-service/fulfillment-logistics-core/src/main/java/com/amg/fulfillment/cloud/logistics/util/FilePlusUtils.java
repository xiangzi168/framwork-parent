package com.amg.fulfillment.cloud.logistics.util;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.amg.fulfillment.cloud.basic.common.oss.aliyun.utils.OSSUtils;
import com.amg.fulfillment.cloud.logistics.model.req.ExportExcelReq;
import com.amg.fulfillment.cloud.order.api.util.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Seraph on 2021/5/13
 */
public class FilePlusUtils {

    public static File exportExcel(ExportExcelReq exportExcelReq, List<?> list, Class clazz)
    {
        String title = StringUtils.isBlank(exportExcelReq.getTitle()) ? "title" : exportExcelReq.getTitle();
        String sheetName = StringUtils.isBlank(exportExcelReq.getSheetName()) ? "sheetName" : exportExcelReq.getSheetName();
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(true);
        String fileName = exportExcelReq.getFileName();
        String excelName =exportExcelReq.getFileName().substring(0, fileName.indexOf(".")) + ".xls";

        return FileUtils.defaultExportZip(list, clazz, excelName, fileName, exportParams);
    }

    public static <T> List<T> downloadFile(String filePath, Class clazz)
    {
        return FilePlusUtils.downloadFile(filePath, clazz, 1, 1);
    }

    public static <T> List<T> downloadFile(String filePath, Class clazz, Integer titleRows, Integer headerRows)
    {
        File file = OSSUtils.downloadFile(filePath);
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        return ExcelImportUtil.importExcel(file, clazz, params);
    }
}
