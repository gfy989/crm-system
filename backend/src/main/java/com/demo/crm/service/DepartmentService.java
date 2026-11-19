package com.demo.crm.service;

import com.demo.crm.entity.Department;
import com.demo.crm.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentMapper departmentMapper;

    /**
     * 平铺列表转树：按 sort,id 排好序的列表，依次挂到各自的父节点下
     */
    public List<Department> tree() {
        List<Department> all = departmentMapper.selectAll();
        Map<Long, Department> map = new HashMap<>();
        for (Department d : all) {
            map.put(d.getId(), d);
        }
        List<Department> roots = new java.util.ArrayList<>();
        for (Department d : all) {
            Department parent = map.get(d.getParentId());
            if (parent == null) {
                roots.add(d);          // 顶级部门
            } else {
                parent.getChildren().add(d);
            }
        }
        return roots;
    }

    public void save(Department dept) {
        if (dept.getId() == null) {
            departmentMapper.insert(dept);
        } else {
            departmentMapper.update(dept);
        }
    }

    public void delete(Long id) {
        if (departmentMapper.countChildren(id) > 0) {
            throw new RuntimeException("请先删除子部门");
        }
        departmentMapper.deleteById(id);
    }
}
