/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package azoressalon_system;

/**
 *
 * @author ragha
 */


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CartFrame extends JFrame {

    static final Color BROWN = new Color(0x73, 0x4B, 0x3A);
    static final Color BEIGE = new Color(0xE9, 0xDF, 0xDA);
    static final Color GRAY_TEXT = new Color(0x99, 0x99, 0x99);
    static final Color RED = new Color(0xB0, 0x1E, 0x1E);

    static final Font TITLE = new Font("Serif", Font.BOLD, 30);
    static final Font BOLD = new Font("Serif", Font.BOLD, 16);
    static final Font NORMAL = new Font("Serif", Font.PLAIN, 15);
    static final Font SMALL = new Font("Serif", Font.PLAIN, 13);

    static class CartItem {
        String name, date, time;
        int price;

        CartItem(String name, String date, String time, int price) {
            this.name = name;
            this.date = date;
            this.time = time;
            this.price = price;
        }
    }

    private static final List<CartItem> cart = new ArrayList<>();

    private JPanel cartListPanel;
    private JLabel totalLabel;

    public CartFrame() {
        super("Azores Salon");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);

        add(buildCartScreen());
        refreshCart();
    }

    static class ListPanel extends JPanel implements Scrollable {

        ListPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
        }

        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
            return 16;
        }

        public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
            return 64;
        }

        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    public static boolean addToCart(String name, String date, String time, int price) {
        for (CartItem item : cart) {
            if (item.date.equals(date) && item.time.equals(time)) return false;
        }

        cart.add(new CartItem(name, date, time, price));
        return true;
    }

    private JPanel buildCartScreen() {
        JPanel shell = screenShell();

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(16, 26, 22, 26));

        JLabel title = new JLabel("Shopping Cart");
        title.setFont(TITLE);
        content.add(title, BorderLayout.NORTH);

        cartListPanel = new ListPanel();

        JScrollPane scroll = new JScrollPane(cartListPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        content.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);

        JSeparator separator = new JSeparator();
        separator.setForeground(BROWN);

        bottom.add(separator);
        bottom.add(Box.createVerticalStrut(8));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);

        JLabel totalText = new JLabel("Total");
        totalText.setFont(BOLD);

        totalLabel = new JLabel("0 SAR");
        totalLabel.setFont(BOLD);

        totalRow.add(totalText, BorderLayout.WEST);
        totalRow.add(totalLabel, BorderLayout.EAST);

        bottom.add(totalRow);
        bottom.add(Box.createVerticalStrut(14));

        JPanel buttons = new JPanel(new BorderLayout());
        buttons.setOpaque(false);

        JButton continueShopping = makeSecondaryButton("Continue Shopping");
        continueShopping.setPreferredSize(new Dimension(190, 38));

        continueShopping.addActionListener(e -> {
            new ServicesFrame().setVisible(true);
            dispose();
        });

        JButton checkout = makeMainButton("Checkout");
        checkout.addActionListener(e -> checkout());

        buttons.add(continueShopping, BorderLayout.WEST);
        buttons.add(checkout, BorderLayout.EAST);

        bottom.add(buttons);

        content.add(bottom, BorderLayout.SOUTH);
        shell.add(content, BorderLayout.CENTER);

        return shell;
    }

    private void refreshCart() {
        cartListPanel.removeAll();
        int total = 0;

        if (cart.isEmpty()) {
            JLabel empty = new JLabel("Your cart is empty");
            empty.setFont(NORMAL);
            empty.setForeground(GRAY_TEXT);
            cartListPanel.add(empty);
        }

        for (CartItem item : new ArrayList<>(cart)) {
            total += item.price;
            cartListPanel.add(buildRow(item));
            cartListPanel.add(Box.createVerticalStrut(12));
        }

        totalLabel.setText(total + " SAR");
        cartListPanel.revalidate();
        cartListPanel.repaint();
    }

    private JPanel buildRow(CartItem item) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new CompoundBorder(new LineBorder(BROWN, 1), new EmptyBorder(8, 10, 8, 10)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        left.add(label(item.name, BOLD, Color.BLACK));
        left.add(label(item.date + " - " + item.time, SMALL, GRAY_TEXT));

        row.add(left, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        right.setOpaque(false);

        JLabel price = new JLabel(item.price + " SAR");
        price.setFont(SMALL);

        JButton remove = new JButton("Remove");
        remove.setFont(NORMAL);
        remove.setForeground(RED);
        remove.setBackground(Color.WHITE);
        remove.setOpaque(true);
        remove.setFocusPainted(false);
        remove.setBorder(new CompoundBorder(new LineBorder(RED, 1), new EmptyBorder(6, 16, 6, 16)));
        remove.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        remove.addActionListener(e -> {
            cart.remove(item);
            refreshCart();
        });

        right.add(price);
        right.add(remove);

        row.add(right, BorderLayout.EAST);

        return row;
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty. Add a service first.");
            return;
        }

        new BookingConfirmationFrame().setVisible(true);
        dispose();
    }

    private JPanel screenShell() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BEIGE);
        shell.add(createHeader(), BorderLayout.NORTH);
        return shell;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(BROWN);
        header.setPreferredSize(new Dimension(10, 55));

        JLabel salonName = new JLabel("Azores Salon");
        salonName.setFont(new Font("Serif", Font.BOLD, 22));
        salonName.setForeground(Color.WHITE);

        header.add(salonName);
        return header;
    }

    private JButton makeMainButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BOLD);
        button.setBackground(BROWN);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(2, 6, 2, 6));
        button.setPreferredSize(new Dimension(170, 38));
        return button;
    }

    private JButton makeSecondaryButton(String text) {
        JButton button = makeMainButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(BROWN);
        button.setBorder(new LineBorder(BROWN, 2));
        button.setBorderPainted(true);
        return button;
    }

    private JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static List<CartItem> getCart() {
        return new ArrayList<>(cart);
    }

    public static int getTotal() {
        int total = 0;

        for (CartItem item : cart) {
            total += item.price;
        }

        return total;
    }

    public static void clearCart() {
        cart.clear();
    }
}