//package com.amg.fulfillment.cloud.logistics.service.impl;
//
//import com.amg.fulfillment.cloud.logistics.api.config.depository.WanBConfig;
//import com.amg.fulfillment.cloud.logistics.api.dto.depository.GoodSku;
//import com.amg.fulfillment.cloud.logistics.api.dto.depository.ProductVariant;
//import com.amg.fulfillment.cloud.logistics.api.common.Constant;
//import com.amg.fulfillment.cloud.logistics.dto.depository.*;
//import com.amg.fulfillment.cloud.logistics.service.IDepositoryService;
//import junit.framework.TestCase;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@EnableConfigurationProperties(value = {WanBConfig.class})
//public class DepositoryServiceImplTest extends TestCase {
//
//    @Autowired
//    IDepositoryService depositoryService;
//
//    @Test
//    public void testAddPurchaseOrder() {
//        //商品
//        ProductDto productDto1 = new ProductDto();
//        productDto1.setSku("1138");
//        productDto1.setQuantity(1);
//        ProductDto productDto2 = new ProductDto();
//        productDto2.setSku("1139");
//        productDto2.setQuantity(2);
//        //物流
//        ExpressDto expressDto = new ExpressDto();
//        expressDto.setExpressNo("123545221");
//        DepositoryPurchaseOrderDto depositoryPurchaseOrderDto = new DepositoryPurchaseOrderDto();
//        depositoryPurchaseOrderDto.setDepositoryCode("WB");
//        depositoryPurchaseOrderDto.setRemark("测试采购订单备注");
//        depositoryPurchaseOrderDto.setWarehouseCode("SZ");
//        depositoryPurchaseOrderDto.setPurchaseWay(Constant.PURCHASE_WAY_STOCK);
//        depositoryPurchaseOrderDto.setProducts(Arrays.asList(productDto1,productDto2));
//        depositoryPurchaseOrderDto.setExpresses(Arrays.asList(expressDto));
//        PurchaseResultDto purchaseResultDto = depositoryService.addPurchaseOrder(depositoryPurchaseOrderDto);
//        System.out.println("输出：" + purchaseResultDto);
//        Assert.assertNotNull(purchaseResultDto.getStatus());
//        Assert.assertTrue(purchaseResultDto.getSuccessSign());
//        Assert.assertNotNull(purchaseResultDto.getCreateDate());
//
//
//    }
//
//    @Test
//    public void testAddOutDepositoryOrder() {
//        AddressDto address = new AddressDto();
//        address.setContacter("tome");
//        address.setPostCode("GB");
//        OutDepositoryOrderItemDto item1 = new OutDepositoryOrderItemDto("YCSBNG8361455", 1);
//        ExpressDto expressDto = new ExpressDto();
//        expressDto.setServiceId("CM_DHL");
//        expressDto.setTrackingNumber("00340009923334552189");
//        expressDto.setLabelUrl("http://label.sample.com/00340009923334552189.pdf");
//        OutDepositoryOrderDto outDepositoryOrderDto = new OutDepositoryOrderDto();
//        outDepositoryOrderDto.setDepositoryCode("WB");
//        outDepositoryOrderDto.setDispatchOrderId("WB1382545050135433216");
//        outDepositoryOrderDto.setProduceDispatchOrder(Constant.YES);
//        outDepositoryOrderDto.setWarehouseCode("SZ");
//        outDepositoryOrderDto.setRemark("4月14号测试备注");
//        outDepositoryOrderDto.setEstimatedWeight(0.012);
//        outDepositoryOrderDto.setAddress(address);
//        outDepositoryOrderDto.setOrderItems(Arrays.asList(item1));
//        outDepositoryOrderDto.setExpress(expressDto);
//        OutDepositoryResultDto outDepositoryResultDto = depositoryService.addOutDepositoryOrder(outDepositoryOrderDto);
//        System.out.println("输出：" + outDepositoryResultDto);
//        Assert.assertNotNull(outDepositoryResultDto.getRefId());
//        Assert.assertNull(outDepositoryResultDto.getErrorMsg());
//        Assert.assertTrue("WB".equals(outDepositoryResultDto.getDeposityCode()));
//    }
//
//    @Test
//    public void testCancelOutDepositoryOrder() {
//        OutDepositoryOrderDto outDepositoryOrderDto = new OutDepositoryOrderDto();
//        outDepositoryOrderDto.setDispatchOrderId("WB1382545050135433216");
//        outDepositoryOrderDto.setWarehouseCode("SZ");
//        outDepositoryOrderDto.setDepositoryCode("WB");
//        OutDepositoryResultDto outDepositoryResultDto = depositoryService.cancelOutDepositoryOrder(outDepositoryOrderDto);
//        System.out.println("输出：" + outDepositoryResultDto);
//        Assert.assertNotNull(outDepositoryResultDto.getCancellStatus());
//        Assert.assertNotNull(outDepositoryResultDto.getCancellRequestOn());
//    }
//
//    @Test
//    public void testGetOutDepositoryOrder() {
//        OutDepositoryOrderDto outDepositoryOrderDto = new OutDepositoryOrderDto();
//        outDepositoryOrderDto.setDepositoryCode("WB");
////        outDepositoryOrderDto.setDispatchOrderId("DTAL2011000008021111");
//        outDepositoryOrderDto.setDispatchOrderId("WB1382545050135433216");
//        OutDepositoryOrderResultDto outDepositoryOrder = depositoryService.getOutDepositoryOrder(outDepositoryOrderDto);
//        System.out.println("输出：" + outDepositoryOrder);
//        Assert.assertNotNull(outDepositoryOrder.getWbData().getStatus());
//        Assert.assertNotNull(outDepositoryOrder.getWbData().getCreateOn());
//    }
//
//    @Test
//    public void testGetDepositoryCount() {
//        WanBSearchDto wanBSearchDto = new WanBSearchDto();
//        wanBSearchDto.setSku("YCSBNG8361455");
//        wanBSearchDto.setWarehouseCode("SZ");
//        wanBSearchDto.setType("InStock");
//        DepositorySearchDto depositorySearchDto = new DepositorySearchDto();
//        depositorySearchDto.setDepositoryCode("WB");
//        depositorySearchDto.setWanBSearchDto(wanBSearchDto);
//        List<InventoryDto> depositoryCount = depositoryService.getDepositoryCount(depositorySearchDto);
//        System.out.println("输出：" + depositoryCount);
//        Assert.assertTrue(depositoryCount.size()>0);
//        Assert.assertNotNull(depositoryCount.get(0).getSku());
//    }
//
//    @Test
//    public void testAddMaterialToDepository() {
//        ProductVariant productVariant = new ProductVariant("spec", "采购规格1");
//        GoodSku goodSku = new GoodSku();
//        goodSku.setDepositoryCode("WB");
//        goodSku.setSku("sd01100000802");
//        goodSku.setName("B3090臧");
//        goodSku.setWeightInKg(0.0020);
//        goodSku.setVariants(Collections.singletonList(productVariant));
//        OutDepositoryResultDto outDepositoryResultDto = depositoryService.addMaterialToDepository(goodSku);
//        System.out.println("输出：" + outDepositoryResultDto);
//        Assert.assertTrue(outDepositoryResultDto.isSuccessSign());
//        Assert.assertNull(outDepositoryResultDto.getErrorMsg());
//    }
//
//    @Test
//    public void testGetMaterialFromDepository() {
//        GoodSku goodSku = new GoodSku();
//        goodSku.setDepositoryCode("WB");
//        goodSku.setSku("sd01100000802");
//        GoodSku materialFromDepository = depositoryService.getMaterialFromDepository(goodSku);
//        System.out.println("输出：" + materialFromDepository);
//        Assert.assertNotNull(materialFromDepository.getSku());
//        Assert.assertTrue(materialFromDepository.getVariants().size()>0);
//        Assert.assertTrue("spec".equals(materialFromDepository.getVariants().get(0).getName()));
//    }
//}