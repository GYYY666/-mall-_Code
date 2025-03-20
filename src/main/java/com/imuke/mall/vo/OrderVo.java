package com.imuke.mall.vo;

import com.imuke.mall.pojo.Shipping;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author guanyun
 * @since 2025/3/3 19:56
 */
@Data
public class OrderVo {
    private Long orderNo;

	private BigDecimal payment;

	private Integer paymentType;

	private Integer postage;

	private Integer status;

	private Date paymentTime;

	private Date sendTime;

	private Date endTime;

	private Date closeTime;

	private Date createTime;

	private List<OrderItemVo> orderItemVoList;

	private Integer shippingId;

	private Shipping shippingVo;

}
