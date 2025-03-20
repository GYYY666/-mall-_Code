package com.imuke.mall.vo;

import lombok.Data;

import java.util.List;

/**
 * 返回的数据格式
 * @author guanyun
 * @since 2025/2/19 22:21
 */
@Data
public class CategoryVo {

    private Integer id;

    private Integer parentId;

    private String name;

    private Integer sortOrder;

    private List<CategoryVo> subCategories;
}
