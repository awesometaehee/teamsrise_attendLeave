package com.of.attendLeave.modules.attn.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder = true)
public class AttendancePolicyDto {
    private Integer apIdx;                  // 정책 버전 ID (PK)
    private String oid;                     // 사원 oid (NOT NULL)
    private Integer companyIdx;             // 회사 ID
    private String tid;                     // 테넌트 ID
    private String name;                    // 사원명
    private String deptCode;                // 부서코드
    private String deptName;                // 부서명

    private LocalTime planIn;               // 계획 출근 시간
    private LocalTime planOut;              // 계획 퇴근 시간
    private Integer breakMinutes;           // 휴게 시간(분)
    private Boolean autoBreakEnabled;       // 휴게 자동 차감 여부

    private Integer lateGraceMin;           // 지각 유예 시간(분)
    private Integer earlyGraceMin;          // 조퇴 유예 시간(분)

    private Boolean overtimeEnabled;        // 연장근무 자동 계산 여부
    private Integer workStandardMinutes;    // 일일 표준 근로시간(분)
    private Integer overtimeThresholdMin;   // 연장근무 산정 임계치(분)

    private Boolean gpsCheckEnabled;        // GPS 위치 확인 여부
    private Integer gpsCheckRadius;         // GPS 허용 반경(미터)
    private BigDecimal officeLatitude;      // 사무실 위도
    private BigDecimal officeLongitude;     // 사무실 경도

    private Boolean notificationEnabled;    // 알림 기능 사용 여부
    private Boolean teamsNotification;      // Teams 알림 사용 여부

    private Boolean annualLeaveAutoGrant;   // 연차 자동 부여 여부
    private String annualLeaveGrantMmdd;    // 연차 부여 기준일 (MM-DD)
    private Integer carryOverMaxDays;       // 연차 최대 이월 가능 일수

    private Integer roundingUnitMin;        // 근무시간 라운딩 단위(분)
    private String roundingMode;            // 라운딩 방식 (ceil, floor, nearest)

    private LocalDateTime createdAt;        // 생성 일시
    private String createdBy;               // 생성자
    private LocalDateTime updatedAt;        // 수정 일시
    private String updatedBy;               // 수정자
    private Integer activeYn;               // 사용 여부

    private LocalDate fromDate;             // 정책 시작일
    private LocalDate toDate;               // 정책 종료일
    private int priority;                   // 충돌 시 우선순위 (높을수록 우선)
    private String reason;                  // 정책 부여 사유

}
