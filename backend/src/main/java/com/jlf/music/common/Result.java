
package com.jlf.music.common;

import com.jlf.music.common.constant.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;


@Data
@NoArgsConstructor
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 后台是否处理成功（状态）
     */
    private boolean state;

    /**
     * 前后端约定的状态码（状态码）
     */
    private int code;

    /**
     * 后台响应的信息（处理信息）
     */
    private String message;

    /**
     * 后台响应的数据（返回数据）
     * transient 标记对象的某个字段在序列化过程中应当被忽略
     */
    private transient T data;

    /**
     * 返回一个表示操作成功的响应对象，没有携带数据。
     *
     * @param <T> 泛型类型
     * @return Result<T> 返回成功的结果对象
     */
    public static <T> Result<T> success() {
        Result<T> r = new Result<>();
        r.setState(true);
        r.setCode(Constant.SUCCESS_CODE);
        r.setMessage(Constant.SUCCESS);
        return r;
    }

    /**
     * 返回一个表示操作成功的响应对象，携带返回数据。
     *
     * @param <T> 泛型类型
     * @param t   返回的数据
     * @return Result<T> 返回成功的结果对象，包含数据
     */
    public static <T> Result<T> success(T t) {
        Result<T> r = new Result<>();
        r.setState(true);
        r.setCode(Constant.SUCCESS_CODE);
        r.setMessage(Constant.SUCCESS);
        r.setData(t);
        return r;
    }

    /**
     * 返回一个表示操作成功的响应对象，携带状态码和信息。
     *
     * @param <T>   泛型类型
     * @param code  状态码
     * @param message 响应信息
     * @return Result<T> 返回成功的结果对象，包含状态码和消息
     */
    public static <T> Result<T> success(int code, String message) {
        Result<T> r = new Result<>();
        r.setState(true);
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    /**
     * 返回一个表示操作成功的响应对象，携带状态码、消息和返回数据。
     *
     * @param <T>   泛型类型
     * @param code  状态码
     * @param message 响应信息
     * @param t     返回的数据
     * @return Result<T> 返回成功的结果对象，包含状态码、消息和数据
     */
    public static <T> Result<T> success(int code, String message, T t) {
        Result<T> r = new Result<>();
        r.setState(true);
        r.setCode(code);
        r.setMessage(message);
        r.setData(t);
        return r;
    }

    /**
     * 返回一个表示操作失败的响应对象，没有携带数据。
     *
     * @param <T> 泛型类型
     * @return Result<T> 返回失败的结果对象
     */
    public static <T> Result<T> fail() {
        Result<T> r = new Result<>();
        r.setState(false);
        r.setCode(Constant.FAIL_CODE);
        r.setMessage(Constant.FAIL);
        return r;
    }

    /**
     * 返回一个表示操作失败的响应对象，携带返回数据。
     *
     * @param <T> 泛型类型
     * @param t   返回的数据
     * @return Result<T> 返回失败的结果对象，包含数据
     */
    public static <T> Result<T> fail(T t) {
        Result<T> r = new Result<>();
        r.setState(false);
        r.setCode(Constant.FAIL_CODE);
        r.setMessage(Constant.FAIL);
        r.setData(t);
        return r;
    }

    /**
     * 返回一个表示操作失败的响应对象，携带状态码和消息。
     *
     * @param <T>   泛型类型
     * @param code  状态码
     * @param message 响应信息
     * @return Result<T> 返回失败的结果对象，包含状态码和消息
     */
    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.setState(false);
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    /**
     * 返回一个表示操作失败的响应对象，携带状态码、消息和返回数据。
     *
     * @param <T>   泛型类型
     * @param code  状态码
     * @param message 响应信息
     * @param t     返回的数据
     * @return Result<T> 返回失败的结果对象，包含状态码、消息和数据
     */
    public static <T> Result<T> fail(int code, String message, T t) {
        Result<T> r = new Result<>();
        r.setState(false);
        r.setCode(code);
        r.setMessage(message);
        r.setData(t);
        return r;
    }

}


