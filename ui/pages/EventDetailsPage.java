package com.campusevents.ui.pages;

import com.campusevents.dao.EventDAO;
import com.campusevents.dao.RegistrationDAO;
import com.campusevents.model.Event;
import com.campusevents.model.EventStatus;
import com.campusevents.model.Registration;
import com.campusevents.model.User;
import com.campusevents.ui.MainFrame;
import com.campusevents.ui.components.Footer;
import com.campusevents.ui.components.RegistrationPassDialog;
import com.campusevents.ui.components.ScrollablePanel;
import com.campusevents.util.DateUtil;
import com.campusevents.util.ImageUtil;
import com.campusevents.util.SessionManager;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

/**
 * Event Details Page displaying high-res thumbnail banner, comprehensive academic & speaker metadata,
 * seat availability, real-time registration verification, and immediate Pass issuance.
 */
public class EventDetailsPage extends ScrollablePanel {

    private final MainFrame mainFrame;
    private final EventDAO eventDAO = new EventDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    private int currentEventId;
    private Event currentEvent;
    private boolean isUserAlreadyRegistered = false;

    private JPanel contentPanel;
    private JLabel statusLabel;

    public EventDetailsPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.BG_MAIN);
        initUI();
    }

    private void initUI() {
        // Back Navigation Bar
        JPanel navRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        navRow.setBackground(Color.WHITE);
        navRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_LIGHT));

        JButton backBtn = new JButton("← Back to Events");
        UIConstants.styleOutlineButton(backBtn);
        backBtn.addActionListener(e -> mainFrame.navigateTo("EVENTS"));

        navRow.add(backBtn);
        add(navRow);

        statusLabel = new JLabel("Loading event details...");
        statusLabel.setFont(UIConstants.FONT_BODY);
        statusLabel.setForeground(UIConstants.TEXT_MUTED);
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);
        statusLabel.setBorder(new EmptyBorder(40, 0, 40, 0));
        add(statusLabel);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        add(contentPanel);

        add(Box.createVerticalGlue());
        add(new Footer(mainFrame));
    }

    public void loadEvent(int eventId) {
        this.currentEventId = eventId;
        statusLabel.setVisible(true);
        statusLabel.setText("Loading event details from database...");
        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                currentEvent = eventDAO.getById(eventId);
                User user = SessionManager.getInstance().getCurrentUser();
                if (user != null && currentEvent != null) {
                    isUserAlreadyRegistered = registrationDAO.isUserRegistered(user.getId(), eventId);
                } else {
                    isUserAlreadyRegistered = false;
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    statusLabel.setVisible(false);
                    if (currentEvent != null) {
                        buildEventDetailsUI();
                    } else {
                        statusLabel.setText("Event not found or has been removed.");
                        statusLabel.setVisible(true);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Failed to load event: " + ex.getMessage());
                    statusLabel.setForeground(UIConstants.ACCENT_RED);
                }
            }
        }.execute();
    }

    private void buildEventDetailsUI() {
        contentPanel.removeAll();

        JPanel mainCard = new JPanel(new BorderLayout(0, 24));
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(28, 36, 36, 36)
        ));
        mainCard.setMaximumSize(new Dimension(1000, 2000));
        mainCard.setAlignmentX(CENTER_ALIGNMENT);

        // 1. Top Section: Large Thumbnail (16:9 banner ~920x380)
        ImageIcon bannerIcon = ImageUtil.getImageIcon(currentEvent.getThumbnailPath(), 920, 380, currentEvent.getTitle());
        JLabel bannerLabel = new JLabel(bannerIcon);
        bannerLabel.setHorizontalAlignment(JLabel.CENTER);
        bannerLabel.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true));
        mainCard.add(bannerLabel, BorderLayout.NORTH);

        // 2. Middle Body Section
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);

        // Header Line: Course & Status Badges
        JPanel badgesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        badgesRow.setOpaque(false);

        JLabel courseBadge = new JLabel(currentEvent.getCourseName() != null ? currentEvent.getCourseName().toUpperCase() : "GENERAL ACADEMIC");
        courseBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        courseBadge.setOpaque(true);
        courseBadge.setBackground(UIConstants.BADGE_BLUE_BG);
        courseBadge.setForeground(UIConstants.BADGE_BLUE_TEXT);
        courseBadge.setBorder(new EmptyBorder(5, 12, 5, 12));

        EventStatus status = currentEvent.getStatus();
        JLabel statusBadge = new JLabel(status.getLabel());
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusBadge.setOpaque(true);
        statusBadge.setBackground(status.getBgColor());
        statusBadge.setForeground(status.getTextColor());
        statusBadge.setBorder(new EmptyBorder(5, 12, 5, 12));

        badgesRow.add(courseBadge);
        badgesRow.add(statusBadge);

        if (isUserAlreadyRegistered) {
            JLabel registeredBadge = new JLabel("✓ YOU ARE REGISTERED");
            registeredBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            registeredBadge.setOpaque(true);
            registeredBadge.setBackground(UIConstants.BADGE_GREEN_BG);
            registeredBadge.setForeground(UIConstants.BADGE_GREEN_TEXT);
            registeredBadge.setBorder(new EmptyBorder(5, 12, 5, 12));
            badgesRow.add(registeredBadge);
        }

        // Title
        JLabel titleLabel = new JLabel("<html><body style='width: 880px;'><b>" + currentEvent.getTitle() + "</b></body></html>");
        titleLabel.setFont(UIConstants.FONT_HERO);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);

        // Description (4-6 detailed lines)
        JLabel descLabel = new JLabel("<html><body style='width: 880px; color: #3C4043; font-size: 13px; line-height: 1.6;'>" +
                currentEvent.getDescription().replace("\n", "<br>") + "</body></html>");

        bodyPanel.add(badgesRow);
        bodyPanel.add(Box.createVerticalStrut(12));
        bodyPanel.add(titleLabel);
        bodyPanel.add(Box.createVerticalStrut(16));
        bodyPanel.add(descLabel);
        bodyPanel.add(Box.createVerticalStrut(24));

        // Metadata Info Grid (2 Columns, 4 Rows)
        JPanel metaGrid = new JPanel(new GridLayout(4, 2, 20, 16));
        metaGrid.setBackground(new Color(248, 249, 250));
        metaGrid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(18, 22, 18, 22)
        ));

        metaGrid.add(createMetaItem("📍 Venue & Building", currentEvent.getVenue() + " (" + currentEvent.getBuilding() + ")"));
        metaGrid.add(createMetaItem("📅 Start Date & Time", DateUtil.formatDateTime(currentEvent.getStartDateTime())));
        metaGrid.add(createMetaItem("🏁 End Date & Time", DateUtil.formatDateTime(currentEvent.getEndDateTime())));
        metaGrid.add(createMetaItem("⏰ Registration Deadline", DateUtil.formatDateTime(currentEvent.getRegistrationDeadline())));
        metaGrid.add(createMetaItem("🎟️ Total / Remaining Seats", currentEvent.getTotalSeats() + " Total  |  " + currentEvent.getSeatsRemaining() + " Seats Left"));

        String feeStr = currentEvent.isFree() ? "Free (No Fee)" : "Rs. " + currentEvent.getFee();
        metaGrid.add(createMetaItem("💰 Registration Fee", feeStr));
        metaGrid.add(createMetaItem("👤 Organizer / Speaker", currentEvent.getOrganizer()));
        metaGrid.add(createMetaItem("✉️ Contact Person", currentEvent.getContact()));

        bodyPanel.add(metaGrid);
        bodyPanel.add(Box.createVerticalStrut(28));

        // 3. Action Section: Register Button
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        actionRow.setOpaque(false);

        if (isUserAlreadyRegistered) {
            JButton viewPassBtn = new JButton("View Your Admission Pass");
            UIConstants.styleSuccessButton(viewPassBtn);
            viewPassBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            viewPassBtn.addActionListener(e -> openExistingPass());
            actionRow.add(viewPassBtn);

        } else {
            JButton registerBtn = new JButton("Register Now");
            registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));

            boolean canRegister = currentEvent.isRegistrationAllowed();
            if (canRegister) {
                UIConstants.stylePrimaryButton(registerBtn);
                registerBtn.addActionListener(e -> handleRegistrationClick());
            } else {
                registerBtn.setEnabled(false);
                registerBtn.setBackground(new Color(218, 220, 224));
                registerBtn.setForeground(UIConstants.TEXT_MUTED);

                String reason = "Registration Unavailable";
                if (currentEvent.getStatus() == EventStatus.COMPLETED) reason = "Event Completed";
                else if (currentEvent.getStatus() == EventStatus.SEATS_FULL) reason = "All Seats Full";
                else if (currentEvent.getStatus() == EventStatus.REGISTRATION_CLOSED) reason = "Registration Closed";

                registerBtn.setText(reason);
            }
            actionRow.add(registerBtn);
        }

        bodyPanel.add(actionRow);
        mainCard.add(bodyPanel, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(24, 20, 40, 20));
        wrapper.add(mainCard);

        contentPanel.add(wrapper);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createMetaItem(String label, String value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(UIConstants.TEXT_MUTED);

        JLabel val = new JLabel(value != null ? value : "N/A");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(UIConstants.TEXT_PRIMARY);

        p.add(lbl);
        p.add(Box.createVerticalStrut(3));
        p.add(val);
        return p;
    }

    private void handleRegistrationClick() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "You must be logged in to register for college events.\nWould you like to log in now?",
                    "Login Required",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                mainFrame.navigateTo("LOGIN");
            }
            return;
        }

        if (user.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Administrators cannot register as student attendees.", "Admin Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show Confirmation Dialog with prefilled details
        showRegistrationDialog(user);
    }

    private void showRegistrationDialog(User user) {
        JDialog dialog = new JDialog(mainFrame, "Confirm Event Registration", true);
        dialog.setLayout(new BorderLayout(16, 16));
        dialog.setResizable(false);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 12));
        form.setBorder(new EmptyBorder(20, 24, 10, 24));

        JTextField nameField = new JTextField(user.getFullName());
        nameField.setEditable(false);

        JTextField emailField = new JTextField(user.getEmail());
        emailField.setEditable(false);

        JTextField enrField = new JTextField(user.getEnrollmentNo() != null ? user.getEnrollmentNo() : "Pending");
        enrField.setEditable(false);

        JTextField eventField = new JTextField(currentEvent.getTitle());
        eventField.setEditable(false);

        JTextField reqField = new JTextField();
        reqField.putClientProperty("JTextField.placeholderText", "e.g. Lab setup, wheelchair access, diet (optional)");

        form.add(new JLabel("Student Name:"));
        form.add(nameField);
        form.add(new JLabel("Email Address:"));
        form.add(emailField);
        form.add(new JLabel("Enrollment No:"));
        form.add(enrField);
        form.add(new JLabel("Selected Event:"));
        form.add(eventField);
        form.add(new JLabel("Special Requirements:"));
        form.add(reqField);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        bottomBar.setBackground(UIConstants.BG_MAIN);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER_LIGHT));

        JButton confirmBtn = new JButton("Confirm Registration");
        UIConstants.stylePrimaryButton(confirmBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(UIConstants.FONT_BODY);
        cancelBtn.addActionListener(e -> dialog.dispose());

        confirmBtn.addActionListener(e -> {
            confirmBtn.setEnabled(false);
            confirmBtn.setText("Registering...");
            String requirements = reqField.getText().trim();

            new SwingWorker<Registration, Void>() {
                @Override
                protected Registration doInBackground() throws Exception {
                    return registrationDAO.registerStudent(user.getId(), currentEvent.getId(), requirements);
                }

                @Override
                protected void done() {
                    try {
                        Registration reg = get();
                        dialog.dispose();

                        // Refresh page state
                        loadEvent(currentEventId);

                        // Present Registration Pass Modal Dialog
                        RegistrationPassDialog passDialog = new RegistrationPassDialog(mainFrame, reg);
                        passDialog.setVisible(true);

                    } catch (Exception ex) {
                        confirmBtn.setEnabled(true);
                        confirmBtn.setText("Confirm Registration");
                        JOptionPane.showMessageDialog(dialog, "Registration Failed:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        bottomBar.add(confirmBtn);
        bottomBar.add(cancelBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bottomBar, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setSize(520, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void openExistingPass() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        new SwingWorker<Registration, Void>() {
            @Override
            protected Registration doInBackground() throws Exception {
                for (Registration r : registrationDAO.getByUserId(user.getId())) {
                    if (r.getEventId() == currentEventId && !"CANCELLED".equalsIgnoreCase(r.getStatus())) {
                        return r;
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    Registration reg = get();
                    if (reg != null) {
                        RegistrationPassDialog passDialog = new RegistrationPassDialog(mainFrame, reg);
                        passDialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(EventDetailsPage.this, "Active registration pass not found.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(EventDetailsPage.this, "Failed to load pass: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}

