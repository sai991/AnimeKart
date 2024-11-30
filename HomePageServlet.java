import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.*;

// Data structure for JSON parsing
class RequestData {
    String username;
    String password;
    String action;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    String address;
    String orderDetails;
}


public class HomePageServlet extends HttpServlet {

    static Connection conn;
    static Statement statement;

    public HomePageServlet() {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:h2:~/Desktop/myservers/databases/shoppingdb",
                    "sa",
                    ""
            );
            statement = conn.createStatement();
            System.out.println("ShoppingServlet: Successful connection to H2 database");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("ShoppingServlet: doPost()");
        handleRequest(req, resp);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("ShoppingServlet: doGet()");
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Read and parse the incoming JSON request
            StringBuilder sbuf = new StringBuilder();
            BufferedReader bufReader = req.getReader();
            String inputStr;

            while ((inputStr = bufReader.readLine()) != null) {
                sbuf.append(inputStr);
            }

            String jsonInput = sbuf.toString();
            System.out.println("Received: " + jsonInput);

            Gson gson = new Gson();
            RequestData requestData = gson.fromJson(jsonInput, RequestData.class);

            String action = requestData.action;

            // Prepare response
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter writer = resp.getWriter();

            String outputJson = "";

            switch (action) {
                case "login":
                    outputJson = handleLogin(requestData.username, requestData.password,req);
                    break;
                case "signup":
                    outputJson = handleSignup(requestData);
                    break;
                case "placeOrder":
                    outputJson = handlePlaceOrder(requestData.username, requestData.orderDetails);
                    break;
                case "viewOrders":
                    outputJson = handleViewOrders(requestData.username);
                    break;
                default:
                    outputJson = "{\"status\":\"error\",\"message\":\"Invalid action\"}";
            }

            writer.write(outputJson);
            writer.flush();

            System.out.println("Response sent: " + outputJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String handleLogin(String username, String password,HttpServletRequest req) {
        try {
            String sql = "SELECT USERID FROM USER WHERE EMAIL = ? AND PASSWORD = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("USERID");

                // Store the USER_ID in HttpSession
                HttpSession session = req.getSession();
                session.setAttribute("USERID", userId);
                return "{\"status\":\"success\",\"userId\":\"" + rs.getInt("USERID") + "\"}";
            } else {
                return "{\"status\":\"failure\",\"message\":\"Invalid credentials\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"Internal server error\"}";
        }
    }

    private String handleSignup(RequestData requestData) {
        try {
            String sql = "INSERT INTO USER (FIRSTNAME, LASTNAME, EMAIL, PHONENUMBER, ADDRESS, PASSWORD) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, requestData.firstName);
            ps.setString(2, requestData.lastName);
            ps.setString(3, requestData.email);
            ps.setString(4, requestData.phoneNumber);
            ps.setString(5, requestData.address);
            ps.setString(6, requestData.password);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                return "{\"status\":\"success\",\"message\":\"User registered successfully\"}";
            } else {
                return "{\"status\":\"failure\",\"message\":\"Failed to register user\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"Internal server error\"}";
        }
    }

    private String handlePlaceOrder(String username, String orderDetails) {
        try {
            String sql = "INSERT INTO ORDERS (USERID, ORDER_DETAILS, STATUS) " +
                    "SELECT USERID, ?, 'Pending' FROM USERS WHERE USERNAME = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, orderDetails);
            ps.setString(2, username);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                return "{\"status\":\"success\",\"message\":\"Order placed successfully\"}";
            } else {
                return "{\"status\":\"failure\",\"message\":\"Failed to place order\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"Internal server error\"}";
        }
    }

    private String handleViewOrders(String username) {
        try {
            String sql = "SELECT O.ORDER_ID, O.ORDER_DETAILS, O.STATUS " +
                    "FROM ORDERS O JOIN USERS U ON O.USER_ID = U.USER_ID " +
                    "WHERE U.USERNAME = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            List<Map<String, String>> orders = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> order = new HashMap<>();
                order.put("orderId", String.valueOf(rs.getInt("ORDER_ID")));
                order.put("orderDetails", rs.getString("ORDER_DETAILS"));
                order.put("status", rs.getString("STATUS"));
                orders.add(order);
            }

            Gson gson = new Gson();
            return gson.toJson(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"Internal server error\"}";
        }
    }
}
