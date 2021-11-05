package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/4/29
 */

@Getter
public enum UrlConfigEnum {
    ;

    @Getter
    public enum UrlConfigEnumPX4
    {
        //直发服务
        DS_XMS_ORDER_CANCEL("ds.xms.order.cancel", "取消直发委托单", "客户通过此接口取消委托单 适用场景：已调用创建并预报接口（订单已预报状态），货还在您手中"),
        DS_XMS_ORDER_HOLD("ds.xms.order.hold", "申请|取消拦截订单", "申请拦截订单成功，将在订单列表显示成功标识，4PX拦截成功之后将作为问题件管理，您可在问题件看到拦截件 适用场景：已调用创建并预报接口（订单已预报状态），货正在送往4PX仓的路上"),
        DS_XMS_BAG_CREATE("ds.xms.bag.create", "直发授权-完成装袋", "直发授权-完成装袋"),
        DS_XMS_LOGISTICS_PRODUCT_GETLIST("ds.xms.logistics_product.getlist", "物流产品查询", "物流产品查询"),
        DS_XMS_BATCH_OUTBOUND_CREATE("ds.xms.batch_outbound.create", "直发授权-出库预报", "直发授权-出库预报"),
        DS_XMS_BAG_LABEL_GET("ds.xms.bag_label.get", "直发授权-袋标签", "直发授权-袋标签"),
        DS_XMS_ORDER_INBOUND_CREATE("ds.xms.order_inbound.create", "直发授权-单票入库", "直发授权-单票入库"),
        DS_XMS_ORDER_OUTBOUND_CREATE("ds.xms.order_outbound.create", "直发授权-单票出库", "直发授权-单票出库"),
        DS_XMS_ORDER_AIRLINE_CREATE("ds.xms.order_airline.create", "直发授权-单票交航", "直发授权-单票交航"),
        DS_XMS_DEPARTURE_MAILITEM("ds.xms.departure.mailitem", "直发授权-批量单票出库预备", "直发授权-批量单票出库预备"),
        DS_XMS_ORDER_UPDATEWEIGHT("ds.xms.order.updateweight", "更新预报重", "更新预报重（只有已预报且未到仓的包裹订单，能更新预报重）"),
        DS_XMS_ESTIMATED_COST_GET("ds.xms.estimated_cost.get", "预估费用查询/运费试算", "预估费用查询/运费试算"),
        DS_XMS_ORDER_GET("ds.xms.order.get", "查询直发委托单", "支持两种场景：1.单个查询（通过请求单号、单号类型查询） 2.批量查询（时间组合+委托单状态） 注意：调用logistics_channel_no接口时，物流渠道号码（logistics_channel_no）和物流渠道商（logistics_channel_name）如果有值则返回，如果没有则返回为空。某些物流产品在4PX仓内换号的，需要在库内作业中才能查询到这两个字段。"),
        DS_XMS_LABEL_GET("ds.xms.label.get", "获取标签", "暂时支持特定客户、特定产品预生成标签才能直接从4PX服务器获取标签，该模式需要找业务人员申请"),
        DS_XMS_LABEL_GETLIST("ds.xms.label.getlist", "批量获取标签", "批量获取标签"),
        DS_XMS_ORDER_CREATE("ds.xms.order.create", "创建直发委托单", "下发包裹维度的直发委托 1.0.0版本：公共版本 1.1.0版本：公共版本+预生成标签（预生成标签暂时对部分客户开放）"),

        //物流轨迹
        TR_ORDER_TRACKING_GET("tr.order.tracking.get", "物流轨迹查询", "物流轨迹查询"),
        COM_TRACK_BUSINESS_CREATE("com.track.business.create", "添加业务数据", "添加业务数据"),

        ;

        UrlConfigEnumPX4(String url, String name, String desc)
        {
            this.url = url;
            this.name = name;
            this.desc = desc;
        }

        private String url;
        private String name;
        private String desc;
    }

