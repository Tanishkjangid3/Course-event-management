package com.campusevents;

import com.campusevents.db.DBConnection;
import com.campusevents.db.DatabaseInitializer;
import com.campusevents.ui.MainFrame;
import com.campusevents.util.UIConstants;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application entry point for "CampusCourse Events".
 * Initializes FlatLaf Light theme, validates MySQL database connectivity,
 * auto-provisions schema seeds if needed, and launches the single-window MainFrame.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Initialize FlatLaf Look and Feel with modern Google Material palette
        try {
            FlatLightLaf.setup();

            // Custom UI properties for modern aesthetics
            UIManager.put("Component.accentColor", UIConstants.PRIMARY_BLUE);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 8);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("Table.selectionBackground", new Color(232, 240, 254));
            UIManager.put("Table.selectionForeground", UIConstants.TEXT_PRIMARY);
            UIManager.put("Table.gridColor", new Color(241, 243, 244));
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));

        } catch (Exception e) {
            System.err.println("Notice: Could not set FlatLaf Look and Feel: " + e.getMessage());
        }

        // 2. Validate Database Connectivity and auto-provision schema
        DBConnection.ensureDatabaseExists();
        boolean connected = false;
        try {
            connected = DBConnection.testConnection();
        } catch (Exception e) {
            connected = false;
        }

        if (!connected) {
            boolean recovered = DBConnection.showConnectionErrorDialog(null, null);
            if (!recovered) {
                System.err.println("Exiting: Unable to establish database connection.");
                System.exit(1);
                return;
            }
        }

        // Auto-provision schema and seed data if tables are empty
        DatabaseInitializer.initializeIfNeeded();

        // 3. Launch Main Application Frame on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

