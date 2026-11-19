package com.demo.crm.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

/**
 * 新增 / 修改产品分类的请求体（record 自带字段不可变，配合 @Valid 校验）
 */
public record ProductCategoryRequest(
        @Positive Long parentId,
        @NotBlank(message = "分类名称不能为空") @Size(max = 100) String name,
        @NotBlank(message = "分类编码不能为空") @Size(max = 64) String code,
        @NotNull @Size(max = 50) List<@NotNull @Valid ProductAttributeDefinition> attributeTemplate,
        @NotNull @Min(0) Integer sortOrder,
        @NotNull @Min(0) @Max(1) Integer status,
        @Size(max = 500) String description
) {}
