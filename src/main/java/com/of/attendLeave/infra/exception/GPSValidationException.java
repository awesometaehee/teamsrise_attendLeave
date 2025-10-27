package com.of.attendLeave.infra.exception;

import com.of.attendLeave.modules.util.ErrorCode;

public class GPSValidationException extends CommonException {
    public GPSValidationException() { super(ErrorCode.GPS_VALIDATION_FAILED); }
}
