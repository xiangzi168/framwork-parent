package com.amg.fulfillment.cloud.logistics.aop;

import cn.hutool.core.util.StrUtil;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.dto.depository.AddressDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.WaybillGoodDetailDto;
import com.amg.fulfillment.cloud.logistics.util.ValidatorUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.amg.fulfillment.cloud.logistics.enumeration.ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION;

/**
 * @author Tom
 * @date 2021-04-26-15:03
 */
@Aspect
@Component
public class LogisticNotNullAop {

    private Validator validator = ValidatorUtil.validator;

    /**
     * @author Tom
     * @date 2021/4/26  11:53
     * 物理参数验证
     */
    @Pointcut(value = "execution(* com.amg.fulfillment.cloud.logistics.service.impl.LogisticServiceImpl.addLogisticOrder(..)) && args(logisticOrderDto)")
    public void addLogisticOrderPiont(LogisticOrderDto logisticOrderDto) {

    }

    @Before(value = "addLogisticOrderPiont(logisticOrderDto)")
    public void addLogisticOrderExecute(LogisticOrderDto logisticOrderDto) {
        // 正常验证 hibernate
        Set<ConstraintViolation<LogisticOrderDto>> validates = validator.validate(logisticOrderDto);
        if (Objects.nonNull(validates) && validates.size() > 0) {
            List<String> errors = validates.stream().map(item -> item.getMessage()).collect(Collectors.toList());
            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), errors.toString());
        }

        // 自定义验证--物流公共验证
        AddressDto receiverAddress = logisticOrderDto.getReceiverAddress();
        if (StrUtil.isBlank(receiverAddress.getProvince())) {
            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "物流--province省份/州不能为null");
        }
        if (StrUtil.isBlank(receiverAddress.getCity())) {
            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "物流--city城市不能为null");
        }
        if (StrUtil.isBlank(receiverAddress.getStreet1())) {
            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "物流--street1地址1不能为null");
        }
        if (StrUtil.isBlank(receiverAddress.getTel())) {
            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "物流--tel手机号不能为null");
        }

        //详细子订单验证
        List<WaybillGoodDetailDto> waybillGoodDetailDtos = logisticOrderDto.getWaybillGoodDetailDtos();
        waybillGoodDetailDtos.stream().filter(item -> StrUtil.isBlank(item.getDeclaredNameEn())).findFirst().ifPresent(vo -> {
            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "物流--declaredNameEn 英文申报名称不能为null");
        });
        waybillGoodDetailDtos.stream().filter(item -> StrUtil.isBlank(item.getDeclaredNameCn())).findFirst().ifPresent(vo -> {
            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "物流--declaredNameCn 中文申报名称不能为null");
        });
//        waybillGoodDetailDtos.stream().filter(item -> Objects.isNull(item.getWeightInKg())).findFirst().ifPresent(vo -> {
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "物流--weightInKg 单件重量不能为null");
//        });
//        waybillGoodDetailDtos.stream().filter(item -> Objects.isNull(item.getCurrencyCode())).findFirst().ifPresent(vo -> {
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "物流--currencyCode 货币类型不能为null");
//        });
//        waybillGoodDetailDtos.stream().filter(item -> Objects.isNull(item.getQuantity())).findFirst().ifPresent(vo -> {
//            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "物流--quantity 数量不能为null");
//        });
        waybillGoodDetailDtos.stream().filter(item -> Objects.isNull(item.getGoodsTitle())).findFirst().ifPresent(vo -> {
            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "物流--GoodsTitle（万邦。4px ） 商品描述不能为null");
        });


        // 燕文
        if (Constant.LOGISTIC_YANWEN.equals(logisticOrderDto.getLogisticCode())) {
            //todo 英国需要有 TaxNumber 待放开
//            if ("GB".equals(logisticOrderDto.getReceiverAddress().getCountryCode()) && StrUtil.isBlank(logisticOrderDto.getSendAddress().getTaxNumber())) {
//                throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "物流--燕文TaxNumber 国，请提供店铺VAT税号，税号规则为GB+9位数字");
//            }
        }

        // 4px
//        if (Constant.LOGISTIC_PX4.equals(logisticOrderDto.getLogisticCode())) {
//            RemarkOrderDto remarkOrderDto = logisticOrderDto.getRemarkOrderDto();
//            if (StrUtil.isBlank(remarkOrderDto.getLogisticsProductCode())) {
//                throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "物流--4PX logisticsProductCode 物流产品代码不能为null");
//            }
//        }
    }
}
