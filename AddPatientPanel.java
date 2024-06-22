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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPatientPanel extends JPanel {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField dobField;
    private JComboBox<String> genderComboBox;
    private JTextField contactNumberField;
    private JTextField emailField;
    private JTextArea addressTextArea;
    private JTextArea medicalHistoryTextArea;

    public AddPatientPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form panel to hold input fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        // Create custom titled border with increased text size
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Add Patient");
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

        JLabel dobLabel = new JLabel("Date of Birth (yyyy-MM-dd):");
        dobField = new JTextField(20);

        JLabel genderLabel = new JLabel("Gender:");
        String[] genders = {"Male", "Female", "Other"};
        genderComboBox = new JComboBox<>(genders);
        genderComboBox.setPreferredSize(new Dimension(150, 25));

        JLabel contactNumberLabel = new JLabel("Contact Number:");
        contactNumberField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);

        JLabel addressLabel = new JLabel("Address:");
        addressTextArea = new JTextArea(3, 20);
        JScrollPane addressScrollPane = new JScrollPane(addressTextArea);
        addressScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        addressScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JLabel medicalHistoryLabel = new JLabel("Medical History:");
        medicalHistoryTextArea = new JTextArea(3, 20);
        JScrollPane medicalHistoryScrollPane = new JScrollPane(medicalHistoryTextArea);
        medicalHistoryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        medicalHistoryScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add components to form panel
        addToPanel(formPanel, firstNameLabel, firstNameField, gbc);
        addToPanel(formPanel, lastNameLabel, lastNameField, gbc);
        addToPanel(formPanel, dobLabel, dobField, gbc);
        addToPanel(formPanel, genderLabel, genderComboBox, gbc);
        addToPanel(formPanel, contactNumberLabel, contactNumberField, gbc);
        addToPanel(formPanel, emailLabel, emailField, gbc);
        addToPanel(formPanel, addressLabel, addressScrollPane, gbc);
        addToPanel(formPanel, medicalHistoryLabel, medicalHistoryScrollPane, gbc);

        // Submit button
        JButton submitButton = new JButton("Add Patient");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPatientToDatabase();
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

    private void addPatientToDatabase() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String dobText = dobField.getText();
        String gender = (String) genderComboBox.getSelectedItem();
        String contactNumber = contactNumberField.getText();
        String email = emailField.getText();
        String address = addressTextArea.getText();
        String medicalHistory = medicalHistoryTextArea.getText();

        // Validate input
        if (firstName.isEmpty() || lastName.isEmpty() || dobText.isEmpty() || gender.isEmpty() || contactNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        // Validate Date of Birth
        if (!isValidDateOfBirth(dobText)) {
            JOptionPane.showMessageDialog(this, "Invalid Date of Birth format. Use yyyy-MM-dd and ensure the date is not in the future.");
            return;
        }

        // Validate email
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            return;
        }

        // Construct SQL query
        String sql = "INSERT INTO patients (first_name, last_name, date_of_birth, gender, contact_number, email, address, medical_history) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Execute SQL query
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/patient_management_system", "root", "");
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setDate(3, java.sql.Date.valueOf(dobText));
            statement.setString(4, gender);
            statement.setString(5, contactNumber);
            statement.setString(6, email);
            statement.setString(7, address);
            statement.setString(8, medicalHistory);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Patient added successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add patient. Please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private boolean isValidDateOfBirth(String dobText) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            Date dob = dateFormat.parse(dobText);
            if (dob.after(new Date())) {
                return false; // DOB is in the future
            }
            return true;
        } catch (ParseException e) {
            return false; // Invalid date format
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
        dobField.setText("");
        genderComboBox.setSelectedIndex(0);
        contactNumberField.setText("");
        emailField.setText("");
        addressTextArea.setText("");
        medicalHistoryTextArea.setText("");
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
            JFrame frame = new JFrame("Add Patient Panel");
            frame.setSize(500, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Add the AddPatientPanel to the JFrame
            AddPatientPanel addPatientPanel = new AddPatientPanel();
            frame.add(addPatientPanel);

            // Increase the font size of the "Add Patient" button
            Component[] components = addPatientPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JPanel) {
                    JPanel panel = (JPanel) component;
                    for (Component innerComponent : panel.getComponents()) {
                        if (innerComponent instanceof JButton) {
                            JButton button = (JButton) innerComponent;
                            if (button.getText().equals("Add Patient")) {
                                button.setFont(button.getFont().deriveFont(16f)); // Set font size 16
                            }
                        }
                    }
                }
            }

            frame.setVisible(true);
        });
    }
}
