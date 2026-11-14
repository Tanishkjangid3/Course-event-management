package com.campusevents.ui.components;

import com.campusevents.model.User;
import com.campusevents.ui.MainFrame;
import com.campusevents.util.ImageUtil;
import com.campusevents.util.SessionManager;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Top persistent navigation bar with college brand logo, links, dynamic role-based menus,
 * and user session state integration.
 */
public class Navbar extends JPanel {

    private final MainFrame mainFrame;
    private JPanel linksPanel;
    private JPanel authPanel;

    private JLabel homeLink;
    private JLabel coursesLink;
    private JLabel eventsLink;
    private JLabel myRegistrationsLink;
    private JLabel adminLink;
    private JLabel aboutLink;

    private String activePage = "HOME";

    public Navbar(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initUI();
        SessionManager.getInstance().addSessionListener(user -> updateAuthSection());
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_NAVBAR);
        setPreferredSize(new Dimension(1100, 68));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_LIGHT),
                new EmptyBorder(0, 24, 0, 24)
        ));

        // 1. Brand Logo & Title (Left)
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);
        brandPanel.setCursor(UIConstants.HAND_CURSOR);
        brandPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.navigateTo("HOME");
            }
        });

        ImageIcon logoIcon = ImageUtil.getImageIcon("images/logo.png", 42, 42, "CE");
        JLabel logoLabel = new JLabel(logoIcon);

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);

        JLabel titleLabel = new JLabel("CampusCourse");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(UIConstants.PRIMARY_BLUE);

        JLabel subtitleLabel = new JLabel("EVENTS MANAGEMENT");
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        subtitleLabel.setForeground(UIConstants.TEXT_MUTED);

        titleContainer.add(titleLabel);
        titleContainer.add(subtitleLabel);

        brandPanel.add(logoLabel);
        brandPanel.add(titleContainer);

        // 2. Navigation Links (Center)
        linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        linksPanel.setOpaque(false);

        homeLink = createNavLink("Home", "HOME");
        coursesLink = createNavLink("Courses", "COURSES");
        eventsLink = createNavLink("Events", "EVENTS");
        myRegistrationsLink = createNavLink("My Registrations", "MY_REGISTRATIONS");
        adminLink = createNavLink("Admin Panel", "ADMIN_DASHBOARD");
        aboutLink = createNavLink("About", "ABOUT");

        linksPanel.add(homeLink);
        linksPanel.add(coursesLink);
        linksPanel.add(eventsLink);
        linksPanel.add(myRegistrationsLink);
        linksPanel.add(adminLink);
        linksPanel.add(aboutLink);

        // 3. Auth Actions (Right)
        authPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        authPanel.setOpaque(false);

        add(brandPanel, BorderLayout.WEST);
        add(linksPanel, BorderLayout.CENTER);
        add(authPanel, BorderLayout.EAST);

        updateAuthSection();
    }

    private JLabel createNavLink(String text, String pageKey) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(UIConstants.TEXT_SECONDARY);
        label.setCursor(UIConstants.HAND_CURSOR);
        label.setBorder(new EmptyBorder(8, 6, 8, 6));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!pageKey.equals(activePage)) {
                    label.setForeground(UIConstants.PRIMARY_BLUE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!pageKey.equals(activePage)) {
                    label.setForeground(UIConstants.TEXT_SECONDARY);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.navigateTo(pageKey);
            }
        });
        return label;
    }

    public void setActivePage(String pageKey) {
        this.activePage = pageKey;
        resetLinkStyles();

        if ("HOME".equals(pageKey)) highlightLink(homeLink);
        else if ("COURSES".equals(pageKey)) highlightLink(coursesLink);
        else if ("EVENTS".equals(pageKey)) highlightLink(eventsLink);
        else if ("MY_REGISTRATIONS".equals(pageKey)) highlightLink(myRegistrationsLink);
        else if (pageKey != null && pageKey.startsWith("ADMIN_")) highlightLink(adminLink);
        else if ("ABOUT".equals(pageKey)) highlightLink(aboutLink);
    }

    private void resetLinkStyles() {
        JLabel[] all = {homeLink, coursesLink, eventsLink, myRegistrationsLink, adminLink, aboutLink};
        for (JLabel l : all) {
            l.setForeground(UIConstants.TEXT_SECONDARY);
            l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
    }

    private void highlightLink(JLabel label) {
        if (label != null) {
            label.setForeground(UIConstants.PRIMARY_BLUE);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
    }

    public void updateAuthSection() {
        authPanel.removeAll();
        User user = SessionManager.getInstance().getCurrentUser();

        if (user != null) {
            // Student or Admin logged in
            myRegistrationsLink.setVisible(user.isStudent());
            adminLink.setVisible(user.isAdmin());

            // User Info pill
            JPanel userPill = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            userPill.setBackground(new Color(241, 243, 244));
            userPill.setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true));

            JLabel roleBadge = new JLabel(user.isAdmin() ? "ADMIN" : "STUDENT");
            roleBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            roleBadge.setOpaque(true);
            roleBadge.setBackground(user.isAdmin() ? UIConstants.PRIMARY_BLUE : UIConstants.ACCENT_GREEN);
            roleBadge.setForeground(Color.WHITE);
            roleBadge.setBorder(new EmptyBorder(2, 6, 2, 6));

            JLabel nameLabel = new JLabel("Hi, " + user.getFullName());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nameLabel.setForeground(UIConstants.TEXT_PRIMARY);

            userPill.add(roleBadge);
            userPill.add(nameLabel);

            JButton logoutBtn = new JButton("Logout");
            UIConstants.styleOutlineButton(logoutBtn);
            logoutBtn.addActionListener(e -> {
                SessionManager.getInstance().logout();
                mainFrame.navigateTo("HOME");
            });

            authPanel.add(userPill);
            authPanel.add(logoutBtn);

        } else {
            // Guest mode
            myRegistrationsLink.setVisible(false);
            adminLink.setVisible(false);

            JButton loginBtn = new JButton("Login");
            UIConstants.styleOutlineButton(loginBtn);
            loginBtn.addActionListener(e -> mainFrame.navigateTo("LOGIN"));

            JButton signUpBtn = new JButton("Sign Up");
            UIConstants.stylePrimaryButton(signUpBtn);
            signUpBtn.addActionListener(e -> mainFrame.navigateTo("SIGNUP"));

            authPanel.add(loginBtn);
            authPanel.add(signUpBtn);
        }

        authPanel.revalidate();
        authPanel.repaint();
        linksPanel.revalidate();
        linksPanel.repaint();
    }
}

