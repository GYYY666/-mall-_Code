package com.imuke.mall.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author guanyun
 * @since 2025/3/6 22:02
 */
@Data
public class OrderCreateForm {
    @NotNull
    private Integer shippingId;

}
