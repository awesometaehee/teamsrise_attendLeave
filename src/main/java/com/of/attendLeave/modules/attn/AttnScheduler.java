package com.of.attendLeave.modules.attn;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AttnScheduler {
    private static final Logger logger = LoggerFactory.getLogger(AttnScheduler.class);
    private final AttnService service;

    @Scheduled(cron = "0 30 09 * * *")
    public void createTodayRow() {
        List<Map<String, Object>> companies = service.findActiveCompanies();

        for(Map<String, Object> company : companies) {
            int companyIdx = Integer.parseInt(company.get("company_idx").toString());
            service.prebuildForDate(companyIdx, LocalDate.now());
        }
    }
}
