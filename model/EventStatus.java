package com.campusevents.model;

import com.campusevents.util.UIConstants;
import java.awt.Color;
import java.time.LocalDateTime;

/**
 * Status enumeration for Events with automated status calculation and badge color bindings.
 */
public enum EventStatus {
    UPCOMING("UPCOMING", UIConstants.BADGE_BLUE_BG, UIConstants.BADGE_BLUE_TEXT),
    ONGOING("ONGOING", UIConstants.BADGE_GREEN_BG, UIConstants.BADGE_GREEN_TEXT),
    COMPLETED("COMPLETED", UIConstants.BADGE_GRAY_BG, UIConstants.BADGE_GRAY_TEXT),
    REGISTRATION_CLOSED("REGISTRATION CLOSED", UIConstants.BADGE_RED_BG, UIConstants.BADGE_RED_TEXT),
    SEATS_FULL("SEATS FULL", UIConstants.BADGE_YELLOW_BG, UIConstants.BADGE_YELLOW_TEXT);

    private final String label;
    private final Color bgColor;
    private final Color textColor;

    EventStatus(String label, Color bgColor, Color textColor) {
        this.label = label;
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    public String getLabel() {
        return label;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    /**
     * Computes status automatically based on current date/time, start, end, deadline, and available seats.
     */
    public static EventStatus computeStatus(LocalDateTime start, LocalDateTime end, LocalDateTime deadline, int seatsLeft) {
        LocalDateTime now = LocalDateTime.now();

        if (end != null && now.isAfter(end)) {
            return COMPLETED;
        }

        if (start != null && end != null && !now.isBefore(start) && !now.isAfter(end)) {
            return ONGOING;
        }

        // Before start time
        if (seatsLeft <= 0) {
            return SEATS_FULL;
        }

        if (deadline != null && now.isAfter(deadline)) {
            return REGISTRATION_CLOSED;
        }

        return UPCOMING;
    }
}

