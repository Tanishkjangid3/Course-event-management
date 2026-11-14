package com.campusevents.model;

/**
 * Course domain entity.
 */
public class Course {
    private int id;
    private String name;
    private String department;
    private String duration;
    private String description;
    private String imagePath;
    private int eventCount;

    public Course() {}

    public Course(int id, String name, String department, String duration, String description, String imagePath) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.duration = duration;
        this.description = description;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getEventCount() { return eventCount; }
    public void setEventCount(int eventCount) { this.eventCount = eventCount; }

    @Override
    public String toString() {
        return name;
    }
}

