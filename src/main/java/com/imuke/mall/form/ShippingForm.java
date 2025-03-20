package com.imuke.mall.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author guanyun
 * @since 2025/3/1 10:53
 */
@Data
public class ShippingForm {


    @NotBlank
    private String receiverName;
    @NotBlank
    private String receiverPhone;
    @NotBlank
    private String receiverMobile;
    @NotBlank
    private String receiverProvince;
    @NotBlank
    private String receiverCity;
    @NotBlank
    private String receiverDistrict;
    @NotBlank
    private String receiverAddress;
    @NotBlank
    private String receiverZip;
}
