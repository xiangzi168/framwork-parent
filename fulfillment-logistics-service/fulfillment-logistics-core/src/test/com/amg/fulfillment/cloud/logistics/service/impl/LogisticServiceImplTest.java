//package com.amg.fulfillment.cloud.logistics.service.impl;
//
//import cn.hutool.core.date.DateUtil;
//import com.alibaba.fastjson.JSON;
//import com.amg.fulfillment.cloud.logistics.api.common.Constant;
//import com.amg.fulfillment.cloud.logistics.api.dto.depository.Address;
//import com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb.LogisticOrderForWanb;
//import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen.LogisticOrderForYanWen;
////import com.amg.fulfillment.cloud.logistics.dto.CurrencyDto;
//import com.amg.fulfillment.cloud.logistics.dto.depository.AddressDto;
//import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
//import com.amg.fulfillment.cloud.logistics.enumeration.LogisticTypeEnum;
//import com.amg.fulfillment.cloud.logistics.service.ILogisticService;
//import junit.framework.TestCase;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.FilterType;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ComponentScan(basePackages = {"com.amg.fulfillment.cloud.logistics"},
//    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,pattern = ".*grpc.*")
//)
//public class LogisticServiceImplTest extends TestCase {
//
//    @Autowired
//    private ILogisticService logisticService;
//
//    @Test
//    public void testGetLogisticTrackForWanB() {
//        //----------------------------------万邦------------------------
//        LogisticOrderSearchDto logisticOrderSearchDto = createLogisticSearchDto(LogisticTypeEnum.WANB.getCode(), "YT2111121272000001");
//        LogisticTrackResponseDto logisticTrackResponseDto = logisticService.getLogisticTrack(logisticOrderSearchDto);
//        System.out.println("输出 -->" + JSON.toJSONString(logisticTrackResponseDto));
////        Assert.assertEquals(LogisticTypeEnum.WANB.getCode(), logisticTrackResponseDto.getLogsiticCode());
////        Assert.assertTrue(logisticTrackResponseDto.isSuccessSign());
////        Assert.assertNotNull(logisticTrackResponseDto.getTrackPointDtos());
////        Assert.assertNotNull(logisticTrackResponseDto.getTrackingNumber());
//    }
//
//
//    @Test
//    public void testGetLogisticTrackForYanWen() {
//        //----------------------------------燕文------------------------
//        LogisticOrderSearchDto logisticOrderSearchDto = createLogisticSearchDto(LogisticTypeEnum.YANWEN.getCode(), "YT2111121272000001");
//        LogisticTrackResponseDto logisticTrackResponseDto = logisticService.getLogisticTrack(logisticOrderSearchDto);
//        System.out.println("输出 -->" + JSON.toJSONString(logisticTrackResponseDto));
////        Assert.assertEquals(LogisticTypeEnum.YANWEN.getCode(), logisticTrackResponseDto.getLogsiticCode());
////        Assert.assertTrue(logisticTrackResponseDto.isSuccessSign());
////        Assert.assertNotNull(logisticTrackResponseDto.getTrackPointDtos());
////        Assert.assertNotNull(logisticTrackResponseDto.getTrackingNumber());
//    }
//
//    @Test
//    public void testGetLogisticTrackForYunTu() {
//        //----------------------------------云途------------------------
//        LogisticOrderSearchDto logisticOrderSearchDto = createLogisticSearchDto(LogisticTypeEnum.YUNTU.getCode(), "YT2111121272000001");
//        LogisticTrackResponseDto logisticTrackResponseDto = logisticService.getLogisticTrack(logisticOrderSearchDto);
//        System.out.println("输出 -->" + JSON.toJSONString(logisticTrackResponseDto));
////        Assert.assertEquals(LogisticTypeEnum.YUNTU.getCode(), logisticTrackResponseDto.getLogsiticCode());
////        Assert.assertTrue(logisticTrackResponseDto.isSuccessSign());
////        Assert.assertNotNull(logisticTrackResponseDto.getTrackPointDtos());
////        Assert.assertNotNull(logisticTrackResponseDto.getWaybillNo());
//    }
//
//     @Test
//    public void testGetLogisticTrackFor4PX() {
//        //----------------------------------4PX------------------------
////        LogisticOrderSearchDto logisticOrderSearchDto = createLogisticSearchDto(LogisticTypeEnum.PX4.getCode(), "YIN201803280000002");
//        LogisticOrderSearchDto logisticOrderSearchDto = createLogisticSearchDto(LogisticTypeEnum.PX4.getCode(), " ");
//        LogisticTrackResponseDto logisticTrackResponseDto = logisticService.getLogisticTrack(logisticOrderSearchDto);
//        System.out.println("输出 -->" + JSON.toJSONString(logisticTrackResponseDto));
////        Assert.assertEquals(LogisticTypeEnum.PX4.getCode(), logisticTrackResponseDto.getLogsiticCode());
////        Assert.assertTrue(logisticTrackResponseDto.isSuccessSign());
////        Assert.assertNotNull(logisticTrackResponseDto.getTrackPointDtos());
////        Assert.assertNotNull(logisticTrackResponseDto.getWaybillNo());
//    }
//
//
//    @Test
//    public void testAddLogisticOrderForYanB() {
//        //----------------------------------万邦------------------------
//        WaybillGoodDetailDto waybillGoodDetailDto = new WaybillGoodDetailDto();
//        waybillGoodDetailDto.setDeclaredNameEn("Media");
//        waybillGoodDetailDto.setDeclaredNameCn("播放器");
//        waybillGoodDetailDto.setGoodsTitle("是一个播放器哦");
//        waybillGoodDetailDto.setQuantity(1);
//        waybillGoodDetailDto.setDeclaredValue(22.1);
//        waybillGoodDetailDto.setWeightInKg(12.1);
//        //特殊配置
//        RemarkOrderDto remarkOrderDto = new RemarkOrderDto();
//        remarkOrderDto.setWarehouseCode("SZ");
//        LogisticOrderDto logisticOrderDto = createLogisticOrderDto(LogisticTypeEnum.WANB.getCode(), "MALL2111121272000001", waybillGoodDetailDto,remarkOrderDto);
////        logisticOrderDto.setProduceLogisticOrder(Constant.YES);
//        LogisticOrderResponseDto logisticOrderResponseDto = logisticService.addLogisticOrder(logisticOrderDto);
//        System.out.println("输出 -->" + JSON.toJSONString(logisticOrderResponseDto));
//        Assert.assertEquals(LogisticTypeEnum.WANB.getCode(), logisticOrderResponseDto.getLogsiticCode());
//        Assert.assertTrue(logisticOrderResponseDto.isSuccessSign());
//        Assert.assertNotNull(logisticOrderResponseDto.getLogisticsOrderNo());
//    }
//
//
//    @Test
//    public void testAddLogisticOrderForYanWen() {
//        //----------------------------------燕文------------------------
//        WaybillGoodDetailDto waybillGoodDetailDto = new WaybillGoodDetailDto();
//        waybillGoodDetailDto.setDeclaredNameEn("Media");
//        waybillGoodDetailDto.setDeclaredNameCn("播放器");
//        waybillGoodDetailDto.setQuantity(1);
//        waybillGoodDetailDto.setDeclaredValue(12.1);
//        waybillGoodDetailDto.setWeightInKg(1.1);
//        //特殊配置
//        RemarkOrderDto remarkOrderDto = new RemarkOrderDto();
//        remarkOrderDto.setChannel("895");
//        LogisticOrderDto logisticOrderDto = createLogisticOrderDto(LogisticTypeEnum.YANWEN.getCode(), "MALL2111121272000001", waybillGoodDetailDto,remarkOrderDto);
////        logisticOrderDto.setProduceLogisticOrder(Constant.YES);
//        LogisticOrderResponseDto logisticOrderResponseDto = logisticService.addLogisticOrder(logisticOrderDto);
//        System.out.println("输出 -->" + JSON.toJSONString(logisticOrderResponseDto));
//        Assert.assertEquals(LogisticTypeEnum.YANWEN.getCode(), logisticOrderResponseDto.getLogsiticCode());
//        Assert.assertEquals("没有错误", logisticOrderResponseDto.getMessage());
//        Assert.assertTrue(logisticOrderResponseDto.isSuccessSign());
//        Assert.assertNotNull(logisticOrderResponseDto.getTrackingNumber());
//    }
//
//    @Test
//    public void testAddLogisticOrderForYunTu() {
//        //----------------------------------云途------------------------
//        WaybillGoodDetailDto waybillGoodDetailDto = new WaybillGoodDetailDto();
//        waybillGoodDetailDto.setDeclaredNameEn("Media");
//        waybillGoodDetailDto.setDeclaredNameCn("播放器");
//        waybillGoodDetailDto.setQuantity(1);
//        waybillGoodDetailDto.setDeclaredValue(22.1);
//        waybillGoodDetailDto.setWeightInKg(12.1);
//        LogisticOrderDto logisticOrderDto = createLogisticOrderDto(LogisticTypeEnum.YUNTU.getCode(), "MALL2111121272000001", waybillGoodDetailDto, null);
////        logisticOrderDto.setProduceLogisticOrder(Constant.YES);
//        LogisticOrderResponseDto logisticOrderResponseDto = logisticService.addLogisticOrder(logisticOrderDto);
//        System.out.println("输出 -->" + JSON.toJSONString(logisticOrderResponseDto));
//        Assert.assertEquals(LogisticTypeEnum.YUNTU.getCode(), logisticOrderResponseDto.getLogsiticCode());
//        Assert.assertTrue(logisticOrderResponseDto.isSuccessSign());
//        Assert.assertNotNull(logisticOrderResponseDto.getWaybillNo());
//    }
//
//    @Test
//    public void testAddLogisticOrderFor4PX() {
//        WaybillGoodDetailDto waybillGoodDetailDto = new WaybillGoodDetailDto();
//        waybillGoodDetailDto.setDeclaredNameEn("Media");
//        waybillGoodDetailDto.setDeclaredNameCn("播放器");
//        waybillGoodDetailDto.setQuantity(1);
//        waybillGoodDetailDto.setDeclaredValue(22.1);
//        waybillGoodDetailDto.setWeightInKg(12.1);
//        LogisticOrderDto logisticOrderDto = createLogisticOrderDto(LogisticTypeEnum.PX4.getCode(), "YIN201803280000004", waybillGoodDetailDto, null);
////        logisticOrderDto.setProduceLogisticOrder(Constant.YES);
//        LogisticOrderResponseDto logisticOrderResponseDto = logisticService.addLogisticOrder(logisticOrderDto);
//        System.out.println("输出 -->" + JSON.toJSONString(logisticOrderResponseDto));
//        Assert.assertEquals(LogisticTypeEnum.PX4.getCode(), logisticOrderResponseDto.getLogsiticCode());
//        Assert.assertTrue(logisticOrderResponseDto.isSuccessSign());
//        Assert.assertEquals("System processing succeeded",logisticOrderResponseDto.getMessage());
//    }
//
//
//
//
//    public LogisticOrderSearchDto createLogisticSearchDto(String logisticCode, String trackingNumber) {
//        LogisticOrderSearchDto logisticOrderSearchDto = new LogisticOrderSearchDto();
//        logisticOrderSearchDto.setLogisticCode(logisticCode);
//        logisticOrderSearchDto.setTrackingNumber(trackingNumber);
//        return logisticOrderSearchDto;
//    }
//
//    public LogisticOrderDto createLogisticOrderDto(String logisticCode, String logisticOrderNo, WaybillGoodDetailDto waybillGoodDetailDto, RemarkOrderDto remarkOrderDto) {
//
//        // 货币
////        CurrencyDto currencyDto = new CurrencyDto();
////        currencyDto.setTotalMoney(12.33);
////        currencyDto.setMoneyCode("USD");
//        // 收件地址
//        AddressDto receiver = new AddressDto();
//        receiver.setContacter("tom");
//        receiver.setStreet1("Street1");
//        receiver.setCity("NewYork");
//        receiver.setProvince("NY");
//        receiver.setPostCode("NW1 6XE");
//        receiver.setCountryCode("GB");
//        receiver.setTel("15811324456");
//        // 寄件地址
//        AddressDto sender = new AddressDto();
//        sender.setContacter("jerry");
//        sender.setCountryCode("CN");
//        sender.setCity("shenzhen");
//        LogisticOrderDto logisticOrderDto = new LogisticOrderDto();
//        logisticOrderDto.setLogisticCode(logisticCode);
//        logisticOrderDto.setLogisticOrderNo(logisticOrderNo);
////        logisticOrderDto.setLength(1.2);
////        logisticOrderDto.setWidth(1.23);
////        logisticOrderDto.setHeight(4.2);
////        logisticOrderDto.setLength(3.0);
////        logisticOrderDto.setUnit("M");
////        logisticOrderDto.setPackageCount(1);
////        logisticOrderDto.setReceiverAddress(receiver);
////        logisticOrderDto.setSendAddress(sender);
//////        logisticOrderDto.setSendDate(DateUtil.parseDateTime("2021-04-23 12:00:00").toJdkDate());
//////        logisticOrderDto.setQuantity(2);
////        logisticOrderDto.setWeightInKg(1.22);
////        logisticOrderDto.setPackageType("SPX");
////        logisticOrderDto.setCurrencyDto(currencyDto);
////        // 申报信息
////        logisticOrderDto.setWaybillGoodDetailDtos(Collections.singletonList(waybillGoodDetailDto));
////        // 特殊信息
////        logisticOrderDto.setRemarkOrderDto(remarkOrderDto);
//        return logisticOrderDto;
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    //---------------------------------------------调试-----------------------------------------------------------
//    public LogisticOrderSearchDto createLogisticWB() {
//        String trackingNumber = "SBXTT0000057001YQ";
//        LogisticOrderSearchDto logisticOrderSearchDto = new LogisticOrderSearchDto();
//        logisticOrderSearchDto.setLogisticCode("");
//        logisticOrderSearchDto.setTrackingNumber(trackingNumber);
//        return logisticOrderSearchDto;
//    }
//
//    public LogisticOrderForWanb createlogisticWB() {
//        Address address = new Address();
//        address.setContacter("tom");
//        address.setStreet1("Street1");
//        address.setCity("NewYork");
//        address.setProvince("NY");
//        address.setPostCode("NW1 6XE");
//        address.setCountryCode("GB");
//
//        LogisticOrderForWanb.ItemDetail itemDetail = LogisticOrderForWanb.ItemDetail.builder()
//                .GoodsTitle("测试标题")
//                .DeclaredNameCn("测试")
//                .DeclaredNameEn("TEST")
//                .DeclaredValue(new LogisticOrderForWanb.Money("USD", 12.222))
//                .WeightInKg(Double.valueOf(1))
//                .Quantity(1).build();
//        LogisticOrderForWanb logisticOrderForWanb =
//                LogisticOrderForWanb.builder()
//                        .ReferenceId("MALL1000000008")
//                        .ShippingAddress(address)
//                        .WeightInKg(Double.valueOf(1.13))
//                        .ItemDetails(Arrays.asList(itemDetail))
//                        .TotalValue(new LogisticOrderForWanb.Money("USD", 22.222))
//                        .TotalVolume(new LogisticOrderForWanb.CubeSize(Double.valueOf(1), Double.valueOf(1), Double.valueOf(1.01), "CM"))
//                        .WithBatteryType("NOBattery")
//                        .WarehouseCode("SZ")
//                        .ShippingMethod("3HPA")
//                        .ItemType("SPX")
//                        .build();
//        return logisticOrderForWanb;
//    }
//
//
//    public LogisticOrderForYanWen createlogisticYanWen() {
//        //收件人
//        LogisticOrderForYanWen.Receiver receiver = LogisticOrderForYanWen.Receiver.builder()
////                .Userid(userId)
//                .Name("tom")
//                .Phone("15811456692")
//                .Country("us")
//                .Postcode("253400")
//                .State("FL")
//                .City("city")
//                .Address1("address").build();
//        //发件
//        LogisticOrderForYanWen.Sender sender = LogisticOrderForYanWen.Sender.builder()
//                .TaxNumber("xxxx").build();
//        //商品
//        LogisticOrderForYanWen.GoodsName goodsName = LogisticOrderForYanWen.GoodsName.builder()
////                .Userid(userId)
//                .NameCh("多媒体播放器")
//                .NameEn("MedialPlayer")
//                .Weight(122)
//                .DeclaredValue(12.334)
//                .DeclaredCurrency("USD").build();
//        LogisticOrderForYanWen orderForYanWen = LogisticOrderForYanWen.builder()
////                .Userid(userId)
//                .Channel("895")
//                .UserOrderNumber("mall12314545")
//                .SendDate(DateUtil.now())
//                .Receiver(receiver)
//                .Sender(sender)
//                .Quantity(1)
//                .GoodsName(goodsName).build();
//        return orderForYanWen;
//    }
//}