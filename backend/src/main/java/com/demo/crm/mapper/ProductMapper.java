package com.demo.crm.mapper;

import com.demo.crm.entity.Product;

import java.util.List;

public interface ProductMapper {
    List<Product> findByCategory(Long categoryId);
}
