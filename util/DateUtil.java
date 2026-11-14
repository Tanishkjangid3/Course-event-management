package com.campusevents.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date and time formatting and parsing utilities for CampusCourse Events.
 */
public class DateUtil {

    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    public static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final DateTimeFormatter TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatDateTime(LocalDateTime ldt) {
        if (ldt == null) return "N/A";
        return ldt.format(DISPLAY_FORMATTER);
    }

    public static String formatDateOnly(LocalDateTime ldt) {
        if (ldt == null) return "N/A";
        return ldt.format(DATE_ONLY_FORMATTER);
    }

    public static String formatTimeOnly(LocalDateTime ldt) {
        if (ldt == null) return "N/A";
        return ldt.format(TIME_ONLY_FORMATTER);
    }

    public static String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return formatDateTime(timestamp.toLocalDateTime());
    }

    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    public static Timestamp toTimestamp(LocalDateTime ldt) {
        return ldt != null ? Timestamp.valueOf(ldt) : null;
    }

    public static String formatRange(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) return "TBD";
        if (start != null && end == null) return formatDateTime(start);
        if (start == null) return "Until " + formatDateTime(end);

        if (start.toLocalDate().isEqual(end.toLocalDate())) {
            return start.format(DATE_ONLY_FORMATTER) + " (" + start.format(TIME_ONLY_FORMATTER) + " - " + end.format(TIME_ONLY_FORMATTER) + ")";
        } else {
            return start.format(DISPLAY_FORMATTER) + " - " + end.format(DISPLAY_FORMATTER);
        }
    }
}

