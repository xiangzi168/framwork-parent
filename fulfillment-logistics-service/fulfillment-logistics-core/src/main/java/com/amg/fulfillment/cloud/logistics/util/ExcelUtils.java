package com.amg.fulfillment.cloud.logistics.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;

/**
 * Created by Seraph on 2021/5/26
 */

@Slf4j
public class ExcelUtils {
    public static String getNormativeFileName(String fileName) {
        String replace1 = fileName.replace(":", "");
        String replace2 = replace1.replace("-", "");
        return replace2;
    }

}
