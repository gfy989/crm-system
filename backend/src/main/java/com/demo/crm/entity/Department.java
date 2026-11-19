package com.demo.crm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门。parent_id 自关联，0 表示顶级，可组成任意多级的树。
 */
@Data
public class Department {
    private Long id;
    private String name;
    private String code;
    private Long parentId;
    private Integer sort;
    private Integer status;       // 1启用 0停用
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private java.util.List<Department> children = new java.util.ArrayList<>(); // 树形结构用，非表字段
}
