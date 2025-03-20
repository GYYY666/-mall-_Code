package com.imuke.mall.pojo;

import lombok.Data;

/**
 * @author guanyun
 * @since 2025/2/24 21:33
 */
@Data
public class Cart {

    private Integer productId;

	private Integer quantity;

    private Boolean produceSelected;

    public Cart() {
    }

    public Cart(Integer productId, Integer quantity, Boolean produceSelected) {
        this.productId = productId;
        this.quantity = quantity;
        this.produceSelected = produceSelected;
    }
}
