package com.campusevents.ui.pages;

import com.campusevents.dao.CourseDAO;
import com.campusevents.dao.EventDAO;
import com.campusevents.model.Event;
import com.campusevents.ui.MainFrame;
import com.campusevents.ui.components.EventCard;
import com.campusevents.ui.components.Footer;
import com.campusevents.ui.components.ScrollablePanel;
import com.campusevents.ui.components.StatCard;
import com.campusevents.util.ImageUtil;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

/**
 * Main Landing / Home page featuring the Hero Banner, live database statistics,
 * Happening Now alert section, Upcoming Events strip, Previous Events highlights,
 * and the 3-step 'How It Works' guide.
 */
public class HomePage extends ScrollablePanel {

    private final MainFrame mainFrame;
    private final EventDAO eventDAO = new EventDAO();

    private StatCard coursesStat;
    private StatCard upcomingStat;
    private StatCard ongoingStat;
    private StatCard pastStat;
    private StatCard regStat;

    private JPanel happeningNowContainer;
    private JPanel upcomingContainer;
    private JPanel previousContainer;

    public HomePage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.BG_MAIN);
        initUI();
    }

    private void initUI() {
        // 1. Hero Banner
        add(createHeroBanner());
        add(Box.createVerticalStrut(28));

        // 2. Live Stats Bar
        add(createStatsSection());
        add(Box.createVerticalStrut(36));

        // 3. Happening Now (Ongoing Events)
        happeningNowContainer = new JPanel();
        happeningNowContainer.setLayout(new BoxLayout(happeningNowContainer, BoxLayout.Y_AXIS));
        happeningNowContainer.setOpaque(false);
        add(happeningNowContainer);

        // 4. Upcoming Events Strip
        add(createSectionHeader("Upcoming Events", "Next academic workshops and technical seminars scheduled on campus", "View All Events →", "EVENTS"));
        add(Box.createVerticalStrut(14));
        upcomingContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        upcomingContainer.setOpaque(false);
        add(upcomingContainer);
        add(Box.createVerticalStrut(36));

        // 5. How It Works Section
        add(createHowItWorksSection());
        add(Box.createVerticalStrut(36));

        // 6. Previous Events Highlights
        add(createSectionHeader("Previous Events", "Highlights and recaps of recently completed campus sessions", null, null));
        add(Box.createVerticalStrut(14));
        previousContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        previousContainer.setOpaque(false);
        add(previousContainer);
        add(Box.createVerticalStrut(40));

        // 7. Footer
        add(new Footer(mainFrame));
    }

    private JPanel createHeroBanner() {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon heroImg = ImageUtil.getImageIcon("images/hero.jpg", getWidth(), getHeight(), "HERO");
                if (heroImg != null) {
                    g.drawImage(heroImg.getImage(), 0, 0, getWidth(), getHeight(), null);
                }
                // Gradient dark overlay for crystal-clear text readability
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setColor(new Color(15, 23, 42, 195)); // Deep slate overlay
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        hero.setPreferredSize(new Dimension(1100, 360));
        hero.setMinimumSize(new Dimension(800, 320));
        hero.setBorder(new EmptyBorder(40, 60, 40, 60));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel badge = new JLabel("CAMPUSCOURSE EVENTS 2026");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(new Color(251, 188, 4));

        JLabel headline = new JLabel("<html><body style='width: 750px;'>Discover Workshops, Seminars & Events for Your Courses</body></html>");
        headline.setFont(UIConstants.FONT_HERO);
        headline.setForeground(Color.WHITE);

        JLabel subheadline = new JLabel("<html><body style='width: 650px; color: #E2E8F0; font-size: 13px;'>" +
                "Empower your college journey. Seamlessly browse, register, and receive official verified admission passes " +
                "for departmental hackathons, tech bootcamps, and career conferences.</body></html>");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        btnRow.setOpaque(false);

        JButton browseBtn = new JButton("Browse Events →");
        UIConstants.stylePrimaryButton(browseBtn);
        browseBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        browseBtn.addActionListener(e -> mainFrame.navigateTo("EVENTS"));

        JButton exploreCoursesBtn = new JButton("Explore Courses");
        UIConstants.styleOutlineButton(exploreCoursesBtn);
        exploreCoursesBtn.setBackground(new Color(255, 255, 255, 220));
        exploreCoursesBtn.addActionListener(e -> mainFrame.navigateTo("COURSES"));

        btnRow.add(browseBtn);
        btnRow.add(exploreCoursesBtn);

        content.add(badge);
        content.add(Box.createVerticalStrut(12));
        content.add(headline);
        content.add(Box.createVerticalStrut(14));
        content.add(subheadline);
        content.add(Box.createVerticalStrut(24));
        content.add(btnRow);

        hero.add(content, BorderLayout.WEST);
        return hero;
    }

    private JPanel createStatsSection() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 20, 0, 20));

        coursesStat = new StatCard("TOTAL COURSES", "-", UIConstants.PRIMARY_BLUE);
        upcomingStat = new StatCard("UPCOMING EVENTS", "-", UIConstants.ACCENT_GREEN);
        ongoingStat = new StatCard("HAPPENING NOW", "-", UIConstants.ACCENT_YELLOW);
        pastStat = new StatCard("PAST EVENTS", "-", UIConstants.TEXT_MUTED);
        regStat = new StatCard("TOTAL REGISTRATIONS", "-", UIConstants.ACCENT_RED);

        wrapper.add(coursesStat);
        wrapper.add(upcomingStat);
        wrapper.add(ongoingStat);
        wrapper.add(pastStat);
        wrapper.add(regStat);

        return wrapper;
    }

    private JPanel createSectionHeader(String title, String subtitle, String actionText, String actionTarget) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 48, 0, 48));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.FONT_TITLE_LARGE);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(UIConstants.FONT_BODY);
        subLabel.setForeground(UIConstants.TEXT_SECONDARY);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subLabel);

        panel.add(textPanel, BorderLayout.WEST);

        if (actionText != null && actionTarget != null) {
            JLabel actionLabel = new JLabel(actionText);
            actionLabel.setFont(UIConstants.FONT_BODY_BOLD);
            actionLabel.setForeground(UIConstants.PRIMARY_BLUE);
            actionLabel.setCursor(UIConstants.HAND_CURSOR);
            actionLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mainFrame.navigateTo(actionTarget);
                }
            });
            panel.add(actionLabel, BorderLayout.EAST);
        }

        return panel;
    }

    private JPanel createHowItWorksSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, UIConstants.BORDER_LIGHT),
                new EmptyBorder(36, 48, 40, 48)
        ));

        JLabel title = new JLabel("How It Works");
        title.setFont(UIConstants.FONT_TITLE_LARGE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Three simple steps to participate in premier campus course events");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        section.add(title);
        section.add(Box.createVerticalStrut(6));
        section.add(subtitle);
        section.add(Box.createVerticalStrut(28));

        JPanel stepsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 28, 10));
        stepsRow.setOpaque(false);

        stepsRow.add(createStepCard("1", "Choose Event", "Browse workshops, competitions, and seminars matched to your academic stream.", UIConstants.PRIMARY_BLUE));
        stepsRow.add(createStepCard("2", "Register", "Confirm your enrollment number, specify requirements, and secure your seat.", UIConstants.ACCENT_GREEN));
        stepsRow.add(createStepCard("3", "Attend & Learn", "Download or print your official verified entry pass with barcode for campus access.", UIConstants.ACCENT_YELLOW));

        section.add(stepsRow);
        return section;
    }

    private JPanel createStepCard(String number, String header, String description, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.BG_MAIN);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(22, 24, 22, 24)
        ));
        card.setPreferredSize(new Dimension(310, 170));

        // Circular number badge
        JLabel numBadge = new JLabel(number, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        numBadge.setPreferredSize(new Dimension(36, 36));
        numBadge.setMaximumSize(new Dimension(36, 36));
        numBadge.setFont(new Font("Segoe UI", Font.BOLD, 15));
        numBadge.setForeground(Color.WHITE);

        JLabel head = new JLabel(header);
        head.setFont(new Font("Segoe UI", Font.BOLD, 16));
        head.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel desc = new JLabel("<html><body style='width: 250px; color: #5F6368; font-size: 12px;'>" + description + "</body></html>");

        card.add(numBadge);
        card.add(Box.createVerticalStrut(12));
        card.add(head);
        card.add(Box.createVerticalStrut(6));
        card.add(desc);

        return card;
    }

    /**
     * Refreshes stats and event strips asynchronously off the EDT using SwingWorker.
     */
    public void loadData() {
        new SwingWorker<Void, Void>() {
            private EventDAO.EventStats stats;
            private List<Event> ongoing;
            private List<Event> upcoming;
            private List<Event> previous;

            @Override
            protected Void doInBackground() throws Exception {
                stats = eventDAO.getEventStats();
                ongoing = eventDAO.getOngoing();
                upcoming = eventDAO.getUpcoming(3);
                previous = eventDAO.getPrevious(3);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (stats != null) {
                        coursesStat.setValue(stats.totalCourses);
                        upcomingStat.setValue(stats.upcomingEvents);
                        ongoingStat.setValue(stats.ongoingEvents);
                        pastStat.setValue(stats.pastEvents);
                        regStat.setValue(stats.totalRegistrations);
                    }

                    // Render Happening Now section
                    happeningNowContainer.removeAll();
                    if (ongoing != null && !ongoing.isEmpty()) {
                        happeningNowContainer.add(createSectionHeader("Happening Now", "Live events in progress across campus venues today", null, null));
                        happeningNowContainer.add(Box.createVerticalStrut(14));
                        JPanel ongoingRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
                        ongoingRow.setOpaque(false);
                        for (Event e : ongoing) {
                            ongoingRow.add(new EventCard(e, mainFrame));
                        }
                        happeningNowContainer.add(ongoingRow);
                        happeningNowContainer.add(Box.createVerticalStrut(36));
                    }

                    // Render Upcoming Strip
                    upcomingContainer.removeAll();
                    if (upcoming != null) {
                        for (Event e : upcoming) {
                            upcomingContainer.add(new EventCard(e, mainFrame));
                        }
                    }

                    // Render Previous Strip
                    previousContainer.removeAll();
                    if (previous != null) {
                        for (Event e : previous) {
                            previousContainer.add(new EventCard(e, mainFrame));
                        }
                    }

                    revalidate();
                    repaint();

                } catch (Exception ex) {
                    System.err.println("Error loading Home page data: " + ex.getMessage());
                }
            }
        }.execute();
    }
}

