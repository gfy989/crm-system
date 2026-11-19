package com.demo.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 分类属性模板里的单个属性定义，也是产品分类 attribute_template JSON 数组的元素
 */
public record ProductAttributeDefinition(
        @NotBlank @Pattern(regexp = "[A-Za-z][A-Za-z0-9_]{0,63}", message = "属性编码需使用英文开头的字母、数字或下划线") String code,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Pattern(regexp = "string|number|integer|boolean|enum", message = "不支持的属性类型") String type,
        @Size(max = 20) String unit,
        boolean required,
        @Size(max = 100) List<@NotBlank @Size(max = 100) String> options
) {}
