package com.imuke.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 前端数据  返回格式
 * @author guanyun
 * @since 2025/2/21 21:06
 */
@Data
public class ProductVo {
     private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private BigDecimal price;

    private Integer status;

}
