package com.campusevents.ui.pages;

import com.campusevents.dao.CourseDAO;
import com.campusevents.dao.EventDAO;
import com.campusevents.model.Course;
import com.campusevents.model.Event;
import com.campusevents.model.EventFilter;
import com.campusevents.ui.MainFrame;
import com.campusevents.ui.components.EventCard;
import com.campusevents.ui.components.Footer;
import com.campusevents.ui.components.ScrollablePanel;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

/**
 * Events Catalog Page providing real-time live database filtering by title,
 * academic course, college venue, and status tabs (All, Upcoming, Ongoing, Previous).
 */
public class EventsPage extends ScrollablePanel {

    private final MainFrame mainFrame;
    private final EventDAO eventDAO = new EventDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    private JTextField searchField;
    private JComboBox<CourseItem> courseCombo;
    private JComboBox<String> venueCombo;

    private JToggleButton allTab;
    private JToggleButton upcomingTab;
    private JToggleButton ongoingTab;
    private JToggleButton previousTab;

    private JPanel gridContainer;
    private JLabel statusLabel;
    private JLabel resultsCountLabel;

    private Integer initialCourseId = null;

    // Helper wrapper for JComboBox to hold course ID and display name
    public static class CourseItem {
        public final Integer id;
        public final String name;

        public CourseItem(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public EventsPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.BG_MAIN);
        initUI();
    }

