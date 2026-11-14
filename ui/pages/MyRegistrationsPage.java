package com.campusevents.ui.pages;

import com.campusevents.dao.RegistrationDAO;
import com.campusevents.dao.UserDAO;
import com.campusevents.model.Registration;
import com.campusevents.model.User;
import com.campusevents.ui.MainFrame;
import com.campusevents.ui.components.Footer;
import com.campusevents.ui.components.RegistrationPassDialog;
import com.campusevents.ui.components.ScrollablePanel;
import com.campusevents.util.DateUtil;
import com.campusevents.util.PasswordUtil;
import com.campusevents.util.SessionManager;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Student Registrations & Profile Hub:
 * View active/past admission passes, cancel registrations before start time (returning seats),
 * and update personal profile details or password.
 */
public class MyRegistrationsPage extends ScrollablePanel {

    private final MainFrame mainFrame;
    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final UserDAO userDAO = new UserDAO();

    private JTable regTable;
    private DefaultTableModel tableModel;
    private List<Registration> registrationsList;

    private JTextField nameField;
    private JTextField phoneField;
    private JLabel emailLabel;
    private JLabel enrLabel;
    private JLabel courseLabel;

    public MyRegistrationsPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.BG_MAIN);
        initUI();
    }

    private void initUI() {
        // Page Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_LIGHT),
                new EmptyBorder(28, 48, 28, 48)
        ));

        JLabel title = new JLabel("My Registrations & Profile");
        title.setFont(UIConstants.FONT_HERO);
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Manage your booked event passes, cancel upcoming enrollments, or update your student profile.");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_SECONDARY);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        add(header);
        add(Box.createVerticalStrut(24));

        // Two Section Layout: Top Registrations Table, Bottom Profile & Password Cards
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(0, 48, 20, 48));

        // 1. Registrations Card
        mainContent.add(createRegistrationsSection());
        mainContent.add(Box.createVerticalStrut(28));

        // 2. Profile & Security Row
        mainContent.add(createProfileAndSecurityRow());

        add(mainContent);
        add(Box.createVerticalGlue());
        add(new Footer(mainFrame));
    }

    private JPanel createRegistrationsSection() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel secTitle = new JLabel("My Event Registrations");
        secTitle.setFont(UIConstants.FONT_TITLE);
        secTitle.setForeground(UIConstants.TEXT_PRIMARY);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton viewPassBtn = new JButton("View Admission Pass");
        UIConstants.stylePrimaryButton(viewPassBtn);
        viewPassBtn.addActionListener(e -> handleViewPass());

        JButton cancelRegBtn = new JButton("Cancel Registration");
        UIConstants.styleDangerButton(cancelRegBtn);
        cancelRegBtn.addActionListener(e -> handleCancelRegistration());

        btnPanel.add(viewPassBtn);
        btnPanel.add(cancelRegBtn);

        topRow.add(secTitle, BorderLayout.WEST);
        topRow.add(btnPanel, BorderLayout.EAST);
        card.add(topRow, BorderLayout.NORTH);

        // Table
        String[] columns = {"Pass No", "Event Title", "Venue", "Start Date & Time", "Fee", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        regTable = new JTable(tableModel);
        regTable.setRowHeight(34);
        regTable.setFont(UIConstants.FONT_BODY);
        regTable.getTableHeader().setFont(UIConstants.FONT_BODY_BOLD);
        regTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom Status Cell Renderer for Badges
        regTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String st = value != null ? value.toString() : "";
                l.setHorizontalAlignment(CENTER);
                l.setFont(UIConstants.FONT_BODY_BOLD);

                if ("REGISTERED".equalsIgnoreCase(st)) {
                    l.setForeground(UIConstants.BADGE_GREEN_TEXT);
                } else if ("ATTENDED".equalsIgnoreCase(st)) {
                    l.setForeground(UIConstants.PRIMARY_BLUE);
                } else if ("CANCELLED".equalsIgnoreCase(st)) {
                    l.setForeground(UIConstants.ACCENT_RED);
                }
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(regTable);
        scroll.setPreferredSize(new Dimension(800, 220));
        scroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1));

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel createProfileAndSecurityRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 24, 0));
        row.setOpaque(false);

        // Profile Details Card
        JPanel profCard = new JPanel();
        profCard.setLayout(new BoxLayout(profCard, BoxLayout.Y_AXIS));
        profCard.setBackground(Color.WHITE);
        profCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel profTitle = new JLabel("Personal Details");
        profTitle.setFont(UIConstants.FONT_TITLE);

        JPanel pForm = new JPanel(new GridLayout(5, 2, 10, 10));
        pForm.setOpaque(false);

        nameField = new JTextField();
        phoneField = new JTextField();
        emailLabel = new JLabel("-");
        enrLabel = new JLabel("-");
        courseLabel = new JLabel("-");

        pForm.add(new JLabel("Full Name:"));
        pForm.add(nameField);
        pForm.add(new JLabel("Phone (10 Digits):"));
        pForm.add(phoneField);
        pForm.add(new JLabel("College Email:"));
        pForm.add(emailLabel);
        pForm.add(new JLabel("Enrollment No:"));
        pForm.add(enrLabel);
        pForm.add(new JLabel("Degree Course:"));
        pForm.add(courseLabel);

        JButton saveProfBtn = new JButton("Save Profile Details");
        UIConstants.stylePrimaryButton(saveProfBtn);
        saveProfBtn.setAlignmentX(LEFT_ALIGNMENT);
        saveProfBtn.addActionListener(e -> handleSaveProfile());

        profCard.add(profTitle);
        profCard.add(Box.createVerticalStrut(14));
        profCard.add(pForm);
        profCard.add(Box.createVerticalStrut(16));
        profCard.add(saveProfBtn);

        // Security / Change Password Card
        JPanel secCard = new JPanel();
        secCard.setLayout(new BoxLayout(secCard, BoxLayout.Y_AXIS));
        secCard.setBackground(Color.WHITE);
        secCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel secTitle = new JLabel("Change Password");
        secTitle.setFont(UIConstants.FONT_TITLE);

        JPanel passForm = new JPanel(new GridLayout(3, 2, 10, 10));
        passForm.setOpaque(false);

        JPasswordField currentPassField = new JPasswordField();
        JPasswordField newPassField = new JPasswordField();
        JPasswordField confirmPassField = new JPasswordField();

        passForm.add(new JLabel("Current Password:"));
        passForm.add(currentPassField);
        passForm.add(new JLabel("New Password (Min 6):"));
        passForm.add(newPassField);
        passForm.add(new JLabel("Confirm New Password:"));
        passForm.add(confirmPassField);

        JButton updatePassBtn = new JButton("Update Password");
        UIConstants.styleOutlineButton(updatePassBtn);
        updatePassBtn.setAlignmentX(LEFT_ALIGNMENT);
        updatePassBtn.addActionListener(e -> handleChangePassword(currentPassField, newPassField, confirmPassField));

        secCard.add(secTitle);
        secCard.add(Box.createVerticalStrut(14));
        secCard.add(passForm);
        secCard.add(Box.createVerticalStrut(16));
        secCard.add(updatePassBtn);

        row.add(profCard);
        row.add(secCard);
        return row;
    }

    public void loadData() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            mainFrame.navigateTo("LOGIN");
            return;
        }

        // Fill Profile Info
        nameField.setText(user.getFullName());
        phoneField.setText(user.getPhone());
        emailLabel.setText(user.getEmail());
        enrLabel.setText(user.getEnrollmentNo() != null ? user.getEnrollmentNo() : "N/A");
        courseLabel.setText(user.getCourseName() != null ? user.getCourseName() : "General");

        // Load Registrations from DB
        new SwingWorker<List<Registration>, Void>() {
            @Override
            protected List<Registration> doInBackground() throws Exception {
                return registrationDAO.getByUserId(user.getId());
            }

            @Override
            protected void done() {
                try {
                    registrationsList = get();
                    tableModel.setRowCount(0);
                    if (registrationsList != null) {
                        for (Registration r : registrationsList) {
                            String feeStr = (r.getFee() == null || r.getFee().signum() == 0) ? "Free" : "Rs. " + r.getFee();
                            tableModel.addRow(new Object[]{
                                    r.getRegNo(),
                                    r.getEventTitle(),
                                    r.getVenue(),
                                    DateUtil.formatDateTime(r.getStartDateTime()),
                                    feeStr,
                                    r.getStatus()
                            });
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading registrations: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void handleViewPass() {
        int row = regTable.getSelectedRow();
        if (row < 0 || registrationsList == null || row >= registrationsList.size()) {
            JOptionPane.showMessageDialog(this, "Please select a registration from the table to view the pass.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Registration reg = registrationsList.get(row);
        RegistrationPassDialog dialog = new RegistrationPassDialog(mainFrame, reg);
        dialog.setVisible(true);
    }

    private void handleCancelRegistration() {
        int row = regTable.getSelectedRow();
        if (row < 0 || registrationsList == null || row >= registrationsList.size()) {
            JOptionPane.showMessageDialog(this, "Please select a registration to cancel.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Registration reg = registrationsList.get(row);

        if ("CANCELLED".equalsIgnoreCase(reg.getStatus())) {
            JOptionPane.showMessageDialog(this, "This registration is already cancelled.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!reg.canCancel()) {
            JOptionPane.showMessageDialog(this, "Registration cannot be cancelled because the event has already started.", "Cancellation Closed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel your registration for:\n\"" + reg.getEventTitle() + "\"?\n\nYour seat will be returned immediately.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return registrationDAO.cancelRegistration(reg.getId(), reg.getUserId());
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(MyRegistrationsPage.this, "Registration successfully cancelled. The seat has been returned.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MyRegistrationsPage.this, "Failed to cancel registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void handleSaveProfile() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (phone.length() != 10 || !phone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return userDAO.updateProfile(user.getId(), name, phone);
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        user.setFullName(name);
                        user.setPhone(phone);
                        JOptionPane.showMessageDialog(MyRegistrationsPage.this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        mainFrame.getNavbar().updateAuthSection();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MyRegistrationsPage.this, "Error updating profile: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void handleChangePassword(JPasswordField curr, JPasswordField next, JPasswordField confirm) {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        String oldP = new String(curr.getPassword());
        String newP = new String(next.getPassword());
        String confP = new String(confirm.getPassword());

        if (oldP.isEmpty() || newP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter current and new password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newP.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newP.equals(confP)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Verify current password
                User fresh = userDAO.findById(user.getId());
                if (!PasswordUtil.verifyPassword(oldP, fresh.getPasswordHash())) {
                    throw new Exception("Current password is incorrect.");
                }

                String newHash = PasswordUtil.hashPassword(newP);
                return userDAO.updatePassword(user.getId(), newHash);
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(MyRegistrationsPage.this, "Password successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        curr.setText("");
                        next.setText("");
                        confirm.setText("");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MyRegistrationsPage.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}

