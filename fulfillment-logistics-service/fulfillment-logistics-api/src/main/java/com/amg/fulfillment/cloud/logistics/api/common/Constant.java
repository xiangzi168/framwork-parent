package com.amg.fulfillment.cloud.logistics.api.common;

/**
 * @author Tom
 * @date 2021-04-13-18:52
 */
public abstract class Constant {

    //--------------------------------------constant----------------------------------------------------------//
    public static final Integer YES = 1;
    public static final Integer NO = 2;
    public static final Integer NO_0 = 0;
    public static final Boolean STATUS_YES = true;  //单纯是否状态  是状态
    public static final Boolean STATUS_NO = false;  //单纯是否状态  否状态
    public static final String SUCCESS_STR = "成功";
    public static final String FAIL_STR = "失败";
    public static final String YES_CN = "是";
    public static final String NO_CN = "否";
    public static final String SYSTEM_ROOT = "root";
    //--------------------------------------depostitory---------------------------------------------------------//
    public static final String DEPOSITORY_WB = "WB";
    public static final String DEPOSITORY_WB_NAME = "万邦";
    public static final String DEPOSITORY_WAREHOUSECODE = "SZ";
    public static final String PURCHASE_WAY_STOCK = "StockReplenishment";
    public static final String PURCHASE_WAY_SALE = "SalesOrderFulfillment";
    public static final String DEFAULT_SALEORDER_PRODUCT_NAME_CN = "未知商品名称";
    public static final double DEFAULT_SALEORDER_PRODUCT_WEIGHT_KG = 0.001;

    //---------------------------------------depostitory--------------系统生产物流订单前缀-------------------------------//\
    public static final String DEPOSITORY_LOGO = "MALL";
    //----------------------------------------logistic--------------------------------------------------------//
    public static final String LOGISTIC_WB = "WB";
    public static final String LOGISTIC_YUNTU = "YUNTU";
    public static final String LOGISTIC_YANWEN = "YANWEN";
    public static final String LOGISTIC_PX4 = "4PX";
    public static final String LOGISTIC_TEST = "TEST";
    //---------------------------------------logistic--------------系统生产物流订单前缀-------------------------------//
    public static final String LOGISTIC_LOGO = "LOG";
    //------------------------------------------币种------------------------------------------------------//
    public static final String CURRENT_USD = "USD";
    //------------------------------------------尺码------------------------------------------------------//
    public static final String SIZE_CHINESE = "尺码,尺寸,Size,SIZE,size";
    //------------------------------------------1688------------------------------------------------------//
    public static final String WEBSITE = "1688";
    //------------------------------------------Rides key------------------------------------------------------//
    public static final String LOGISTICS_LOGISTIC_MATCH_RULE = "logistics:logistic:rule:match";
    public static final String LOGISTICS_LOGISTIC_FREIGHT_RULE = "logistics:logistic:rule:freight";
    public static final String LOGISTICS_DELIVERY_PACKAGE_SERIAL_NUMBER = "logistics:delivery:package:";
    public static final String LOGISTICS_DEPOSITORY_PREDICITON_PACKAGE_SERIAL_NUMBER = "logistics:depository:package:";

    //------------------------------------------MQ key------------------------------------------------------//
    public static final String LOGISTIC_TRACE_CHANGE_MQ = "logistic-trace-change";
    public static final String LOGISTIC_PURCHASE_STATUS_TAOBAO_PDD_NOTICE = "logistic-purchase-status-taobao-pdd-notice";
    public static final String LOGISTIC_PURCHASE_PREDICTION_WAYBILL_NOTICE = "logistic-purchase-prediction-waybill-notice";
    public static final Integer FILE_IMPORT_COUNT_CHECK = 1000;        //文件导入数量校验
}