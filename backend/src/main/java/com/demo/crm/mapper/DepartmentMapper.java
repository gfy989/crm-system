package com.demo.crm.mapper;

import com.demo.crm.entity.Department;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DepartmentMapper {

    @Select("select * from sys_department order by sort, id")
    List<Department> selectAll();

    @Select("select * from sys_department where id = #{id}")
    Department selectById(Long id);

    @Select("select count(*) from sys_department where parent_id = #{id}")
    int countChildren(Long id);

    void insert(Department dept);

    void update(Department dept);

    @Delete("delete from sys_department where id = #{id}")
    void deleteById(Long id);
}
