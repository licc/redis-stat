
package com.huan.redisstat.common.exception;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 错误码
 *
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Accessors(chain = true)
public class ErrorCode {

    /**
     * 服务器错误
     */
    public static final ErrorCode SERVER_ERROR = new ErrorCode(999, "服务器异常,请稍后再试");
    public static final ErrorCode OPT_ERROR = new ErrorCode(998, "操作失败");

    /**
     * 缺少参数
     */
    public static final ErrorCode ILLEGAL_PARAMS = new ErrorCode(1, "参数错误");

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;
}
