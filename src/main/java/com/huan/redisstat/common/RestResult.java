
package com.huan.redisstat.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.huan.redisstat.common.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author lihuan
 * 通用返回
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class RestResult<T> {

    @JSONField(ordinal = 1)
    private Integer code = 0;
    @JSONField(ordinal = 2)
    private String message = "成功";
    @JSONField(ordinal = 3)
    private T data;

    private RestResult() {
    }

    private RestResult(T data) {
        this.data = data;
    }

    public static <T> RestResult<T> newInstance(T data) {
        return new RestResult<T>(data);
    }

    public static <T> RestResult<T> newInstance() {
        return new RestResult<T>();
    }

    public static <T> RestResult<T> newInstance(ErrorCode errorCode) {
        return new RestResult<T>().setCode(errorCode.getCode()).setMessage(errorCode.getMessage());
    }

    public static <T> RestResult<T> newInstance(ErrorCode errorCode, String message) {
        return new RestResult<T>().setCode(errorCode.getCode()).setMessage(message);
    }

}
