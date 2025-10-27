package com.of.attendLeave.infra.exception;

import com.of.attendLeave.modules.util.ErrorCode;

public class CommonException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private ErrorCode errorCode;

    public CommonException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorCode getErrorCodeEnum() {
        return errorCode;
    }
}
