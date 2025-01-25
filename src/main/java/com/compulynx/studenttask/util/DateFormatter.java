package com.compulynx.studenttask.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DateFormatter {
    public static Date parseDate(String dateString) {
        List<String> formats = Arrays.asList("yyyy-MM-dd", "dd/MM/yyyy");
        for (String format : formats) {
            try {
                return new SimpleDateFormat(format).parse(dateString);
            } catch (ParseException e) {
                // Continue to next format
            }
        }
        throw new IllegalArgumentException("Invalid date format");
    }
}
