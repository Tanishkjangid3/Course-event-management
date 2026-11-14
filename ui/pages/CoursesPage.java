package com.campusevents.ui.pages;

import com.campusevents.dao.CourseDAO;
import com.campusevents.model.Course;
import com.campusevents.ui.MainFrame;
import com.campusevents.ui.components.CourseCard;
import com.campusevents.ui.components.Footer;
import com.campusevents.ui.components.ScrollablePanel;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

/**
 * Courses Catalog Page displaying college academic courses and their linked event counts.
 */
public class CoursesPage extends ScrollablePanel {

    private final MainFrame mainFrame;
    private final CourseDAO courseDAO = new CourseDAO();
    private JPanel gridContainer;
    private JLabel statusLabel;

    public CoursesPage(MainFrame mainFrame) {
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
                new EmptyBorder(32, 48, 32, 48)
        ));

        JLabel title = new JLabel("Academic Programs & Courses");
        title.setFont(UIConstants.FONT_HERO);
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Explore college degree programs and view specialized technical events organized for each branch.");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_SECONDARY);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        add(header);
        add(Box.createVerticalStrut(24));

        statusLabel = new JLabel("Loading academic courses...");
        statusLabel.setFont(UIConstants.FONT_BODY);
        statusLabel.setForeground(UIConstants.TEXT_MUTED);
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(statusLabel);

        // Course Cards Grid
        gridContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 24));
        gridContainer.setOpaque(false);
        gridContainer.setBorder(new EmptyBorder(0, 30, 30, 30));
        add(gridContainer);

        add(Box.createVerticalGlue());
        add(new Footer(mainFrame));
    }

    public void loadData() {
        statusLabel.setVisible(true);
        statusLabel.setText("Loading academic courses from database...");

        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return courseDAO.getAll();
            }

            @Override
            protected void done() {
                try {
                    List<Course> courses = get();
                    gridContainer.removeAll();
                    statusLabel.setVisible(false);

                    if (courses != null && !courses.isEmpty()) {
                        for (Course c : courses) {
                            gridContainer.add(new CourseCard(c, mainFrame));
                        }
                    } else {
                        statusLabel.setText("No courses currently registered in system.");
                        statusLabel.setVisible(true);
                    }

                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    statusLabel.setText("Failed to load courses: " + ex.getMessage());
                    statusLabel.setForeground(UIConstants.ACCENT_RED);
                }
            }
        }.execute();
    }
}

