package com.of.attendLeave.modules.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestUser {
        private String oid;
        private int companyIdx;
        private String tid;
        private String deptCode;
        private List<String> roles;
}
