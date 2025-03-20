package com.imuke.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imuke.mall.dao.ShippingMapper;
import com.imuke.mall.enums.ResponseEnum;
import com.imuke.mall.form.ShippingForm;
import com.imuke.mall.pojo.Shipping;
import com.imuke.mall.service.IShippingService;
import com.imuke.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guanyun
 * @since 2025/3/1 10:58
 */
@Service
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ResponseVo<Map<String, Integer>> add(Integer uid, ShippingForm form){
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form, shipping);
        shipping.setUserId(uid);
        int row = shippingMapper.insertSelective(shipping); //写入成功则为1，失败则为0
        if (row == 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        Map<String, Integer> map = new HashMap<>();
        map.put("shippingId", shipping.getId());
        return ResponseVo.sucess(map);
    }
    @Override
    public ResponseVo delete(Integer uid, Integer shippingId){
        int row = shippingMapper.deleteByIdAndUid(uid, shippingId);
        if(row == 0){
            return ResponseVo.error(ResponseEnum.DELETE_SHIPPING_FAIL);
        }

        return ResponseVo.sucess();
    }

    @Override
    public ResponseVo<String> update(Integer uid, Integer shippingId, ShippingForm form){
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form, shipping);
        shipping.setUserId(uid);
        shipping.setId(shippingId);
        int row = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (row == 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.sucess();
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippings = shippingMapper.selectByUid(uid);
        PageInfo pageInfo = new PageInfo<>(shippings);
        return ResponseVo.sucess(pageInfo);
    }

}
