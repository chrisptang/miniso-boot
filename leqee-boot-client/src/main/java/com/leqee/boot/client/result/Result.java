package com.leqee.boot.client.result;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * 一个RPC服务的返回值wrapper；
 *
 * @param <T> 数据类型
 */
public class Result<T> implements Serializable {

    private static final int DEFAULT_SUCCESS_CODE = 0;

    private static final int DEFAULT_FAILED_CODE = -1;


    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 返回值
     */
    private final T data;

    /**
     * 构造函数，私有；
     *
     * @param data
     * @param message
     * @param code
     */
    private Result(T data, String message, int code) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    /**
     * 构造一个成功返回（无异常）的返回值
     *
     * @param data 返回数据
     * @param <D>
     * @return
     */
    public static <D> Result<D> success(D data) {
        return new Result(data, null, DEFAULT_SUCCESS_CODE);
    }

    /**
     * 构造一个失败的（有异常）返回值
     *
     * @param code    错误代码
     * @param message 错误消息
     * @return
     */
    public static <D> Result<D> failed(int code, String message) {
        return new Result(null, message, code == DEFAULT_SUCCESS_CODE ? DEFAULT_FAILED_CODE : code);
    }

    /**
     * 构造一个失败的（有异常）返回值
     * 使用默认的错误码：-1
     *
     * @param message 错误消息
     * @return
     */
    public static <D> Result<D> failed(String message) {
        return new Result(null, message, DEFAULT_FAILED_CODE);
    }

    /**
     * 判断一个result实例是否是成功的；
     *
     * @param result
     * @return
     */
    public static boolean isSuccess(Result<?> result) {
        return result != null && result.code == DEFAULT_SUCCESS_CODE;
    }

    /**
     * 实例方法，请优先使用类方法；
     *
     * @return 判断一个result实例是否是成功的（服务调用无异常）；
     */
    public boolean isSuccess() {
        return isSuccess(this);
    }

    /**
     * 判断一个result实例是否是失败的（服务调用有异常，或者与预期不服）；
     *
     * @param result
     * @return
     */
    public static boolean isFailed(Result<?> result) {
        return result == null || result.code != DEFAULT_SUCCESS_CODE;
    }

    /**
     * 判断一个result实例的数据是否为"非空"
     * 返回true的前提：1、实例是成功的；2、实例的返回数据不为空；3、实例返回的数据如为Collection或者Map，需为"非Empty"；
     *
     * @param result
     * @return
     */
    public static boolean isNonEmptyResult(Result<?> result) {
        if (!isSuccess(result)) {
            return false;
        }
        Object data = result.data;
        if (data == null) {
            return false;
        }
        if (data instanceof Collection) {
            return !((Collection<?>) data).isEmpty();
        }
        if (data instanceof Map) {
            return !((Map) data).isEmpty();
        }

        return true;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Result(code:" + this.code + ", message:" + this.getMessage() + ", data:" + this.getData() + ")";
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Result)) {
            return false;
        }
        Result<?> objResult = (Result) obj;
        if (this.code != objResult.getCode()) {
            return false;
        }
        if (!("" + this.getMessage()).equals("" + objResult.getMessage())) {
            return false;
        }
        if (isFailed(this) && isFailed(objResult)) {
            return this.code == objResult.getCode() && ("" + this.getMessage()).equals("" + objResult.getMessage());
        }

        return this.getData() != null && this.getData().equals(objResult.getData());
    }
}
