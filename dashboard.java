package mysql;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class dashboard extends JFrame {
    private JPanel mainPanel;
    private JPanel buttonPanel;

    public dashboard() {
        // Set frame properties
        setTitle("Dashboard");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // Create main panel with image
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 255, 255));
        
        // Load and display image
        ImageIcon imageIcon = new ImageIcon("images/patient-management.jpeg");
        JLabel imageLabel = new JLabel(imageIcon);
        mainPanel.add(imageLabel, BorderLayout.CENTER);

        // Add main panel to the center
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Create left panel with buttons
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(220, 220, 220));

        // Add buttons to the left panel
        addButton("Add Patient", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPanel(new AddPatientPanel());
            }
        });
        addButton("Patient Records", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPanel(new PatientRecordsPanel());
            }
        });
        addButton("Update Patient Details", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPanel(new UpdatePatientDetailPanel());
            }
        });
        addButton("Add Doctor", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPanel(new AddDoctorPanel());
            }
        });
        addButton("Update Doctor Details", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPanel(new UpdateDoctorDetailsPanel());
            }
        });
        addButton("Logout", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Perform logout and show login page
                new LoginPage().setVisible(true);
                dispose();
            }
        });

        // Add button panel to the left side
        getContentPane().add(buttonPanel, BorderLayout.WEST);
    }

    private void addButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getMinimumSize().height));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(listener);
        button.setFocusPainted(false);
        buttonPanel.add(button);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void showPanel(JComponent component) {
        mainPanel.removeAll();
        mainPanel.add(component, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        // Set the look and feel to the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show the dashboard
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new dashboard().setVisible(true);
            }
        });
    }
}
