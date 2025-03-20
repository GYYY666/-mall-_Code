package com.imuke.mall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author guanyun
 * @since 2025/3/3 20:00
 */
@Data
public class OrderItemVo {
    private Long orderNo;

	private Integer productId;

	private String productName;

	private String productImage;

	private BigDecimal currentUnitPrice;

	private Integer quantity;

	private BigDecimal totalPrice;

	private Date createTime;

}
