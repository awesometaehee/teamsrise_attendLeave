package com.of.attendLeave.modules.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String toDateOnly(String dateTime) {
        if(dateTime == null || dateTime.isBlank()) {
            return null;
        }

        try {
            OffsetDateTime odt = OffsetDateTime.parse(dateTime);
            LocalDate localDate = odt.toLocalDate();
            return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return dateTime;
        }
    }
}
