package com.campusevents.ui.components;

import com.campusevents.ui.MainFrame;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Global application footer with college event cell credentials, quick navigation, and copyright.
 */
public class Footer extends JPanel {

    public Footer(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_FOOTER);
        setBorder(new EmptyBorder(32, 40, 24, 40));

        JPanel topGrid = new JPanel(new GridLayout(1, 3, 30, 10));
        topGrid.setOpaque(false);

        // Section 1: About Cell
        JPanel col1 = new JPanel();
        col1.setLayout(new BoxLayout(col1, BoxLayout.Y_AXIS));
        col1.setOpaque(false);

        JLabel h1 = new JLabel("CampusCourse Events");
        h1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        h1.setForeground(Color.WHITE);

        JLabel p1 = new JLabel("<html>Centralized Academic & Technology Event Management Portal for College Workshops, Seminars, and Hackathons.</html>");
        p1.setFont(UIConstants.FONT_SMALL);
        p1.setForeground(UIConstants.TEXT_FOOTER_LIGHT);

        col1.add(h1);
        col1.add(Box.createVerticalStrut(8));
        col1.add(p1);

        // Section 2: Quick Links
        JPanel col2 = new JPanel();
        col2.setLayout(new BoxLayout(col2, BoxLayout.Y_AXIS));
        col2.setOpaque(false);

        JLabel h2 = new JLabel("Quick Navigation");
        h2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        h2.setForeground(Color.WHITE);

        JLabel l1 = new JLabel("• Academic Department Courses");
        l1.setFont(UIConstants.FONT_SMALL);
        l1.setForeground(UIConstants.TEXT_FOOTER_LIGHT);

        JLabel l2 = new JLabel("• Upcoming Seminars & Hackathons");
        l2.setFont(UIConstants.FONT_SMALL);
        l2.setForeground(UIConstants.TEXT_FOOTER_LIGHT);

        JLabel l3 = new JLabel("• Instant Verified Admission Passes");
        l3.setFont(UIConstants.FONT_SMALL);
        l3.setForeground(UIConstants.TEXT_FOOTER_LIGHT);

        col2.add(h2);
        col2.add(Box.createVerticalStrut(8));
        col2.add(l1);
        col2.add(Box.createVerticalStrut(4));
        col2.add(l2);
        col2.add(Box.createVerticalStrut(4));
        col2.add(l3);

        // Section 3: Contact & Support
        JPanel col3 = new JPanel();
        col3.setLayout(new BoxLayout(col3, BoxLayout.Y_AXIS));
        col3.setOpaque(false);

        JLabel h3 = new JLabel("College Events Cell");
        h3.setFont(new Font("Segoe UI", Font.BOLD, 15));
        h3.setForeground(Color.WHITE);

        JLabel c1 = new JLabel("Email: events.cell@campus.edu");
        c1.setFont(UIConstants.FONT_SMALL);
        c1.setForeground(UIConstants.TEXT_FOOTER_LIGHT);

        JLabel c2 = new JLabel("Helpdesk: +91 98765 43210 (Ext. 204)");
        c2.setFont(UIConstants.FONT_SMALL);
        c2.setForeground(UIConstants.TEXT_FOOTER_LIGHT);

        JLabel c3 = new JLabel("Location: Student Activity Center, Block B");
        c3.setFont(UIConstants.FONT_SMALL);
        c3.setForeground(UIConstants.TEXT_FOOTER_LIGHT);

        col3.add(h3);
        col3.add(Box.createVerticalStrut(8));
        col3.add(c1);
        col3.add(Box.createVerticalStrut(4));
        col3.add(c2);
        col3.add(Box.createVerticalStrut(4));
        col3.add(c3);

        topGrid.add(col1);
        topGrid.add(col2);
        topGrid.add(col3);

        // Bottom Copyright Line
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(24, 0, 0, 0));

        JLabel copyright = new JLabel("© 2026 CampusCourse Events Management System. All Rights Reserved. Built for Academic Project & Exam.");
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyright.setForeground(new Color(154, 160, 166));
        bottomBar.add(copyright);

        add(topGrid, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }
}

