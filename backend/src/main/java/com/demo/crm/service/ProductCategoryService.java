package com.demo.crm.service;

import com.demo.crm.common.BusinessException;
import com.demo.crm.dto.ProductCategoryRequest;
import com.demo.crm.entity.ProductCategory;
import com.demo.crm.mapper.ProductCategoryMapper;
import com.demo.crm.mapper.ProductMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 产品分类业务：树形查询、新增/修改（含属性模板校验、防环形上级）、删除前检查
 */
@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductCategoryMapper mapper;
    private final ProductMapper products;
    private final ProductAttributes attributes;
    private final ObjectMapper json;

    // 全部分类按 id 建索引，并算出每个分类是否可用（自身和所有上级都启用）
    public Map<Long, ProductCategory> index() {
        Map<Long, ProductCategory> categories = new LinkedHashMap<>();
        mapper.findAll().forEach(c -> categories.put(c.getId(), c));
        categories.values().forEach(c -> c.setAvailable(available(c, categories)));
        return categories;
    }

    private boolean available(ProductCategory category, Map<Long, ProductCategory> categories) {
        while (category != null) {
            if (category.getStatus() == 0) return false;
            category = category.getParentId() == null ? null : categories.get(category.getParentId());
        }
        return true;
    }

    public ProductCategory require(Long id, Map<Long, ProductCategory> categories) {
        ProductCategory category = categories.get(id);
        if (category == null) throw new BusinessException(404, "产品分类不存在");
        return category;
    }

    public ProductCategory get(Long id) { return require(id, index()); }

    // 组装树；enabledOnly=true 时跳过不可用（含上级停用）的分类
    public List<ProductCategory> tree(boolean enabledOnly) {
        Map<Long, ProductCategory> categories = index();
        List<ProductCategory> roots = new ArrayList<>();
        for (var category : categories.values()) {
            if (enabledOnly && !category.isAvailable()) continue;
            if (category.getParentId() == null) roots.add(category);
            else categories.get(category.getParentId()).getChildren().add(category);
        }
        return roots;
    }

    public boolean isLeaf(Long id, Map<Long, ProductCategory> categories) {
        return categories.values().stream().noneMatch(c -> Objects.equals(c.getParentId(), id));
    }

    public List<Long> descendantIds(Long id, Map<Long, ProductCategory> categories) {
        require(id, categories);
        List<Long> ids = new ArrayList<>(); ids.add(id);
        for (int i = 0; i < ids.size(); i++) {
            Long parentId = ids.get(i);
            categories.values().stream().filter(c -> Objects.equals(c.getParentId(), parentId)).forEach(c -> ids.add(c.getId()));
        }
        return ids;
    }

    @Transactional
    public ProductCategory save(Long id, ProductCategoryRequest request) {
        mapper.lockAll();   // 锁全表防止并发改树导致环
        Map<Long, ProductCategory> categories = index();
        ProductCategory category = id == null ? new ProductCategory() : require(id, categories);
        attributes.validateTemplate(request.attributeTemplate());
        String code = request.code().trim();
        if (categories.values().stream().anyMatch(c -> !Objects.equals(c.getId(), id) && c.getCode().equalsIgnoreCase(code)))
            throw new BusinessException(409, "分类编码已存在");
        Long parentId = request.parentId();
        while (parentId != null) {
            if (Objects.equals(parentId, id)) throw new BusinessException(409, "上级分类不能是自身或下级分类");
            var parent = require(parentId, categories);
            if (Objects.equals(parentId, request.parentId()) && mapper.countProducts(parentId) > 0)
                throw new BusinessException(409, "上级分类已有产品，不能再增加子分类");
            parentId = parent.getParentId();
        }
        var template = json.valueToTree(request.attributeTemplate());
        if (id != null) {
            // 修改模板时保证已有产品仍能通过校验，避免老产品属性失配
            for (var product : products.findByCategory(id)) {
                try { attributes.validate(product.getAttributes(), template); }
                catch (BusinessException e) { throw new BusinessException(409, "属性模板与现有产品" + product.getCode() + "不兼容：" + e.getMessage()); }
            }
        }
        category.setParentId(request.parentId()); category.setName(request.name().trim()); category.setCode(code);
        category.setAttributeTemplate(template); category.setSortOrder(request.sortOrder()); category.setStatus(request.status()); category.setDescription(request.description());
        if (id == null) mapper.insert(category); else mapper.update(category);
        return get(category.getId());
    }

    @Transactional
    public ProductCategory changeStatus(Long id, int status) {
        mapper.lockAll();
        ProductCategory category = get(id); category.setStatus(status); mapper.update(category);
        return get(id);
    }

    @Transactional
    public void delete(Long id) {
        mapper.lockAll();
        Map<Long, ProductCategory> categories = index(); require(id, categories);
        if (!isLeaf(id, categories)) throw new BusinessException(409, "存在下级分类，不能删除");
        if (mapper.countProducts(id) > 0) throw new BusinessException(409, "分类下存在产品，不能删除");
        mapper.delete(id);
    }
}
