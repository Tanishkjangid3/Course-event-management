package com.campusevents.ui.components;

import com.campusevents.util.UIConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Custom interactive Bar Chart drawn with Graphics2D displaying registrations per event.
 */
public class BarChartPanel extends JPanel {

    private Map<String, Integer> data = new LinkedHashMap<>();

    public BarChartPanel() {
        setBackground(UIConstants.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        setPreferredSize(new Dimension(800, 360));
        setMinimumSize(new Dimension(600, 320));
    }

    public void setData(Map<String, Integer> newData) {
        this.data = newData != null ? newData : new LinkedHashMap<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Chart Title
        g2.setColor(UIConstants.TEXT_PRIMARY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.drawString("Registration Distribution Per Event", 24, 30);

        g2.setColor(UIConstants.TEXT_MUTED);
        g2.setFont(UIConstants.FONT_SMALL);
        g2.drawString("Real-time attendee enrollments across active and past events", 24, 48);

        if (data == null || data.isEmpty()) {
            g2.setColor(UIConstants.TEXT_MUTED);
            g2.setFont(UIConstants.FONT_BODY);
            g2.drawString("No registration data available to display.", width / 2 - 120, height / 2);
            g2.dispose();
            return;
        }

        // Layout bounds
        int padLeft = 60;
        int padRight = 30;
        int padTop = 75;
        int padBottom = 75;

        int chartW = width - padLeft - padRight;
        int chartH = height - padTop - padBottom;

        // Max value
        int maxVal = 1;
        for (int v : data.values()) {
            if (v > maxVal) maxVal = v;
        }
        // Round up to nearest even ceiling
        int yCeiling = ((maxVal + 2) / 2) * 2;
        if (yCeiling < 4) yCeiling = 4;

        // Draw horizontal grid lines and Y-axis labels
        int gridSteps = 4;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i <= gridSteps; i++) {
            int yVal = (yCeiling * i) / gridSteps;
            int yPos = padTop + chartH - (int) ((double) i / gridSteps * chartH);

            // Grid line
            g2.setColor(new Color(241, 243, 244));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawLine(padLeft, yPos, padLeft + chartW, yPos);

            // Y label
            g2.setColor(UIConstants.TEXT_MUTED);
            String label = String.valueOf(yVal);
            g2.drawString(label, padLeft - fm.stringWidth(label) - 10, yPos + 4);
        }

        // Draw Bars
        int barCount = data.size();
        int totalSlotWidth = chartW / barCount;
        int barWidth = Math.min(48, (int) (totalSlotWidth * 0.58));

        int idx = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            int val = entry.getValue();
            int barHeight = (int) (((double) val / yCeiling) * chartH);
            int barX = padLeft + (idx * totalSlotWidth) + ((totalSlotWidth - barWidth) / 2);
            int barY = padTop + chartH - barHeight;

            // Bar Gradient
            GradientPaint barGrad = new GradientPaint(
                    barX, barY,
                    idx % 2 == 0 ? UIConstants.PRIMARY_BLUE : new Color(66, 133, 244),
                    barX, barY + barHeight,
                    idx % 2 == 0 ? new Color(11, 87, 208) : new Color(26, 115, 232)
            );
            g2.setPaint(barGrad);
            g2.fillRoundRect(barX, barY, barWidth, Math.max(4, barHeight), 8, 8);

            // Value label above bar
            g2.setColor(UIConstants.PRIMARY_BLUE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String valStr = String.valueOf(val);
            int valW = fm.stringWidth(valStr);
            g2.drawString(valStr, barX + (barWidth - valW) / 2, barY - 6);

            // Event title below bar (truncated if necessary)
            g2.setColor(UIConstants.TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String title = entry.getKey();
            if (title.length() > 14) {
                title = title.substring(0, 12) + "..";
            }
            int titleW = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, barX + (barWidth - titleW) / 2, padTop + chartH + 18);

            idx++;
        }

        // X-axis line
        g2.setColor(UIConstants.BORDER_LIGHT);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(padLeft, padTop + chartH, padLeft + chartW, padTop + chartH);

        g2.dispose();
    }
}

