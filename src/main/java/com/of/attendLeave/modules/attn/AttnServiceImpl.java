package com.of.attendLeave.modules.attn;

import com.of.attendLeave.infra.exception.CommonException;
import com.of.attendLeave.modules.attn.dto.AttendanceDto;
import com.of.attendLeave.modules.attn.dto.AttendancePolicyDto;
import com.of.attendLeave.modules.user.RequestUser;
import com.of.attendLeave.modules.util.DateUtil;
import com.of.attendLeave.modules.util.ErrorCode;
import com.of.attendLeave.modules.util.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttnServiceImpl implements AttnService {

    private final AttnMapper mapper;

    @Override
    public List<Map<String, Object>> findActiveCompanies() {
        return mapper.findActiveCompanies();
    }

    @Override
    public void prebuildForDate(int companyIdx, LocalDate workDate) {
        List<Map<String, Object>> employees = mapper.findActiveByCompany(companyIdx);

        for(Map<String, Object> emp : employees) {
            String oid = emp.get("oid").toString();
            String tid = emp.get("tid").toString();

            int empExist = mapper.existEmp(companyIdx, tid, oid); // 사원 정보 유무
            if(empExist == 0) {
                continue;
            }

            int attExist = mapper.existAttendance(companyIdx, tid, oid, workDate); // 근태 정보 유무
            if(attExist > 0) {
                continue;
            }

            mapper.attendanceInsert(companyIdx, tid, oid, workDate);
        }
    }

    @Override
    @Transactional
    public void updateCommuteByOid(Map<String, Object> params) {
        int exist = mapper.findAttendanceTodayByOid(params);
        if(exist == 0) {
            mapper.insertAttendanceToday(params);
        }

        mapper.updateCommuteTodayByOid(params);

        if("OUT".equals(params.get("type"))) {
            mapper.updateCheckoutAndRecalc(params);
        }
    }

    @Override
    public AttendanceDto findAttendanceByOid(Map<String, Object> params) {
        return mapper.findAttendanceByOid(params);
    }

    @Override
    public Map<String, Object> initDashBoard(Map<String, Object> params) {
        AttendanceDto today = mapper.findAttendanceByOid(params);
        List<Map<String, Object>> history = mapper.getLeaveHistoryGrouped(params);
        Map<String, Object> summary = mapper.getMonthlySummary(params);
        Map<String, Object> orgSummary = mapper.getOrgSummary(params);

        return Map.of("today", today,
                "history", history,
                "summary", summary,
                "orgSummary", orgSummary);
    }

    @Override
    public List<AttendanceDto> getAttnByWorkDate(RequestUser user, Map<String, Object> params) {
        String start = DateUtil.toDateOnly((String) params.getOrDefault("start", ""));
        String end = DateUtil.toDateOnly((String) params.getOrDefault("end", ""));

        Map<String, Object> map = new HashMap<>();
        map.put("oid", user.oid());
        map.put("companyIdx", user.companyIdx());
        map.put("start", start);
        map.put("end", end);

        return mapper.getAttnByWorkDate(map);
    }

    @Override
    public Map<String, Object> summaryMonth(RequestUser user, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put("oid", user.oid());
        map.put("companyIdx", user.companyIdx());
        map.put("startYmdD", params.get("startYmdD"));
        map.put("endYmdD", params.get("endYmdD"));

        return mapper.getMonthlySummary(map);
    }

    @Override
    public Map<String, Object> getPolicyList(RequestUser user, Map<String, Object> params) {
        int page = (Integer) params.getOrDefault("page", 1);
        int rows = (Integer) params.getOrDefault("rows", 15);

        Map<String, Object> map = new HashMap<>();
        map.put("deptCode", params.get("deptCode"));
        map.put("companyIdx", user.companyIdx());
        map.put("tid", user.tid());
        map.put("keyword", params.get("keyword"));
        map.put("activeYn", params.get("activeYn"));
        map.put("fromDate", params.get("fromDate"));
        map.put("toDate", params.get("toDate"));
        map.put("page", page);
        map.put("rows", rows);

        int offset = (page - 1) * rows;
        if(offset < 0) offset = 0;
        map.put("offset", offset);
        List<AttendancePolicyDto> list = mapper.getPolicyList(map);

        int total = mapper.getPolicyCount(map);
        PageInfo pi = new PageInfo(total, page, rows);

        return Map.of("list", list, "pageInfo", pi);
    }

    @Override
    public Map<String, Object> getPolicyListByMe(RequestUser user, Map<String, Object> params) {
        int page = (Integer) params.getOrDefault("page", 1);
        int rows = (Integer) params.getOrDefault("rows", 15);

        Map<String, Object> map = new HashMap<>();
        map.put("companyIdx", user.companyIdx());
        map.put("tid", user.tid());
        map.put("oid", user.oid());
        map.put("activeYn", params.get("activeYn"));
        map.put("fromDate", params.get("fromDate"));
        map.put("toDate", params.get("toDate"));
        map.put("page", page);
        map.put("rows", rows);

        int offset = (page - 1) * rows;
        if(offset < 0) offset = 0;
        map.put("offset", offset);
        List<AttendancePolicyDto> list = mapper.getPolicyListByMe(map);

        int total = mapper.getPolicyByMeCount(map);
        PageInfo pi = new PageInfo(total, page, rows);

        return Map.of("list", list, "pageInfo", pi);
    }

    @Override
    @Transactional
    public Map<String, Object> savePolicyByMe(RequestUser user, AttendancePolicyDto params) {
        // 1) 날짜 검증
        LocalDate ns = params.getFromDate();
        LocalDate ne = params.getToDate();
        if(ns == null || ne == null || ne.isBefore(ns)) {
            throw new CommonException(ErrorCode.POLICY_DATE_FAILED);
        }

        // 2) 겹치는 활성 정책 잠금 조회 (FOR UPDATE)
        List<AttendancePolicyDto> overlaps =
                mapper.selectActiveOverlapForUpdate(user.companyIdx(), user.tid(), user.oid(), ns, ne);

        // 3) 기존 정책 분할 (SPLIT)
        for(AttendancePolicyDto old : overlaps) {
            LocalDate os = old.getFromDate();
            LocalDate oe = old.getToDate();

            boolean needLeft = os.isBefore(ns); // 앞 조각 존재?
            boolean needRight = os.isAfter(ne); // 뒤 조각 존재?

            if(needLeft && needRight) {
                // 3-1) 원본을 앞 조각으로 UPDATE, 뒤 조각만 INSERT
                mapper.updatePolicyPeriodAndMeta(
                        old.getApIdx(),
                        os,
                        ns.minusDays(1),
                        append(old.getReason(), "자동 분할(앞)"),
                        user.oid());

                var right = old.toBuilder()
                        .fromDate(ne.plusDays(1))
                        .toDate(oe)
                        .reason(append(old.getReason(), "자동 분할(뒤)"))
                        .activeYn(1)
                        .createdBy(user.oid())
                        .updatedBy(user.oid())
                        .build();

                mapper.insertPolicy(right);
            } else if(needLeft) {
                // 3-2) 새 구간이 오른쪽으로 길게 겹쳐서 뒤 조각이 없을 때
                mapper.updatePolicyPeriodAndMeta(
                        old.getApIdx(),
                        os,
                        ns.minusDays(1),
                        append(old.getReason(), "자동 분할(앞)"),
                        user.oid());
            } else if(needRight) {
                // 3-2) 새 구간이 왼쪽으로 길게 겹쳐서 뒤 조각이 없을 때
                mapper.updatePolicyPeriodAndMeta(
                        old.getApIdx(),
                        ne.plusDays(1),
                        oe,
                        append(old.getReason(), "자동 분할(뒤)"),
                        user.oid());
            } else {
                int isUpdated = mapper.updatePolicyAsNew(old.getApIdx(), params, user.oid());
                return Map.of("isUpdated", isUpdated > 0);
            }
        }

        if(params.getCompanyIdx() == null) params.setCompanyIdx(user.companyIdx());
        if(params.getTid() == null) params.setTid(user.tid());
        if(params.getOid() == null) params.setOid(user.oid());
        if(params.getActiveYn() == null) params.setActiveYn(1);
        if(params.getPriority() == 0) params.setPriority(100);
        params.setCreatedBy(user.oid());
        params.setUpdatedBy(user.oid());

        int isSaved = mapper.insertPolicy(params);

        return Map.of("isSaved", isSaved > 0);
    }

    private String append(String base, String reason) {
        return (base == null || base.isBlank()) ? reason : base + " | " + reason;
    }

    @Override
    public AttendancePolicyDto getPolicyByOid(RequestUser user, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put("oid", params.get("oid"));
        map.put("apIdx", params.get("apIdx"));

        return mapper.getPolicyByOid(map);
    }

    @Override
    public Map<String, Object> updatePolicyByOid(RequestUser user, AttendancePolicyDto params) {
        int isUpdated = mapper.updatePolicyByOid(params);
        return Map.of("isUpdated", isUpdated > 0);
    }

    @Override
    public Map<String, Object> updatePolicyStatus(RequestUser user, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put("oid", params.get("oid"));
        map.put("companyIdx", user.companyIdx());
        map.put("apIdx", params.get("apIdx"));
        map.put("activeYn", params.get("activeYn"));

        int isUpdated = mapper.updatePolicyStatus(map);
        return Map.of("isUpdated", isUpdated > 0);
    }

    @Override
    public Map<String, Object> getAttnDayList(RequestUser user, Map<String, Object> params) {
        int page = (Integer) params.getOrDefault("page", 1);
        int rows = (Integer) params.getOrDefault("rows", 15);

        Map<String, Object> map = new HashMap<>();
        map.put("oid", user.oid());
        map.put("companyIdx", user.companyIdx());
        map.put("tid", user.tid());
        map.put("status", params.get("status"));
        map.put("anomalyCode", params.get("anomalyCode"));
        map.put("fromDate", params.get("fromDate"));
        map.put("toDate", params.get("toDate"));
        map.put("page", page);
        map.put("rows", rows);

        int offset = (page - 1) * rows;
        if(offset < 0) offset = 0;
        map.put("offset", offset);
        List<AttendanceDto> list = mapper.getAttnDayList(map);

        int total = mapper.getAttnDayCount(map);
        PageInfo pi = new PageInfo(total, page, rows);

        return Map.of("list", list, "pageInfo", pi);
    }

    @Override
    public Map<String, Object> getMngAttnDayList(RequestUser user, Map<String, Object> params) {
        int page = (Integer) params.getOrDefault("page", 1);
        int rows = (Integer) params.getOrDefault("rows", 15);

        Map<String, Object> map = new HashMap<>();
        map.put("companyIdx", user.companyIdx());
        map.put("tid", user.tid());
        map.put("status", params.get("status"));
        map.put("anomalyCode", params.get("anomalyCode"));
        map.put("keyword", params.get("keyword"));
        map.put("deptCode", params.get("deptCode"));
        map.put("fromDate", params.get("fromDate"));
        map.put("toDate", params.get("toDate"));
        map.put("page", page);
        map.put("rows", rows);

        int offset = (page - 1) * rows;
        if(offset < 0) offset = 0;
        map.put("offset", offset);
        List<AttendanceDto> list = mapper.getMngAttnDayList(map);

        int total = mapper.getMngAttnDayCount(map);
        PageInfo pi = new PageInfo(total, page, rows);

        return Map.of("list", list, "pageInfo", pi);
    }
}
