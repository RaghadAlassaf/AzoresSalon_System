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
import java.util.*;

public class SalonClient {
   
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8189;
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 60000;

    // Sends a request and receives the server response.
    private static List<String> sendRequest(List<String> request) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, PORT), CONNECTION_TIMEOUT);

            socket.setSoTimeout(READ_TIMEOUT);

            try (Scanner input = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
                 PrintWriter output = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {
                for (String line : request) {
                    if (line == null || line.contains("\n") || line.contains("\r")) {
                        throw new IOException("Invalid request data.");
                    }
                    output.println(line);
                }

                if (output.checkError()) {
                    throw new IOException("Could not send the request to the server.");
                }
                String status = readLine(input);

                if (status.equals("ERROR")) {
                    throw new IOException(readLine(input));
                }
                if (!status.equals("OK")) {
                    throw new IOException("Invalid response received from the server.");
                }
                int size = parseNumber(readLine(input), "Invalid response size received from the server.");

                if (size < 0 || size > 10000) {
                    throw new IOException("Invalid number of response records.");
                }
                List<String> response = new ArrayList<>();

                for (int i = 0; i < size; i++) {
                    response.add(readLine(input));
                }
                return response;
            }
        } catch (ConnectException ex) {
            throw new IOException("Could not connect to the salon server. Make sure the server is running.", ex);
        } catch (SocketTimeoutException ex) {
            throw new IOException("The server took too long to respond. Please verify the request status.", ex);
        }
    }

    // Reads a line and detects connection failures.
    private static String readLine(Scanner input) throws IOException {
        if (!input.hasNextLine()) {
            if (input.ioException() != null) {
                throw input.ioException();
            }

            throw new IOException("The server disconnected before completing the response.");
        }

        return input.nextLine();
    }

    // Converts numeric response fields into integers.
    private static int parseNumber(String value, String message) throws IOException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IOException(message, ex);
        }
    }

    // Checks whether the server is available.
    public static String ping() throws IOException {
        List<String> response = sendRequest(Arrays.asList("PING"));

        if (response.size() != 1) {
            throw new IOException("Invalid connection test response.");
        }

        return response.get(0);
    }

    // Sends a customer registration request to the server.
    public static void signUp(String name, String mobile, String password) throws IOException {
        if (sendRequest(Arrays.asList("SIGNUP", name, mobile, password)).size() != 1) {
            throw new IOException("Invalid registration response.");
        }
    }

    // Sends a login request and retrieves customer information.
    public static List<String> login(String mobile, String password) throws IOException {
        List<String> response = sendRequest(Arrays.asList("LOGIN", mobile, password));

        if (response.isEmpty()) {
            return response;
        }
        if (response.size() != 3) {
            throw new IOException("Invalid login response received from the server.");
        }

        if (parseNumber(response.get(0), "Invalid customer ID received from the server.") <= 0) {
            throw new IOException("Invalid customer ID received from the server.");
        }

        if (response.get(1).isBlank() || response.get(2).isBlank()) {
            throw new IOException("Incomplete customer information received from the server.");
        }

        return response;
    }

    // Retrieves services from the selected category.
    public static List<String> getServices(String category) throws IOException {
        List<String> response = sendRequest(Arrays.asList("SERVICES", category));

        if (response.size() % 4 != 0) {
            throw new IOException("Invalid service information received from the server.");
        }

        return response;
    }

    // Sends a booking request and retrieves confirmation details.
    public static List<String> book(String token, List<CartFrame.CartItem> items) throws IOException {
        if (token == null || token.isBlank()) {
            throw new IOException("Please log in before confirming your booking.");
        }

        if (items == null || items.isEmpty()) {
            throw new IOException("Your cart is empty.");
        }

        if (items.size() > 40) {
            throw new IOException("A booking cannot contain more than 40 services.");
        }

        List<String> request = new ArrayList<>();

        request.add("BOOK");
        request.add(token);
        request.add(String.valueOf(items.size()));

        for (CartFrame.CartItem item : items) {
            request.add(item.name);
            request.add(item.date);
            request.add(item.time);
        }

        List<String> response = sendRequest(request);

        if (response.size() != 4 + items.size() || response.get(0).trim().isEmpty() || !response.get(1).equals("Confirmed")) {
            throw new IOException("Invalid booking response. Check My Bookings before trying again.");
        }

        try {
            int total = Integer.parseInt(response.get(2));
            long sum = 0;

            for (int i = 4; i < response.size(); i++) {
                int price = Integer.parseInt(response.get(i));
                if (price < 0) {
                    throw new IOException("Invalid service price. Check My Bookings before trying again.");
                }
                sum += price;
            }

            if (total < 0 || total != sum) {
                throw new IOException("Booking total does not match service prices. Check My Bookings before trying again.");
            }
        } catch (NumberFormatException ex) {
            throw new IOException("Invalid booking price. Check My Bookings before trying again.", ex);
        }

        return response;
    }

    // Retrieves the authenticated customer's booking history.
    public static List<String> getMyBookings(String token) throws IOException {
        if (token == null || token.isBlank()) {
            throw new IOException("Please log in to view your bookings.");
        }
        List<String> response = sendRequest(Arrays.asList("MY_BOOKINGS", token));

        if (response.size() % 8 != 0) {
            throw new IOException("Invalid booking history received from the server.");
        }
        return response;
    }

    // Sends a logout request to end the customer session.
    public static void logout(String token) throws IOException {
        if (token == null || token.isBlank()) {
            return;
        }

        if (sendRequest(Arrays.asList("LOGOUT", token)).size() != 1) {
            throw new IOException("Invalid logout response.");
        }
    }
}

