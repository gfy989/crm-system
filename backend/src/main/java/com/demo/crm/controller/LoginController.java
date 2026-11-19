package com.demo.crm.controller;

import com.demo.crm.common.Result;
import com.demo.crm.entity.Employee;
import com.demo.crm.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final EmployeeMapper employeeMapper;

    /** 员工编号 + 密码登录 */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        Employee emp = employeeMapper.selectByEmpNoAndPassword(body.get("empNo"), body.get("password"));
        if (emp == null) {
            return Result.error("员工编号或密码错误");
        }
        // 简单起见用 UUID 当 token，后续可换成 JWT
        return Result.ok(Map.of("token", UUID.randomUUID().toString(), "employee", emp));
    }
}
