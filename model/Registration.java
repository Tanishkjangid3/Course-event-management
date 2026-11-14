package com.campusevents.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Registration domain entity representing a student's event enrollment and pass.
 */
public class Registration {
    private int id;
    private String regNo;
    private int userId;
    private String userName;
    private String userEmail;
    private String enrollmentNo;
    private int eventId;
    private String eventTitle;
    private String courseName;
    private String venue;
    private String building;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private BigDecimal fee = BigDecimal.ZERO;
    private Timestamp registeredOn;
    private String status; // "REGISTERED", "ATTENDED", "CANCELLED"
    private String requirements;

    public Registration() {}

    /**
     * Determines whether cancellation is permitted:
     * Allowed only if status is REGISTERED and the event has not yet started.
     */
    public boolean canCancel() {
        if (!"REGISTERED".equalsIgnoreCase(status)) {
            return false;
        }
        if (startDateTime == null) {
            return true;
        }
        return LocalDateTime.now().isBefore(startDateTime);
    }

    public boolean isAttended() {
        return "ATTENDED".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(status);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getEnrollmentNo() { return enrollmentNo; }
    public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public Timestamp getRegisteredOn() { return registeredOn; }
    public void setRegisteredOn(Timestamp registeredOn) { this.registeredOn = registeredOn; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
}

