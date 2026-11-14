package com.campusevents.model;

/**
 * Filter criteria for querying events dynamically.
 */
public class EventFilter {

    public enum StatusTab {
        ALL("All Events"),
        UPCOMING("Upcoming"),
        ONGOING("Happening Now"),
        PREVIOUS("Previous");

        private final String label;

        StatusTab(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private String searchQuery;
    private Integer courseId;
    private String venue;
    private StatusTab statusTab = StatusTab.ALL;

    public EventFilter() {}

    public EventFilter(String searchQuery, Integer courseId, String venue, StatusTab statusTab) {
        this.searchQuery = searchQuery;
        this.courseId = courseId;
        this.venue = venue;
        this.statusTab = statusTab != null ? statusTab : StatusTab.ALL;
    }

    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public StatusTab getStatusTab() { return statusTab; }
    public void setStatusTab(StatusTab statusTab) { this.statusTab = statusTab != null ? statusTab : StatusTab.ALL; }
}

