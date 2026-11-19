package com.demo.crm.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统一响应格式：code=0 成功，其他失败
 */
@Data
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "success", data);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(1, msg, null);
    }
}
