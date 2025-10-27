package com.of.attendLeave.modules.attn;

import com.of.attendLeave.modules.attn.dto.AttendanceDto;
import com.of.attendLeave.modules.attn.dto.AttendancePolicyDto;
import com.of.attendLeave.modules.user.RequestUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttnService {

    List<Map<String, Object>> findActiveCompanies();

    void prebuildForDate(int companyIdx, LocalDate now);

    void updateCommuteByOid(Map<String, Object> params);

    AttendanceDto findAttendanceByOid(Map<String, Object> params);

    Map<String, Object> initDashBoard(Map<String, Object> params);

    List<AttendanceDto> getAttnByWorkDate(RequestUser user, Map<String, Object> params);

    Map<String, Object> summaryMonth(RequestUser user, Map<String, Object> params);

    Map<String, Object> getPolicyList(RequestUser user, Map<String, Object> params);

    AttendancePolicyDto getPolicyByOid(RequestUser user, Map<String, Object> params);

    Map<String, Object> updatePolicyByOid(RequestUser user, AttendancePolicyDto params);

    Map<String, Object> updatePolicyStatus(RequestUser user, Map<String, Object> params);

    Map<String, Object> getAttnDayList(RequestUser user, Map<String, Object> params);

    Map<String, Object> getMngAttnDayList(RequestUser user, Map<String, Object> params);

    Map<String, Object> getPolicyListByMe(RequestUser user, Map<String, Object> params);

    Map<String, Object> savePolicyByMe(RequestUser user, AttendancePolicyDto params);
}
