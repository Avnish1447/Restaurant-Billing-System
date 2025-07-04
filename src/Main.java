import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Main {
    private JFrame frame;
    private JTable menuTable;
    private JTextField customerNameField;
    private JTextField totalPriceField;
    private DefaultTableModel model;
    private Connection conn;

    public Main() {
        // Set up the main frame
        frame = new JFrame("Restaurant Billing System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Customer name input panel with center alignment
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        customerPanel.add(new JLabel("Customer Name:"));
        customerNameField = new JTextField(15);
        customerNameField.setHorizontalAlignment(SwingConstants.CENTER);
        customerPanel.add(customerNameField);
        frame.add(customerPanel, BorderLayout.NORTH);


        String[] columnNames = {"Item ID", "Item Name", "Price", "Quantity"};
        model = new DefaultTableModel(columnNames, 0);
        menuTable = new JTable(model) {
            @Override
            public Class<?> getColumnClass(int column) {

                if (column == 3) {
                    return Integer.class;
                }
                return super.getColumnClass(column);
            }
        };
        menuTable.setDefaultEditor(Object.class, null);
        menuTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()));
        JScrollPane scrollPane = new JScrollPane(menuTable);
        frame.add(scrollPane, BorderLayout.CENTER);


        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); 
        totalPanel.add(new JLabel("Total Price:"));
        totalPriceField = new JTextField(10);
        totalPriceField.setEditable(false);
        totalPriceField.setHorizontalAlignment(SwingConstants.CENTER);
        totalPanel.add(totalPriceField);

        // Add calculate and save buttons
        JButton calculateButton = new JButton("Calculate Total");
        calculateButton.setHorizontalAlignment(SwingConstants.CENTER); 
        calculateButton.addActionListener(e -> calculateTotal());
        totalPanel.add(calculateButton);

        JButton saveButton = new JButton("Save Order");
        saveButton.setHorizontalAlignment(SwingConstants.CENTER); // Center-align text in the button
        saveButton.addActionListener(e -> saveOrder());
        totalPanel.add(saveButton);

        frame.add(totalPanel, BorderLayout.SOUTH);

        // Connect to MySQL database
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurant_billing", "root", "Password");
            System.out.println("Database connected successfully.");
            loadMenu();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMenu() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM menu");
            while (rs.next()) {
                String itemId = rs.getString("item_id");
                String itemName = rs.getString("item_name");
                String price = rs.getString("price");
                model.addRow(new Object[]{itemId, itemName, price, 0}); // Initializing quantity as 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void calculateTotal() {
        double total = 0.0;
        for (int i = 0; i < model.getRowCount(); i++) {
            int quantity = Integer.parseInt(model.getValueAt(i, 3).toString()); // Get quantity from the table
            if (quantity > 0) {
                double price = Double.parseDouble(model.getValueAt(i, 2).toString()); // Get price from the table
                total += price * quantity;
            }
        }
        totalPriceField.setText(String.format("%.2f", total));
    }

    private void saveOrder() {
        String customerName = customerNameField.getText().trim();
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a customer name.");
            return;
        }
        double total;
        try {
            total = Double.parseDouble(totalPriceField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid total price.");
            return;
        }
        try {
            conn.setAutoCommit(false); // Start transaction
            try (PreparedStatement psOrder = conn.prepareStatement(
                    "INSERT INTO orders (customer_name, total_price) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, customerName);
                psOrder.setDouble(2, total);
                psOrder.executeUpdate();
                ResultSet generatedKeys = psOrder.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    try (PreparedStatement psItem = conn.prepareStatement(
                            "INSERT INTO order_items (order_id, item_id, quantity, price) VALUES (?, ?, ?, ?)")) {
                        for (int i = 0; i < model.getRowCount(); i++) {
                            int quantity = Integer.parseInt(model.getValueAt(i, 3).toString());
                            if (quantity > 0) {
                                int itemId = Integer.parseInt(model.getValueAt(i, 0).toString());
                                double price = Double.parseDouble(model.getValueAt(i, 2).toString());
                                psItem.setInt(1, orderId);
                                psItem.setInt(2, itemId);
                                psItem.setInt(3, quantity);
                                psItem.setDouble(4, price);
                                psItem.addBatch();
                            }
                        }
                        psItem.executeBatch();
                    }
                }
                conn.commit(); // Commit transaction
                JOptionPane.showMessageDialog(frame, "Order saved successfully!");
            } catch (SQLException ex) {
                conn.rollback(); // Rollback on error
                throw ex;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving order: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Create and show the GUI
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.frame.setVisible(true);
        });
    }
}

