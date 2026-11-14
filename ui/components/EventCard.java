package com.campusevents.ui.components;

import com.campusevents.model.Event;
import com.campusevents.model.EventStatus;
import com.campusevents.ui.MainFrame;
import com.campusevents.util.DateUtil;
import com.campusevents.util.ImageUtil;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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
 * Modern card representing an Event with smooth thumbnail, status badge,
 * key timing and venue metadata, and interactive hover highlights.
 */
public class EventCard extends JPanel {

    private final Event event;
    private final MainFrame mainFrame;
    private boolean hovered = false;

    public EventCard(Event event, MainFrame mainFrame) {
        this.event = event;
        this.mainFrame = mainFrame;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_CARD);
        setPreferredSize(new Dimension(340, 410));
        setMaximumSize(new Dimension(380, 430));
        setCursor(UIConstants.HAND_CURSOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(0, 0, 12, 0)
        ));

        // 1. Thumbnail Header with Overlay Badge
        JPanel thumbPanel = new JPanel(new BorderLayout());
        thumbPanel.setPreferredSize(new Dimension(340, 180));
        thumbPanel.setBackground(new Color(230, 235, 240));

        ImageIcon icon = ImageUtil.getImageIcon(event.getThumbnailPath(), 340, 180, event.getTitle());
        JLabel imgLabel = new JLabel(icon);
        imgLabel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        // Status Badge on Thumbnail
        EventStatus status = event.getStatus();
        JLabel badge = new JLabel(status.getLabel());
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBackground(status.getBgColor());
        badge.setForeground(status.getTextColor());
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));

        imgLabel.add(badge);
        thumbPanel.add(imgLabel, BorderLayout.CENTER);

        // 2. Body Details
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(12, 16, 8, 16));

        // Course tag
        JLabel courseTag = new JLabel(event.getCourseName() != null ? event.getCourseName().toUpperCase() : "GENERAL");
        courseTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        courseTag.setForeground(UIConstants.PRIMARY_BLUE);

        // Title
        String titleText = event.getTitle();
        if (titleText.length() > 42) {
            titleText = titleText.substring(0, 40) + "...";
        }
        JLabel titleLabel = new JLabel("<html><body style='width: 280px;'><b>" + titleText + "</b></body></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);

        // Date and Time
        String dateTimeStr = "📅 " + DateUtil.formatDateTime(event.getStartDateTime());
        JLabel dateLabel = new JLabel(dateTimeStr);
        dateLabel.setFont(UIConstants.FONT_BODY);
        dateLabel.setForeground(UIConstants.TEXT_SECONDARY);

        // Venue
        String venueStr = "📍 " + event.getVenue() + (event.getBuilding() != null ? " (" + event.getBuilding() + ")" : "");
        if (venueStr.length() > 38) venueStr = venueStr.substring(0, 36) + "...";
        JLabel venueLabel = new JLabel(venueStr);
        venueLabel.setFont(UIConstants.FONT_BODY);
        venueLabel.setForeground(UIConstants.TEXT_SECONDARY);

        body.add(courseTag);
        body.add(Box.createVerticalStrut(4));
        body.add(titleLabel);
        body.add(Box.createVerticalStrut(10));
        body.add(dateLabel);
        body.add(Box.createVerticalStrut(4));
        body.add(venueLabel);

        // 3. Footer row: Seats left / Fee + View Details button
        JPanel footRow = new JPanel(new BorderLayout());
        footRow.setOpaque(false);
        footRow.setBorder(new EmptyBorder(8, 16, 4, 16));

        JPanel metaPanel = new JPanel();
        metaPanel.setLayout(new BoxLayout(metaPanel, BoxLayout.Y_AXIS));
        metaPanel.setOpaque(false);

        int seats = event.getSeatsRemaining();
        String seatText = seats > 0 ? (seats + " seats left") : "Seats Full";
        JLabel seatLabel = new JLabel(seatText);
        seatLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        seatLabel.setForeground(seats > 0 ? UIConstants.ACCENT_GREEN : UIConstants.ACCENT_RED);

        String feeText = event.isFree() ? "Free Event" : ("Fee: Rs. " + event.getFee());
        JLabel feeLabel = new JLabel(feeText);
        feeLabel.setFont(UIConstants.FONT_SMALL);
        feeLabel.setForeground(UIConstants.TEXT_MUTED);

        metaPanel.add(seatLabel);
        metaPanel.add(feeLabel);

        JButton viewBtn = new JButton("View Details");
        UIConstants.styleOutlineButton(viewBtn);
        viewBtn.addActionListener(e -> mainFrame.navigateToEventDetails(event.getId()));

        footRow.add(metaPanel, BorderLayout.WEST);
        footRow.add(viewBtn, BorderLayout.EAST);

        add(thumbPanel, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
        add(footRow, BorderLayout.SOUTH);

        // Interactive hover effects
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.PRIMARY_BLUE, 1, true),
                        new EmptyBorder(0, 0, 12, 0)
                ));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                        new EmptyBorder(0, 0, 12, 0)
                ));
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.navigateToEventDetails(event.getId());
            }
        };

        addMouseListener(mouseAdapter);
        thumbPanel.addMouseListener(mouseAdapter);
        imgLabel.addMouseListener(mouseAdapter);
        body.addMouseListener(mouseAdapter);
    }
}

