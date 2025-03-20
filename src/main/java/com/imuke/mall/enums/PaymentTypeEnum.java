package com.imuke.mall.enums;

import lombok.Getter;

/**
 * @author guanyun
 * @since 2025/3/4 20:07
 */
@Getter
public enum PaymentTypeEnum {

    PAY_ONLINE(1),
    ;
    Integer code;

    PaymentTypeEnum(Integer code) {
        this.code = code;
    }
}
