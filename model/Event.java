package com.campusevents.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Event domain entity representing college workshops, seminars, competitions, and camps.
 */
public class Event {
    private int id;
    private String title;
    private Integer courseId;
    private String courseName;
    private String description;
    private String venue;
    private String building;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime registrationDeadline;
    private int totalSeats;
    private int registeredCount;
    private BigDecimal fee = BigDecimal.ZERO;
    private String organizer;
    private String contact;
    private String thumbnailPath;
    private Timestamp createdAt;

    public Event() {}

    public int getSeatsRemaining() {
        return Math.max(0, totalSeats - registeredCount);
    }

    public boolean isFree() {
        return fee == null || fee.compareTo(BigDecimal.ZERO) == 0;
    }

    public EventStatus getStatus() {
        return EventStatus.computeStatus(startDateTime, endDateTime, registrationDeadline, getSeatsRemaining());
    }

    public boolean isRegistrationAllowed() {
        EventStatus st = getStatus();
        return st == EventStatus.UPCOMING && getSeatsRemaining() > 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public LocalDateTime getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(LocalDateTime registrationDeadline) { this.registrationDeadline = registrationDeadline; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getRegisteredCount() { return registeredCount; }
    public void setRegisteredCount(int registeredCount) { this.registeredCount = registeredCount; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return title;
    }
}

