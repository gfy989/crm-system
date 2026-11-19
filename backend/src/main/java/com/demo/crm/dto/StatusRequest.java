package com.demo.crm.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 启用 / 停用请求体
 */
public record StatusRequest(@NotNull @Min(0) @Max(1) Integer status) {}
