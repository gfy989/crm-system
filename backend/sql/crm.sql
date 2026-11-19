-- CRM 数据库初始化脚本
create database if not exists crm default charset utf8mb4;
use crm;

-- 部门表（parent_id 自关联实现多级）
create table sys_department (
    id          bigint primary key auto_increment,
    name        varchar(50)  not null comment '部门名称',
    code        varchar(50)  not null comment '部门编码',
    parent_id   bigint       not null default 0 comment '上级部门id，0为顶级',
    sort        int          not null default 0 comment '排序',
    status      tinyint      not null default 1 comment '1启用 0停用',
    create_time datetime     default current_timestamp comment '创建时间',
    unique key uk_code (code)
) comment '部门表';

-- 员工表
create table sys_employee (
    id          bigint primary key auto_increment,
    emp_no      varchar(30)  not null comment '员工编号（登录账号）',
    name        varchar(30)  not null comment '姓名',
    password    varchar(64)  not null comment '密码',
    phone       varchar(20)  comment '手机号',
    email       varchar(50)  comment '邮箱',
    dept_id     bigint       comment '所属部门id',
    status      tinyint      not null default 1 comment '1启用 0停用',
    create_time datetime     default current_timestamp comment '创建时间',
    unique key uk_emp_no (emp_no)
) comment '员工表';

-- 角色表
create table sys_role (
    id          bigint primary key auto_increment,
    name        varchar(30)  not null comment '角色名称',
    code        varchar(30)  not null comment '角色编码',
    remark      varchar(200) comment '备注',
    status      tinyint      not null default 1 comment '1启用 0停用',
    create_time datetime     default current_timestamp comment '创建时间',
    unique key uk_code (code)
) comment '角色表';

-- 权限表（菜单/按钮，parent_id 实现树形）
create table sys_permission (
    id          bigint primary key auto_increment,
    name        varchar(50)  not null comment '权限名称',
    code        varchar(50)  not null comment '权限编码',
    type        tinyint      not null default 1 comment '1菜单 2按钮',
    parent_id   bigint       not null default 0 comment '上级权限id，0为顶级',
    path        varchar(100) comment '前端路由路径',
    sort        int          not null default 0 comment '排序',
    create_time datetime     default current_timestamp comment '创建时间',
    unique key uk_code (code)
) comment '权限表';

-- 员工-角色 中间表
create table sys_employee_role (
    id          bigint primary key auto_increment,
    employee_id bigint not null comment '员工id',
    role_id     bigint not null comment '角色id',
    unique key uk_emp_role (employee_id, role_id)
) comment '员工角色中间表';

-- 角色-权限 中间表
create table sys_role_permission (
    id            bigint primary key auto_increment,
    role_id       bigint not null comment '角色id',
    permission_id bigint not null comment '权限id',
    unique key uk_role_perm (role_id, permission_id)
) comment '角色权限中间表';

-- 种子数据：部门（与原型一致）
insert into sys_department (id, name, code, parent_id, sort) values
(1, '华辰智能装备有限公司', 'HC',          0, 0),
(2, '销售部',             'HC-SALES',    1, 1),
(3, '华东销售组',         'HC-SALES-E',  2, 1),
(4, '华南销售组',         'HC-SALES-S',  2, 2),
(5, '市场部',             'HC-MKT',      1, 2),
(6, '技术部',             'HC-TECH',     1, 3),
(7, '生产部',             'HC-PROD',     1, 4),
(8, '售后服务部',         'HC-SERVICE',  1, 5),
(9, '财务部',             'HC-FIN',      1, 6);

-- 种子数据：管理员账号（admin / 123456）
insert into sys_employee (emp_no, name, password, dept_id) values
('admin', '系统管理员', '123456', 1);
