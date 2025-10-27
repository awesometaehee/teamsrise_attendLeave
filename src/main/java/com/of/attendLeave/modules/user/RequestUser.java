package com.of.attendLeave.modules.user;

import java.util.List;

public record RequestUser(
        String oid,
        int companyIdx,
        String tid,
        String deptCode,
        List<String> roles
) {}
