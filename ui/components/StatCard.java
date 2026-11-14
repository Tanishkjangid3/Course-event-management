package com.campusevents.ui.components;

import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Metric card displaying a large statistic, descriptive title, and accent stripe.
 */
public class StatCard extends JPanel {

    private final Color accentColor;
    private final JLabel valueLabel;
    private final JLabel titleLabel;

    public StatCard(String title, String initialValue, Color accentColor) {
        this.accentColor = accentColor;
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));
        setPreferredSize(new Dimension(190, 110));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(UIConstants.TEXT_MUTED);

        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(UIConstants.FONT_STAT_NUMBER);
        valueLabel.setForeground(UIConstants.TEXT_PRIMARY);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(valueLabel);

        add(content, BorderLayout.CENTER);
    }

    public void setValue(int val) {
        valueLabel.setText(String.valueOf(val));
        valueLabel.repaint();
    }

    public void setValue(String val) {
        valueLabel.setText(val);
        valueLabel.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Top accent indicator bar
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, getWidth(), 5, 4, 4);

        g2.dispose();
    }
}

