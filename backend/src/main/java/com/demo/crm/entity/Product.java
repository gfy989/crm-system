package com.demo.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品档案（SKU）。attributes 按所属分类的属性模板填写差异属性（JSON 对象列）。
 * 产品管理完整接口后续做产品档案模块时实现，这里先保证分类业务可用。
 */
@Data
public class Product {
    private Long id;
    private Long categoryId;
    private String code;
    private String name;
    private String specification;
    private String brand;
    private String material;
    private String color;
    private String baseUnit;
    @JsonInclude(JsonInclude.Include.NON_NULL) private BigDecimal referencePrice;
    private Integer sourceType;   // 1自制 2外购，NULL待确认
    private JsonNode attributes;
    private String coverImage;
    private String description;
    private Integer sortOrder;
    private Integer status;       // 1启用 0停用
    private boolean available;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private LocalDateTime updatedAt;
}
