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
import java.math.BigDecimal;



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
    String cartId;


      String userId;
    String productId;
    String productName;
    String productPrice;

    String name;
    String price;
    String image;
}


public class AdminServlet extends HttpServlet {

    static Connection conn;
    static Statement statement;

    public AdminServlet() {
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
                case "adminLogin":
                    outputJson = handleLogin(requestData.username, requestData.password,req);
                    break;

                case "addProduct":
                    outputJson = addProduct(requestData.name,requestData.price,requestData.image);
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
            String sql = "SELECT ADMINID FROM ADMIN WHERE USERNAME = ? AND PASSWORD = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("ADMINID");

                // Store the USER_ID in HttpSession
                HttpSession session = req.getSession();
                session.setAttribute("ADMINID", userId);
                System.out.println(session.getAttribute("ADMINID"));
                return "{\"status\":\"success\",\"adminId\":\"" + rs.getInt("ADMINID") + "\"}";
            } else {
                return "{\"status\":\"failure\",\"message\":\"Invalid credentials\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"Internal server error\"}";
        }
    }

     private String addProduct(String name, String price, String imagePath) {
    try {
        // SQL query to insert a new product into the PRODUCTS table
        String sql = "INSERT INTO PRODUCTS (NAME, PRICE, IMAGE_PATH) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);

        // Setting values for the placeholders in the query
        ps.setString(1, name);
        ps.setString(2, price);
        ps.setString(3, imagePath);

        // Executing the query and checking the number of affected rows
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 1) {
            return "{\"status\":\"success\",\"message\":\"Product added successfully\"}";
        } else {
            return "{\"status\":\"failure\",\"message\":\"Failed to add product\"}";
        }
    } catch (Exception e) {
        e.printStackTrace();
        // Returning an error response if an exception occurs
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

    

private String fetchOrderHistory(int userId) {
    try {
        String sql = "SELECT ORDERID, ITEMNAME, PRICE, STATUS, TIMESTAMP FROM ORDERS WHERE USERID = ? ORDER BY TIMESTAMP DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();
        List<Map<String, String>> orders = new ArrayList<>();

        while (rs.next()) {
            Map<String, String> order = new HashMap<>();
            order.put("orderId", String.valueOf(rs.getInt("ORDERID")));
            order.put("itemName", rs.getString("ITEMNAME"));
            order.put("price", String.valueOf(rs.getDouble("PRICE")));
            order.put("status", rs.getString("STATUS"));
            order.put("timestamp", rs.getString("TIMESTAMP"));
            orders.add(order);
        }

        Gson gson = new Gson();
        return gson.toJson(orders);
    } catch (Exception e) {
        e.printStackTrace();
        return "{\"status\":\"error\",\"message\":\"Internal server error.\"}";
    }
}


private String placeOrder(int userId) {
    try {
        // Insert items from the CART table into the ORDERS table
        String insertOrderSql = "INSERT INTO ORDERS (USERID, ITEMNAME, PRICE, STATUS, TIMESTAMP) " +
                                "SELECT USERID, ITEMNAME, PRICE, 'Pending', NOW() FROM CART WHERE USERID = ?";
        PreparedStatement insertOrderPs = conn.prepareStatement(insertOrderSql);
        insertOrderPs.setInt(1, userId);

        int rowsInserted = insertOrderPs.executeUpdate();
        if (rowsInserted > 0) {
            // Clear the CART table after placing the order
            String clearCartSql = "DELETE FROM CART WHERE USERID = ?";
            PreparedStatement clearCartPs = conn.prepareStatement(clearCartSql);
            clearCartPs.setInt(1, userId);
            clearCartPs.executeUpdate();

            return "{\"status\":\"success\",\"message\":\"Order placed successfully.\"}";
        } else {
            return "{\"status\":\"failure\",\"message\":\"No items in the cart.\"}";
        }
    } catch (Exception e) {
        e.printStackTrace();
        return "{\"status\":\"error\",\"message\":\"Internal server error.\"}";
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
