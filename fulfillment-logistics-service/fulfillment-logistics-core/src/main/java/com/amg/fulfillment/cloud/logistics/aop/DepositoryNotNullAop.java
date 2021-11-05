//package com.amg.fulfillment.cloud.logistics.aop;
//
//import cn.hutool.core.util.StrUtil;
//import com.amg.framework.boot.base.exception.GlobalException;
//import com.amg.fulfillment.cloud.logistics.dto.depository.*;
//import com.amg.fulfillment.cloud.logistics.util.ValidatorUtil;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//import javax.validation.ConstraintViolation;
//import javax.validation.Validator;
//import java.util.List;
//import java.util.Objects;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static com.amg.fulfillment.cloud.logistics.enumeration.ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION;
///**
// * @author Tom
// * @date 2021-04-25-14:28
// */
//@Aspect
//@Component
//public class DepositoryNotNullAop {
//
//
//    private Validator validator = ValidatorUtil.validator;
//
//
//    /**
//     * @param depositoryPurchaseOrderDto 采购验证切面
//     * @author Tom
//     * @date 2021/4/26  11:53
//     */
//    @Pointcut(value = "execution(* com.amg.fulfillment.cloud.logistics.service.impl.DepositoryServiceImpl.addPurchaseOrder(..)) && args(depositoryPurchaseOrderDto)")
//    public void addPurchaseOrderPiont(DepositoryPurchaseOrderDto depositoryPurchaseOrderDto) {
//    }
//
//
//    /**
//     * @author Tom
//     * @date 2021/4/26  14:30
//     * @param outDepositoryOrderDto
//     * 推单出库验证切面
//     */
//    @Pointcut(value = "execution(* com.amg.fulfillment.cloud.logistics.service.impl.DepositoryServiceImpl.addOutDepositoryOrder(..)) && args(outDepositoryOrderDto)")
//    public void addOutDepositoryOrderPiont(OutDepositoryOrderDto outDepositoryOrderDto) {
//    }
//
//    /**
//     * @author Tom
//     * @date 2021/4/30  10:42
//     * @param depositorySaleOrderDto
//     * 推送销售订单切面
//     */
//    @Pointcut(value = "execution(* com.amg.fulfillment.cloud.logistics.service.impl.DepositoryServiceImpl.addSaleOrder(..)) && args(depositorySaleOrderDto)")
//    public void addSaleOrderPiont(DepositorySaleOrderDto depositorySaleOrderDto) {
//    }
//
//
//    @Before(value = "addPurchaseOrderPiont(depositoryPurchaseOrderDto)")
//    public void addPurchaseOrderExecute(DepositoryPurchaseOrderDto depositoryPurchaseOrderDto) {
//        // 正常验证 hibernate
//        Set<ConstraintViolation<DepositoryPurchaseOrderDto>> validates = validator.validate(depositoryPurchaseOrderDto);
//        if (Objects.nonNull(validates) && validates.size() > 0) {
//            List<String> errors = validates.stream().map(item -> item.getMessage()).collect(Collectors.toList());
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), errors.toString());
//        }
//
//        // 自定义验证
//        List<ExpressDto> expresses = depositoryPurchaseOrderDto.getExpresses();
//        expresses.stream().filter(item -> StrUtil.isBlank(item.getExpressNo())).findFirst().ifPresent(vo -> {
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "仓库采购-快递公司单号不能为null");
//        });
//        expresses.stream().filter(item -> StrUtil.isBlank(item.getServiceId())).findFirst().ifPresent(vo -> {
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "仓库采购-物流渠道Id不能为null，任何新的物流渠道在万邦仓库登记");
//        });
//        expresses.stream().filter(item -> StrUtil.isBlank(item.getTrackingNumber())).findFirst().ifPresent(vo -> {
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "仓库采购-跟踪号不能为null");
//        });
//        expresses.stream().filter(item -> StrUtil.isBlank(item.getLabelUrl())).findFirst().ifPresent(vo -> {
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "仓库采购-包裹⾯单地址不能为null");
//        });
//    }
//
//
//    @Before(value = "addOutDepositoryOrderPiont(outDepositoryOrderDto)")
//    public void addOutDepositoryOrderExecute(OutDepositoryOrderDto outDepositoryOrderDto) {
//        // 正常验证 hibernate
//        Set<ConstraintViolation<OutDepositoryOrderDto>> validates = validator.validate(outDepositoryOrderDto);
//        if (Objects.nonNull(validates) && validates.size() > 0) {
//            List<String> errors = validates.stream().map(item -> item.getMessage()).collect(Collectors.toList());
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), errors.toString());
//        }
//
//        // 自定义验证
//        ExpressDto expressDto = outDepositoryOrderDto.getExpress();
//        if (StrUtil.isBlank(expressDto.getServiceId())) {
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "serviceId-物流渠道Id不能为null");
//        }
//        if (StrUtil.isBlank(expressDto.getTrackingNumber())) {
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "trackingNumber-跟踪号不能为null");
//        }
//        if (StrUtil.isBlank(expressDto.getLabelUrl())) {
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "labelUrl-包裹⾯单地址不能为null");
//        }
//
//    }
//
//
//    @Before(value = "addSaleOrderPiont(depositorySaleOrderDto)")
//    public void addSaleOrderExecute(DepositorySaleOrderDto depositorySaleOrderDto) {
//        // 正常验证 hibernate
//        Set<ConstraintViolation<DepositorySaleOrderDto>> validates = validator.validate(depositorySaleOrderDto);
//        if (Objects.nonNull(validates) && validates.size() > 0) {
//            List<String> errors = validates.stream().map(item -> item.getMessage()).collect(Collectors.toList());
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), errors.toString());
//        }
//
//        // 自定义验证
//        List<ProductDto> productDtos = depositorySaleOrderDto.getProductDtos();
//        productDtos.stream().filter(item -> StrUtil.isBlank(item.getProductName())).findFirst().ifPresent(vo ->{
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "仓库推送销售名称-productName不能为null");
//        });
//        productDtos.stream().filter(item -> Objects.isNull(item.getWeightInKg())).findFirst().ifPresent(vo ->{
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "仓库推送销售重量(kg)-weightInkg不能为null");
//        });
//        productDtos.stream().filter(item -> !Objects.isNull(item.getVariants()) && item.getVariants().size()>0).forEach(item ->{
//            item.getVariants().stream().filter(v -> StrUtil.isBlank(v.getName()) || StrUtil.isBlank(v.getValue())).findFirst()
//                    .ifPresent(e->{
//                        throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "仓库推送销售规格-ProductVariant如果有，其属性不能为null");
//                    });
//        });
//    }
//}
