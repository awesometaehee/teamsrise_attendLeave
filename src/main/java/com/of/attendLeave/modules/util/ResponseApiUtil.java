package com.of.attendLeave.modules.util;

import java.util.HashMap;
import java.util.Map;

/**
 * API 전용 응답 클래스 (기존 패턴 유지)
 */
public class ResponseApiUtil<T> {
    private boolean success = true;
    private String code;
    private String message;
    private T data;
    private PageInfo pageInfo;
    private long timestamp;

    public ResponseApiUtil() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResponseApiUtil<T> success(T data) {
        ResponseApiUtil<T> response = new ResponseApiUtil<>();
        response.setSuccess(true);
        response.setCode("SUCCESS");
        response.setData(data);
        return response;
    }

    public static <T> ResponseApiUtil<T> success(T data, String message) {
        ResponseApiUtil<T> response = new ResponseApiUtil<>();
        response.setSuccess(true);
        response.setCode("SUCCESS");
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ResponseApiUtil<T> success(T data, PageInfo pageInfo) {
        ResponseApiUtil<T> res = success(data);
        res.setPageInfo(pageInfo);
        return res;
    }

    public static <T> ResponseApiUtil<T> error(ErrorCode code) {
        ResponseApiUtil<T> response = new ResponseApiUtil<>();
        response.setSuccess(false);
        response.setCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }

    // 기존 패턴에 맞는 Map 응답
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("code", code);
        result.put("message", message);
        result.put("data", data);
        result.put("pageInfo", pageInfo);
        result.put("timestamp", timestamp);
        return result;
    }

    // Getter & Setter
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public PageInfo getPageInfo() { return pageInfo; }
    public void setPageInfo(PageInfo pageInfo) { this.pageInfo = pageInfo; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
