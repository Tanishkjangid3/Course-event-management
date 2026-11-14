package com.campusevents.util;

import com.campusevents.model.Registration;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * Generates an event registration pass drawn with Graphics2D.
 * Supports high-resolution rendering, PNG export, and direct printing via Java Print API.
 */
public class PassGenerator {

    private static final int PASS_WIDTH = 820;
    private static final int PASS_HEIGHT = 440;

    /**
     * Renders a boarding-pass/ticket pass using Graphics2D.
     */
    public static BufferedImage renderPassImage(Registration reg) {
        BufferedImage image = new BufferedImage(PASS_WIDTH, PASS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Enable high-quality antialiasing and subpixel text rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 1. Outer ticket background with rounded corners
        g2.setColor(new Color(0xF8, 0xF9, 0xFA));
        g2.fillRect(0, 0, PASS_WIDTH, PASS_HEIGHT);

        int pad = 12;
        int tWidth = PASS_WIDTH - (pad * 2);
        int tHeight = PASS_HEIGHT - (pad * 2);

        // Ticket card
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(pad, pad, tWidth, tHeight, 24, 24);

        // Border
        g2.setColor(new Color(218, 220, 224));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(pad, pad, tWidth, tHeight, 24, 24);

        int stubX = PASS_WIDTH - 240;

        // 2. Main Body Left Header (Navy / Google Blue Gradient)
        int headerHeight = 75;
        GradientPaint headerGrad = new GradientPaint(pad, pad, new Color(26, 115, 232), stubX, pad, new Color(11, 87, 208));
        g2.setPaint(headerGrad);
        g2.fillRoundRect(pad, pad, stubX - pad, headerHeight + 10, 24, 24);
        g2.fillRect(pad, pad + 20, stubX - pad, headerHeight - 10);

        // Header Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.drawString("CAMPUSCOURSE EVENTS  •  OFFICIAL ADMISSION PASS", pad + 20, pad + 35);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(232, 240, 254));
        g2.drawString("College Events Cell  |  Verified Academic Registration", pad + 20, pad + 56);

        // 3. Event Details on Main Pass
        int contentY = pad + headerHeight + 30;

        // Event Title
        g2.setColor(UIConstants.TEXT_PRIMARY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        String title = reg.getEventTitle() != null ? reg.getEventTitle() : "Campus Event";
        if (title.length() > 36) title = title.substring(0, 34) + "...";
        g2.drawString(title, pad + 20, contentY);

        // Course & Status Badges
        contentY += 26;
        drawBadge(g2, pad + 20, contentY - 14, reg.getCourseName() != null ? reg.getCourseName() : "General", UIConstants.BADGE_BLUE_BG, UIConstants.BADGE_BLUE_TEXT);

        String status = reg.getStatus() != null ? reg.getStatus() : "REGISTERED";
        Color stBg = "ATTENDED".equalsIgnoreCase(status) ? UIConstants.BADGE_GREEN_BG :
                     "CANCELLED".equalsIgnoreCase(status) ? UIConstants.BADGE_RED_BG : UIConstants.BADGE_GREEN_BG;
        Color stTx = "ATTENDED".equalsIgnoreCase(status) ? UIConstants.BADGE_GREEN_TEXT :
                     "CANCELLED".equalsIgnoreCase(status) ? UIConstants.BADGE_RED_TEXT : UIConstants.BADGE_GREEN_TEXT;
        drawBadge(g2, pad + 190, contentY - 14, status, stBg, stTx);

        // Divider
        contentY += 18;
        g2.setColor(new Color(230, 233, 238));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawLine(pad + 20, contentY, stubX - 20, contentY);

        // Grid Details: Attendee and Event info
        contentY += 24;

        // Column 1: Attendee Info
        drawField(g2, "ATTENDEE NAME", reg.getUserName() != null ? reg.getUserName() : "Student", pad + 20, contentY, true);
        drawField(g2, "ENROLLMENT NO", reg.getEnrollmentNo() != null ? reg.getEnrollmentNo() : "N/A", pad + 20, contentY + 50, false);
        drawField(g2, "EMAIL", reg.getUserEmail() != null ? reg.getUserEmail() : "N/A", pad + 20, contentY + 100, false);

        // Column 2: Event Venue & Timing
        int col2X = pad + 290;
        String dateStr = DateUtil.formatDateTime(reg.getStartDateTime());
        drawField(g2, "DATE & TIME", dateStr, col2X, contentY, true);

        String venueStr = (reg.getVenue() != null ? reg.getVenue() : "Campus") +
                          (reg.getBuilding() != null ? ", " + reg.getBuilding() : "");
        drawField(g2, "VENUE & BUILDING", venueStr, col2X, contentY + 50, false);

        String feeStr = (reg.getFee() == null || reg.getFee().signum() == 0) ? "FREE PASS" : "Rs. " + reg.getFee();
        drawField(g2, "ADMISSION FEE", feeStr, col2X, contentY + 100, false);

        // Bottom Registration ID highlight bar
        int barY = PASS_HEIGHT - pad - 50;
        g2.setColor(new Color(241, 243, 244));
        g2.fillRoundRect(pad + 20, barY, stubX - pad - 40, 38, 10, 10);
        g2.setColor(UIConstants.TEXT_SECONDARY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString("PASS NUMBER:", pad + 32, barY + 24);

        g2.setColor(UIConstants.PRIMARY_BLUE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.drawString(reg.getRegNo() != null ? reg.getRegNo() : "EVT-2026-XXXX", pad + 135, barY + 24);

        // 4. Perforated Divider Line with semi-circle punch holes
        g2.setColor(new Color(200, 205, 210));
        float[] dashPattern = {6.0f, 6.0f};
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
        g2.drawLine(stubX, pad + 15, stubX, PASS_HEIGHT - pad - 15);

        // Punch notches (top and bottom semi-circles)
        g2.setColor(new Color(0xF8, 0xF9, 0xFA));
        g2.fillOval(stubX - 12, pad - 12, 24, 24);
        g2.fillOval(stubX - 12, PASS_HEIGHT - pad - 12, 24, 24);
        g2.setColor(new Color(218, 220, 224));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawArc(stubX - 12, pad - 12, 24, 24, 180, 180);
        g2.drawArc(stubX - 12, PASS_HEIGHT - pad - 12, 24, 24, 0, 180);

        // 5. Right Stub Panel
        int sPad = stubX + 18;

        // Stub Header
        g2.setColor(UIConstants.PRIMARY_BLUE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString("ADMIT STUB", sPad, pad + 35);

        g2.setColor(UIConstants.TEXT_MUTED);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.drawString("Present at gate entrance", sPad, pad + 52);

        // Barcode / Digital Code simulation
        drawBarcode(g2, sPad, pad + 70, 190, 70, reg.getRegNo());

        // Stub details
        int stubDetailsY = pad + 165;
        drawField(g2, "PASS CODE", reg.getRegNo() != null ? reg.getRegNo() : "N/A", sPad, stubDetailsY, true);
        drawField(g2, "STUDENT", reg.getUserName() != null ? reg.getUserName() : "Student", sPad, stubDetailsY + 45, false);
        drawField(g2, "ROOM", reg.getVenue() != null ? reg.getVenue() : "Main Hall", sPad, stubDetailsY + 90, false);

        // Verification Stamp
        g2.setColor(new Color(52, 168, 83, 40));
        g2.fillRoundRect(sPad, PASS_HEIGHT - pad - 50, 190, 36, 8, 8);
        g2.setColor(UIConstants.ACCENT_GREEN);
        g2.drawRoundRect(sPad, PASS_HEIGHT - pad - 50, 190, 36, 8, 8);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString("VERIFIED ENTRY", sPad + 40, PASS_HEIGHT - pad - 27);

        g2.dispose();
        return image;
    }

    private static void drawField(Graphics2D g2, String label, String value, int x, int y, boolean highlight) {
        g2.setColor(UIConstants.TEXT_MUTED);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.drawString(label, x, y);

        g2.setColor(highlight ? UIConstants.PRIMARY_BLUE : UIConstants.TEXT_PRIMARY);
        g2.setFont(new Font("Segoe UI", highlight ? Font.BOLD : Font.PLAIN, 13));

        if (value != null && value.length() > 30) {
            value = value.substring(0, 28) + "...";
        }
        g2.drawString(value != null ? value : "-", x, y + 17);
    }

    private static void drawBadge(Graphics2D g2, int x, int y, String text, Color bg, Color textCol) {
        if (text == null) text = "";
        Font font = new Font("Segoe UI", Font.BOLD, 11);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(text);
        int badgeW = textW + 16;
        int badgeH = 22;

        g2.setColor(bg);
        g2.fillRoundRect(x, y, badgeW, badgeH, 10, 10);

        g2.setColor(textCol);
        g2.drawString(text, x + 8, y + 15);
    }

    private static void drawBarcode(Graphics2D g2, int x, int y, int width, int height, String code) {
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, width, height);

        g2.setColor(Color.BLACK);
        int seed = code != null ? Math.abs(code.hashCode()) : 12345;
        int curX = x + 10;
        int maxW = x + width - 10;

        while (curX < maxW) {
            int barWidth = (seed % 3) + 1;
            g2.fillRect(curX, y + 8, barWidth, height - 26);
            curX += barWidth + ((seed >> 2) % 3) + 1;
            seed = (seed * 31 + 17);
        }

        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        String codeText = code != null ? code : "EVT-2026-0000";
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (width - fm.stringWidth(codeText)) / 2;
        g2.drawString(codeText, tx, y + height - 5);
    }

    /**
     * Saves the pass as a high quality PNG file.
     */
    public static boolean exportToPNG(Registration reg, File targetFile) throws IOException {
        BufferedImage img = renderPassImage(reg);
        return ImageIO.write(img, "PNG", targetFile);
    }

    /**
     * Prints the pass directly via Java PrinterJob.
     */
    public static void printPass(Registration reg, Component parent) {
        BufferedImage passImage = renderPassImage(reg);
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("CampusCourse Events Pass - " + reg.getRegNo());

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Scale to fit printable page area
                double pageWidth = pageFormat.getImageableWidth();
                double pageHeight = pageFormat.getImageableHeight();
                double imgWidth = passImage.getWidth();
                double imgHeight = passImage.getHeight();

                double scaleX = pageWidth / imgWidth;
                double scaleY = pageHeight / imgHeight;
                double scale = Math.min(scaleX, scaleY);

                g2d.scale(scale, scale);
                g2d.drawImage(passImage, 0, 0, null);

                return PAGE_EXISTS;
            }
        });

        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(parent, "Pass printed successfully!", "Print Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException pe) {
                JOptionPane.showMessageDialog(parent, "Printing failed: " + pe.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