    private void initUI() {
        // 1. Header Banner
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_LIGHT),
                new EmptyBorder(28, 48, 20, 48)
        ));

        JLabel title = new JLabel("College Events & Workshops");
        title.setFont(UIConstants.FONT_HERO);
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Browse upcoming seminars, live hackathons, guest lectures, and student competitions.");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_SECONDARY);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(20));

        // 2. Status Filter Tabs
        JPanel tabRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabRow.setOpaque(false);

        ButtonGroup tabGroup = new ButtonGroup();
        allTab = createFilterTab("All Events", true, tabGroup, tabRow);
        upcomingTab = createFilterTab("Upcoming", false, tabGroup, tabRow);
        ongoingTab = createFilterTab("Happening Now", false, tabGroup, tabRow);
        previousTab = createFilterTab("Previous Events", false, tabGroup, tabRow);

        header.add(tabRow);
        add(header);

        // 3. Search and Dropdown Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        filterBar.setBackground(Color.WHITE);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_LIGHT),
                new EmptyBorder(8, 40, 8, 40)
        ));

        // Search Input
        searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Search by event title or keyword...");
        searchField.setFont(UIConstants.FONT_BODY);
        searchField.setPreferredSize(new Dimension(240, 36));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        // Course Combo
        courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(220, 36));
        courseCombo.setFont(UIConstants.FONT_BODY);
        courseCombo.addItem(new CourseItem(null, "All Courses"));
        courseCombo.addActionListener(e -> applyFilters());

        // Venue Combo
        venueCombo = new JComboBox<>();
        venueCombo.setPreferredSize(new Dimension(190, 36));
        venueCombo.setFont(UIConstants.FONT_BODY);
        venueCombo.addItem("All Venues");
        venueCombo.addActionListener(e -> applyFilters());

        // Reset Button
        JButton resetBtn = new JButton("Reset Filters");
        UIConstants.styleOutlineButton(resetBtn);
        resetBtn.setPreferredSize(new Dimension(120, 36));
        resetBtn.addActionListener(e -> resetFilters());

        resultsCountLabel = new JLabel("Showing events...");
        resultsCountLabel.setFont(UIConstants.FONT_BODY_BOLD);
        resultsCountLabel.setForeground(UIConstants.TEXT_SECONDARY);
        resultsCountLabel.setBorder(new EmptyBorder(0, 15, 0, 0));

        filterBar.add(new JLabel("Search:"));
        filterBar.add(searchField);
        filterBar.add(new JLabel("Course:"));
        filterBar.add(courseCombo);
        filterBar.add(new JLabel("Venue:"));
        filterBar.add(venueCombo);
        filterBar.add(resetBtn);
        filterBar.add(resultsCountLabel);

        add(filterBar);
        add(Box.createVerticalStrut(24));

        statusLabel = new JLabel("Loading events...");
        statusLabel.setFont(UIConstants.FONT_BODY);
        statusLabel.setForeground(UIConstants.TEXT_MUTED);
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(statusLabel);

        // 4. Events Cards Grid
        gridContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 24));
        gridContainer.setOpaque(false);
        gridContainer.setBorder(new EmptyBorder(0, 30, 30, 30));
        add(gridContainer);

        add(Box.createVerticalGlue());
        add(new Footer(mainFrame));
    }

    private JToggleButton createFilterTab(String text, boolean selected, ButtonGroup group, JPanel container) {
        JToggleButton btn = new JToggleButton(text, selected);
        btn.setFont(UIConstants.FONT_BODY_BOLD);
        btn.setFocusPainted(false);
        btn.setCursor(UIConstants.HAND_CURSOR);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.addActionListener(e -> applyFilters());
        group.add(btn);
        container.add(btn);
        return btn;
    }

    public void filterByCourse(int courseId) {
        this.initialCourseId = courseId;
        loadData();
    }

    public void loadData() {
        // Load dropdown metadata and then apply filters
        new SwingWorker<Void, Void>() {
            private List<Course> courses;
            private List<String> venues;

            @Override
            protected Void doInBackground() throws Exception {
                courses = courseDAO.getAll();
                venues = eventDAO.getAllVenues();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    // Populate course combo
                    courseCombo.removeAllItems();
                    courseCombo.addItem(new CourseItem(null, "All Courses"));
                    CourseItem selectItem = null;
                    if (courses != null) {
                        for (Course c : courses) {
                            CourseItem item = new CourseItem(c.getId(), c.getName());
                            courseCombo.addItem(item);
                            if (initialCourseId != null && initialCourseId == c.getId()) {
                                selectItem = item;
                            }
                        }
                    }

                    // Populate venue combo
                    venueCombo.removeAllItems();
                    venueCombo.addItem("All Venues");
                    if (venues != null) {
                        for (String v : venues) {
                            venueCombo.addItem(v);
                        }
                    }

                    if (selectItem != null) {
                        courseCombo.setSelectedItem(selectItem);
                        initialCourseId = null;
                    }

                    applyFilters();

                } catch (Exception ex) {
                    System.err.println("Error initializing filters: " + ex.getMessage());
                }
            }
        }.execute();
    }

    public void applyFilters() {
        EventFilter filter = new EventFilter();
        filter.setSearchQuery(searchField.getText().trim());

        CourseItem ci = (CourseItem) courseCombo.getSelectedItem();
        if (ci != null) {
            filter.setCourseId(ci.id);
        }

        String venue = (String) venueCombo.getSelectedItem();
        filter.setVenue(venue);

        if (upcomingTab.isSelected()) {
            filter.setStatusTab(EventFilter.StatusTab.UPCOMING);
        } else if (ongoingTab.isSelected()) {
            filter.setStatusTab(EventFilter.StatusTab.ONGOING);
        } else if (previousTab.isSelected()) {
            filter.setStatusTab(EventFilter.StatusTab.PREVIOUS);
        } else {
            filter.setStatusTab(EventFilter.StatusTab.ALL);
        }

        statusLabel.setVisible(true);
        statusLabel.setText("Querying events from database...");

        new SwingWorker<List<Event>, Void>() {
            @Override
            protected List<Event> doInBackground() throws Exception {
                return eventDAO.getAll(filter);
            }

            @Override
            protected void done() {
                try {
                    List<Event> events = get();
                    gridContainer.removeAll();
                    statusLabel.setVisible(false);

                    if (events != null && !events.isEmpty()) {
                        resultsCountLabel.setText("Found " + events.size() + (events.size() == 1 ? " event" : " events"));
                        for (Event e : events) {
                            gridContainer.add(new EventCard(e, mainFrame));
                        }
                    } else {
                        resultsCountLabel.setText("0 events found");
                        statusLabel.setText("No matching events found for the selected filter criteria.");
                        statusLabel.setVisible(true);
                    }

                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    statusLabel.setText("Failed to retrieve events: " + ex.getMessage());
                    statusLabel.setForeground(UIConstants.ACCENT_RED);
                }
            }
        }.execute();
    }

    private void resetFilters() {
        searchField.setText("");
        if (courseCombo.getItemCount() > 0) courseCombo.setSelectedIndex(0);
        if (venueCombo.getItemCount() > 0) venueCombo.setSelectedIndex(0);
        allTab.setSelected(true);
        applyFilters();
    }
}

