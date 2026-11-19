package com.demo.crm.controller;

import com.demo.crm.common.Result;
import com.demo.crm.dto.ProductCategoryRequest;
import com.demo.crm.dto.StatusRequest;
import com.demo.crm.entity.ProductCategory;
import com.demo.crm.service.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-category")
@RequiredArgsConstructor
public class ProductCategoryController {
    private final ProductCategoryService service;

    @GetMapping("/tree")
    public Result<List<ProductCategory>> tree(@RequestParam(defaultValue = "false") boolean enabledOnly) {
        return Result.ok(service.tree(enabledOnly));
    }

    @GetMapping("/{id}")
    public Result<ProductCategory> get(@PathVariable Long id) { return Result.ok(service.get(id)); }

    @PostMapping
    public Result<ProductCategory> create(@Valid @RequestBody ProductCategoryRequest r) { return Result.ok(service.save(null, r)); }

    @PutMapping("/{id}")
    public Result<ProductCategory> update(@PathVariable Long id, @Valid @RequestBody ProductCategoryRequest r) { return Result.ok(service.save(id, r)); }

    @PatchMapping("/{id}/status")
    public Result<ProductCategory> status(@PathVariable Long id, @Valid @RequestBody StatusRequest r) { return Result.ok(service.changeStatus(id, r.status())); }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) { service.delete(id); return Result.ok(true); }
}
