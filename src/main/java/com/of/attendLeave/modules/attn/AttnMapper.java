package com.of.attendLeave.modules.attn;

import com.of.attendLeave.modules.attn.dto.AttendanceDto;
import com.of.attendLeave.modules.attn.dto.AttendancePolicyDto;
import com.of.attendLeave.modules.user.RequestUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface AttnMapper {

    List<Map<String, Object>> findActiveCompanies();

    List<Map<String, Object>> findActiveByCompany(int companyIdx);

    int existEmp(@Param("companyIdx") int companyIdx, @Param("tid") String tid, @Param("oid") String oid);

    int existAttendance(@Param("companyIdx") int companyIdx
            , @Param("tid") String tid
            , @Param("oid") String oid
            , @Param("workDate") LocalDate workDate);

    void attendanceInsert(@Param("companyIdx") int companyIdx
            , @Param("tid") String tid
            , @Param("oid") String oid
            , @Param("workDate") LocalDate workDate);

    int findAttendanceTodayByOid(Map<String, Object> params);

    void insertAttendanceToday(Map<String, Object> params);

    void updateCommuteTodayByOid(Map<String, Object> params);

    void updateCheckoutAndRecalc(Map<String, Object> params);

    AttendanceDto findAttendanceByOid(Map<String, Object> params);

    List<Map<String, Object>> getLeaveHistoryGrouped(Map<String, Object> params);

    Map<String, Object> getMonthlySummary(Map<String, Object> params);

    Map<String, Object> getOrgSummary(Map<String, Object> params);

    List<AttendanceDto> getAttnByWorkDate(Map<String, Object> params);

    List<AttendancePolicyDto> getPolicyList(Map<String, Object> params);

    int getPolicyCount(Map<String, Object> params);

    AttendancePolicyDto getPolicyByOid(Map<String, Object> map);

    int updatePolicyByOid(AttendancePolicyDto params);

    int updatePolicyStatus(Map<String, Object> map);

    List<AttendanceDto> getAttnDayList(Map<String, Object> map);

    int getAttnDayCount(Map<String, Object> map);

    List<AttendanceDto> getMngAttnDayList(Map<String, Object> map);

    int getMngAttnDayCount(Map<String, Object> map);

    List<AttendancePolicyDto> getPolicyListByMe(Map<String, Object> map);

    int getPolicyByMeCount(Map<String, Object> map);

    List<AttendancePolicyDto> selectActiveOverlapForUpdate(@Param("companyIdx") int companyIdx
            , @Param("tid") String tid, @Param("oid") String oid, @Param("from") LocalDate from, @Param("to") LocalDate to);

    int insertPolicy(AttendancePolicyDto dto);

    void deactivatePolicy(@Param("apIdx") Integer apIdx, @Param("updatedBy") String updatedBy);

    void updatePolicyPeriodAndMeta(@Param("apIdx") Integer apIdx
            , @Param("from") LocalDate from, @Param("to") LocalDate to
            , @Param("reason") String reason, @Param("updatedBy") String updatedBy);

    int updatePolicyAsNew(@Param("apIdx") Integer apIdx, AttendancePolicyDto dto, @Param("updatedBy") String updatedBy);
}
