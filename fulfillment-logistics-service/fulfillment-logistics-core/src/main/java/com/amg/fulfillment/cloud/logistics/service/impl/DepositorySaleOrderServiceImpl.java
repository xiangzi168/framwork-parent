package com.amg.fulfillment.cloud.logistics.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.fulfillment.cloud.logistics.api.client.InventoryStoreClient;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.ProductVariant;
import com.amg.fulfillment.cloud.logistics.api.enumeration.ChangeLogoByOrderFromEnum;
import com.amg.fulfillment.cloud.logistics.api.proto.InventoryStoreGTO;
import com.amg.fulfillment.cloud.logistics.api.util.BeanConvertUtils;
import com.amg.fulfillment.cloud.logistics.dto.depository.DepositorySaleOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.depository.ProductDto;
import com.amg.fulfillment.cloud.logistics.dto.depository.SaleOrderResultDto;
import com.amg.fulfillment.cloud.logistics.entity.DepositoryProductDO;
import com.amg.fulfillment.cloud.logistics.entity.DepositorySaleOrderDO;
import com.amg.fulfillment.cloud.logistics.entity.DepositorySaleOrderDetailDO;
import com.amg.fulfillment.cloud.logistics.enumeration.BaseLogisticsResponseCodeEnum;
import com.amg.fulfillment.cloud.logistics.enumeration.SizesEnToCnEnum;
import com.amg.fulfillment.cloud.logistics.factory.DepositoryFactory;
import com.amg.fulfillment.cloud.logistics.grpc.SaleOrderAssignmentPurchaseIdService;
import com.amg.fulfillment.cloud.logistics.manager.IDepositoryManager;
import com.amg.fulfillment.cloud.logistics.mapper.DepositorySaleOrderMapper;
import com.amg.fulfillment.cloud.logistics.model.req.DepositorySaleOrderReq;
import com.amg.fulfillment.cloud.logistics.model.req.DepositorySaleSearchReq;
import com.amg.fulfillment.cloud.logistics.model.req.PredictionSaleReq;
import com.amg.fulfillment.cloud.logistics.model.req.SaleOrderProductsReq;
import com.amg.fulfillment.cloud.logistics.model.vo.DepositorySaleOrderPredictionVO;
import com.amg.fulfillment.cloud.logistics.model.vo.DepositorySaleOrderVO;
import com.amg.fulfillment.cloud.logistics.service.IDepositoryProductService;
import com.amg.fulfillment.cloud.logistics.service.IDepositorySaleOrderDetailService;
import com.amg.fulfillment.cloud.logistics.service.IDepositorySaleOrderService;
import com.amg.fulfillment.cloud.logistics.util.GrpcJsonUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzx
 * @since 2021-05-14
 */
@Slf4j
@Service
public class DepositorySaleOrderServiceImpl extends ServiceImpl<DepositorySaleOrderMapper, DepositorySaleOrderDO> implements IDepositorySaleOrderService {

    @Autowired
    private DepositoryFactory depositoryFactory;
    @Autowired
    private InventoryStoreClient inventoryStoreClient;
    @Autowired
    private IDepositoryProductService productService;
    @Autowired
    private IDepositorySaleOrderDetailService depositorySaleOrderDetailService;
    @Autowired
    private DepositorySaleOrderMapper depositorySaleOrderMapper;

    private List<String> sizeList = Arrays.asList(StringUtils.split(Constant.SIZE_CHINESE, ","));
    private BigDecimal DEFAULT_1G = new BigDecimal( "0.001"); // 仓库最低重量是1g

    @Override
    public DepositorySaleOrderVO getSaleDetail(Long id) {
        DepositorySaleOrderDO saleOrderDO = this.getById(id);
        String jsonString = JSON.toJSONString(saleOrderDO);
        DepositorySaleOrderVO saleOrderVO = JSON.parseObject(jsonString, DepositorySaleOrderVO.class);
        return saleOrderVO;
    }

