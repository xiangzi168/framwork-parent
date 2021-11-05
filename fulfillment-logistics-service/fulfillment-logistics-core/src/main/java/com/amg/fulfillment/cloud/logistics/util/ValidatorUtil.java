package com.amg.fulfillment.cloud.logistics.util;

import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @author Tom
 * @date 2021-04-26-15:16
 */
public abstract class ValidatorUtil {

    public static Validator validator = null;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

}
