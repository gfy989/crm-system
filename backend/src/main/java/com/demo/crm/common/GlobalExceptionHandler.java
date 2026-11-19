package com.demo.crm.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理：把各类异常统一转成 Result 结构 + 对应的 HTTP 状态码
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> business(BusinessException e) {
        return error(e.getStatus(), e.getMessage());
    }

    // @Valid 校验失败时取第一条错误提示
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> validation(MethodArgumentNotValidException e) {
        return error(400, e.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Result<Void>> invalidRequest(Exception e) {
        return error(400, "请求参数格式不正确");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Void>> duplicate(DuplicateKeyException e) {
        return error(409, "编码、账号等唯一字段已存在");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<Void>> relatedData(DataIntegrityViolationException e) {
        return error(409, "存在关联数据，无法完成操作");
    }

    private ResponseEntity<Result<Void>> error(int status, String message) {
        return ResponseEntity.status(status).body(Result.error(message));
    }
}