    @Override
    public Long addSale(DepositorySaleOrderReq saleOrderReq) {
        DepositorySaleOrderDO saleOrderDO = getDepositorySaleOrderDO(saleOrderReq);
        if (!Objects.isNull(saleOrderDO)) {
//            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200101, "该销售订单【" + saleOrderReq.getSaleOrderId() + "】已经存在，请勿重复添加。");
            return 0L;
        }
        DepositorySaleOrderDO depositorySaleOrderDO = new DepositorySaleOrderDO();
        depositorySaleOrderDO.setSaleOrder(saleOrderReq.getSaleOrderId());
        depositorySaleOrderDO.setDepositoryCode(saleOrderReq.getDepositoryCode());
        depositorySaleOrderDO.setDepositoryName(Optional.ofNullable(saleOrderReq.getDepositoryName()).orElse(Constant.DEPOSITORY_WB_NAME));
        depositorySaleOrderDO.setSaleNotice(Constant.NO_0);
        depositorySaleOrderDO.setRemark(Optional.of(saleOrderReq.getRemark()).isPresent() ? (saleOrderReq.getRemark().length() > 80 ? saleOrderReq.getRemark().substring(0, 80) : saleOrderReq.getRemark()) : null);
        // 获取尺码
        setProductSizeAttributeFromGrpc(saleOrderReq);
        // 组装发送仓库的数据格式
        String content = packingParamsToDepositoryContent(saleOrderReq);
        depositorySaleOrderDO.setPredictionContent(content);
        this.save(depositorySaleOrderDO);
        // 添加详情
        List<DepositorySaleOrderDetailDO> orderDetailDOS = new ArrayList<>();
        List<DepositoryProductDO> depositoryProductDOS = new ArrayList<>();
        for (SaleOrderProductsReq product : saleOrderReq.getProducts()) {
            DepositorySaleOrderDetailDO detailDO = new DepositorySaleOrderDetailDO();
            detailDO.setSaleId(depositorySaleOrderDO.getId());
            detailDO.setSaleOrder(saleOrderReq.getSaleOrderId());
            detailDO.setItemId(product.getItemId());
            detailDO.setPurchaseId(product.getPurchaseId());
            detailDO.setSku(product.getSku());
            detailDO.setName(Optional.of(product.getProductName()).isPresent() ? (product.getProductName().length() > 50 ? product.getProductName().substring(0, 50) : product.getProductName()) : null);
            detailDO.setPredictionState(Constant.YES);
            orderDetailDOS.add(detailDO);

            DepositoryProductDO depositoryProductDO = new DepositoryProductDO();
            depositoryProductDO.setSku(product.getSku());
            depositoryProductDO.setName((Optional.of(product.getProductName()).isPresent() ? (product.getProductName().length() > 400 ? product.getProductName().substring(0, 400) : product.getProductName()) : null));
            depositoryProductDO.setVariants(product.getVariants().isEmpty() ? null : JSON.toJSONString(product.getVariants()));
            depositoryProductDO.setExpireWeight(product.getWeight());
            depositoryProductDO.setImages((Objects.isNull(product.getImageUrls()) || product.getImageUrls().isEmpty()) ? null : JSON.toJSONString(product.getImageUrls()));
            depositoryProductDOS.add(depositoryProductDO);
        }
        depositorySaleOrderDetailService.saveBatch(orderDetailDOS);
        productService.saveBatch(depositoryProductDOS.stream().distinct().collect(Collectors.toList()));
        return depositorySaleOrderDO.getId();
    }


