package com.demo.crm.common;

import lombok.Getter;

/**
 * 业务异常：service 里抛出，由 GlobalExceptionHandler 统一转成接口响应
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int status;

    public BusinessException(int status, String message) {
        super(message);
        this.status = status;
    }
}
