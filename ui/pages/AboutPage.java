package com.campusevents.ui.pages;

import com.campusevents.ui.MainFrame;
import com.campusevents.ui.components.Footer;
import com.campusevents.ui.components.ScrollablePanel;
import com.campusevents.util.UIConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * About Page describing the College Events Cell, its mission, core values,
 * contact communication channels, and a styled campus map address block.
 */
public class AboutPage extends ScrollablePanel {

    private final MainFrame mainFrame;

    public AboutPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.BG_MAIN);
        initUI();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_LIGHT),
                new EmptyBorder(32, 48, 32, 48)
        ));

        JLabel title = new JLabel("About CampusCourse Events");
        title.setFont(UIConstants.FONT_HERO);
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Centralizing and elevating student academic enrichment, technical mastery, and professional growth.");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_SECONDARY);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);
        add(header);

        add(Box.createVerticalStrut(28));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 48, 20, 48));

        // Mission & Vision Grid
        JPanel grid = new JPanel(new GridLayout(1, 2, 24, 0));
        grid.setOpaque(false);

        grid.add(createCard("🎯 Our Mission", 
                "To deliver a modern, high-transparency platform connecting students across engineering, computing, management, and data disciplines with practical industry seminars, coding hackathons, and corporate aptitude bootcamps.",
                UIConstants.PRIMARY_BLUE));

        grid.add(createCard("🚀 Our Vision", 
                "To empower every graduate with experiential technical exposure, verified participation credentials, and direct mentorship opportunities with esteemed faculty and visiting corporate technology leaders.",
                UIConstants.ACCENT_GREEN));

        content.add(grid);
        content.add(Box.createVerticalStrut(28));

        // Campus Map & Contact Block
        content.add(createMapAndContactSection());

        add(content);
        add(Box.createVerticalGlue());
        add(new Footer(mainFrame));
    }

    private JPanel createCard(String title, String description, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(24, 28, 24, 28)
        ));

        JLabel t = new JLabel(title);
        t.setFont(UIConstants.FONT_TITLE);
        t.setForeground(accent);

        JLabel d = new JLabel("<html><body style='width: 380px; color: #3C4043; font-size: 13px; line-height: 1.6;'>" + description + "</body></html>");

        card.add(t);
        card.add(Box.createVerticalStrut(10));
        card.add(d);
        return card;
    }

    private JPanel createMapAndContactSection() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 24, 0));
        panel.setOpaque(false);

        // Map Graphic Block
        JPanel mapBlock = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Map canvas terrain background
                g2.setColor(new Color(234, 237, 240));
                g2.fillRect(0, 0, w, h);

                // Grid roads
                g2.setColor(new Color(255, 255, 255));
                g2.setStroke(new BasicStroke(16.0f));
                g2.drawLine(0, h / 2, w, h / 2);
                g2.drawLine(w / 3, 0, w / 3, h);
                g2.drawLine((2 * w) / 3, 0, (2 * w) / 3, h);

                // Secondary roads
                g2.setStroke(new BasicStroke(6.0f));
                g2.drawLine(0, h / 4, w, h / 4);
                g2.drawLine(0, (3 * h) / 4, w, (3 * h) / 4);

                // Campus Building Blocks
                g2.setColor(new Color(209, 219, 228));
                g2.fillRoundRect(w / 3 + 20, 25, 90, 60, 8, 8);
                g2.fillRoundRect(w / 3 + 20, h / 2 + 25, 110, 70, 8, 8);
                g2.fillRoundRect(25, h / 4 + 20, 80, 50, 8, 8);

                // Main Pin at Student Activity Center
                int pinX = w / 2;
                int pinY = h / 2 - 20;

                g2.setColor(new Color(234, 67, 53, 60));
                g2.fillOval(pinX - 18, pinY + 8, 36, 12);

                g2.setColor(UIConstants.ACCENT_RED);
                g2.fillOval(pinX - 14, pinY - 26, 28, 28);
                int[] px = {pinX - 12, pinX, pinX + 12};
                int[] py = {pinY - 6, pinY + 12, pinY - 6};
                g2.fillPolygon(px, py, 3);

                g2.setColor(Color.WHITE);
                g2.fillOval(pinX - 6, pinY - 18, 12, 12);

                // Pin label
                g2.setColor(new Color(32, 33, 36));
                g2.fillRoundRect(pinX - 85, pinY - 58, 170, 26, 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString("College Events Cell - Block B", pinX - 75, pinY - 41);

                g2.dispose();
            }
        };

        mapBlock.setBackground(Color.WHITE);
        mapBlock.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true));
        mapBlock.setPreferredSize(new Dimension(440, 260));

        // Contact Information Details
        JPanel contactCard = new JPanel();
        contactCard.setLayout(new BoxLayout(contactCard, BoxLayout.Y_AXIS));
        contactCard.setBackground(Color.WHITE);
        contactCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(24, 28, 24, 28)
        ));

        JLabel contactTitle = new JLabel("Official Contact & Office Hours");
        contactTitle.setFont(UIConstants.FONT_TITLE);

        JLabel ad1 = new JLabel("🏢 Address: Student Activity Center, 2nd Floor, Block B");
        ad1.setFont(UIConstants.FONT_BODY);

        JLabel ad2 = new JLabel("🏛️ Institution: University College of Engineering & Technology");
        ad2.setFont(UIConstants.FONT_BODY);

        JLabel em = new JLabel("✉️ Email: events.cell@campus.edu  |  support@campusevents.ac.in");
        em.setFont(UIConstants.FONT_BODY);

        JLabel ph = new JLabel("📞 Phone: +91 98765 43210 / +91 (011) 2766-7000");
        ph.setFont(UIConstants.FONT_BODY);

        JLabel hr = new JLabel("⏱️ Hours: Monday – Saturday, 09:00 AM – 05:30 PM");
        hr.setFont(UIConstants.FONT_BODY);

        contactCard.add(contactTitle);
        contactCard.add(Box.createVerticalStrut(14));
        contactCard.add(ad1);
        contactCard.add(Box.createVerticalStrut(8));
        contactCard.add(ad2);
        contactCard.add(Box.createVerticalStrut(8));
        contactCard.add(em);
        contactCard.add(Box.createVerticalStrut(8));
        contactCard.add(ph);
        contactCard.add(Box.createVerticalStrut(8));
        contactCard.add(hr);

        panel.add(mapBlock);
        panel.add(contactCard);
        return panel;
    }
}

