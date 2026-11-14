package com.campusevents.model;

import java.sql.Timestamp;

/**
 * User domain entity representing Admins and Students.
 */
public class User {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String enrollmentNo;
    private Integer courseId;
    private String courseName;
    private String year;
    private String passwordHash;
    private String role; // "ADMIN" or "STUDENT"
    private Timestamp createdAt;

    public User() {}

    public User(int id, String fullName, String email, String phone, String enrollmentNo, 
                Integer courseId, String courseName, String year, String passwordHash, 
                String role, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.enrollmentNo = enrollmentNo;
        this.courseId = courseId;
        this.courseName = courseName;
        this.year = year;
        this.passwordHash = passwordHash;
        this.role = role != null ? role.toUpperCase() : "STUDENT";
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isStudent() {
        return "STUDENT".equalsIgnoreCase(role);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEnrollmentNo() { return enrollmentNo; }
    public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return fullName + " (" + email + ")";
    }
}

