package com.imuke.mall.service;

import com.github.pagehelper.PageInfo;
import com.imuke.mall.MallApplicationTests;
import com.imuke.mall.enums.ResponseEnum;
import com.imuke.mall.form.ShippingForm;
import com.imuke.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
@Slf4j
public class IShippingServiceTest extends MallApplicationTests {

    @Autowired
    private IShippingService shippingService;

    private Integer uid = 1;

    private ShippingForm shippingForm;

    private Integer shippingId;

    @Before
    public void before() {
        ShippingForm shippingForm = new ShippingForm();
        shippingForm.setReceiverName("张三");
        shippingForm.setReceiverAddress("广东省深圳市南山区");
        shippingForm.setReceiverCity("深圳市");
        shippingForm.setReceiverDistrict("南山区");
        shippingForm.setReceiverMobile("12345678901");
        shippingForm.setReceiverProvince("广东省");
        shippingForm.setReceiverZip("518000");
        this.shippingForm = shippingForm;

        add();
    }

    public void add() {

        ResponseVo<Map<String, Integer>> responseVo = shippingService.add(uid, shippingForm);
        log.info("response={}", responseVo);
        this.shippingId = responseVo.getData().get("shippingId");
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @After
    public void delete() {
        ResponseVo responseVo = shippingService.delete(uid, shippingId);
        log.info("response={}", responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    public void update() {
        shippingForm.setReceiverCity("苏州");
        ResponseVo responseVo = shippingService.update(uid, shippingId, shippingForm);
        log.info("response={}", responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = shippingService.list(uid, 1, 10);
        log.info("response={}", responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());

    }
}