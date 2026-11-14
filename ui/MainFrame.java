package com.campusevents.ui;

import com.campusevents.model.User;
import com.campusevents.ui.components.Navbar;
import com.campusevents.ui.pages.AboutPage;
import com.campusevents.ui.pages.CoursesPage;
import com.campusevents.ui.pages.EventDetailsPage;
import com.campusevents.ui.pages.EventsPage;
import com.campusevents.ui.pages.HomePage;
import com.campusevents.ui.pages.LoginPage;
import com.campusevents.ui.pages.MyRegistrationsPage;
import com.campusevents.ui.pages.SignUpPage;
import com.campusevents.ui.pages.admin.AdminPanelPage;
import com.campusevents.util.ImageUtil;
import com.campusevents.util.SessionManager;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

/**
 * Single-window application frame hosting all pages via CardLayout navigation.
 * Guarantees zero auxiliary windows for pages, vertical-only scrolling, and smooth state updates.
 */
public class MainFrame extends JFrame {

    private Navbar navbar;
    private CardLayout cardLayout;
    private JPanel cardsPanel;

    private final Map<String, JScrollPane> scrollPanes = new HashMap<>();

    // Registered Pages
    private HomePage homePage;
    private CoursesPage coursesPage;
    private EventsPage eventsPage;
    private EventDetailsPage eventDetailsPage;
    private LoginPage loginPage;
    private SignUpPage signUpPage;
    private MyRegistrationsPage myRegistrationsPage;
    private AboutPage aboutPage;
    private AdminPanelPage adminPanelPage;

    public MainFrame() {
        super("CampusCourse Events - College Course Event Management System");
        initWindow();
        initPages();
        navigateTo("HOME");
    }

    private void initWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 720));
        setPreferredSize(new Dimension(1280, 840));

        // Window Icon
        ImageIcon icon = ImageUtil.getImageIcon("images/logo.png", 64, 64, "CE");
        if (icon != null) {
            setIconImage(icon.getImage());
        }

        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.BG_MAIN);

        // Top Persistent Navbar
        navbar = new Navbar(this);
        add(navbar, BorderLayout.NORTH);

        // Center CardLayout Panel
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(UIConstants.BG_MAIN);
        add(cardsPanel, BorderLayout.CENTER);
    }

    private void initPages() {
        homePage = new HomePage(this);
        coursesPage = new CoursesPage(this);
        eventsPage = new EventsPage(this);
        eventDetailsPage = new EventDetailsPage(this);
        loginPage = new LoginPage(this);
        signUpPage = new SignUpPage(this);
        myRegistrationsPage = new MyRegistrationsPage(this);
        aboutPage = new AboutPage(this);
        adminPanelPage = new AdminPanelPage(this);

        registerPage("HOME", homePage);
        registerPage("COURSES", coursesPage);
        registerPage("EVENTS", eventsPage);
        registerPage("EVENT_DETAILS", eventDetailsPage);
        registerPage("LOGIN", loginPage);
        registerPage("SIGNUP", signUpPage);
        registerPage("MY_REGISTRATIONS", myRegistrationsPage);
        registerPage("ABOUT", aboutPage);
        registerPage("ADMIN_DASHBOARD", adminPanelPage);
    }

    /**
     * Wraps a page in a vertical-only JScrollPane and registers it into the CardLayout.
     */
    private void registerPage(String name, JComponent page) {
        JScrollPane scroll = new JScrollPane(page);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);

        scrollPanes.put(name, scroll);
        cardsPanel.add(scroll, name);
    }

    /**
     * Primary navigation method to swap active page.
     */
    public void navigateTo(String pageKey) {
        // Enforce role access control
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (pageKey.startsWith("ADMIN_") && (currentUser == null || !currentUser.isAdmin())) {
            navigateTo("LOGIN");
            return;
        }
        if ("MY_REGISTRATIONS".equals(pageKey) && (currentUser == null || !currentUser.isStudent())) {
            navigateTo("LOGIN");
            return;
        }

        cardLayout.show(cardsPanel, pageKey);
        navbar.setActivePage(pageKey);

        // Scroll target page back to top
        JScrollPane currentScroll = scrollPanes.get(pageKey);
        if (currentScroll != null) {
            SwingUtilities.invokeLater(() -> currentScroll.getViewport().setViewPosition(new Point(0, 0)));
        }

        // Trigger asynchronous data load for target page
        if ("HOME".equals(pageKey)) homePage.loadData();
        else if ("COURSES".equals(pageKey)) coursesPage.loadData();
        else if ("EVENTS".equals(pageKey)) eventsPage.loadData();
        else if ("MY_REGISTRATIONS".equals(pageKey)) myRegistrationsPage.loadData();
        else if ("SIGNUP".equals(pageKey)) signUpPage.loadCourses();
        else if ("ADMIN_DASHBOARD".equals(pageKey)) adminPanelPage.loadData();
    }

    /**
     * Navigates directly to Event Details page for a specific event ID.
     */
    public void navigateToEventDetails(int eventId) {
        cardLayout.show(cardsPanel, "EVENT_DETAILS");
        navbar.setActivePage("EVENTS");

        JScrollPane scroll = scrollPanes.get("EVENT_DETAILS");
        if (scroll != null) {
            SwingUtilities.invokeLater(() -> scroll.getViewport().setViewPosition(new Point(0, 0)));
        }

        eventDetailsPage.loadEvent(eventId);
    }

    /**
     * Navigates to Events catalog pre-filtered by an academic course ID.
     */
    public void navigateToEventsFilteredByCourse(int courseId) {
        cardLayout.show(cardsPanel, "EVENTS");
        navbar.setActivePage("EVENTS");

        JScrollPane scroll = scrollPanes.get("EVENTS");
        if (scroll != null) {
            SwingUtilities.invokeLater(() -> scroll.getViewport().setViewPosition(new Point(0, 0)));
        }

        eventsPage.filterByCourse(courseId);
    }

    public Navbar getNavbar() {
        return navbar;
    }
}

