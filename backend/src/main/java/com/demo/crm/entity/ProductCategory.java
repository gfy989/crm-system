package com.demo.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品分类。parent_id 自关联组成多级树，attribute_template 是本分类的属性模板（JSON 数组列）
 */
@Data
public class ProductCategory {
    private Long id;
    private Long parentId;
    private String name;
    private String code;
    private JsonNode attributeTemplate;
    private Integer sortOrder;
    private Integer status;       // 1启用 0停用
    private String description;
    private boolean available;    // 连同上级都启用才算可用，树形查询时计算，非表字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private LocalDateTime updatedAt;
    private List<ProductCategory> children = new ArrayList<>();
}
