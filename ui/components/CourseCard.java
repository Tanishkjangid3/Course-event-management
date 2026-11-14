package com.campusevents.ui.components;

import com.campusevents.model.Course;
import com.campusevents.ui.MainFrame;
import com.campusevents.util.ImageUtil;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
 * Card representing an Academic Course with thumbnail image, duration badge,
 * department metadata, and an action button to browse course events.
 */
public class CourseCard extends JPanel {

    private final Course course;
    private final MainFrame mainFrame;

    public CourseCard(Course course, MainFrame mainFrame) {
        this.course = course;
        this.mainFrame = mainFrame;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_CARD);
        setPreferredSize(new Dimension(340, 390));
        setMaximumSize(new Dimension(380, 410));
        setCursor(UIConstants.HAND_CURSOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(0, 0, 14, 0)
        ));

        // 1. Thumbnail Header with Duration Badge
        JPanel thumbPanel = new JPanel(new BorderLayout());
        thumbPanel.setPreferredSize(new Dimension(340, 160));
        thumbPanel.setBackground(new Color(230, 235, 240));

        ImageIcon icon = ImageUtil.getImageIcon(course.getImagePath(), 340, 160, course.getName());
        JLabel imgLabel = new JLabel(icon);
        imgLabel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JLabel durationBadge = new JLabel(course.getDuration());
        durationBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        durationBadge.setOpaque(true);
        durationBadge.setBackground(new Color(32, 33, 36, 190));
        durationBadge.setForeground(Color.WHITE);
        durationBadge.setBorder(new EmptyBorder(4, 10, 4, 10));

        imgLabel.add(durationBadge);
        thumbPanel.add(imgLabel, BorderLayout.CENTER);

        // 2. Body Details
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(14, 16, 8, 16));

        JLabel deptLabel = new JLabel(course.getDepartment().toUpperCase());
        deptLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        deptLabel.setForeground(UIConstants.PRIMARY_BLUE);

        JLabel titleLabel = new JLabel("<html><body style='width: 280px;'><b>" + course.getName() + "</b></body></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);

        String desc = course.getDescription() != null ? course.getDescription() : "";
        if (desc.length() > 110) {
            desc = desc.substring(0, 108) + "...";
        }
        JLabel descLabel = new JLabel("<html><body style='width: 280px; color: #5F6368;'>" + desc + "</body></html>");
        descLabel.setFont(UIConstants.FONT_BODY);

        body.add(deptLabel);
        body.add(Box.createVerticalStrut(4));
        body.add(titleLabel);
        body.add(Box.createVerticalStrut(8));
        body.add(descLabel);

        // 3. Footer row: Event count + Explore Events button
        JPanel footRow = new JPanel(new BorderLayout());
        footRow.setOpaque(false);
        footRow.setBorder(new EmptyBorder(8, 16, 4, 16));

        int count = course.getEventCount();
        String countStr = count + (count == 1 ? " Event" : " Events");
        JLabel countLabel = new JLabel("🎯 " + countStr);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(UIConstants.TEXT_SECONDARY);

        JButton exploreBtn = new JButton("Explore Events →");
        UIConstants.styleOutlineButton(exploreBtn);
        exploreBtn.addActionListener(e -> mainFrame.navigateToEventsFilteredByCourse(course.getId()));

        footRow.add(countLabel, BorderLayout.WEST);
        footRow.add(exploreBtn, BorderLayout.EAST);

        add(thumbPanel, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
        add(footRow, BorderLayout.SOUTH);

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.PRIMARY_BLUE, 1, true),
                        new EmptyBorder(0, 0, 14, 0)
                ));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                        new EmptyBorder(0, 0, 14, 0)
                ));
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.navigateToEventsFilteredByCourse(course.getId());
            }
        };

        addMouseListener(ma);
        thumbPanel.addMouseListener(ma);
        body.addMouseListener(ma);
    }
}

