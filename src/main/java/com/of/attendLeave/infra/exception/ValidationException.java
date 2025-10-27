package com.of.attendLeave.infra.exception;

import com.of.attendLeave.modules.util.ErrorCode;

public class ValidationException extends RuntimeException {
    private final ErrorCode errorCode;

    public ValidationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCodeEnum() {
        return errorCode;
    }
}
