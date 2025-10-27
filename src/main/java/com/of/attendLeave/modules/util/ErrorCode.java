package com.of.attendLeave.modules.util;

public enum ErrorCode {
    // 일반적인 오류
    INVALID_PARAMETER("ATT001", "잘못된 파라미터입니다."),
    UNAUTHORIZED("ATT002", "권한이 없습니다."),
    SYSTEM_ERROR("ATT003", "시스템 오류가 발생했습니다."),

    // 근태 관련 오류
    GPS_VALIDATION_FAILED("ATT101", "GPS 위치 검증에 실패했습니다."),
    ALREADY_CHECKED_IN("ATT102", "이미 출근 처리되었습니다."),
    NOT_CHECKED_IN("ATT103", "출근 기록이 없습니다."),
    DUPLICATE_CHECK("ATT104", "중복된 근태 기록입니다."),
    POLICY_DATE_FAILED("ATT105", "정책시작일/종료일이 유효하지 않습니다."),

    // 휴가 관련 오류
    INSUFFICIENT_LEAVE_BALANCE("ATT201", "휴가 잔여 일수가 부족합니다."),
    LEAVE_REQUEST_NOT_FOUND("ATT202", "휴가 신청을 찾을 수 없습니다."),
    INVALID_LEAVE_PERIOD("ATT203", "유효하지 않은 휴가 기간입니다."),
    LEAVE_ALREADY_APPROVED("ATT204", "이미 승인된 휴가입니다."),

    // ERP 연동 오류
    ERP_CONNECTION_FAILED("ATT301", "ERP 연동에 실패했습니다."),
    ERP_SYNC_FAILED("ATT302", "ERP 동기화에 실패했습니다."),
    ERP_DATA_INVALID("ATT303", "ERP 데이터가 유효하지 않습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