    @Getter
    public enum UrlConfigEnumWanB
    {
        //包裹 API 列表
        LABEL("/api/parcels/%s/label", "获取包裹的标签", "该接口用于获取包裹的标签文件。标签为PDF文件。"),
        LABELS("/api/parcels/labels?processCodes=%s", "批量获取包裹的标签", "该接口用于获取包裹的标签文件。由于不同的渠道的标签尺寸、页数都均有可能不一样，为了减少不必要的麻烦， 此接口限定只能对同一发货渠道的包裹进行批量打印！"),
        TRACKPOINTS("/api/trackPoints?trackingNumber=%s", "查询轨迹", "通过此接口，可以使用处理号、跟踪号或者客单号查询包裹的轨迹信息。"),
        WAREHOUSES("/api/warehouses", "获取全部仓库信息", "获取全部仓库信息"),
        SERVICES("/api/services", "获取产品服务信息", "获取产品服务信息"),
        CANCELLATION_DELETE("/api/parcels/%s/cancellation", "取消截件/继续发货", "取消截件/继续发货"),
        CANCELLATION_POST("/api/parcels/%s/cancellation", "取消发货/截件", "取消截件/继续发货"),
        SEARCH_PARCELS("/api/parcels?referenceId=%s", "搜索包裹", "搜索包裹"),
        PARCELS_INFO("/api/parcels/%s", "获取包裹信息", "获取包裹信息"),
        PARCELS_DELETE("/api/parcels/%s", "删除包裹", "在确认交运前，可通过此接口删除包裹"),
        PARCELS_CONFIRMATION_POST("/api/parcels/%s/confirmation", "确认交运包裹", "该接口用于确认交运包裹。确认交运意味着所有数据都已经提交到WanbExpress系统并保证为真实有效的数据。对于没有确认交运的包裹，就算货物到了我司仓库，我们也不会处理。"),
        PARCELS_CONFIRMATIONS_PATCH("/api/parcels.confirmations", "批量确认交运包裹", "此接口虽为批量操作接口，但并不具有原子性。仍需要解析返回结果中每票的操作结果。"),
        PARCELS_ITEMDETAILS("/api/parcels/%s/itemDetails", "修改包裹件内明细", "在确认交运前，可通过此接口用于修改包裹件内明细。包含SKU、产品名称、申报品名、申报价值等信息。"),
        PARCELS_CUSTOMERWEIGHTS_PATCH("/api/parcels.customerWeights", "批量修改包裹重量", "通过此接口，可以跟据处理号或者客单号，批量修改包裹的重量。"),
        PARCELS_CUSTOMERWEIGHT_PUT("/api/parcels/%s/customerWeight", "修改包裹预报重量", "该接口用于修改包裹预报重量。"),
        CREATE_PARCELS("/api/parcels", "创建包裹", "创建包裹。"),
        WHOAMI("/api/whoami", "验证API授权", "验证API授权。"),

        //来货/揽收袋 API 列表
        PARCELCOLLECTBAGS("/api/parcelCollectBags", "创建来货/揽收袋", "创建来货/揽收袋"),
        UDPATE_PARCELCOLLECTBAGS_BASICINFO("/api/parcelCollectBags/%s/basicInfo", "修改来货/揽收袋信息", "通过此接口，可以修改袋子的重量、尺寸、预计到仓时间等信息"),
        UDPATE_PARCELCOLLECTBAGS_ITEMS("/api/parcelCollectBags/%s/items", "更新来货/揽收袋内包裹明细", "通过此接口，通过传入处理号或者包裹客单号的方式更新袋内包裹的明细"),
        PARCELCOLLECTBAGS_CONFIRMATION("/api/parcelCollectBags/%s/confirmation", "确认交运来货/揽收袋", "通过些接口可以确认袋子重量以及袋内明细已经准备并可以交付我司处理。"),
        DELETE_PARCELCOLLECTBAGS("/api/parcelCollectBags/%s", "删除交运来货/揽收袋", "通过些接口可以删除未确认交运的袋子。"),
        GET_PARCELCOLLECTBAGS("/api/parcelCollectBags/%s", "获取来货/揽收袋", "通过些接口可以获取来货袋信息。"),
        SEARCH_PARCELCOLLECTBAGS("/api/parcelCollectBags?referenceIds=%s", "搜索来货揽收袋", "通过些接口可用您的袋号来指搜索来货袋信息。"),

        //仓库
        SALESORDERS("/api/rps/salesOrders/%s", "推送销售订单接口", "推送销售订单接口"),
        PURCHASEORDERS("/api/rps/purchaseOrders/%s", "推送备货采购订单路径", "送备货采购订单路径"),
        UPDATEPURCHASEORDERS("/api/rps/purchaseOrders/%s/productContent", "推送备货采购订单路径", "更新采购订单路径"),
        DISPATCHORDERS("/api/rps/dispatchOrders/%s", "推送出库", "推送出库"),
        DISPATCHORDERS_CANCELLATION("/api/rps/dispatchOrders/%s/cancellation", "取消出库订单路径", "取消出库订单路径"),
        INVENTORIES("/api/rps/inventories/%s?warehouseCode=%s&type=%s", "查询产品库存路径", "查询产品库存路径"),
        TRADEITEMS("/api/rps/tradeItems/%s", "推送/查询产品资料路径", "推送/查询产品资料路径")

        ;


