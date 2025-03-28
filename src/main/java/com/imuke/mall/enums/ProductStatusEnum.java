package com.imuke.mall.enums;

import lombok.Getter;

/**
 * @author guanyun
 * @since 2025/2/22 11:27
 */
@Getter
public enum ProductStatusEnum {

    ON_SALE(1),

	OFF_SALE(2),

	DELETE(3),

	;

	Integer code;

	ProductStatusEnum(Integer code) {
		this.code = code;
	}
}
