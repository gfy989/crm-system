package com.demo.crm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品属性筛选条件（产品模块按属性模板过滤时使用，属性 JSON 转成的查询条件）
 */
@Data
public class ProductAttributeFilter {
    private String path;          // JSON 路径，如 $.storageGB
    private boolean numeric;      // 是否数值类型
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal exactNumber;
    private String value;
    private List<String> values;
}
