package com.of.attendLeave.modules.leave.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LeaveErpVacDto {
    @JsonProperty("WkItemName")
    private String wkItemName;
    @JsonProperty("WkItemSeq")
    private int wkItemSeq;
    @JsonProperty("IsHalf")
    private String isHalf;
    @JsonProperty("SMHalfTypeName")
    private String smHalfTypeName;
    @JsonProperty("SMDTCType")
    private int smDtcType;
    @JsonProperty("SMAbsWkSort")
    private int smAbsWkSort;
    @JsonProperty("IsCC")
    private String isCC;
    @JsonProperty("DTCTypeCode")
    private String dtcTypeCode;
    private int companyIdx;
    private String tid;
}
