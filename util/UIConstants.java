package com.campusevents.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

/**
 * Global UI palette, fonts, and styling constants for CampusCourse Events.
 * Conforms to Google / FlatLaf Material palette requirements:
 * Blue #1A73E8, Green #34A853, Yellow #FBBC04, Red #EA4335, Background #F8F9FA.
 */
public class UIConstants {

    // Primary Palette
    public static final Color PRIMARY_BLUE = new Color(0x1A, 0x73, 0xE8);
    public static final Color PRIMARY_BLUE_HOVER = new Color(0x15, 0x57, 0xB0);
    public static final Color ACCENT_GREEN = new Color(0x34, 0xA8, 0x53);
    public static final Color ACCENT_GREEN_HOVER = new Color(0x2D, 0x8E, 0x47);
    public static final Color ACCENT_YELLOW = new Color(0xFB, 0xBC, 0x04);
    public static final Color ACCENT_YELLOW_HOVER = new Color(0xE3, 0x74, 0x00);
    public static final Color ACCENT_RED = new Color(0xEA, 0x43, 0x35);
    public static final Color ACCENT_RED_HOVER = new Color(0xC5, 0x22, 0x1F);

    // Surface & Background Colors
    public static final Color BG_MAIN = new Color(0xF8, 0xF9, 0xFA);
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_NAVBAR = Color.WHITE;
    public static final Color BG_FOOTER = new Color(0x20, 0x21, 0x24);
    public static final Color TEXT_FOOTER_LIGHT = new Color(0xBD, 0xC1, 0xC6);

    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(0x20, 0x21, 0x24);
    public static final Color TEXT_SECONDARY = new Color(0x5F, 0x63, 0x68);
    public static final Color TEXT_MUTED = new Color(0x80, 0x86, 0x8B);

    // Borders & Dividers
    public static final Color BORDER_LIGHT = new Color(0xDA, 0xDC, 0xE0);
    public static final Color BORDER_FOCUS = PRIMARY_BLUE;

    // Badges & Tag Backgrounds
    public static final Color BADGE_GREEN_BG = new Color(230, 244, 234);
    public static final Color BADGE_GREEN_TEXT = new Color(19, 115, 51);
    public static final Color BADGE_BLUE_BG = new Color(232, 240, 254);
    public static final Color BADGE_BLUE_TEXT = new Color(26, 115, 232);
    public static final Color BADGE_YELLOW_BG = new Color(254, 247, 224);
    public static final Color BADGE_YELLOW_TEXT = new Color(180, 99, 0);
    public static final Color BADGE_RED_BG = new Color(252, 232, 230);
    public static final Color BADGE_RED_TEXT = new Color(197, 34, 31);
    public static final Color BADGE_GRAY_BG = new Color(241, 243, 244);
    public static final Color BADGE_GRAY_TEXT = new Color(95, 99, 104);

    // Fonts
    public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font FONT_TITLE_LARGE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_STAT_NUMBER = new Font("Segoe UI", Font.BOLD, 28);

    // Cursors
    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    // Common Borders
    public static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
    );

    public static final Border CARD_BORDER_THIN = BorderFactory.createLineBorder(BORDER_LIGHT, 1, true);

    /**
     * Styles a primary action button with standard colors, font, and hand cursor.
     */
    public static void stylePrimaryButton(JButton button) {
        button.setBackground(PRIMARY_BLUE);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BODY_BOLD);
        button.setFocusPainted(false);
        button.setCursor(HAND_CURSOR);
        button.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        button.putClientProperty("JButton.buttonType", "roundRect");
    }

    /**
     * Styles an outline button.
     */
    public static void styleOutlineButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY_BLUE);
        button.setFont(FONT_BODY_BOLD);
        button.setFocusPainted(false);
        button.setCursor(HAND_CURSOR);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_BLUE, 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
    }

    /**
     * Styles a danger (red) button.
     */
    public static void styleDangerButton(JButton button) {
        button.setBackground(ACCENT_RED);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BODY_BOLD);
        button.setFocusPainted(false);
        button.setCursor(HAND_CURSOR);
        button.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        button.putClientProperty("JButton.buttonType", "roundRect");
    }

    /**
     * Styles a success (green) button.
     */
    public static void styleSuccessButton(JButton button) {
        button.setBackground(ACCENT_GREEN);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BODY_BOLD);
        button.setFocusPainted(false);
        button.setCursor(HAND_CURSOR);
        button.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        button.putClientProperty("JButton.buttonType", "roundRect");
    }
}

