package com.jlf.music.exception;

import com.jlf.music.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionInterceptor {

    /**
     * 全局抛出业务异常类
     *
     * @param e 业务异常类
     * @return Result
     */
    @ExceptionHandler(value = ServiceException.class)
    public Result<Object> serviceException(ServiceException e) {
        log.error("An error occurred: ", e);
        return Result.fail(e.getCode(), e.getMessage());
    }
}
