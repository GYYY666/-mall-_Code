package com.imuke.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author guanyun
 * @since 2025/2/22 11:21
 */
@Data
public class ProductDetailVo {

    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private BigDecimal price;

    private Integer status;

    private Integer stock;

}
