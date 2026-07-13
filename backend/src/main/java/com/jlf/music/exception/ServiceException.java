package com.jlf.music.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jlf.music.common.constant.Constant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 自定义异常捕捉类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 7064034156794512610L;

    /**
     * 异常信息
     */
    private String message;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 异常发生的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    /**
     * 异常状态（例如，是否成功）
     */
    private Boolean state;

    /**
     * 默认构造函数，初始化一个默认的失败异常对象
     * 设置默认的错误码（FAIL_CODE），默认的消息（FAIL），当前时间戳，状态为失败
     */
    public ServiceException() {
        this.code = Constant.FAIL_CODE;
        this.message = Constant.FAIL;
        this.timestamp = new Date();
        this.state = Boolean.FALSE;
    }

    /**
     * 构造函数，允许设置错误码和异常消息
     *
     * @param code 错误码
     * @param message 异常消息
     */
    public ServiceException(Integer code, String message) {
        this.message = message;
        this.code = code;
        this.timestamp = new Date();
        this.state = Boolean.FALSE;
    }

    /**
     * 构造函数，允许设置错误码、异常状态和异常消息
     *
     * @param code 错误码
     * @param state 异常状态（例如，是否成功）
     * @param message 异常消息
     */
    public ServiceException(Integer code, Boolean state, String message) {
        this.message = message;
        this.code = code;
        this.state = state;
        this.timestamp = new Date();
    }

    /**
     * 构造函数，允许仅设置异常消息
     *
     * @param message 异常消息
     */
    public ServiceException(String message) {
        this.message = message;
        this.timestamp = new Date();
        this.code = Constant.FAIL_CODE;
        this.state = Boolean.FALSE;
    }
}