    @Override
    public Long updateSale(DepositorySaleOrderReq saleOrderReq) {
        DepositorySaleOrderDO saleOrderDO = getDepositorySaleOrderDO(saleOrderReq);
        if (Objects.isNull(saleOrderDO)) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200101, "该销售订单【" + saleOrderReq.getSaleOrderId() + "】不存在，请先添加。");
        }
        return null;
    }

    @Override
    public DepositorySaleOrderPredictionVO prediction(PredictionSaleReq predictionSaleReq) {
        DepositorySaleOrderPredictionVO predictionVo = new DepositorySaleOrderPredictionVO();
        DepositorySaleOrderDO depositorySaleOrderDO = new DepositorySaleOrderDO();
        depositorySaleOrderDO.setDepositoryCode(Optional.ofNullable(predictionSaleReq.getDepositoryCode()).orElse(Constant.DEPOSITORY_WB));
        depositorySaleOrderDO.setDepositoryName(Optional.ofNullable(predictionSaleReq.getDepositoryName()).orElse(Constant.DEPOSITORY_WB_NAME));
        LambdaQueryWrapper<DepositorySaleOrderDO> lambdaQueryWrapper = Wrappers.<DepositorySaleOrderDO>lambdaQuery()
                .in(DepositorySaleOrderDO::getId, predictionSaleReq.getIds())
                .eq(DepositorySaleOrderDO::getDepositoryCode, depositorySaleOrderDO.getDepositoryCode());
        List<DepositorySaleOrderDO> list = this.list(lambdaQueryWrapper);
        if (!Objects.isNull(list) && list.isEmpty()) {
            return new DepositorySaleOrderPredictionVO();
        }
        for (DepositorySaleOrderDO v : list) {
            DepositorySaleOrderPredictionVO.SaleOrderResult saleOrderResult = new DepositorySaleOrderPredictionVO.SaleOrderResult();
            if (v.getSaleNotice() == Constant.YES) {
                saleOrderResult.setId(v.getId());
                saleOrderResult.setSaleOrderId(v.getSaleOrder());
                saleOrderResult.setPredictionResult(Boolean.TRUE);
                predictionVo.getSaleOrderResults().add(saleOrderResult);
                continue;
            }
            IDepositoryManager depositoryManager = depositoryFactory.createDepositoryManager(v.getDepositoryCode());
            DepositorySaleOrderDto depositorySaleOrderDto = new DepositorySaleOrderDto();
            depositorySaleOrderDto.setSaleOrderId(v.getSaleOrder());
            depositorySaleOrderDto.setRemark(v.getRemark());
            List<ProductDto> productDtos = JSON.parseArray(v.getPredictionContent(), ProductDto.class);
            // 对商品名进行200字符限制
            productDtos.stream().forEach(item -> {
                String substringName = StringUtils.substring(item.getProductName(), 0, 150);
                item.setProductName(substringName);
            });
            depositorySaleOrderDto.setProductDtos(productDtos);
            SaleOrderResultDto saleOrderResultDto = depositoryManager.addSaleOrder(depositorySaleOrderDto);
            if (saleOrderResultDto.isSuccessSign()) {
                LambdaUpdateWrapper<DepositorySaleOrderDO> updateWrapper = Wrappers.<DepositorySaleOrderDO>lambdaUpdate()
                        .set(DepositorySaleOrderDO::getSaleNotice, Boolean.TRUE)
                        .set(DepositorySaleOrderDO::getNoticeFailReason,
                                StrUtil.isBlank(v.getNoticeFailReason()) ? null : "【解决】" + v.getNoticeFailReason())
                        .set(DepositorySaleOrderDO::getUpdateTime, DateUtil.date().toJdkDate())
                        .eq(DepositorySaleOrderDO::getId, v.getId());
                this.update(updateWrapper);
                saleOrderResult.setId(v.getId());
                saleOrderResult.setSaleOrderId(v.getSaleOrder());
                saleOrderResult.setPredictionResult(Boolean.TRUE);
                predictionVo.getSaleOrderResults().add(saleOrderResult);
            } else {
                LambdaUpdateWrapper<DepositorySaleOrderDO> updateWrapper = Wrappers.<DepositorySaleOrderDO>lambdaUpdate()
                        .set(DepositorySaleOrderDO::getNoticeFailReason, StringUtils.isNotBlank(saleOrderResultDto.getErrorMsg())
                                        ? (saleOrderResultDto.getErrorMsg().length() > 60 ? saleOrderResultDto.getErrorMsg().substring(0, 60) : saleOrderResultDto.getErrorMsg())
                                        : null)
                        .set(DepositorySaleOrderDO::getUpdateTime, DateUtil.date().toJdkDate())
                        .eq(DepositorySaleOrderDO::getId, v.getId());
                this.update(updateWrapper);
                saleOrderResult.setId(v.getId());
                saleOrderResult.setSaleOrderId(v.getSaleOrder());
                saleOrderResult.setPredictionResult(Boolean.FALSE);
                predictionVo.getSaleOrderResults().add(saleOrderResult);
            }
        }
        return predictionVo;
    }

    @Override
    public Page<DepositorySaleOrderVO> list(DepositorySaleSearchReq depositorySaleSearchReq) {
        Page<DepositorySaleOrderDO> page = new Page(depositorySaleSearchReq.getPage(), depositorySaleSearchReq.getRow());
        LambdaQueryWrapper<DepositorySaleOrderDO> queryWrapper = Wrappers.<DepositorySaleOrderDO>lambdaQuery()
                .eq(DepositorySaleOrderDO::getSaleNotice, depositorySaleSearchReq.getSaleNotice());
        Page<DepositorySaleOrderDO> depositorySaleOrderDOPage = depositorySaleOrderMapper.selectPage(page, queryWrapper);
        List<DepositorySaleOrderDO> records = depositorySaleOrderDOPage.getRecords();
        String jsonString = JSON.toJSONString(records);
        List<DepositorySaleOrderVO> depositorySaleOrderVOS = JSON.parseArray(jsonString, DepositorySaleOrderVO.class);
        Page<DepositorySaleOrderVO> depositorySaleOrderVOPage = BeanConvertUtils.copyProperties(depositorySaleOrderDOPage, Page.class);
        depositorySaleOrderVOPage.setRecords(depositorySaleOrderVOS);
        return depositorySaleOrderVOPage;
    }

    @Override
    public List<SaleOrderAssignmentPurchaseIdService.SaleOrderResponse> assignmentPurchaseId(List<SaleOrderAssignmentPurchaseIdService.SaleOrderRequest> saleOrderRequestList) {
        List<SaleOrderAssignmentPurchaseIdService.SaleOrderResponse> list = new ArrayList<>();
        // 获取销售订单详情
        Set<String> saleOrderSet = saleOrderRequestList.stream().map(SaleOrderAssignmentPurchaseIdService.SaleOrderRequest::getSaleOrderId).collect(Collectors.toSet());
        LambdaQueryWrapper<DepositorySaleOrderDetailDO> queryWrapper = Wrappers.<DepositorySaleOrderDetailDO>lambdaQuery().in(DepositorySaleOrderDetailDO::getSaleOrder, saleOrderSet);
        List<DepositorySaleOrderDetailDO> depositorySaleOrderDetailDOS = depositorySaleOrderDetailService.list(queryWrapper);
        Map<String, List<DepositorySaleOrderDetailDO>> mapBySaleOrder = depositorySaleOrderDetailDOS.stream().collect(Collectors.groupingBy(DepositorySaleOrderDetailDO::getSaleOrder));
        for (SaleOrderAssignmentPurchaseIdService.SaleOrderRequest item : saleOrderRequestList) {
            SaleOrderAssignmentPurchaseIdService.SaleOrderResponse orderResponse = new SaleOrderAssignmentPurchaseIdService.SaleOrderResponse();
            String salesOrderId = item.getSaleOrderId();
            String itemId = item.getItemId();
            String purchaseId = item.getPurchaseId();
            orderResponse.setSaleOrderId(salesOrderId);
            orderResponse.setPurchaseId(purchaseId);
            orderResponse.setItemId(itemId);
            try {
                List<DepositorySaleOrderDetailDO> findSaleOrder = Optional.ofNullable(mapBySaleOrder.get(salesOrderId)).orElse(new ArrayList<>());
                DepositorySaleOrderDetailDO depositorySaleOrderDetailDO = findSaleOrder.stream().filter(detail -> StringUtils.isNotBlank(detail.getItemId()) && detail.getItemId().equals(itemId)).findFirst().orElse(new DepositorySaleOrderDetailDO());
                if (StringUtils.isNotBlank(depositorySaleOrderDetailDO.getPurchaseId()) && depositorySaleOrderDetailDO.getPurchaseId().equals(purchaseId)) {
                    orderResponse.setSuccess(true);
                    list.add(orderResponse);
                    continue;
                } else if (StringUtils.isBlank(purchaseId)) {
                    orderResponse.setSuccess(false);
                    list.add(orderResponse);
                    continue;
                } else {
                    LambdaUpdateWrapper<DepositorySaleOrderDetailDO> updateWrapper = Wrappers.<DepositorySaleOrderDetailDO>lambdaUpdate()
                            .set(DepositorySaleOrderDetailDO::getPurchaseId, purchaseId)
                            .eq(DepositorySaleOrderDetailDO::getId, depositorySaleOrderDetailDO.getId());
                    boolean update = depositorySaleOrderDetailService.update(updateWrapper);
                    orderResponse.setSuccess(update);
                    list.add(orderResponse);
                }
            } catch (Exception e) {
                log.error("分派采购id失败！失败原因：{}", e);
                orderResponse.setSuccess(false);
                list.add(orderResponse);
            }
        }
        return list;
    }

    // todo:可以优化多线程获取
    private void setProductSizeAttributeFromGrpc(DepositorySaleOrderReq saleOrderReq) {
        List<SaleOrderProductsReq> products = saleOrderReq.getProducts();
        lop1:
        for (SaleOrderProductsReq product : products) {
            //判断是否包含关键字
            boolean present = product.getVariants().stream().filter(pro -> sizeList.contains(pro.getName())).findFirst().isPresent();
            if (present) {
                ProductVariant productVariant = product.getVariants().stream().filter(pro -> sizeList.contains(pro.getName())).findFirst().get();
                String spuCode = product.getSpu();
                if (StrUtil.isBlank(spuCode)) {
                    return;
                }
                InventoryStoreGTO.GetSPUSizeTableReq request = InventoryStoreGTO.GetSPUSizeTableReq.newBuilder()
                        .setSpuCode(spuCode)
                        .build();
                try {
                    // 调用GRPC获取尺码
                    log.info("调用GRPC获取尺码，请求的销售订购单是：{}，查询的spucode是：{}", saleOrderReq.getSaleOrderId(), spuCode);
                    InventoryStoreGTO.GetSPUSizeTableReply spuSizeTable = inventoryStoreClient.getSpuSizeTable(request);
                    log.info("调用GRPC获取尺码，查询的spucode是：{},返回数据内容是：{}", spuCode, GrpcJsonUtil.jsonFormat.printToString(spuSizeTable));
                    List<InventoryStoreGTO.Size> sizesList = spuSizeTable.getSizeTable().getSizeEntity().getSizeTable().getSizesList();
                    for (InventoryStoreGTO.Size size : sizesList) {
                        ArrayList<ProductVariant> variants = createVariant(productVariant, size);
                        if (variants.size() > 0) {
                            product.getVariants().addAll(variants);
                            continue lop1;
                        }
                    }
                } catch (Exception e) {
                    log.error("调用GRPC获取尺码失败，请求的销售订购单是：{},查询的spucode是：{},失败原因：{}", saleOrderReq.getSaleOrderId(), spuCode, e);
                }
            }
        }
    }

    private ArrayList<ProductVariant> createVariant(ProductVariant productVariant, InventoryStoreGTO.Size size) {
        ArrayList<ProductVariant> variants = new ArrayList<>();
        List<InventoryStoreGTO.KV> namesList = size.getNamesList();
        for (InventoryStoreGTO.KV kv : namesList) {
            if (kv.getValue().toLowerCase().equals(productVariant.getValue().toLowerCase())) {
                List<InventoryStoreGTO.KV> bodyPartsList = size.getBodyPartsList();
                for (InventoryStoreGTO.KV part : bodyPartsList) {
                    // 转化成中文
                    SizesEnToCnEnum toCnEnum = SizesEnToCnEnum.getSizesEnToCnEnumByEn(part.getKey());
                    ProductVariant productVariant_new = new ProductVariant(toCnEnum.getZh(), part.getValue());
                    variants.add(productVariant_new);
                }
            }
        }
        return variants;
    }

    private DepositorySaleOrderDO getDepositorySaleOrderDO(DepositorySaleOrderReq saleOrderReq) {
        LambdaQueryWrapper<DepositorySaleOrderDO> lambdaQueryWrapper = Wrappers.<DepositorySaleOrderDO>lambdaQuery()
                .eq(DepositorySaleOrderDO::getSaleOrder, saleOrderReq.getSaleOrderId())
                .eq(DepositorySaleOrderDO::getDepositoryCode, saleOrderReq.getDepositoryCode());
        DepositorySaleOrderDO saleOrderDO = this.getOne(lambdaQueryWrapper);
        return saleOrderDO;
    }

    private String packingParamsToDepositoryContent(DepositorySaleOrderReq saleOrderReq) {
        String packageMessage = JSON.toJSONString(saleOrderReq);
        DepositorySaleOrderReq packingReq = JSON.parseObject(packageMessage, DepositorySaleOrderReq.class);
        // 将消息以采购id维度转成以sku维度
        // 去除是备货的商品
//        List<SaleOrderProductsReq> aliProducts = packingReq.getProducts().stream().filter(product -> product.getSkuChannel() == DeliveryPackagePurchaseChannelEnum.ALIBABA.getType()).collect(Collectors.toList());
//        packingReq.setProducts(aliProducts);
        Map<String, List<SaleOrderProductsReq>> skuGroups = packingReq.getProducts().stream().collect(Collectors.groupingBy(SaleOrderProductsReq::getSku));
        HashMap<String, SaleOrderProductsReq> realMap = new HashMap<>();
        for (Map.Entry<String, List<SaleOrderProductsReq>> entry : skuGroups.entrySet()) {
            // 统计数量
            entry.getValue().get(0).setQuantity(entry.getValue().size());
            // 转化G ->KG (历史原因)
            entry.getValue().get(0).setWeight(!Objects.isNull(entry.getValue().get(0).getWeight())
                    ? entry.getValue().get(0).getWeight().divide(new BigDecimal("1000"))
                    : new BigDecimal(Constant.DEFAULT_SALEORDER_PRODUCT_WEIGHT_KG+""));
            realMap.putIfAbsent(entry.getKey(), entry.getValue().get(0));
            // 对没有商品名称的赋默认值
            entry.getValue().get(0).setProductName(StringUtils.isNotBlank(entry.getValue().get(0).getProductName()) ? entry.getValue().get(0).getProductName() : Constant.DEFAULT_SALEORDER_PRODUCT_NAME_CN);
        }
        packingReq.setProducts(new ArrayList<>(realMap.values()));
        // 只去第一张图片
        packingReq.getProducts().stream().forEach(item -> {
            item.setImageUrls((!Objects.isNull(item.getImageUrls()) && !item.getImageUrls().isEmpty()) ? Collections.singletonList(item.getImageUrls().get(0)) : Collections.emptyList());
        });
        List<ProductDto> productDtos = packingReq.getProducts().stream().map(product -> {
            ProductDto productDto = BeanConvertUtils.copyProperties(product, ProductDto.class);
            if (product.getWeight().compareTo(DEFAULT_1G) < 0) {
                productDto.setWeightInKg(Constant.DEFAULT_SALEORDER_PRODUCT_WEIGHT_KG);
            }else{
                productDto.setWeightInKg(product.getWeight().doubleValue());
            }
            return productDto;
        }).collect(Collectors.toList());
        if(ChangeLogoByOrderFromEnum.validateChangeLogoByOrderFrom(saleOrderReq.getProducts().get(0).getOrderFrom())){
            productDtos.stream().forEach(item ->{
                if (Objects.isNull(item.getVariants())) {
                    item.setVariants(new ArrayList());
                }
                item.getVariants().add(ChangeLogoByOrderFromEnum.getChangeLogoVariants());
            });
        }
        return JSON.toJSONString(productDtos);
    }
}
