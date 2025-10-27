package com.of.attendLeave.modules.leave.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class LeaveTypeDto {
    private Integer companyIdx;
    private String tid;
    private String name;
    private String code;
    private String source;
    private String  externalId;
    private String unitType;
    private Integer allowPartial;
    private Integer carryOverYn;
    private Integer paidYn;
    private String useTiming;
    private BigDecimal defaultEntitlement;
    private BigDecimal maxCarryOverDays;
    private Integer expireMonths;
    private Integer activeYn;
    private Integer sortOrder;
    private String oid;
    private String halfTypeName;
    private Integer absSortCode;
    private String dtcTypeCode;
    private int isCC;
    private String updatedAt;
}
