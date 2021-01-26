package com.huan.redisstat.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lihuan
 * 业务异常，用于捕获异常时做特殊处理
 */
@Getter
@Setter
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = -1;
    private Integer code;
    private String errorMessage;

    public BusinessException(Integer code, String errorMessage) {
        super(errorMessage);
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
    }

}
