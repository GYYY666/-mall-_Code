package com.imuke.mall.service;

import com.imuke.mall.vo.CategoryVo;
import com.imuke.mall.vo.ResponseVo;

import java.util.List;
import java.util.Set;


public interface ICategoryService {

    ResponseVo<List<CategoryVo>> selectAll();

    void findSubCategoryId(Integer id, Set<Integer> resultSet);
}
