package com.of.attendLeave.modules.leave;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.of.attendLeave.modules.leave.dto.LeaveDto;
import com.of.attendLeave.modules.leave.dto.LeaveErpVacDto;
import com.of.attendLeave.modules.leave.dto.LeaveTypeDto;
import com.of.attendLeave.modules.user.RequestUser;
import com.of.attendLeave.modules.util.PageInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {
    private final LeaveMapper mapper;
    private final Logger log = LoggerFactory.getLogger(LeaveServiceImpl.class);

    @Override
    public List<LeaveDto> getLeavesByScope(RequestUser user, Map<String, Object> params) {
        String scope = (String) params.getOrDefault("scope", "me");
        String start = (String) params.getOrDefault("start", "");
        String end = (String) params.getOrDefault("end", "");
        String wkItem = (String) params.get("wkItem");
        String statusCsv = (String) params.get("status");

        Set<String> statuses = new LinkedHashSet<>();
        statuses.add("결재완료");

        if("me".equals(scope) && statusCsv != null && !statusCsv.isBlank()) {
            Arrays.stream(statusCsv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).forEach(statuses::add);
        }

        if(!"me".equals(scope)) {
            statuses = Set.of("결재완료");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("oid", user.oid());
        map.put("companyIdx", user.companyIdx());
        map.put("tid", user.tid());
        map.put("deptCode", user);
        map.put("scope", scope);
        map.put("start", start);
        map.put("end", end);
        map.put("wkItem", wkItem);
        map.put("statuses", statuses);

        return mapper.getLeavesByScope(map);
    }

    @Override
    @Transactional
    public Map<String, Object> syncErpRaw(RequestUser user) throws JsonProcessingException {
        // 1. ERP 휴가항목 API 호출
        String erpVacJson = erpVacKindCallApi();

        ObjectMapper om = new ObjectMapper();
        JsonNode root = om.readTree(erpVacJson);
        JsonNode arr = root.path("DataBlock1");

        // 2. 휴가항목 DTO 변환
        List<LeaveErpVacDto> erpList = om.readerForListOf(LeaveErpVacDto.class).readValue(arr.toString());

        Map<String, Object> out = new LinkedHashMap<>();
        if(erpList.isEmpty()) {
            out.put("resultCode", "S");
            out.put("resultMsg", "대상 없음");
            out.put("received", 0);
            out.put("insertedOrUpdated", 0);
            out.put("skipped", 0);
            out.put("errors", 0);
        }

        int skipped = 0, errors = 0;

        // 3. DTO에 tid, companyIdx SET
        for(LeaveErpVacDto src : erpList) {
            try {
                src.setTid(user.tid());
                src.setCompanyIdx(user.companyIdx());
            } catch(Exception e) {
                errors++;
                log.warn("raw 전처리 실패: {} ({})", src.getWkItemName(), src.getWkItemSeq(), e);
            }
        }

        // 4. ERP 휴가 항목 DB UPSERT
        int affectedFirst = mapper.upsertRawBatch(erpList);

        // 5. ERP 휴가 항목에 추가된 데이터가 있다면 INSERT
        Map<String, Object> dMap = new HashMap<>();
        dMap.put("oid", user.oid());
        dMap.put("tid", user.tid());
        dMap.put("companyIdx", user.companyIdx());
        int affectedSecond = mapper.upsertLeaveType(dMap);

        // 6. ERP 데이터를 최소 한건이라도 반영됐다면 DB에 더 이상 없는 항목 비활성화
        int deactivated = 0;
        if(affectedSecond > 0) {
            deactivated = mapper.deactivateMissingFromRaw(dMap);
            log.info("ERP missing deactivated : {}", deactivated);
        }

        out.put("resultCode", "S");
        out.put("resultMsg", "동기화 완료");
        out.put("received", erpList.size());
        out.put("insertedOrUpdated", affectedFirst);
        out.put("skipped", skipped);
        out.put("errors", errors);
        out.put("deactivated", deactivated);

        return out;
    }

    @Override
    public Map<String, Object> getLeaveTypeList(RequestUser user, Map<String, Object> params) {
        int page = (Integer) params.getOrDefault("page", 1);
        int rows = (Integer) params.getOrDefault("rows", 15);

        Map<String, Object> map = new HashMap<>();
        map.put("oid", user.oid());
        map.put("companyIdx", user.companyIdx());
        map.put("keyword", params.get("keyword"));
        map.put("unitType", params.get("unitType"));
        map.put("active", params.get("active"));
        map.put("source", params.get("source"));
        map.put("page", page);
        map.put("rows", rows);

        int offset = (page - 1) * rows;
        if(offset < 0) offset = 0;
        map.put("offset", offset);

        List<LeaveTypeDto> list = mapper.getLeaveTypeList(map);
        int total = mapper.getLeaveTypeListCount(map);
        PageInfo pi = new PageInfo(total, page, rows);

        return Map.of("list", list, "pageInfo", pi);
    }

    @Override
    public Map<String, Object> createLeaveType(RequestUser user, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put("oid", user.oid());
        map.put("companyIdx", user.companyIdx());
        map.put("tid", user.tid());
        map.put("name", params.get("name"));
        map.put("code", params.get("code"));
        map.put("unitType", params.get("unitType"));
        map.put("allowPartial", params.get("allowPartial"));
        map.put("paidYn", params.get("paidYn"));
        map.put("carryOverYn", params.get("carryOverYn"));
        map.put("useTiming", params.get("useTiming"));
        map.put("activeYn", params.get("activeYn"));

        mapper.createLeaveType(map);
        return Map.of("id", map.get("leaveTypeId"));
    }

    @Override
    public LeaveTypeDto getLeaveTypeByCode(RequestUser user, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put("tid", user.tid());
        map.put("companyIdx", user.companyIdx());
        map.put("code", params.get("code"));

        return mapper.getLeaveTypeByCode(map);
    }

    @Override
    public List<LeaveTypeDto> getLeaveType(RequestUser user) {
        Map<String, Object> map = new HashMap<>();
        map.put("companyIdx", user.companyIdx());
        map.put("tid", user.tid());

        return mapper.getLeaveType(map);
    }

    @Override
    public Map<String, Object> updateLeaveType(RequestUser user, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put("oid", user.oid());
        map.put("companyIdx", user.companyIdx());
        map.put("tid", user.tid());
        map.put("name", params.get("name"));
        map.put("code", params.get("code"));
        map.put("unitType", params.get("unitType"));
        map.put("allowPartial", params.get("allowPartial"));
        map.put("paidYn", params.get("paidYn"));
        map.put("carryOverYn", params.get("carryOverYn"));
        map.put("useTiming", params.get("useTiming"));
        map.put("activeYn", params.get("activeYn"));

        int isUpdated = mapper.updateLeaveType(map);

        return Map.of("isUpdated", isUpdated > 0);
    }

    @Override
    public Map<String, Object> updateStatus(RequestUser user, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put("companyIdx", user.companyIdx());
        map.put("code", params.get("code"));
        map.put("activeYn", params.get("activeYn"));

        int isUpdated = mapper.updateStatus(map);

        return Map.of("isUpdated", isUpdated > 0);
    }

    @Override
    public Map<String, Object> deleteLeaveType(RequestUser user, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put("tid", user.tid());
        map.put("companyIdx", user.companyIdx());
        map.put("codeList", params.get("codeList"));

        int count = mapper.deleteLeaveType(map);

        return Map.of("count", count);
    }

    public String erpVacKindCallApi() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://acedemo.ksystemace.com/Angkor.Ylw.Common.HttpExecute/RestOutsideService.svc/OpenApi/VEN.Ylw.XPR.BssWkVacAPI/WkVacAPIQuery";

        Map<String, Object> dataBlock1Item = new HashMap<>();
        dataBlock1Item.put("WkItemName", "");

        Map<String, Object> innerRoot = new HashMap<>();
        innerRoot.put("DataBlock1", List.of(dataBlock1Item));

        Map<String, Object> data = new HashMap<>();
        data.put("ROOT", innerRoot);

        Map<String, Object> root = new HashMap<>();
        root.put("certId", "DMTEK");
        root.put("certKey", "DMTEK_KEY");
        root.put("dsnOper", "acedemodmtech_oper");
        root.put("dsnBis", "acedemodmtech_bis");
        root.put("companySeq", 1);
        root.put("languageSeq", 1);
        root.put("securityType", 0);
        root.put("userId", "");
        root.put("data", data);

        Map<String, Object> body = new HashMap<>();
        body.put("ROOT", root);

        // HTTP 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // POST 호출
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("API 호출 실패: " + response.getStatusCode());
        }
    }
}
