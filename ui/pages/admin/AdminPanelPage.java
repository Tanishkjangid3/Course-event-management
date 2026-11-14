package com.campusevents.ui.pages.admin;

import com.campusevents.dao.CourseDAO;
import com.campusevents.dao.EventDAO;
import com.campusevents.dao.RegistrationDAO;
import com.campusevents.dao.UserDAO;
import com.campusevents.model.Course;
import com.campusevents.model.Event;
import com.campusevents.model.Registration;
import com.campusevents.model.User;
import com.campusevents.ui.MainFrame;
import com.campusevents.ui.components.BarChartPanel;
import com.campusevents.ui.components.Footer;
import com.campusevents.ui.components.ScrollablePanel;
import com.campusevents.ui.components.StatCard;
import com.campusevents.util.CSVExporter;
import com.campusevents.util.DateUtil;
import com.campusevents.util.ImageUtil;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Master Administrator Console coordinating:
 * 1. Overview Dashboard (Real-time stat cards + Graphics2D bar chart)
 * 2. Event Management (JTable, Add/Edit with JSpinner Date models, Thumbnail upload & preview, validation)
 * 3. Registration Management (JTable, Event filtering, Mark Attended, Delete, CSV Export)
 * 4. Course Management (JTable, Add/Edit/Delete)
 * 5. Student User Directory (JTable, live search)
 */
public class AdminPanelPage extends ScrollablePanel {

    private final MainFrame mainFrame;
    private final EventDAO eventDAO = new EventDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final UserDAO userDAO = new UserDAO();

    private CardLayout adminCards;
    private JPanel adminContainer;

    // Sub-nav Buttons
    private JToggleButton dashBtn;
    private JToggleButton eventsBtn;
    private JToggleButton regsBtn;
    private JToggleButton coursesBtn;
    private JToggleButton usersBtn;

    // 1. Dashboard Components
    private StatCard totalCoursesStat;
    private StatCard upcomingEventsStat;
    private StatCard ongoingEventsStat;
    private StatCard pastEventsStat;
    private StatCard totalRegsStat;
    private BarChartPanel barChartPanel;

    // 2. Events Components
    private JTable eventsTable;
    private DefaultTableModel eventsModel;
    private List<Event> adminEventsList;
    private JTextField eventSearchField;

    // 3. Registrations Components
    private JTable regsTable;
    private DefaultTableModel regsModel;
    private List<Registration> adminRegsList;
    private JComboBox<EventComboItem> regEventFilterCombo;
    private JTextField regSearchField;

    // 4. Courses Components
    private JTable coursesTable;
    private DefaultTableModel coursesModel;
    private List<Course> adminCoursesList;

    // 5. Users Components
    private JTable usersTable;
    private DefaultTableModel usersModel;
    private List<User> adminUsersList;
    private JTextField userSearchField;

    public static class EventComboItem {
        public final Integer id;
        public final String title;

        public EventComboItem(Integer id, String title) {
            this.id = id;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    public AdminPanelPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.BG_MAIN);
        initUI();
    }

