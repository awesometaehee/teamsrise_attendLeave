package com.of.attendLeave.modules.attn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AttendanceDto {
    private int adIdx;
    private int company_idx;
    private String oid;
    private String workDate;
    private String status;
    private int version;
    private String planIn;
    private String planOut;
    private String checkIn;
    private String checkInSrc;
    private String checkOut;
    private String checkOutSrc;
    private int breakMinutes;
    private int workMinutes;
    private int lateMinutes;
    private int earlyLeaveMinutes;
    private int overtimeMinutes;
    private String anomalyCode;
    private String notes;
    private String name;
    private String deptCode;
    private String deptName;
}
