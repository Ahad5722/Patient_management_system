package mysql;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PatientRecordsPanel extends JPanel {
    private JList<String> patientList;
    private JList<String> doctorList;
    private JTextArea diagnosisTextArea;
    private JTextArea treatmentTextArea;
    private JTextField patientSearchField;
    private JTextField doctorSearchField;
    private DatabaseConnector databaseConnector;

    public PatientRecordsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        databaseConnector = new DatabaseConnector();

        JPanel patientPanel = createPatientPanel();
        JPanel doctorPanel = createDoctorPanel();
        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.add(patientPanel, BorderLayout.NORTH);
        leftPanel.add(doctorPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadPatients();
        loadDoctors();
    }

    private JPanel createPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Patients"));

        patientSearchField = new JTextField(15);
        JButton searchPatientButton = new JButton("Search");
        searchPatientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPatients();
            }
        });

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search Patient by Last Name:"));
        searchPanel.add(patientSearchField);
        searchPanel.add(searchPatientButton);

        patientList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(patientList);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Doctors"));

        doctorSearchField = new JTextField(15);
        JButton searchDoctorButton = new JButton("Search");
        searchDoctorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDoctors();
            }
        });

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search Doctor by Last Name:"));
        searchPanel.add(doctorSearchField);
        searchPanel.add(searchDoctorButton);

        doctorList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(doctorList);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add Medical Record"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel diagnosisLabel = new JLabel("Diagnosis:");
        diagnosisTextArea = new JTextArea(3, 20);
        JScrollPane diagnosisScrollPane = new JScrollPane(diagnosisTextArea);
        diagnosisScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JLabel treatmentLabel = new JLabel("Treatment:");
        treatmentTextArea = new JTextArea(3, 20);
        JScrollPane treatmentScrollPane = new JScrollPane(treatmentTextArea);
        treatmentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        panel.add(diagnosisLabel, gbc);
        gbc.gridx++;
        panel.add(diagnosisScrollPane, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(treatmentLabel, gbc);
        gbc.gridx++;
        panel.add(treatmentScrollPane, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton addRecordButton = new JButton("Add Medical Record");
        addRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMedicalRecord();
            }
        });
        panel.add(addRecordButton);

        JButton searchRecordButton = new JButton("Search Medical Record");
        searchRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchMedicalRecord();
            }
        });
        panel.add(searchRecordButton);

        return panel;
    }

    private void loadPatients() {
        DefaultListModel<String> model = new DefaultListModel<>();
        patientList.setModel(model);
        String searchLastName = patientSearchField.getText().trim();
        String sql = searchLastName.isEmpty() ?
                "SELECT patient_id, first_name, last_name FROM patients ORDER BY last_name" :
                "SELECT patient_id, first_name, last_name FROM patients WHERE last_name LIKE ? ORDER BY last_name";
        try {
            ResultSet resultSet = searchLastName.isEmpty() ?
                    databaseConnector.executeQuery(sql) :
                    databaseConnector.executeQuery(sql, "%" + searchLastName + "%");
            while (resultSet.next()) {
                String patientId = resultSet.getString("patient_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String fullName = lastName + ", " + firstName + " (ID: " + patientId + ")";
                model.addElement(fullName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void loadDoctors() {
        DefaultListModel<String> model = new DefaultListModel<>();
        doctorList.setModel(model);
        String searchLastName = doctorSearchField.getText().trim();
        String sql = searchLastName.isEmpty() ?
                "SELECT doctor_id, first_name, last_name FROM doctors ORDER BY last_name" :
                "SELECT doctor_id, first_name, last_name FROM doctors WHERE last_name LIKE ? ORDER BY last_name";
        try {
            ResultSet resultSet = searchLastName.isEmpty() ?
                    databaseConnector.executeQuery(sql) :
                    databaseConnector.executeQuery(sql, "%" + searchLastName + "%");
            while (resultSet.next()) {
                String doctorId = resultSet.getString("doctor_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String fullName = lastName + ", " + firstName + " (ID: " + doctorId + ")";
                model.addElement(fullName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void addMedicalRecord() {
        String selectedPatient = patientList.getSelectedValue();
        String selectedDoctor = doctorList.getSelectedValue();
        if (selectedPatient == null || selectedDoctor == null) {
            JOptionPane.showMessageDialog(this, "Please select a patient and doctor.");
            return;
        }

        String patientID = getIDFromListValue(selectedPatient);
        String doctorID = getIDFromListValue(selectedDoctor);
        String diagnosis = diagnosisTextArea.getText().trim();
        String treatment = treatmentTextArea.getText().trim();
        String recordDate = LocalDate.now().toString();

        if (diagnosis.isEmpty() || treatment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        String sql = "INSERT INTO medicalrecords (patient_id, doctor_id, diagnosis, treatment, record_date) VALUES (?, ?, ?, ?, ?)";
        try {
            int rowsInserted = databaseConnector.executeUpdate(sql, patientID, doctorID, diagnosis, treatment, recordDate);
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Medical record added successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add medical record. Please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void searchMedicalRecord() {
        String selectedPatient = patientList.getSelectedValue();
        String selectedDoctor = doctorList.getSelectedValue();
        if (selectedPatient == null || selectedDoctor == null) {
            JOptionPane.showMessageDialog(this, "Please select a patient and doctor.");
            return;
        }

        String patientID = getIDFromListValue(selectedPatient);
        String doctorID = getIDFromListValue(selectedDoctor);

        String sql = "SELECT * FROM medicalrecords WHERE patient_id = ? AND doctor_id = ?";
        try {
            ResultSet resultSet = databaseConnector.executeQuery(sql, patientID, doctorID);
            StringBuilder records = new StringBuilder();
            while (resultSet.next()) {
                String recordID = resultSet.getString("record_id");
                String diagnosis = resultSet.getString("diagnosis");
                String treatment = resultSet.getString("treatment");
                String recordDate = resultSet.getString("record_date");

                records.append("Record ID: ").append(recordID).append("\n");
                records.append("Diagnosis: ").append(diagnosis).append("\n");
                records.append("Treatment: ").append(treatment).append("\n");
                records.append("Record Date: ").append(recordDate).append("\n\n");
            }
            if (records.length() > 0) {
                JTextArea recordsTextArea = new JTextArea(20, 80);
                recordsTextArea.setText(records.toString());
                recordsTextArea.setEditable(false);

                JScrollPane scrollPane = new JScrollPane(recordsTextArea);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                // Create the dialog to show the medical records
                JDialog dialog = new JDialog();
                dialog.setTitle("Medical Records");
                dialog.setModal(true); // Set modal to block main window
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                // Add the scroll pane to the dialog content pane
                dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);

                // Create save button and add ActionListener
                JButton saveButton = new JButton("Save Report");
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        generateReport(records.toString(), selectedPatient, selectedDoctor);
                        dialog.dispose(); // Close the dialog after saving
                    }
                });

                // Create cancel button and add ActionListener
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose(); // Close the dialog without saving
                    }
                });

                // Add the buttons to a panel and add to dialog
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);
                dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

                // Pack and set visible after all components are added
                dialog.pack();
                dialog.setLocationRelativeTo(this); // Center dialog relative to parent window
                dialog.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "No medical records found for the selected patient and doctor.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }



    private int showSaveReportDialog(Component parentComponent, String selectedPatient, String selectedDoctor) {
        Object[] options = {"Save Report", "Cancel"};
        return JOptionPane.showOptionDialog(parentComponent,
                "Do you want to save this report?",
                "Save Report",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void generateReport(String records, String selectedPatient, String selectedDoctor) {
        String patientName = selectedPatient.substring(0, selectedPatient.indexOf(" (ID:"));
        String doctorName = selectedDoctor.substring(0, selectedDoctor.indexOf(" (ID:"));
        String fileName = "reports/" + patientName + "_" + doctorName + "_Report.txt";

        StringBuilder report = new StringBuilder();
        report.append("Patient: ").append(patientName).append("\n");
        report.append("Doctor: ").append(doctorName).append("\n\n");
        report.append(records);

        saveReportToFile(report.toString(), fileName);
    }

    private void saveReportToFile(String report, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(report);
            JOptionPane.showMessageDialog(this, "Report generated and saved successfully:\n" + fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving report to file: " + ex.getMessage());
        }
    }

    private String getIDFromListValue(String item) {
        int startIndex = item.lastIndexOf("(ID: ") + 5;
        int endIndex = item.length() - 1;
        return item.substring(startIndex, endIndex);
    }

    private void clearFields() {
        diagnosisTextArea.setText("");
        treatmentTextArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Patient Records Panel");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new PatientRecordsPanel());
            frame.setVisible(true);
        });
    }
}