        UrlConfigEnumWanB(String url, String name, String desc)
        {
            this.url = url;
            this.name = name;
            this.desc = desc;
        }

        private String url;
        private String name;
        private String desc;
    }

    @Getter
    public enum UrlConfigEnumYanWen
    {
        GETCHANNELS("/USERS/%s/GETCHANNELS", "2.获取发货渠道", ""),
        EXPRESSES("/USERS/%s/EXPRESSES", "3.新建快件信息", ""),
        EXPRESSES_INFO("/USERS/%s/EXPRESSES?PAGE=%s&CODE=%s&RECEIVER=%s&CHANNEL=%s&START=%s&END=%s&ISSTATUS=%s", "4.按条件查询快件信息：", ""),
        LABEL("/USERS/%s/EXPRESSES/%s/%sLABEL", "5.单个标签生成", ""),
        LABELS("/USERS/%s/EXPRESSES/%sLABEL", "6.多标签生成", ""),
        ONLINEDATA("/USERS/%s/ONLINEDATA", "7.新建线上数据信息", ""),
        CHANGESTATUS("/USERS/%s/EXPRESSES/CHANGESTATUS/%s", "8.调整快件状态", ""),
        COUNTRIES("/USERS/%s/CHANNELS/%s/COUNTRIES", "9.获取产品可达国家", ""),
        GETONLINECHANNELS("/USERS/%s/GETONLINECHANNELS", "10．获取线上发货渠道", "")
        ;

        UrlConfigEnumYanWen(String url, String name, String desc)
        {
            this.url = url;
            this.name = name;
            this.desc = desc;
        }

        private String url;
        private String name;
        private String desc;
    }

    @Getter
    public enum UrlConfigEnumYunTu
    {
        COMMON_GETCOUNTRY("/api/Common/GetCountry", "2.1 查询国家简码", "客户端向 OMS 请求查询国家简码接口。"),
        COMMON_GETSHIPPINGMETHODS("/api/Common/GetShippingMethods", "2.2 查询运输方式", "客户端向 OMS 请求查询运输方式接口。"),
        COMMON_GETGOODSTYPE("/api/Common/GetGoodsType", "2.3 查询货品类型", "客户端向 OMS 请求查询货品类型接口。"),
        FREIGHT_GETPRICETRIAL("/api/Freight/GetPriceTrial", "2.4 查询价格", "客户端向 OMS 请求查询报价接口。"),
        WAYBILL_GETTRACKINGNUMBER("/api/Waybill/GetTrackingNumber", "2.5 查询跟踪号", "客户端向 OMS 请求查询跟踪号接口。(30 分钟缓存)"),
        WAYBILL_GETSENDER("/api/WayBill/GetSender", "2.6 查询发件人信息", "系统将分配好的发件人信息返回给客户"),
        WAYBILL_CREATEORDER("/api/WayBill/CreateOrder", "2.7 运单申请", "客户端向 OMS 请求运单申请。"),
        WAYBILL_GETORDER("/api/WayBill/GetOrder?OrderNumber=%s", "2.8 查询运单", "客户端向 OMS 请求运单查询。"),
        WAYBILL_UPDATEWEIGHT("/api/WayBill/UpdateWeight", "2.9 修改订单预报重量", "客户端向 OMS 发送需要修改的订单重量。"),
        WAYBILL_DELETE("/api/WayBill/Delete", "2.10 订单删除", "客户端向 OMS 发送需要删除的订单单号。"),
        WAYBILL_INTERCEPT("/api/WayBill/Intercept", "2.11 订单拦截", "客户端向 OMS 发送需要拦截的订单单号。"),
        LABEL_PRINT("/api/Label/Print", "2.12 标签打印", "客户端向 OMS 请求标签打印地址。"),
        FREIGHT_GETSHIPPINGFEEDETAIL("/api/Freight/GetShippingFeeDetail", "2.13 查询物流运费明细", "客户端向 OMS 请求地址验证。"),
        COMMON_REGISTER("/api/Common/Register", "2.14 用户注册", "客户端向 OMS 请求用户注册。"),
        TRACKING_GETTRACKALLINFO("/api/Tracking/GetTrackAllInfo?OrderNumber=%s", "2.15 查询物流轨迹信息", "客户端向 OMS 请求物流轨迹信息;"),
        WAYBILL_GETCARRIER("/api/Waybill/GetCarrier", "2.16 查询末端派送商", "客户端向 OMS 请求查询 末端派送商接 口。")
        ;

        UrlConfigEnumYunTu(String url, String name, String desc)
        {
            this.url = url;
            this.name = name;
            this.desc = desc;
        }

        private String url;
        private String name;
        private String desc;
    }
}
