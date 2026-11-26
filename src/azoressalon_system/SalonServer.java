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
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Handles client requests using the client/server model. The server manages
 * database operations, service files, customer accounts, and booking tasks.
 */
public class SalonServer {
    
    private static final int PORT = 8189;

    // Creates three reusable worker threads for booking tasks
    private static final ExecutorService POOL = Executors.newFixedThreadPool(3);

    // Stores authenticated customer sessions
    private static final Map<String, Integer> sessions = new HashMap<>();

    // Protects shared session information from concurrent access
    private static final Object SESSION_LOCK = new Object();

    // Starts the server and waits for client connections
    public static void main(String[] args) {
        // Shuts down the booking thread pool when the server stops
        Runtime.getRuntime().addShutdownHook(new Thread(SalonServer::shutdownPool));

        // Creates a server socket that accepts localhost connections only
        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName("127.0.0.1"))) {
            System.out.println("Azores Salon Server started successfully.");
            System.out.println("Waiting for clients on localhost:" + PORT);
            while (true) {
                Socket client = server.accept();

                System.out.println("Client connected: " + client.getRemoteSocketAddress());

                // Creates a separate thread to handle each client request
                Thread worker = new Thread(() -> handleClient(client));
                worker.start();
            }
        } catch (IOException ex) {
            System.err.println("Server error: " + ex.getMessage());
        } finally {
            // Releases the booking pool if the server loop ends.
            shutdownPool();
        }
    }

    // Releases the booking worker threads when the server stops
    private static void shutdownPool() {
        POOL.shutdown();
        try {
            if (!POOL.awaitTermination(5, TimeUnit.SECONDS)) {
                POOL.shutdownNow();
            }
        } catch (InterruptedException ex) {
            POOL.shutdownNow();
            Thread.currentThread().interrupt();
            System.err.println("Thread pool shutdown was interrupted.");
        }
    }

    // Receives and processes requests from a connected client
    private static void handleClient(Socket client) {
        // Automatically closes the socket and its input/output streams
        try (Socket socket = client; Scanner input = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8); PrintWriter output = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {
            // Prevents the server from waiting indefinitely for client data
            socket.setSoTimeout(30000);
            try {
                String command = readLine(input);
                List<String> response;

                switch (command) {
                    case "PING":
                        response = new ArrayList<>();
                        response.add("Azores Salon Server is connected.");
                        break;

                    case "SIGNUP":
                        response = signUp(readLine(input), readLine(input), readLine(input));
                        break;

                    case "LOGIN":
                        response = login(readLine(input), readLine(input));
                        break;

                    case "SERVICES":
                        response = getServices(readLine(input));
                        break;

                    case "BOOK":
                        response = book(input);
                        break;

                    case "MY_BOOKINGS":
                        response = getMyBookings(readLine(input));
                        break;

                    case "LOGOUT":
                        response = logout(readLine(input));
                        break;

                    default:
                        throw new IOException("Invalid request received from client.");
                }

                // Sends a successful response and its data to the client
                output.println("OK");
                output.println(response.size());
                for (String line : response) {
                    output.println(line);
                }
                if (output.checkError()) {
                    System.err.println("Could not send the response to the client.");
                }
            } catch (SQLException ex) {
                sendError(output, "A database error occurred. Please try again.");
                System.err.println("Database error: " + ex.getMessage());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                sendError(output, "The request was interrupted. Please verify its status.");
            } catch (ExecutionException ex) {
                // Display a descriptive message for duplicate appointments
                Throwable cause = ex.getCause();
                if (cause instanceof IOException && cause.getMessage() != null && cause.getMessage().startsWith("You already have an appointment")) {
                    sendError(output, cause.getMessage());
                } else {
                    sendError(output, "Could not complete the booking. Please verify its status before trying again.");
                }

                System.err.println("Booking task error: " + cause);
            } catch (IOException | IllegalArgumentException ex) {
                sendError(output, ex.getMessage());
                System.err.println("Request error: " + ex.getMessage());
            }
        } catch (IOException ex) {
            System.err.println("Client connection error: " + ex.getMessage());
        }
    }

    // Reads one line of data sent by the client
    private static String readLine(Scanner input) throws IOException {
        if (!input.hasNextLine()) {
            if (input.ioException() != null) {
                throw new IOException("An error occurred while receiving client data.", input.ioException());
            }
            throw new IOException("Incomplete request or disconnected client.");
        }
        return input.nextLine();
    }

    // Sends a descriptive error response to the client
    private static void sendError(PrintWriter output, String message) {
        if (message == null) {
            message = "An unexpected error occurred.";
        }

        output.println("ERROR");
        output.println(message.replace('\n', ' ').replace('\r', ' '));
        if (output.checkError()) {
            System.err.println("Could not send the error response to the client.");
        }
    }

    // Registers a new customer in the database
    private static List<String> signUp(String name, String mobile, String password) throws SQLException, IOException {
        name = name.trim();

        // Validates customer information on the server
        if (name.isEmpty() || name.length() > 100) {
            throw new IOException("Please enter a valid full name.");
        }
        if (!mobile.matches("05\\d{8}")) {
            throw new IOException("Mobile number must be 10 digits and start with 05.");
        }
        if (password.length() < 6 || password.length() > 255) {
            throw new IOException("Password must contain between 6 and 255 characters.");
        }

        String sql = "INSERT INTO customers (full_name, phone_number, password) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, mobile);
            ps.setString(3, password);
            // The UNIQUE phone constraint also prevents concurrent duplicate registrations.
            ps.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                throw new IOException("This mobile number is already registered.");
            }
            throw ex;
        }

        System.out.println("New customer registered successfully.");

        List<String> response = new ArrayList<>();
        response.add("Account created successfully.");
        return response;
    }

    // Verifies customer credentials and creates a login session
    private static List<String> login(String mobile, String password) throws SQLException {
        String sql = "SELECT customer_id, full_name, password FROM customers WHERE phone_number = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mobile);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && password.equals(rs.getString("password"))) {
                    int customerId = rs.getInt("customer_id");
                    String customerName = rs.getString("full_name");

                    // Generates a unique session token for the authenticated customer
                    String token = UUID.randomUUID().toString();

                    // CRITICAL SECTION: Protects the shared session map
                    synchronized (SESSION_LOCK) {
                        sessions.put(token, customerId);
                    }

                    List<String> response = new ArrayList<>();

                    response.add(String.valueOf(customerId));
                    response.add(customerName);
                    response.add(token);

                    System.out.println("Customer logged in: " + customerId);
                    return response;
                }
            }
        }
        return new ArrayList<>();
    }

    // Retrieves the customer ID associated with a valid session
    private static int getSessionCustomer(String token) throws IOException {
        Integer customerId;

        // CRITICAL SECTION: Protects access to shared session information
        synchronized (SESSION_LOCK) {
            customerId = sessions.get(token);
        }
        if (customerId == null) {
            throw new IOException("Your session is invalid. Please log in again.");
        }
        return customerId;
    }

    // Retrieves booking history for the authenticated customer only
    private static List<String> getMyBookings(String token) throws IOException, SQLException {
        int customerId = getSessionCustomer(token);
        List<String> response = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.created_at, b.status, b.total_amount, s.service_name, s.service_date, s.service_time, s.price FROM bookings b JOIN bookingservices s ON b.booking_id = s.booking_id WHERE b.customer_id = ? ORDER BY b.created_at DESC, b.booking_id DESC, s.booking_service_id ASC";

        // Automatically closes database resources after retrieving the records
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Keeps the response within the client protocol limit
                    if (response.size() + 8 > 10000) {
                        throw new IOException("Booking history exceeds the response limit.");
                    }
                    response.add(rs.getString("booking_id"));
                    response.add(rs.getString("created_at"));
                    response.add(rs.getString("status"));
                    response.add(rs.getBigDecimal("total_amount").stripTrailingZeros().toPlainString());
                    response.add(rs.getString("service_name"));
                    response.add(rs.getString("service_date"));
                    response.add(rs.getString("service_time"));
                    response.add(rs.getBigDecimal("price").stripTrailingZeros().toPlainString());
                }
            }
        }
        return response;
    }

    // Ends the customer's login session
    private static List<String> logout(String token) {
       Integer customerId;

       // CRITICAL SECTION: Protects session removal from concurrent access
       synchronized (SESSION_LOCK) {
       customerId = sessions.remove(token);
     }

       if (customerId != null) {
        System.out.println("Customer logged out: " + customerId);
    }

       List<String> response = new ArrayList<>();
       response.add("Logged out successfully.");
       return response;
}

    // Reads services from the server-side services file
    private static List<String> getServices(String category) throws IOException {
        List<String> services = new ArrayList<>();

        // Automatically closes the file reader
        try (BufferedReader reader = new BufferedReader(new FileReader("services.txt"))) {
            String line;
            boolean selected = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // Identifies the selected service category
                if (line.startsWith("[") && line.endsWith("]")) {
                    selected = line.substring(1, line.length() - 1).equalsIgnoreCase(category);
                } else if (selected) {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length != 4) {
                        throw new IOException("Invalid service information in services.txt.");
                    }

                    // Validates the service price and duration
                    try {
                        int price = Integer.parseInt(parts[2].trim());
                        int duration = Integer.parseInt(parts[3].trim());
                        if (price < 0 || duration <= 0) {
                            throw new IOException("Invalid service price or duration.");
                        }
                    } catch (NumberFormatException ex) {
                        throw new IOException("Invalid numeric value in services.txt.", ex);
                    }
                    for (String part : parts) {
                        services.add(part.trim());
                    }
                }
            }
        }
        return services;
    }

    // Retrieves the actual service price from the server-side file
    private static int getServicePrice(String serviceName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("services.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length == 4 && parts[0].trim().equalsIgnoreCase(serviceName)) {
                    try {
                        int price = Integer.parseInt(parts[2].trim());
                        if (price < 0) {
                            throw new IOException("Invalid service price.");
                        }
                        return price;
                    } catch (NumberFormatException ex) {
                        throw new IOException("Invalid service price in services.txt.", ex);
                    }
                }
            }
        }
        throw new IOException("The selected service was not found.");
    }

    // Validates appointment dates and times on the server
    private static void validateAppointment(String date, String time) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.ENGLISH);
        try {
            LocalDate appointmentDate = LocalDate.parse(date, formatter);
            LocalDate today = LocalDate.now();
            if (!date.equals(appointmentDate.format(formatter)) || !appointmentDate.isAfter(today) || appointmentDate.isAfter(today.plusDays(5))) {
                throw new IOException("Please select an available appointment date.");
            }
        } catch (DateTimeParseException ex) {
            throw new IOException("Invalid appointment date.", ex);
        }

        String[] availableTimes = {"10:00 AM", "12:00 PM", "2:00 PM", "4:00 PM", "5:00 PM", "6:00 PM"};
        for (String availableTime : availableTimes) {
            if (availableTime.equals(time)) {
                return;
            }
        }
        throw new IOException("Please select a valid appointment time.");
    }

    // Receives booking details and submits a booking task
    private static List<String> book(Scanner input) throws IOException, SQLException, InterruptedException, ExecutionException {
        // Identifies the authenticated customer using the session token
        String token = readLine(input);
        int customerId = getSessionCustomer(token);

        int size;
        try {
            size = Integer.parseInt(readLine(input));
        } catch (NumberFormatException ex) {
            throw new IOException("Invalid number of booking services.", ex);
        }
        if (size <= 0 || size > 40) {
            throw new IOException("Invalid number of services in the cart.");
        }

        List<CartFrame.CartItem> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String service = readLine(input);
            String date = readLine(input);
            String time = readLine(input);
            if (service.isBlank() || date.isBlank() || time.isBlank()) {
                throw new IOException("Incomplete booking information.");
            }

            validateAppointment(date, time);

            // Prevents duplicate appointment slots within the same booking
            for (CartFrame.CartItem item : items) {
                if (item.date.equals(date) && item.time.equals(time)) {
                    throw new IOException("Two services cannot have the same appointment date and time.");
                }
            }

            // Uses the server-side price instead of trusting client prices
            int price = getServicePrice(service);

            items.add(new CartFrame.CartItem(service, date, time, price));
        }

        String customerName;

        String sql = "SELECT full_name FROM customers WHERE customer_id = ?";

        // Retrieves the customer's name from the database
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IOException("Customer account was not found.");
                }

                customerName = rs.getString("full_name");
            }
        }

        BookingTask task = new BookingTask(customerId, customerName, items);

        // Executes the booking using the existing thread pool
        // BookingTask protects booking IDs and invoice writing using synchronization
        Future<String> future = POOL.submit(task);
        String bookingId = future.get();

        List<String> response = new ArrayList<>();

        response.add(bookingId);
        response.add("Confirmed");
        response.add(String.valueOf(task.getTotal()));

        // Returns a warning if the invoice file could not be saved
        response.add(task.getInvoiceWarning() == null ? "" : task.getInvoiceWarning());

        // Returns the confirmed price of each service in request order
        for (CartFrame.CartItem item : items) {
            response.add(String.valueOf(item.price));
        }

        System.out.println("Booking completed: " + bookingId);
        return response;
    }
}
