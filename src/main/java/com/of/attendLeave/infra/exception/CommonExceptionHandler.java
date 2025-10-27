package com.of.attendLeave.infra.exception;

import com.of.attendLeave.modules.util.ResponseApiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CommonExceptionHandler.class);

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<?> Exception(CommonException ex) {
        log.error("EXCEPTION ERROR", ex);
        ResponseApiUtil<?> response = ResponseApiUtil.error(ex.getErrorCodeEnum());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> ValidationException(ValidationException ex) {
        log.error("VALIDATION EXCEPTION ERROR", ex);
        ResponseApiUtil<?> response = ResponseApiUtil.error(ex.getErrorCodeEnum());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(GPSValidationException.class)
    public ResponseEntity<?> GPSValidationException(GPSValidationException ex) {
        log.error("GPS VALIDATION EXCEPTION ERROR", ex);
        ResponseApiUtil<?> response = ResponseApiUtil.error(ex.getErrorCodeEnum());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
