package com.campusevents.util;

import com.campusevents.model.Registration;
import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Utility to export registration lists into formatted CSV files.
 */
public class CSVExporter {

    public static boolean exportRegistrations(List<Registration> registrations, Component parent) {
        if (registrations == null || registrations.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No registrations to export.", "Export Notice", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Registrations to CSV");
        chooser.setSelectedFile(new File("registrations_export_" + System.currentTimeMillis() + ".csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            // Write UTF-8 BOM for Microsoft Excel compatibility
            writer.write('\ufeff');

            // Header
            writer.write(escape("Registration No") + "," +
                         escape("Student Name") + "," +
                         escape("Email") + "," +
                         escape("Enrollment No") + "," +
                         escape("Event Title") + "," +
                         escape("Course") + "," +
                         escape("Venue") + "," +
                         escape("Event Date & Time") + "," +
                         escape("Fee") + "," +
                         escape("Status") + "," +
                         escape("Registered On") + "," +
                         escape("Special Requirements") + "\n");

            for (Registration r : registrations) {
                String line = escape(r.getRegNo()) + "," +
                              escape(r.getUserName()) + "," +
                              escape(r.getUserEmail()) + "," +
                              escape(r.getEnrollmentNo()) + "," +
                              escape(r.getEventTitle()) + "," +
                              escape(r.getCourseName()) + "," +
                              escape(r.getVenue() + (r.getBuilding() != null ? " (" + r.getBuilding() + ")" : "")) + "," +
                              escape(DateUtil.formatDateTime(r.getStartDateTime())) + "," +
                              escape(r.getFee() != null ? r.getFee().toString() : "0.00") + "," +
                              escape(r.getStatus()) + "," +
                              escape(DateUtil.formatDateTime(r.getRegisteredOn())) + "," +
                              escape(r.getRequirements()) + "\n";
                writer.write(line);
            }

            JOptionPane.showMessageDialog(parent, 
                    "Successfully exported " + registrations.size() + " registrations to:\n" + file.getAbsolutePath(), 
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Failed to export CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static String escape(String val) {
        if (val == null) return "\"\"";
        return "\"" + val.replace("\"", "\"\"") + "\"";
    }
}

