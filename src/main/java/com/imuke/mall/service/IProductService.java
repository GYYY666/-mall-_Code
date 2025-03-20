package com.imuke.mall.service;

import com.github.pagehelper.PageInfo;
import com.imuke.mall.vo.ProductDetailVo;
import com.imuke.mall.vo.ResponseVo;

public interface IProductService {

    ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize);

	ResponseVo<ProductDetailVo> detail(Integer productId);
}
