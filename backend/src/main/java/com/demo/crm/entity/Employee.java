package com.demo.crm.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Employee {
    private Long id;
    private String empNo;     // 员工编号，登录账号
    private String name;
    private String password;
    private String phone;
    private String email;
    private Long deptId;
    private Integer status;
    private LocalDateTime createTime;
}
