package com.demo.crm.service;

import com.demo.crm.common.BusinessException;
import com.demo.crm.dto.ProductAttributeDefinition;
import com.demo.crm.dto.ProductAttributeFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 属性模板与产品属性值的校验，产品分类保存和产品录入都会用到
 */
@Component
@RequiredArgsConstructor
public class ProductAttributes {
    private final ObjectMapper json;

    public List<ProductAttributeDefinition> definitions(JsonNode template) {
        return json.convertValue(template, new TypeReference<>() {});
    }

    // 模板本身要合法：编码不重复、不占用固定字段、枚举必须配不重复的可选值
    public void validateTemplate(List<ProductAttributeDefinition> template) {
        Set<String> codes = new HashSet<>();
        for (var definition : template) {
            if (!codes.add(definition.code()) || Set.of("material", "color").contains(definition.code()))
                throw new BusinessException(400, "属性编码不能重复，也不能使用固定字段material、color");
            if (definition.type().equals("enum") && (definition.options() == null || definition.options().isEmpty()
                    || new HashSet<>(definition.options()).size() != definition.options().size()))
                throw new BusinessException(400, "枚举属性必须配置不重复的可选值");
        }
    }

    // 产品属性值要符合分类模板：不多不少、类型正确、必填齐全
    public void validate(JsonNode attributes, JsonNode template) {
        if (!attributes.isObject()) throw new BusinessException(400, "产品属性必须为JSON对象");
        Map<String, ProductAttributeDefinition> definitions = new LinkedHashMap<>();
        definitions(template).forEach(d -> definitions.put(d.code(), d));
        attributes.fieldNames().forEachRemaining(code -> {
            if (!definitions.containsKey(code)) throw new BusinessException(400, "分类未定义属性：" + code);
        });
        for (var definition : definitions.values()) {
            JsonNode value = attributes.get(definition.code());
            if (value == null) {
                if (definition.required()) throw new BusinessException(400, "请填写属性：" + definition.name());
                continue;
            }
            validateValue(definition, value);
        }
    }

    private void validateValue(ProductAttributeDefinition definition, JsonNode value) {
        boolean valid = switch (definition.type()) {
            case "number" -> value.isNumber();
            case "integer" -> value.isIntegralNumber();
            case "boolean" -> value.isBoolean();
            case "string" -> value.isTextual() && value.textValue().length() <= 500;
            case "enum" -> value.isTextual() && definition.options().contains(value.textValue());
            default -> false;
        };
        if (!valid || (definition.required() && value.isTextual() && value.textValue().isBlank()))
            throw new BusinessException(400, "属性值类型或内容不正确：" + definition.name());
    }

    // 前端传来的属性筛选 JSON 转成查询条件，产品模块条件查询时使用
    public List<ProductAttributeFilter> filters(String input, JsonNode template) {
        if (input == null || input.isBlank()) return List.of();
        JsonNode values;
        try { values = json.readTree(input); }
        catch (JsonProcessingException e) { throw new BusinessException(400, "属性筛选不是合法JSON"); }
        if (!values.isObject()) throw new BusinessException(400, "属性筛选必须为JSON对象");
        Map<String, ProductAttributeDefinition> definitions = new HashMap<>();
        definitions(template).forEach(d -> definitions.put(d.code(), d));
        List<ProductAttributeFilter> filters = new ArrayList<>();
        values.fields().forEachRemaining(entry -> {
            var definition = definitions.get(entry.getKey());
            if (definition == null) throw new BusinessException(400, "分类未定义筛选属性：" + entry.getKey());
            JsonNode value = entry.getValue();
            ProductAttributeFilter filter = new ProductAttributeFilter();
            filter.setPath("$." + definition.code());
            filter.setNumeric(Set.of("number", "integer").contains(definition.type()));
            if (value.isObject() && filter.isNumeric()) {
                value.fieldNames().forEachRemaining(key -> {
                    if (!Set.of("min", "max").contains(key)) throw new BusinessException(400, "数值范围仅支持min和max");
                });
                if (value.isEmpty()) throw new BusinessException(400, "数值筛选范围不能为空");
                if (value.has("min")) { validateValue(definition, value.get("min")); filter.setMin(value.get("min").decimalValue()); }
                if (value.has("max")) { validateValue(definition, value.get("max")); filter.setMax(value.get("max").decimalValue()); }
                if (filter.getMin() != null && filter.getMax() != null && filter.getMin().compareTo(filter.getMax()) > 0)
                    throw new BusinessException(400, "属性筛选下限不能大于上限");
            } else {
                validateValue(definition, value);
                if (filter.isNumeric()) filter.setExactNumber(value.decimalValue());
                else filter.setValue(value.asText());
            }
            filters.add(filter);
        });
        return filters;
    }
}
