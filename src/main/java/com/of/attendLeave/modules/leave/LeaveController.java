package com.of.attendLeave.modules.leave;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.of.attendLeave.modules.leave.dto.LeaveDto;
import com.of.attendLeave.modules.leave.dto.LeaveTypeDto;
import com.of.attendLeave.modules.user.RequestUser;
import com.of.attendLeave.modules.util.PageInfo;
import com.of.attendLeave.modules.util.ResponseApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LeaveController {

    private final LeaveService leaveService;

    /**
     * 캘린더 유형별 연차 조회
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/leave/getLeavesByScope")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> getLeavesByScope(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        List<LeaveDto> leave = leaveService.getLeavesByScope(user, params);
        return ResponseApiUtil.success(leave);
    }

    /**
     * ERP 휴가항목 동기화
     * @param user
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/leave/syncErpRaw")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseApiUtil<?> syncErpRaw(@AuthenticationPrincipal RequestUser user) throws JsonProcessingException {
        Map<String, Object> leave = leaveService.syncErpRaw(user);
        return ResponseApiUtil.success(leave);
    }

    /**
     * 연차 유형 관리 목록 조회
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/leave/getLeaveTypeList")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> getLeaveTypeList(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> leave = leaveService.getLeaveTypeList(user, params);
        return ResponseApiUtil.success(leave.get("list"), (PageInfo) leave.get("pageInfo"));
    }

    /**
     * 휴가 항목 추가
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/leave/createLeaveType")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseApiUtil<?> createLeaveType(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> leave = leaveService.createLeaveType(user, params);
        return ResponseApiUtil.success(leave);
    }

    /**
     * 개별 휴가 항목 조회
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/leave/getLeaveTypeByCode")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseApiUtil<?> getLeaveTypeByCode(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        LeaveTypeDto leave = leaveService.getLeaveTypeByCode(user, params);
        return ResponseApiUtil.success(leave);
    }

    /**
     * 휴가 항목 조회
     * @param user
     * @return
     */
    @PostMapping("/leave/getLeaveType")
    @PreAuthorize("hasRole('NONE')")
    public ResponseApiUtil<?> getLeaveType(@AuthenticationPrincipal RequestUser user) {
        List<LeaveTypeDto> leave = leaveService.getLeaveType(user);
        return ResponseApiUtil.success(leave);
    }

    /**
     * 휴가 항목 수정
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/leave/updateLeaveType")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseApiUtil<?> updateLeaveType(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> leave = leaveService.updateLeaveType(user, params);
        return ResponseApiUtil.success(leave);
    }

    /**
     * 휴가 항목 활성/비활성 update
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/leave/updateStatus")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseApiUtil<?> updateStatus(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> leave = leaveService.updateStatus(user, params);
        return ResponseApiUtil.success(leave);
    }

    /**
     * 휴가 항목 삭제 (FLAG 처리)
     * @param user
     * @param params
     * @return
     */
    @PostMapping("/leave/deleteLeaveType")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseApiUtil<?> deleteLeaveType(@AuthenticationPrincipal RequestUser user, @RequestBody Map<String, Object> params) {
        Map<String, Object> leave = leaveService.deleteLeaveType(user, params);
        return ResponseApiUtil.success(leave);
    }
}
