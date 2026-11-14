package com.campusevents.ui.pages;

import com.campusevents.dao.CourseDAO;
import com.campusevents.dao.UserDAO;
import com.campusevents.model.Course;
import com.campusevents.model.User;
import com.campusevents.ui.MainFrame;
import com.campusevents.ui.components.Footer;
import com.campusevents.ui.components.ScrollablePanel;
import com.campusevents.util.PasswordUtil;
import com.campusevents.util.SessionManager;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

/**
 * Student Registration / Sign Up Page enforcing strict regex validation,
 * phone formatting, unique email constraints, and SHA-256 salted password hashing.
 */
public class SignUpPage extends ScrollablePanel {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    private final MainFrame mainFrame;
    private final UserDAO userDAO = new UserDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField enrField;
    private JComboBox<CourseComboItem> courseCombo;
    private JComboBox<String> yearCombo;
    private JPasswordField passField;
    private JPasswordField confirmPassField;
    private JButton signUpButton;

    public static class CourseComboItem {
        public final int id;
        public final String name;

        public CourseComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public SignUpPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.BG_MAIN);
        initUI();
    }

    private void initUI() {
        add(Box.createVerticalStrut(32));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1, true),
                new EmptyBorder(32, 44, 32, 44)
        ));
        card.setPreferredSize(new Dimension(540, 680));
        card.setMaximumSize(new Dimension(540, 680));

        JLabel title = new JLabel("Create Student Account");
        title.setFont(UIConstants.FONT_HERO);
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Join the campus community to register for courses, workshops, and competitions.");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_SECONDARY);

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));

        // Form Fields Grid
        JPanel formGrid = new JPanel(new GridLayout(8, 2, 14, 12));
        formGrid.setOpaque(false);

        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        phoneField.putClientProperty("JTextField.placeholderText", "10-digit mobile number");
        enrField = new JTextField();
        enrField.putClientProperty("JTextField.placeholderText", "e.g. ENR-2024-XXXX");

        courseCombo = new JComboBox<>();
        yearCombo = new JComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year", "Post-Graduate"});

        passField = new JPasswordField();
        confirmPassField = new JPasswordField();

        formGrid.add(new JLabel("Full Name: *"));
        formGrid.add(nameField);

        formGrid.add(new JLabel("College Email: *"));
        formGrid.add(emailField);

        formGrid.add(new JLabel("Phone (10 Digits): *"));
        formGrid.add(phoneField);

        formGrid.add(new JLabel("Enrollment No: *"));
        formGrid.add(enrField);

        formGrid.add(new JLabel("Academic Course: *"));
        formGrid.add(courseCombo);

        formGrid.add(new JLabel("Current Year: *"));
        formGrid.add(yearCombo);

        formGrid.add(new JLabel("Password (Min 6): *"));
        formGrid.add(passField);

        formGrid.add(new JLabel("Confirm Password: *"));
        formGrid.add(confirmPassField);

        card.add(formGrid);
        card.add(Box.createVerticalStrut(24));

        // Sign Up Button
        signUpButton = new JButton("Create Account & Sign In");
        UIConstants.stylePrimaryButton(signUpButton);
        signUpButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signUpButton.setPreferredSize(new Dimension(450, 42));
        signUpButton.setMaximumSize(new Dimension(450, 42));
        signUpButton.addActionListener(e -> handleSignUp());
        card.add(signUpButton);
        card.add(Box.createVerticalStrut(18));

        // Switch to Login
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        switchPanel.setOpaque(false);

        JLabel promptLabel = new JLabel("Already registered?");
        promptLabel.setFont(UIConstants.FONT_BODY);
        promptLabel.setForeground(UIConstants.TEXT_SECONDARY);

        JButton loginLink = new JButton("Log In Here");
        loginLink.setFont(UIConstants.FONT_BODY_BOLD);
        loginLink.setForeground(UIConstants.PRIMARY_BLUE);
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setCursor(UIConstants.HAND_CURSOR);
        loginLink.addActionListener(e -> mainFrame.navigateTo("LOGIN"));

        switchPanel.add(promptLabel);
        switchPanel.add(loginLink);
        card.add(switchPanel);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setOpaque(false);
        centerWrapper.add(card);

        add(centerWrapper);
        add(Box.createVerticalStrut(40));
        add(Box.createVerticalGlue());
        add(new Footer(mainFrame));
    }

    public void loadCourses() {
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return courseDAO.getAll();
            }

            @Override
            protected void done() {
                try {
                    List<Course> list = get();
                    courseCombo.removeAllItems();
                    if (list != null) {
                        for (Course c : list) {
                            courseCombo.addItem(new CourseComboItem(c.getId(), c.getName()));
                        }
                    }
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private void handleSignUp() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim().toLowerCase();
        String phone = phoneField.getText().trim();
        String enr = enrField.getText().trim();
        CourseComboItem selectedCourse = (CourseComboItem) courseCombo.getSelectedItem();
        String year = (String) yearCombo.getSelectedItem();
        String password = new String(passField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        // 1. Validation Checks
        if (name.isEmpty()) {
            showError("Please enter your full name.", nameField);
            return;
        }

        if (email.isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
            showError("Please enter a valid email address (e.g. student@campus.edu).", emailField);
            return;
        }

        if (phone.isEmpty() || !PHONE_PATTERN.matcher(phone).matches()) {
            showError("Phone number must contain exactly 10 digits.", phoneField);
            return;
        }

        if (enr.isEmpty()) {
            showError("Please enter your college enrollment number.", enrField);
            return;
        }

        if (selectedCourse == null) {
            showError("Please select your academic degree course.", courseCombo);
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters long.", passField);
            return;
        }

        if (!password.equals(confirmPass)) {
            showError("Passwords do not match. Please re-enter confirm password.", confirmPassField);
            return;
        }

        signUpButton.setEnabled(false);
        signUpButton.setText("Creating Account...");

        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                // Check email uniqueness
                if (userDAO.emailExists(email)) {
                    throw new Exception("An account with email '" + email + "' is already registered. Please login.");
                }

                String hashedPassword = PasswordUtil.hashPassword(password);
                User newUser = new User();
                newUser.setFullName(name);
                newUser.setEmail(email);
                newUser.setPhone(phone);
                newUser.setEnrollmentNo(enr);
                newUser.setCourseId(selectedCourse.id);
                newUser.setYear(year);
                newUser.setPasswordHash(hashedPassword);
                newUser.setRole("STUDENT");

                boolean created = userDAO.create(newUser);
                if (!created) {
                    throw new Exception("Could not insert user record. Please try again.");
                }

                return newUser;
            }

            @Override
            protected void done() {
                signUpButton.setEnabled(true);
                signUpButton.setText("Create Account & Sign In");

                try {
                    User createdUser = get();
                    SessionManager.getInstance().login(createdUser);
                    JOptionPane.showMessageDialog(SignUpPage.this, 
                            "Account created successfully!\nWelcome to CampusCourse Events, " + createdUser.getFullName() + ".", 
                            "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                    mainFrame.navigateTo("EVENTS");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SignUpPage.this, ex.getMessage(), "Sign Up Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void showError(String msg, javax.swing.JComponent comp) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
        comp.requestFocus();
    }
}

