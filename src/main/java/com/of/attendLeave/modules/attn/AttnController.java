package com.of.attendLeave.modules.attn;

import com.of.attendLeave.modules.attn.dto.AttendanceDto;
import com.of.attendLeave.modules.attn.dto.AttendancePolicyDto;
import com.of.attendLeave.modules.user.RequestUser;
import com.of.attendLeave.modules.util.PageInfo;
import com.of.attendLeave.modules.util.ResponseApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AttnController {

    private final AttnService attnService;

    /**
     * 근태/휴가 대시보드
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/initDashBoard")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> initDashBoard(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> attn = attnService.initDashBoard(params);
        return ResponseApiUtil.success(attn);
    }

    /**
     * 출퇴근 업데이트
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/attn/updateCommuteByOid")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> updateCommuteByOid(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put("oid", user.getOid());
        map.put("tid", user.getTid());
        map.put("companyIdx", user.getCompanyIdx());
        map.put("type", params.get("type"));
        map.put("timestamp", params.get("timestamp"));
        map.put("source", params.get("source"));
        map.put("status", params.get("status"));
        map.put("workDate", params.get("workDate"));

        attnService.updateCommuteByOid(map);
        AttendanceDto attn = attnService.findAttendanceByOid(map);
        return ResponseApiUtil.success(attn);
    }

    /**
     * 캘린더 근태 조회
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/attn/getAttnByWorkDate")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> getAttnByWorkDate(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        List<AttendanceDto> attn = attnService.getAttnByWorkDate(user, params);
        return ResponseApiUtil.success(attn);
    }

    /**
     * 근태 현황 월별 데이터 조회
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/attn/summaryMonth")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> summaryMonth(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> attn = attnService.summaryMonth(user, params);
        return ResponseApiUtil.success(attn);
    }

    /**
     * 근태 정책 조회 (관리자)
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/attn/getPolicyList")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseApiUtil<?> getPolicyList(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> attn = attnService.getPolicyList(user, params);
        return ResponseApiUtil.success(attn.get("list"), (PageInfo) attn.get("pageInfo"));
    }

    /**
     * 근태 정책 조회 (사용자)
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/attn/getPolicyListByMe")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> getPolicyListByMe(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> attn = attnService.getPolicyListByMe(user, params);
        return ResponseApiUtil.success(attn.get("list"), (PageInfo) attn.get("pageInfo"));
    }

    @PostMapping("/attn/savePolicyByMe")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> savePolicyByMe(@AuthenticationPrincipal RequestUser user, @RequestBody AttendancePolicyDto params) {
        Map<String, Object> attn = attnService.savePolicyByMe(user, params);
        return ResponseApiUtil.success(attn);
    }

    /**
     * 근태 정책 사원별 조회
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/attn/getPolicyByOid")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> getPolicyByOid(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        AttendancePolicyDto attn = attnService.getPolicyByOid(user, params);
        return ResponseApiUtil.success(attn);
    }

    /**
     * 근태 정책 수정
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/attn/updatePolicyByOid")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> updatePolicyByOid(@AuthenticationPrincipal RequestUser user, @RequestBody AttendancePolicyDto params) {
        Map<String, Object> attn = attnService.updatePolicyByOid(user, params);
        return ResponseApiUtil.success(attn);
    }

    /**
     * 근태 정책 활성/비활성
     * @param user
     * @param params
     * @return
     */
    @PostMapping("attn/updatePolicyStatus")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> updatePolicyStatus(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> attn = attnService.updatePolicyStatus(user, params);
        return ResponseApiUtil.success(attn);
    }

    /**
     * 근태 내역 조회 (사용자)
     * @param user
     * @param params
     * @return
     */
    @PostMapping("attn/getAttnDayList")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> getAttnDayList(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> attn = attnService.getAttnDayList(user, params);
        return ResponseApiUtil.success(attn.get("list"), (PageInfo) attn.get("pageInfo"));
    }

    /**
     * 근태 내역 조회 (관리자)
     * @param user
     * @param params
     * @return
     */
    @PostMapping("attn/getMngAttnDayList")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseApiUtil<?> getMngAttnDayList(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> attn = attnService.getMngAttnDayList(user, params);
        return ResponseApiUtil.success(attn.get("list"), (PageInfo) attn.get("pageInfo"));
    }

    /**
     * 근태 사원별 휴게 시작 시간 조회
     * @param user
     * @param params
     * @return
     */
    @PostMapping("attn/getBreakStart")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> getBreakStart(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> attn = attnService.getBreakStart(user, params);
        return ResponseApiUtil.success(attn);
    }
}
