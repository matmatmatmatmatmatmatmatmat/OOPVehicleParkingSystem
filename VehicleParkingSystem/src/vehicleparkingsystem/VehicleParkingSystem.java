package vehicleparkingsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class VehicleParkingSystem {
    private static final int MAX_SLOTS = 10;
    private static final double RATE_PER_HOUR = 20.0;

    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField licenseField, brandField, modelField;
    private JPanel parkingVisualPanel;
    private JLabel[] parkingSlots;
    private JLabel dateTimeLabel;

    private Map<String, Vehicle> parkingData;

    // Vehicle class to hold additional information
    private class Vehicle {
        String licensePlate;
        String brand;
        String model;
        LocalDateTime entryTime;
        Color color;

        Vehicle(String licensePlate, String brand, String model, LocalDateTime entryTime, Color color) {
            this.licensePlate = licensePlate;
            this.brand = brand;
            this.model = model;
            this.entryTime = entryTime;
            this.color = color;
        }
    }

    public VehicleParkingSystem() {
        parkingData = new HashMap<>();
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Vehicle Parking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Layout setup
        frame.setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Vehicle Parking System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        dateTimeLabel = new JLabel("", SwingConstants.RIGHT);
        dateTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        headerPanel.add(dateTimeLabel, BorderLayout.SOUTH);

        frame.add(headerPanel, BorderLayout.NORTH);

        // Start DateTime Updater
        startDateTimeUpdater();

        // Parking Visual Panel
        parkingVisualPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        parkingSlots = new JLabel[MAX_SLOTS];
        for (int i = 0; i < MAX_SLOTS; i++) {
            parkingSlots[i] = new JLabel("Slot " + (i + 1), SwingConstants.CENTER);
            parkingSlots[i].setOpaque(true);
            parkingSlots[i].setBackground(Color.GREEN);
            parkingSlots[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            parkingSlots[i].setIcon(null);
            parkingVisualPanel.add(parkingSlots[i]);
        }
        frame.add(parkingVisualPanel, BorderLayout.EAST);

        // Table setup (Removed the Color column)
        String[] columnNames = {"Slot #", "License Plate", "Brand", "Model", "Entry Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JScrollPane tableScrollPane = new JScrollPane(table);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 1));

        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.add(new JLabel("License Plate: "));
        licenseField = new JTextField(15);
        formPanel.add(licenseField);
        
        formPanel.add(new JLabel("Car Brand: "));
        brandField = new JTextField(15);
        formPanel.add(brandField);
        
        formPanel.add(new JLabel("Car Model: "));
        modelField = new JTextField(15);
        formPanel.add(modelField);

        JButton addButton = new JButton("Add Vehicle");
        JButton removeButton = new JButton("Remove Vehicle");

        formPanel.add(addButton);
        formPanel.add(removeButton);
        inputPanel.add(formPanel);

        JPanel summaryPanel = new JPanel(new FlowLayout());
        JLabel summaryLabel = new JLabel("Available Slots: " + (MAX_SLOTS - parkingData.size()));
        summaryPanel.add(summaryLabel);
        inputPanel.add(summaryPanel);

        frame.add(inputPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addVehicle(summaryLabel);
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeVehicle(summaryLabel);
            }
        });

        frame.setVisible(true);
    }

    private void startDateTimeUpdater() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                dateTimeLabel.setText("Current Date & Time: " + now.format(formatter));
            }
        });
        timer.start();
    }

    private void addVehicle(JLabel summaryLabel) {
        String licensePlate = licenseField.getText().trim();
        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();

        if (licensePlate.isEmpty() || brand.isEmpty() || model.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields (License Plate, Brand, Model) are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (parkingData.size() >= MAX_SLOTS) {
            JOptionPane.showMessageDialog(frame, "Parking lot is full.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (parkingData.containsKey(licensePlate)) {
            JOptionPane.showMessageDialog(frame, "Vehicle is already parked.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Color picker dialog to choose the car's color
        Color carColor = JColorChooser.showDialog(frame, "Choose Car Color", Color.BLACK); 
        if (carColor == null) {
            carColor = Color.BLACK;  // Default color if no selection is made
        }

        LocalDateTime entryTime = LocalDateTime.now();
        parkingData.put(licensePlate, new Vehicle(licensePlate, brand, model, entryTime, carColor));
        tableModel.addRow(new Object[]{parkingData.size(), licensePlate, brand, model, entryTime});

        // Update visual representation
        updateParkingVisual();

        licenseField.setText("");
        brandField.setText("");
        modelField.setText("");
        updateSummary(summaryLabel);
    }

    private void removeVehicle(JLabel summaryLabel) {
        String licensePlate = licenseField.getText().trim();
        if (licensePlate.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "License plate cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!parkingData.containsKey(licensePlate)) {
            JOptionPane.showMessageDialog(frame, "Vehicle not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Vehicle vehicle = parkingData.remove(licensePlate);
        LocalDateTime exitTime = LocalDateTime.now();
        Duration duration = Duration.between(vehicle.entryTime, exitTime);

        double hours = Math.ceil((double) duration.toMinutes() / 60);
        double fee = hours * RATE_PER_HOUR;

        JOptionPane.showMessageDialog(frame, String.format("Vehicle removed. Total charge: PHP %.2f", fee));

        // Remove vehicle from table
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 1).equals(licensePlate)) {
                tableModel.removeRow(i);
                break;
            }
        }

        // Update visual representation
        updateParkingVisual();

        licenseField.setText("");
        brandField.setText("");
        modelField.setText("");
        refreshTableSlots();
        updateSummary(summaryLabel);
    }

    private void refreshTableSlots() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i + 1, i, 0);
        }
    }

    private void updateSummary(JLabel summaryLabel) {
        summaryLabel.setText("Available Slots: " + (MAX_SLOTS - parkingData.size()));
    }

    private void updateParkingVisual() {
        ImageIcon carIcon = new ImageIcon("car_icon.png"); // Provide the path to your car icon

        for (int i = 0; i < MAX_SLOTS; i++) {
            if (i < parkingData.size()) {
                Vehicle vehicle = (Vehicle) parkingData.values().toArray()[i];
                parkingSlots[i].setBackground(vehicle.color); // Set background to the car's color
                parkingSlots[i].setText("Occupied");
                parkingSlots[i].setIcon(carIcon);
            } else {
                parkingSlots[i].setBackground(Color.GREEN);
                parkingSlots[i].setText("Slot " + (i + 1));
                parkingSlots[i].setIcon(null);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VehicleParkingSystem::new);
    }
}
