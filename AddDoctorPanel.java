package mysql;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddDoctorPanel extends JPanel {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField specializationField;
    private JTextField contactNumberField;
    private JTextField emailField;
    private JTextArea addressTextArea;

    public AddDoctorPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form panel to hold input fields
        JPanel formPanel = new JPanel(new GridBagLayout());

        // Create custom titled border with increased text size
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Add Doctor");
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD, 20f)); // Increase font size
        formPanel.setBorder(titledBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Labels and fields
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameField = new JTextField(20);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameField = new JTextField(20);

        JLabel specializationLabel = new JLabel("Specialization:");
        specializationField = new JTextField(20);

        JLabel contactNumberLabel = new JLabel("Contact Number:");
        contactNumberField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);

        JLabel addressLabel = new JLabel("Address:");
        addressTextArea = new JTextArea(3, 20);
        JScrollPane addressScrollPane = new JScrollPane(addressTextArea);
        addressScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        addressScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add components to form panel
        addToPanel(formPanel, firstNameLabel, firstNameField, gbc);
        addToPanel(formPanel, lastNameLabel, lastNameField, gbc);
        addToPanel(formPanel, specializationLabel, specializationField, gbc);
        addToPanel(formPanel, contactNumberLabel, contactNumberField, gbc);
        addToPanel(formPanel, emailLabel, emailField, gbc);
        addToPanel(formPanel, addressLabel, addressScrollPane, gbc);

        // Submit button
        JButton submitButton = new JButton("Add Doctor");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDoctorToDatabase();
            }
        });

        // Add form panel and submit button to the main panel
        add(formPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addToPanel(JPanel panel, JLabel label, JComponent component, GridBagConstraints gbc) {
        panel.add(label, gbc);
        gbc.gridx++;
        panel.add(component, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addDoctorToDatabase() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String specialization = specializationField.getText();
        String contactNumber = contactNumberField.getText();
        String email = emailField.getText();
        String address = addressTextArea.getText();

        // Validate input
        if (firstName.isEmpty() || lastName.isEmpty() || specialization.isEmpty() || contactNumber.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        // Validate email
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            return;
        }

        // Construct SQL query
        String sql = "INSERT INTO doctors (first_name, last_name, specialization, contact_number, email, address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        // Execute SQL query
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/patient_management_system", "root", "");
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, specialization);
            statement.setString(4, contactNumber);
            statement.setString(5, email);
            statement.setString(6, address);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Doctor added successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add doctor. Please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        specializationField.setText("");
        contactNumberField.setText("");
        emailField.setText("");
        addressTextArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Set the look and feel to the system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Create and configure the main JFrame
            JFrame frame = new JFrame("Add Doctor Panel");
            frame.setSize(500, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Add the AddDoctorPanel to the JFrame
            frame.add(new AddDoctorPanel());

            frame.setVisible(true);
        });
    }
}
