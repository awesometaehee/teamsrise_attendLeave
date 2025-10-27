package com.of.attendLeave.modules.leave.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LeaveDto {
    private int elSeq;
    private int wkItemSeq;
    private String wkItemCode;
    private String wkItemName;
    private String name;
    private String deptCode;
    private String deptName;
    private int empSeq;
    private String appDate;
    private int IDX_NO;
    private int conSeq;
    private String vacDate;
    private String begTime;
    private String endTime;
    private int dtCnt;
    private String vacReason;
    private String wkEmpName;
    private String telNo;
    private String docNo;
    private int wkDay;
    private int wkHour;
    private int wkMinute;
    private String workingTag;
    private String dsnBis;
    private String dsnOper;
    private int esIdx;
    private String esStatus;
}
