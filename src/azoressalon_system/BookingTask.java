/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package azoressalon_system;

/**
 *
 * @author ragha
 */

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;


// Represents a customer booking executed by a worker thread
public class BookingTask implements Callable<String> {
    
    // Protects booking ID generation and invoice file access
    private static final Object INVOICE_LOCK = new Object();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.ENGLISH);
    private final int customerId;
    private final String customerName;
    private final ArrayList<CartFrame.CartItem> items;
    private final int total;

    // Stores a warning if the invoice file cannot be saved
    private String invoiceWarning;

    // Initializes the customer information and booking details
    public BookingTask(int customerId, String customerName, List<CartFrame.CartItem> cartItems) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer must be logged in.");
        }

        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required.");
        }

        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Booking must contain at least one service.");
        }

        this.customerId = customerId;
        this.customerName = customerName;
        this.items = new ArrayList<>();

        int sum = 0;

        // Copies the cart items to prevent changes during booking
        for (CartFrame.CartItem item : cartItems) {
            items.add(new CartFrame.CartItem(item.name, item.date, item.time, item.price));
            sum += item.price;
        }

        this.total = sum;
    }

    // Returns the total booking price
    public int getTotal() {
        return total;
    }

    // Returns the invoice warning if file writing fails
    public String getInvoiceWarning() {
        return invoiceWarning;
    }

    // Executes the booking and returns its generated ID
    @Override
    public String call() throws SQLException, IOException {
        // CRITICAL SECTION: Prevents multiple threads from generating
        // duplicate booking IDs, conflicting appointments, or simultaneous invoice writes.
        synchronized (INVOICE_LOCK) {
            String bookingId;

            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                try {
                    checkAppointments(conn);
                    bookingId = nextBookingId(conn);
                    insertBooking(conn, bookingId);
                    insertBookingServices(conn, bookingId);
                    conn.commit();
                } catch (SQLException | IOException | RuntimeException ex) {
                    // Cancels database changes if an operation fails
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        ex.addSuppressed(rollbackEx);
                        System.err.println("Database rollback error: " + rollbackEx.getMessage());
                    }
                    throw ex;
                }
            }

            // Writes the invoice after the booking is saved successfully
            try {
                writeInvoice(bookingId);
            } catch (IOException ex) {
                // Keeps the confirmed booking if invoice writing fails
                invoiceWarning = "Your booking is confirmed, but the invoice file could not be saved.";

                System.err.println("Invoice file error: " + ex.getMessage());
            }

            System.out.println(Thread.currentThread().getName() + " completed booking " + bookingId);
            return bookingId;
        }
    }

    // Rejects an appointment already booked by the same customer
    private void checkAppointments(Connection conn) throws SQLException, IOException {
        String sql = "SELECT bs.booking_id FROM bookingservices bs JOIN bookings b ON bs.booking_id = b.booking_id WHERE b.customer_id = ? AND b.status = 'Confirmed' AND bs.service_date = ? AND bs.service_time = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CartFrame.CartItem item : items) {
                ps.setInt(1, customerId);
                ps.setDate(2, appointmentDate(item));
                ps.setString(3, item.time);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new IOException("You already have an appointment on " + item.date + " at " + item.time + ". Please select another time.");
                    }
                }
            }
        }
    }

    // Inserts the main booking information into the database
    private void insertBooking(Connection conn, String bookingId) throws SQLException {
        String sql = "INSERT INTO bookings (booking_id, customer_id, total_amount, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingId);
            ps.setInt(2, customerId);
            ps.setInt(3, total);
            ps.setString(4, "Confirmed");
            ps.executeUpdate();
        }
    }

    // Inserts each selected service into the bookingservices table
    private void insertBookingServices(Connection conn, String bookingId) throws SQLException {
        String sql = "INSERT INTO bookingservices (booking_id, service_name, service_date, service_time, price) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CartFrame.CartItem item : items) {
                ps.setString(1, bookingId);
                ps.setString(2, item.name);
                ps.setDate(3, appointmentDate(item));
                ps.setString(4, item.time);
                ps.setInt(5, item.price);
                ps.executeUpdate();
            }
        }
    }

    // Converts the appointment date to SQL format
    private java.sql.Date appointmentDate(CartFrame.CartItem item) {
        return java.sql.Date.valueOf(LocalDate.parse(item.date.trim(), DATE_FORMAT));
    }

    // Generates the next booking ID using existing records
    private String nextBookingId(Connection conn) throws SQLException, IOException {
        int lastId = 1000;

        String sql = "SELECT booking_id FROM bookings WHERE booking_id LIKE 'AZ-%'";

        // Reads existing booking IDs from the database

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lastId = Math.max(lastId, extractNumber(rs.getString("booking_id")));
            }
        }

        File file = new File("invoices.txt");

        // Checks previous invoice IDs if the file exists
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Booking ID: AZ-")) {
                        String value = line.substring("Booking ID: ".length()).trim();

                        lastId = Math.max(lastId, extractNumber(value));
                    }
                }
            }
        }
        return "AZ-" + (lastId + 1);
    }

    // Extracts the numeric part of a booking ID
    private int extractNumber(String bookingId) {
        if (bookingId == null || !bookingId.startsWith("AZ-")) {
            return 1000;
        }

        try {
            return Integer.parseInt(bookingId.substring("AZ-".length()));
        } catch (NumberFormatException ex) {
            // Reports and ignores invalid booking IDs
            System.err.println("Invalid booking ID: " + bookingId);
            return 1000;
        }
    }

    // Writes the confirmed booking to the invoice file
    private void writeInvoice(String bookingId) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("invoices.txt", true))) {
            writer.write("========== Azores Salon Invoice ==========");
            writer.newLine();
            writer.write("Booking ID: " + bookingId);
            writer.newLine();
            writer.write("Customer Name: " + customerName);
            writer.newLine();
            writer.write("------------------------------------------");
            writer.newLine();

            // Writes each service and its appointment details
            for (CartFrame.CartItem item : items) {
                writer.write("Service: " + item.name);
                writer.newLine();
                writer.write("Date: " + item.date);
                writer.newLine();
                writer.write("Time: " + item.time);
                writer.newLine();
                writer.write("Price: " + item.price + " SAR");
                writer.newLine();
                writer.write("------------------------------------------");
                writer.newLine();
            }
            writer.write("Total: " + total + " SAR");
            writer.newLine();
            writer.write("Status: Confirmed");
            writer.newLine();
            writer.write("==========================================");
            writer.newLine();
            writer.newLine();
        }
    }
}
