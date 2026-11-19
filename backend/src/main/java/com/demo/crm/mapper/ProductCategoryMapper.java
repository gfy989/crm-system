package com.demo.crm.mapper;

import com.demo.crm.entity.ProductCategory;

import java.util.List;

public interface ProductCategoryMapper {
    List<ProductCategory> findAll();
    List<ProductCategory> lockAll();          // FOR UPDATE 锁全表，防止并发改树
    ProductCategory findById(Long id);
    int countProducts(Long id);               // 分类下的产品数
    void insert(ProductCategory category);
    void update(ProductCategory category);
    void delete(Long id);
}
