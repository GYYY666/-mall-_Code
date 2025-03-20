package com.imuke.mall.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 添加商品
 *
 * @author guanyun
 * @since 2025/2/24 20:13
 */

@Data
public class CartAddForm {

    @NotNull
    private Integer productId;

    private Boolean selected = true;


}
