package com.demo.crm.mapper;

import com.demo.crm.entity.Employee;
import org.apache.ibatis.annotations.Select;

public interface EmployeeMapper {

    @Select("select * from sys_employee where emp_no = #{empNo} and password = #{password}")
    Employee selectByEmpNoAndPassword(String empNo, String password);
}
