package com.of.attendLeave.modules.leave;

import com.of.attendLeave.modules.leave.dto.LeaveDto;
import com.of.attendLeave.modules.leave.dto.LeaveErpVacDto;
import com.of.attendLeave.modules.leave.dto.LeaveTypeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LeaveMapper {

    List<LeaveDto> getLeavesByScope(Map<String, Object> params);

    int upsertRawBatch(List<LeaveErpVacDto> erpList);

    int deactivateMissingFromRaw(Map<String, Object> dMap);

    int upsertLeaveType(Map<String, Object> dMap);

    List<LeaveTypeDto> getLeaveTypeList(Map<String, Object> params);

    int getLeaveTypeListCount(Map<String, Object> params);

    void createLeaveType(Map<String, Object> map);

    LeaveTypeDto getLeaveTypeByCode(Map<String, Object> map);

    List<LeaveTypeDto> getLeaveType(Map<String, Object> map);

    int updateLeaveType(Map<String, Object> map);

    int updateStatus(Map<String, Object> map);

    int deleteLeaveType(Map<String, Object> map);
}
