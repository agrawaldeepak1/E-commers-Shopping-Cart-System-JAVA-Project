package ECommerce;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private List<Product> products;
    private Cart cart;
    private DefaultListModel<String> productListModel;
    private DefaultListModel<String> cartListModel;

    public MainFrame(List<User> users) {
        setTitle("Cartify");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cart = new Cart(); // Initialize cart
        products = new ArrayList<>(); // Initialize product list
        loadProducts(); // Load product data

        productListModel = new DefaultListModel<>();
        cartListModel = new DefaultListModel<>();
        refreshProductList();

        setLayout(new BorderLayout());
        add(createProductPanel(), BorderLayout.WEST);
        add(createCartPanel(), BorderLayout.EAST);
        add(createBottomPanel(), BorderLayout.SOUTH);

        // Login Dialog
        LoginDialog loginDialog = new LoginDialog(this, users);
        loginDialog.setVisible(true);
        User loggedInUser = loginDialog.getLoggedInUser();

        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(this, "You must log in to continue.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } else {
            JOptionPane.showMessageDialog(this, "Welcome, " + loggedInUser.getUsername() + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
        }

        // Set the background color for the entire frame
        getContentPane().setBackground(new Color(255, 255, 255));
    }

    private void loadProducts() {
        products.add(new Product("P001", "Laptop", 74998));
        products.add(new Product("P002", "Smartphone", 36969));
        products.add(new Product("P003", "Headphones", 5699));
        products.add(new Product("P004", "Keyboard", 3929));
        products.add(new Product("P005", "Mouse", 1924));
        products.add(new Product("P006", "Monitor", 14979));
    }

    private void refreshProductList() {
        productListModel.clear();
        for (Product p : products) {
            productListModel.addElement(p.getId() + " - " + p.getName() + " - ₹" + p.getPrice());
        }
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Products"));
        panel.setBackground(new Color(255, 255, 255));

        JList<String> productList = new JList<>(productListModel);
        productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productList.setBackground(new Color(245, 245, 245));

        panel.add(new JScrollPane(productList), BorderLayout.CENTER);

        JButton addBtn = new JButton("Add to Cart");
        addBtn.setBackground(new Color(255, 153, 0));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);

        addBtn.addActionListener(e -> {
            int selectedIdx = productList.getSelectedIndex();
            if (selectedIdx != -1) {
                Product selectedProd = products.get(selectedIdx);
                cart.addProduct(selectedProd);
                cartListModel.addElement(selectedProd.getId() + " - " + selectedProd.getName());
                JOptionPane.showMessageDialog(this, selectedProd.getName() + " added to cart.");
            } else {
                JOptionPane.showMessageDialog(this, "Select a product first.");
            }
        });

        panel.add(addBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Your Cart"));
        panel.setBackground(new Color(255, 255, 255));

        JList<String> cartList = new JList<>(cartListModel);
        cartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartList.setBackground(new Color(245, 245, 245));

        panel.add(new JScrollPane(cartList), BorderLayout.CENTER);

        JButton removeBtn = new JButton("Remove");
        removeBtn.setBackground(new Color(255, 153, 0));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);

        removeBtn.addActionListener(e -> {
            int selectedIdx = cartList.getSelectedIndex();
            if (selectedIdx != -1) {
                Product selectedProd = cart.getProducts().get(selectedIdx);
                cart.removeProduct(selectedProd);
                cartListModel.remove(selectedIdx);
                JOptionPane.showMessageDialog(this, selectedProd.getName() + " removed.");
            } else {
                JOptionPane.showMessageDialog(this, "Pick something to remove!");
            }
        });

        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.setBackground(new Color(0, 123, 255));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFocusPainted(false);

        checkoutBtn.addActionListener(e -> {
            if (cart.getProducts().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty. Please add items to proceed.", "Empty Cart", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double total = cart.getTotal();
            String discountCode = JOptionPane.showInputDialog(this, "Enter Discount Code (if any)");
            if (discountCode != null && !discountCode.isEmpty()) {
                total = applyDiscount(total, discountCode);
            }

            JOptionPane.showMessageDialog(this, "Your total is ₹" + total);
            initiatePayment(total);
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(255, 255, 255));
        btnPanel.add(removeBtn);
        btnPanel.add(checkoutBtn);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255));

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(0, 123, 255));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> {
            String query = JOptionPane.showInputDialog(this, "Enter Product ID or Name");
            if (query != null && !query.isEmpty()) {
                List<Product> results = searchProduct(query);
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No products found.");
                } else {
                    StringBuilder resultStr = new StringBuilder("Results:\n");
                    for (Product p : results) {
                        resultStr.append(p.getId()).append(" - ").append(p.getName()).append(" - ₹").append(p.getPrice()).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, resultStr.toString());
                }
            }
        });

        JButton offersBtn = new JButton("Offers");
        offersBtn.setBackground(new Color(0, 123, 255));
        offersBtn.setForeground(Color.WHITE);
        offersBtn.setFocusPainted(false);
        offersBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Available Offers:\n" +
                    "- 15% off on Smartphones (Use code: SMARTPHONE15)\n" +
                    "- 10% off on total purchase (Use code: DISCOUNT10)\n" +
                    "- 20% off on total purchase (Use code: DISCOUNT20)");
        });

        panel.add(searchBtn);
        panel.add(offersBtn);
        return panel;
    }

    private List<Product> searchProduct(String query) {
        List<Product> results = new ArrayList<>();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(query.toLowerCase()) || p.getId().equalsIgnoreCase(query)) {
                results.add(p);
            }
        }
        return results;
    }

    private double applyDiscount(double total, String discountCode) {
        if (discountCode.equalsIgnoreCase("DISCOUNT10")) {
            total *= 0.90;
        } else if (discountCode.equalsIgnoreCase("DISCOUNT20")) {
            total *= 0.80;
        } else if (discountCode.equalsIgnoreCase("SMARTPHONE15")) {
            for (Product p : cart.getProducts()) {
                if (p.getName().equalsIgnoreCase("Smartphone")) {
                    total *= 0.85;
                    break;
                }
            }
        }
        return total;
    }

    private void initiatePayment(double amount) {
        int confirmation = JOptionPane.showConfirmDialog(this, "Proceed to payment of ₹" + amount + "?", "Payment Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Payment Successful! Thank you for shopping with us.");
            cart.clearCart();
            cartListModel.clear();
        } else {
            JOptionPane.showMessageDialog(this, "Payment Cancelled.");
        }
    }

    public static void main(String[] args) {
        List<User> users = new ArrayList<>();
        users.add(new User("admin", "admin123"));
        SwingUtilities.invokeLater(() -> new MainFrame(users).setVisible(true));
    }
}
