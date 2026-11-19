package com.demo.crm.controller;

import com.demo.crm.common.Result;
import com.demo.crm.entity.Department;
import com.demo.crm.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /** 部门树，供表格和上级部门下拉框使用 */
    @GetMapping("/tree")
    public Result<List<Department>> tree() {
        return Result.ok(departmentService.tree());
    }

    /** 新增和修改共用：id 为空是新增，否则是修改 */
    @PostMapping
    public Result<Void> save(@RequestBody Department dept) {
        departmentService.save(dept);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return Result.ok(null);
    }
}
