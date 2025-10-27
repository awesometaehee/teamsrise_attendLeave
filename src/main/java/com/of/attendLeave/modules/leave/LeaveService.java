package com.of.attendLeave.modules.leave;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.of.attendLeave.modules.leave.dto.LeaveDto;
import com.of.attendLeave.modules.leave.dto.LeaveTypeDto;
import com.of.attendLeave.modules.user.RequestUser;

import java.util.List;
import java.util.Map;

public interface LeaveService {
    List<LeaveDto> getLeavesByScope(RequestUser user, Map<String, Object> params);

    Map<String, Object> syncErpRaw(RequestUser user) throws JsonProcessingException;

    Map<String, Object> getLeaveTypeList(RequestUser user, Map<String, Object> params);

    Map<String, Object> createLeaveType(RequestUser user, Map<String, Object> params);

    LeaveTypeDto getLeaveTypeByCode(RequestUser user, Map<String, Object> params);

    List<LeaveTypeDto> getLeaveType(RequestUser user);

    Map<String, Object> updateLeaveType(RequestUser user, Map<String, Object> params);

    Map<String, Object> updateStatus(RequestUser user, Map<String, Object> params);

    Map<String, Object> deleteLeaveType(RequestUser user, Map<String, Object> params);
}
