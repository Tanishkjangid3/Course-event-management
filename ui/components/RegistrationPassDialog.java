package com.campusevents.ui.components;

import com.campusevents.model.Registration;
import com.campusevents.util.PassGenerator;
import com.campusevents.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Modal dialog for displaying the verified Registration Pass with options to Save as PNG and Print.
 */
public class RegistrationPassDialog extends JDialog {

    private final Registration registration;
    private final BufferedImage passImage;

    public RegistrationPassDialog(Window owner, Registration registration) {
        super(owner, "Official Admission Pass - " + registration.getRegNo(), ModalityType.APPLICATION_MODAL);
        this.registration = registration;
        this.passImage = PassGenerator.renderPassImage(registration);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setResizable(false);

        // Center Pass Preview
        JLabel imageLabel = new JLabel(new ImageIcon(passImage));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Bottom Action Bar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 14));
        btnPanel.setBackground(UIConstants.BG_MAIN);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER_LIGHT));

        JButton savePngBtn = new JButton("Save as PNG");
        UIConstants.stylePrimaryButton(savePngBtn);
        savePngBtn.addActionListener(e -> saveAsPng());

        JButton printBtn = new JButton("Print Pass");
        UIConstants.styleOutlineButton(printBtn);
        printBtn.addActionListener(e -> PassGenerator.printPass(registration, this));

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(UIConstants.FONT_BODY);
        closeBtn.addActionListener(e -> dispose());

        btnPanel.add(savePngBtn);
        btnPanel.add(printBtn);
        btnPanel.add(closeBtn);

        add(imageLabel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void saveAsPng() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Event Pass as PNG");
        chooser.setSelectedFile(new File("Pass_" + registration.getRegNo() + ".png"));
        chooser.setFileFilter(new FileNameExtensionFilter("PNG Images (*.png)", "png"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            if (!target.getName().toLowerCase().endsWith(".png")) {
                target = new File(target.getAbsolutePath() + ".png");
            }
            try {
                boolean ok = PassGenerator.exportToPNG(registration, target);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Pass saved successfully to:\n" + target.getAbsolutePath(), "Pass Saved", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save PNG.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving pass: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