    private void initUI() {
        // Master Admin Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_LIGHT),
                new EmptyBorder(24, 48, 16, 48)
        ));

        JLabel title = new JLabel("College Events Administration Console");
        title.setFont(UIConstants.FONT_HERO);
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Full institutional control over events, course curriculums, student registrations, and reports.");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_SECONDARY);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(18));

        // Sub-Navigation Tabs Bar
        JPanel tabRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabRow.setOpaque(false);

        ButtonGroup navGroup = new ButtonGroup();
        dashBtn = createNavButton("📊 Dashboard Overview", "DASHBOARD", true, navGroup, tabRow);
        eventsBtn = createNavButton("📅 Manage Events", "EVENTS", false, navGroup, tabRow);
        regsBtn = createNavButton("📝 View Registrations", "REGISTRATIONS", false, navGroup, tabRow);
        coursesBtn = createNavButton("🎓 Academic Courses", "COURSES", false, navGroup, tabRow);
        usersBtn = createNavButton("👥 Student Directory", "USERS", false, navGroup, tabRow);

        header.add(tabRow);
        add(header);
        add(Box.createVerticalStrut(24));

        // Sub-views Container with CardLayout
        adminCards = new CardLayout();
        adminContainer = new JPanel(adminCards);
        adminContainer.setOpaque(false);
        adminContainer.setBorder(new EmptyBorder(0, 48, 20, 48));

        adminContainer.add(createDashboardView(), "DASHBOARD");
        adminContainer.add(createEventsView(), "EVENTS");
        adminContainer.add(createRegistrationsView(), "REGISTRATIONS");
        adminContainer.add(createCoursesView(), "COURSES");
        adminContainer.add(createUsersView(), "USERS");

        add(adminContainer);
        add(Box.createVerticalGlue());
        add(new Footer(mainFrame));
    }

    private JToggleButton createNavButton(String text, String cardKey, boolean selected, ButtonGroup group, JPanel container) {
        JToggleButton btn = new JToggleButton(text, selected);
        btn.setFont(UIConstants.FONT_BODY_BOLD);
        btn.setFocusPainted(false);
        btn.setCursor(UIConstants.HAND_CURSOR);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.addActionListener(e -> {
            adminCards.show(adminContainer, cardKey);
            refreshCurrentTab(cardKey);
        });
        group.add(btn);
        container.add(btn);
        return btn;
    }

    private void refreshCurrentTab(String cardKey) {
        if ("DASHBOARD".equals(cardKey)) loadDashboardData();
        else if ("EVENTS".equals(cardKey)) loadEventsData();
        else if ("REGISTRATIONS".equals(cardKey)) loadRegistrationsData();
        else if ("COURSES".equals(cardKey)) loadCoursesData();
        else if ("USERS".equals(cardKey)) loadUsersData();
    }

    // =========================================================================
    // 1. DASHBOARD VIEW
    // =========================================================================
    private JPanel createDashboardView() {
        JPanel view = new JPanel();
        view.setLayout(new BoxLayout(view, BoxLayout.Y_AXIS));
        view.setOpaque(false);

        // Stat Cards Bar
        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        statsBar.setOpaque(false);

        totalCoursesStat = new StatCard("TOTAL COURSES", "-", UIConstants.PRIMARY_BLUE);
        upcomingEventsStat = new StatCard("UPCOMING EVENTS", "-", UIConstants.ACCENT_GREEN);
        ongoingEventsStat = new StatCard("HAPPENING NOW", "-", UIConstants.ACCENT_YELLOW);
        pastEventsStat = new StatCard("PAST EVENTS", "-", UIConstants.TEXT_MUTED);
        totalRegsStat = new StatCard("TOTAL REGISTRATIONS", "-", UIConstants.ACCENT_RED);

        statsBar.add(totalCoursesStat);
        statsBar.add(upcomingEventsStat);
        statsBar.add(ongoingEventsStat);
        statsBar.add(pastEventsStat);
        statsBar.add(totalRegsStat);

        view.add(statsBar);
        view.add(Box.createVerticalStrut(28));

        // Graphics2D Bar Chart Panel
        barChartPanel = new BarChartPanel();
        view.add(barChartPanel);

        return view;
    }

    // =========================================================================
    // 2. EVENTS VIEW
    // =========================================================================
    private JPanel createEventsView() {
        JPanel view = new JPanel(new BorderLayout(0, 16));
        view.setBackground(Color.WHITE);
        view.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));

        // Action Toolbar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);

        eventSearchField = new JTextField(20);
        eventSearchField.putClientProperty("JTextField.placeholderText", "Search events by title...");
        eventSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadEventsData();
            }
        });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(eventSearchField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton addBtn = new JButton("+ Add New Event");
        UIConstants.stylePrimaryButton(addBtn);
        addBtn.addActionListener(e -> showEventFormDialog(null));

        JButton editBtn = new JButton("Edit Selected");
        UIConstants.styleOutlineButton(editBtn);
        editBtn.addActionListener(e -> handleEditEvent());

        JButton deleteBtn = new JButton("Delete Event");
        UIConstants.styleDangerButton(deleteBtn);
        deleteBtn.addActionListener(e -> handleDeleteEvent());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        topBar.add(searchPanel, BorderLayout.WEST);
        topBar.add(btnPanel, BorderLayout.EAST);
        view.add(topBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Title", "Course", "Venue", "Start Time", "End Time", "Deadline", "Seats", "Booked", "Fee", "Organizer"};
        eventsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        eventsTable = new JTable(eventsModel);
        eventsTable.setRowHeight(32);
        eventsTable.setFont(UIConstants.FONT_BODY);
        eventsTable.getTableHeader().setFont(UIConstants.FONT_BODY_BOLD);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(eventsTable);
        scroll.setPreferredSize(new Dimension(800, 360));
        view.add(scroll, BorderLayout.CENTER);

        return view;
    }

    // =========================================================================
    // 3. REGISTRATIONS VIEW
    // =========================================================================
    private JPanel createRegistrationsView() {
        JPanel view = new JPanel(new BorderLayout(0, 16));
        view.setBackground(Color.WHITE);
        view.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));

        // Filters and Export Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterPanel.setOpaque(false);

        regEventFilterCombo = new JComboBox<>();
        regEventFilterCombo.setPreferredSize(new Dimension(240, 34));
        regEventFilterCombo.addItem(new EventComboItem(null, "All Events"));
        regEventFilterCombo.addActionListener(e -> loadRegistrationsData());

        regSearchField = new JTextField(18);
        regSearchField.putClientProperty("JTextField.placeholderText", "Search student, reg no, or email...");
        regSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadRegistrationsData();
            }
        });

        filterPanel.add(new JLabel("Filter by Event:"));
        filterPanel.add(regEventFilterCombo);
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(regSearchField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton markAttendedBtn = new JButton("✓ Mark Attended");
        UIConstants.styleSuccessButton(markAttendedBtn);
        markAttendedBtn.addActionListener(e -> handleMarkAttended());

        JButton deleteRegBtn = new JButton("Delete Registration");
        UIConstants.styleDangerButton(deleteRegBtn);
        deleteRegBtn.addActionListener(e -> handleDeleteRegistration());

        JButton exportCsvBtn = new JButton("📥 Export to CSV");
        UIConstants.stylePrimaryButton(exportCsvBtn);
        exportCsvBtn.addActionListener(e -> CSVExporter.exportRegistrations(adminRegsList, this));

        btnPanel.add(markAttendedBtn);
        btnPanel.add(deleteRegBtn);
        btnPanel.add(exportCsvBtn);

        topBar.add(filterPanel, BorderLayout.WEST);
        topBar.add(btnPanel, BorderLayout.EAST);
        view.add(topBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Pass No", "Student Name", "Email", "Enrollment No", "Event Title", "Venue", "Registered Date", "Status", "Requirements"};
        regsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        regsTable = new JTable(regsModel);
        regsTable.setRowHeight(32);
        regsTable.setFont(UIConstants.FONT_BODY);
        regsTable.getTableHeader().setFont(UIConstants.FONT_BODY_BOLD);
        regsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        regsTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String st = value != null ? value.toString() : "";
                l.setHorizontalAlignment(CENTER);
                l.setFont(UIConstants.FONT_BODY_BOLD);
                if ("REGISTERED".equalsIgnoreCase(st)) l.setForeground(UIConstants.BADGE_GREEN_TEXT);
                else if ("ATTENDED".equalsIgnoreCase(st)) l.setForeground(UIConstants.PRIMARY_BLUE);
                else if ("CANCELLED".equalsIgnoreCase(st)) l.setForeground(UIConstants.ACCENT_RED);
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(regsTable);
        scroll.setPreferredSize(new Dimension(800, 360));
        view.add(scroll, BorderLayout.CENTER);

        return view;
    }

    // =========================================================================
    // 4. COURSES VIEW
    // =========================================================================
    private JPanel createCoursesView() {
        JPanel view = new JPanel(new BorderLayout(0, 16));
        view.setBackground(Color.WHITE);
        view.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel lbl = new JLabel("Academic Course Curriculums");
        lbl.setFont(UIConstants.FONT_TITLE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton addBtn = new JButton("+ Add Course");
        UIConstants.stylePrimaryButton(addBtn);
        addBtn.addActionListener(e -> showCourseFormDialog(null));

        JButton editBtn = new JButton("Edit Course");
        UIConstants.styleOutlineButton(editBtn);
        editBtn.addActionListener(e -> handleEditCourse());

        JButton deleteBtn = new JButton("Delete Course");
        UIConstants.styleDangerButton(deleteBtn);
        deleteBtn.addActionListener(e -> handleDeleteCourse());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        topBar.add(lbl, BorderLayout.WEST);
        topBar.add(btnPanel, BorderLayout.EAST);
        view.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Course Name", "Department", "Duration", "Linked Events Count", "Description"};
        coursesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        coursesTable = new JTable(coursesModel);
        coursesTable.setRowHeight(32);
        coursesTable.setFont(UIConstants.FONT_BODY);
        coursesTable.getTableHeader().setFont(UIConstants.FONT_BODY_BOLD);
        coursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(coursesTable);
        scroll.setPreferredSize(new Dimension(800, 360));
        view.add(scroll, BorderLayout.CENTER);

        return view;
    }

    // =========================================================================
    // 5. USERS VIEW
    // =========================================================================
    private JPanel createUsersView() {
        JPanel view = new JPanel(new BorderLayout(0, 16));
        view.setBackground(Color.WHITE);
        view.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);

        userSearchField = new JTextField(24);
        userSearchField.putClientProperty("JTextField.placeholderText", "Search student by name, email, or enrollment no...");
        userSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadUsersData();
            }
        });

        searchPanel.add(new JLabel("Search Students:"));
        searchPanel.add(userSearchField);

        topBar.add(searchPanel, BorderLayout.WEST);
        view.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Full Name", "College Email", "Phone", "Enrollment No", "Course", "Year", "Joined Date"};
        usersModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        usersTable = new JTable(usersModel);
        usersTable.setRowHeight(32);
        usersTable.setFont(UIConstants.FONT_BODY);
        usersTable.getTableHeader().setFont(UIConstants.FONT_BODY_BOLD);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(usersTable);
        scroll.setPreferredSize(new Dimension(800, 360));
        view.add(scroll, BorderLayout.CENTER);

        return view;
    }

    // =========================================================================
    // DATA LOADING & ACTIONS
    // =========================================================================
    public void loadData() {
        dashBtn.setSelected(true);
        adminCards.show(adminContainer, "DASHBOARD");
        loadDashboardData();
    }

    private void loadDashboardData() {
        new SwingWorker<Void, Void>() {
            private EventDAO.EventStats stats;
            private Map<String, Integer> chartMap;

            @Override
            protected Void doInBackground() throws Exception {
                stats = eventDAO.getEventStats();
                chartMap = eventDAO.getRegistrationCountsPerEvent();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (stats != null) {
                        totalCoursesStat.setValue(stats.totalCourses);
                        upcomingEventsStat.setValue(stats.upcomingEvents);
                        ongoingEventsStat.setValue(stats.ongoingEvents);
                        pastEventsStat.setValue(stats.pastEvents);
                        totalRegsStat.setValue(stats.totalRegistrations);
                    }
                    if (chartMap != null) {
                        barChartPanel.setData(chartMap);
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading admin dashboard: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void loadEventsData() {
        String query = eventSearchField != null ? eventSearchField.getText().trim() : "";
        new SwingWorker<List<Event>, Void>() {
            @Override
            protected List<Event> doInBackground() throws Exception {
                com.campusevents.model.EventFilter filter = new com.campusevents.model.EventFilter();
                filter.setSearchQuery(query);
                return eventDAO.getAll(filter);
            }

            @Override
            protected void done() {
                try {
                    adminEventsList = get();
                    eventsModel.setRowCount(0);
                    if (adminEventsList != null) {
                        for (Event e : adminEventsList) {
                            eventsModel.addRow(new Object[]{
                                    e.getId(),
                                    e.getTitle(),
                                    e.getCourseName() != null ? e.getCourseName() : "General",
                                    e.getVenue() + " (" + e.getBuilding() + ")",
                                    DateUtil.formatDateTime(e.getStartDateTime()),
                                    DateUtil.formatDateTime(e.getEndDateTime()),
                                    DateUtil.formatDateTime(e.getRegistrationDeadline()),
                                    e.getTotalSeats(),
                                    e.getRegisteredCount(),
                                    e.isFree() ? "Free" : "Rs. " + e.getFee(),
                                    e.getOrganizer()
                            });
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading events table: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void loadRegistrationsData() {
        EventComboItem selectedEvent = regEventFilterCombo != null ? (EventComboItem) regEventFilterCombo.getSelectedItem() : null;
        Integer eventId = (selectedEvent != null) ? selectedEvent.id : null;
        String query = regSearchField != null ? regSearchField.getText().trim() : "";

        new SwingWorker<Void, Void>() {
            private List<Registration> regs;
            private List<Event> events;

            @Override
            protected Void doInBackground() throws Exception {
                regs = registrationDAO.getAllRegistrations(eventId, query);
                events = eventDAO.getAll(null);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    adminRegsList = regs;

                    // Sync event filter combo if needed
                    if (regEventFilterCombo.getItemCount() <= 1 && events != null) {
                        regEventFilterCombo.removeAllItems();
                        regEventFilterCombo.addItem(new EventComboItem(null, "All Events"));
                        for (Event e : events) {
                            regEventFilterCombo.addItem(new EventComboItem(e.getId(), e.getTitle()));
                        }
                    }

                    regsModel.setRowCount(0);
                    if (adminRegsList != null) {
                        for (Registration r : adminRegsList) {
                            regsModel.addRow(new Object[]{
                                    r.getRegNo(),
                                    r.getUserName(),
                                    r.getUserEmail(),
                                    r.getEnrollmentNo(),
                                    r.getEventTitle(),
                                    r.getVenue(),
                                    DateUtil.formatDateTime(r.getRegisteredOn()),
                                    r.getStatus(),
                                    r.getRequirements() != null ? r.getRequirements() : ""
                            });
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading registrations table: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void loadCoursesData() {
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return courseDAO.getAll();
            }

            @Override
            protected void done() {
                try {
                    adminCoursesList = get();
                    coursesModel.setRowCount(0);
                    if (adminCoursesList != null) {
                        for (Course c : adminCoursesList) {
                            coursesModel.addRow(new Object[]{
                                    c.getId(),
                                    c.getName(),
                                    c.getDepartment(),
                                    c.getDuration(),
                                    c.getEventCount(),
                                    c.getDescription()
                            });
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading courses table: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void loadUsersData() {
        String query = userSearchField != null ? userSearchField.getText().trim() : "";
        new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return userDAO.searchStudents(query);
            }

            @Override
            protected void done() {
                try {
                    adminUsersList = get();
                    usersModel.setRowCount(0);
                    if (adminUsersList != null) {
                        for (User u : adminUsersList) {
                            usersModel.addRow(new Object[]{
                                    u.getId(),
                                    u.getFullName(),
                                    u.getEmail(),
                                    u.getPhone(),
                                    u.getEnrollmentNo(),
                                    u.getCourseName() != null ? u.getCourseName() : "None",
                                    u.getYear() != null ? u.getYear() : "N/A",
                                    DateUtil.formatDateTime(u.getCreatedAt())
                            });
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading students table: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // =========================================================================
    // EVENT ADD / EDIT / DELETE ACTIONS & DIALOG
    // =========================================================================
    private void handleEditEvent() {
        int row = eventsTable.getSelectedRow();
        if (row < 0 || adminEventsList == null || row >= adminEventsList.size()) {
            JOptionPane.showMessageDialog(this, "Please select an event from the table to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Event e = adminEventsList.get(row);
        showEventFormDialog(e);
    }

    private void handleDeleteEvent() {
        int row = eventsTable.getSelectedRow();
        if (row < 0 || adminEventsList == null || row >= adminEventsList.size()) {
            JOptionPane.showMessageDialog(this, "Please select an event to delete.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Event e = adminEventsList.get(row);
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete event:\n\"" + e.getTitle() + "\"?\n\nAll linked registrations will be permanently deleted.",
                "Confirm Event Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) return;

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return eventDAO.delete(e.getId());
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(AdminPanelPage.this, "Event deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadEventsData();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminPanelPage.this, "Failed to delete event: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Dialog for Adding or Editing an Event with JSpinner date models, thumbnail upload & preview,
     * and strict validation (end > start, deadline <= start).
     */
    private void showEventFormDialog(Event existingEvent) {
        boolean isEdit = (existingEvent != null);
        JDialog dialog = new JDialog(mainFrame, isEdit ? "Edit Event - " + existingEvent.getTitle() : "Add New Academic Event", true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 16, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = new JTextField(isEdit ? existingEvent.getTitle() : "", 22);
        JComboBox<EventComboItem> courseCombo = new JComboBox<>();
        try {
            List<Course> courses = courseDAO.getAll();
            courseCombo.addItem(new EventComboItem(null, "General / Open Event"));
            for (Course c : courses) {
                EventComboItem item = new EventComboItem(c.getId(), c.getName());
                courseCombo.addItem(item);
                if (isEdit && existingEvent.getCourseId() != null && existingEvent.getCourseId() == c.getId()) {
                    courseCombo.setSelectedItem(item);
                }
            }
        } catch (Exception ignored) {}

        JTextArea descArea = new JTextArea(isEdit ? existingEvent.getDescription() : "", 4, 22);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);

        JTextField venueField = new JTextField(isEdit ? existingEvent.getVenue() : "Seminar Hall A", 18);
        JTextField bldgField = new JTextField(isEdit ? existingEvent.getBuilding() : "Main Block", 18);

        // Date Spinners
        Date startDate = isEdit && existingEvent.getStartDateTime() != null ?
                Date.from(existingEvent.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant()) :
                new Date(System.currentTimeMillis() + 86400000L * 3); // 3 days from now

        Date endDate = isEdit && existingEvent.getEndDateTime() != null ?
                Date.from(existingEvent.getEndDateTime().atZone(ZoneId.systemDefault()).toInstant()) :
                new Date(System.currentTimeMillis() + 86400000L * 3 + 14400000L); // +4 hours

        Date deadlineDate = isEdit && existingEvent.getRegistrationDeadline() != null ?
                Date.from(existingEvent.getRegistrationDeadline().atZone(ZoneId.systemDefault()).toInstant()) :
                new Date(System.currentTimeMillis() + 86400000L * 2); // 2 days from now

        JSpinner startSpinner = new JSpinner(new SpinnerDateModel(startDate, null, null, Calendar.MINUTE));
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "yyyy-MM-dd HH:mm"));

        JSpinner endSpinner = new JSpinner(new SpinnerDateModel(endDate, null, null, Calendar.MINUTE));
        endSpinner.setEditor(new JSpinner.DateEditor(endSpinner, "yyyy-MM-dd HH:mm"));

        JSpinner deadlineSpinner = new JSpinner(new SpinnerDateModel(deadlineDate, null, null, Calendar.MINUTE));
        deadlineSpinner.setEditor(new JSpinner.DateEditor(deadlineSpinner, "yyyy-MM-dd HH:mm"));

        JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(isEdit ? existingEvent.getTotalSeats() : 50, 1, 1000, 5));
        JTextField feeField = new JTextField(isEdit ? existingEvent.getFee().toString() : "0.00", 8);

        JTextField orgField = new JTextField(isEdit ? existingEvent.getOrganizer() : "Prof. Department Head", 18);
        JTextField contactField = new JTextField(isEdit ? existingEvent.getContact() : "coordinator@campus.edu", 18);

        // Thumbnail Upload with Preview
        final String[] thumbPathHolder = {isEdit ? existingEvent.getThumbnailPath() : "images/event1.jpg"};
        JLabel previewLabel = new JLabel(ImageUtil.getImageIcon(thumbPathHolder[0], 120, 68, "PREV"));
        previewLabel.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1));

        JButton uploadBtn = new JButton("Choose Thumbnail File...");
        UIConstants.styleOutlineButton(uploadBtn);
        uploadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Event Thumbnail Image");
            chooser.setFileFilter(new FileNameExtensionFilter("Image Files (*.jpg, *.png)", "jpg", "jpeg", "png"));
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    String relativePath = ImageUtil.uploadThumbnail(file);
                    thumbPathHolder[0] = relativePath;
                    previewLabel.setIcon(ImageUtil.getImageIcon(relativePath, 120, 68, "THUMB"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Failed to upload image: " + ex.getMessage(), "Upload Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel thumbRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        thumbRow.setOpaque(false);
        thumbRow.add(previewLabel);
        thumbRow.add(uploadBtn);

        // Add to GridBagLayout
        int row = 0;
        addFormRow(form, gbc, row++, "Event Title: *", titleField);
        addFormRow(form, gbc, row++, "Academic Course:", courseCombo);
        addFormRow(form, gbc, row++, "Description (4-6 Lines): *", descScroll);
        addFormRow(form, gbc, row++, "Venue (Room): *", venueField);
        addFormRow(form, gbc, row++, "Building: *", bldgField);
        addFormRow(form, gbc, row++, "Start Date & Time: *", startSpinner);
        addFormRow(form, gbc, row++, "End Date & Time: *", endSpinner);
        addFormRow(form, gbc, row++, "Registration Deadline: *", deadlineSpinner);
        addFormRow(form, gbc, row++, "Total Seat Capacity: *", seatsSpinner);
        addFormRow(form, gbc, row++, "Fee (0 = Free): *", feeField);
        addFormRow(form, gbc, row++, "Organizer / Speaker: *", orgField);
        addFormRow(form, gbc, row++, "Contact Details: *", contactField);
        addFormRow(form, gbc, row++, "Thumbnail Image:", thumbRow);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        bottomBar.setBackground(UIConstants.BG_MAIN);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER_LIGHT));

        JButton saveBtn = new JButton(isEdit ? "Update Event" : "Create Event");
        UIConstants.stylePrimaryButton(saveBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(UIConstants.FONT_BODY);
        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String titleVal = titleField.getText().trim();
            String descVal = descArea.getText().trim();
            String venueVal = venueField.getText().trim();
            String bldgVal = bldgField.getText().trim();
            String orgVal = orgField.getText().trim();
            String contactVal = contactField.getText().trim();

            Date sDate = (Date) startSpinner.getValue();
            Date eDate = (Date) endSpinner.getValue();
            Date dDate = (Date) deadlineSpinner.getValue();

            LocalDateTime startLdt = sDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime endLdt = eDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime deadLdt = dDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            // Validation rules
            if (titleVal.isEmpty() || descVal.isEmpty() || venueVal.isEmpty() || bldgVal.isEmpty() || orgVal.isEmpty() || contactVal.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!endLdt.isAfter(startLdt)) {
                JOptionPane.showMessageDialog(dialog, "Validation Error: End date-time must be strictly AFTER Start date-time.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (deadLdt.isAfter(startLdt)) {
                JOptionPane.showMessageDialog(dialog, "Validation Error: Registration deadline must be on or BEFORE the Event start time.", "Invalid Deadline", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal feeVal;
            try {
                feeVal = new BigDecimal(feeField.getText().trim());
                if (feeVal.compareTo(BigDecimal.ZERO) < 0) throw new Exception();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid non-negative fee amount.", "Invalid Fee", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Event ev = isEdit ? existingEvent : new Event();
            ev.setTitle(titleVal);

            EventComboItem selCourse = (EventComboItem) courseCombo.getSelectedItem();
            ev.setCourseId(selCourse != null ? selCourse.id : null);
            ev.setDescription(descVal);
            ev.setVenue(venueVal);
            ev.setBuilding(bldgVal);
            ev.setStartDateTime(startLdt);
            ev.setEndDateTime(endLdt);
            ev.setRegistrationDeadline(deadLdt);
            ev.setTotalSeats((Integer) seatsSpinner.getValue());
            ev.setFee(feeVal);
            ev.setOrganizer(orgVal);
            ev.setContact(contactVal);
            ev.setThumbnailPath(thumbPathHolder[0]);

            saveBtn.setEnabled(false);
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (isEdit) {
                        return eventDAO.update(ev);
                    } else {
                        return eventDAO.create(ev);
                    }
                }

                @Override
                protected void done() {
                    try {
                        boolean ok = get();
                        if (ok) {
                            dialog.dispose();
                            JOptionPane.showMessageDialog(AdminPanelPage.this, "Event successfully " + (isEdit ? "updated!" : "created!"), "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadEventsData();
                        }
                    } catch (Exception ex) {
                        saveBtn.setEnabled(true);
                        JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        bottomBar.add(saveBtn);
        bottomBar.add(cancelBtn);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(bottomBar, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setSize(680, 720);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.25;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_BODY_BOLD);
        form.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        form.add(comp, gbc);
    }

    // =========================================================================
    // REGISTRATION ACTIONS
    // =========================================================================
    private void handleMarkAttended() {
        int row = regsTable.getSelectedRow();
        if (row < 0 || adminRegsList == null || row >= adminRegsList.size()) {
            JOptionPane.showMessageDialog(this, "Please select a registration to mark as Attended.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Registration reg = adminRegsList.get(row);
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return registrationDAO.updateStatus(reg.getId(), "ATTENDED");
            }

            @Override
            protected void done() {
                try {
                    get();
                    loadRegistrationsData();
                    JOptionPane.showMessageDialog(AdminPanelPage.this, "Registration " + reg.getRegNo() + " marked as ATTENDED.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminPanelPage.this, "Error updating status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void handleDeleteRegistration() {
        int row = regsTable.getSelectedRow();
        if (row < 0 || adminRegsList == null || row >= adminRegsList.size()) {
            JOptionPane.showMessageDialog(this, "Please select a registration to delete.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Registration reg = adminRegsList.get(row);
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete registration:\n\"" + reg.getRegNo() + "\" (" + reg.getUserName() + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) return;

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return registrationDAO.deleteRegistration(reg.getId());
            }

            @Override
            protected void done() {
                try {
                    get();
                    loadRegistrationsData();
                    JOptionPane.showMessageDialog(AdminPanelPage.this, "Registration deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminPanelPage.this, "Error deleting registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // =========================================================================
    // COURSE ADD / EDIT / DELETE ACTIONS & DIALOG
    // =========================================================================
    private void handleEditCourse() {
        int row = coursesTable.getSelectedRow();
        if (row < 0 || adminCoursesList == null || row >= adminCoursesList.size()) {
            JOptionPane.showMessageDialog(this, "Please select a course to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Course c = adminCoursesList.get(row);
        showCourseFormDialog(c);
    }

    private void handleDeleteCourse() {
        int row = coursesTable.getSelectedRow();
        if (row < 0 || adminCoursesList == null || row >= adminCoursesList.size()) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Course c = adminCoursesList.get(row);
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete course:\n\"" + c.getName() + "\"?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) return;

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return courseDAO.delete(c.getId());
            }

            @Override
            protected void done() {
                try {
                    get();
                    loadCoursesData();
                    JOptionPane.showMessageDialog(AdminPanelPage.this, "Course deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminPanelPage.this, "Error deleting course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void showCourseFormDialog(Course existingCourse) {
        boolean isEdit = (existingCourse != null);
        JDialog dialog = new JDialog(mainFrame, isEdit ? "Edit Course - " + existingCourse.getName() : "Add New Academic Course", true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 12));
        form.setBorder(new EmptyBorder(20, 24, 16, 24));

        JTextField nameField = new JTextField(isEdit ? existingCourse.getName() : "");
        JTextField deptField = new JTextField(isEdit ? existingCourse.getDepartment() : "");
        JTextField durField = new JTextField(isEdit ? existingCourse.getDuration() : "4 Years");
        JTextField descField = new JTextField(isEdit ? existingCourse.getDescription() : "");
        JTextField imgField = new JTextField(isEdit ? existingCourse.getImagePath() : "images/course1.jpg");

        form.add(new JLabel("Course Name: *"));
        form.add(nameField);
        form.add(new JLabel("Department: *"));
        form.add(deptField);
        form.add(new JLabel("Duration: *"));
        form.add(durField);
        form.add(new JLabel("Description:"));
        form.add(descField);
        form.add(new JLabel("Image Resource Path:"));
        form.add(imgField);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        bottomBar.setBackground(UIConstants.BG_MAIN);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER_LIGHT));

        JButton saveBtn = new JButton(isEdit ? "Update Course" : "Save Course");
        UIConstants.stylePrimaryButton(saveBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(UIConstants.FONT_BODY);
        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String dept = deptField.getText().trim();
            String dur = durField.getText().trim();
            String desc = descField.getText().trim();
            String img = imgField.getText().trim();

            if (name.isEmpty() || dept.isEmpty() || dur.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in course name, department, and duration.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Course c = isEdit ? existingCourse : new Course();
            c.setName(name);
            c.setDepartment(dept);
            c.setDuration(dur);
            c.setDescription(desc);
            c.setImagePath(img);

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (isEdit) return courseDAO.update(c);
                    else return courseDAO.create(c);
                }

                @Override
                protected void done() {
                    try {
                        get();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(AdminPanelPage.this, "Course successfully " + (isEdit ? "updated!" : "added!"), "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadCoursesData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        bottomBar.add(saveBtn);
        bottomBar.add(cancelBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bottomBar, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setSize(520, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}

