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

    String orderId;
    String status;
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
               
                case "updateOrderStatus":
                    outputJson = updateOrderStatus(requestData.orderId,requestData.status);
                    break;

                case "fetchOrders":
                    outputJson = fetchOrders(); 
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
        ps.setString(3, "Images/"+imagePath);

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



    private String updateOrderStatus(String orderId, String status) {
        try {
            String sql = "UPDATE ORDERS SET STATUS = ? WHERE ORDERID=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, status);
            ps.setString(2, orderId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                return "{\"status\":\"success\",\"message\":\"Delivery status updated successfully\"}";
            } else {
                return "{\"status\":\"failure\",\"message\":\"Failed to place order\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"Internal server error\"}";
        }
    }

    

private String fetchOrders() {
    try {
        // Query to fetch all orders
        String sql = "SELECT ORDERID, USERID, ITEMNAME, PRICE, STATUS, TIMESTAMP FROM ORDERS";
        PreparedStatement ps = conn.prepareStatement(sql);

        // Execute the query and process the results
        ResultSet rs = ps.executeQuery();
        StringBuilder jsonResponse = new StringBuilder();
        jsonResponse.append("{\"status\":\"success\",\"orders\":[");

        boolean isFirst = true;
        while (rs.next()) {
            if (!isFirst) {
                jsonResponse.append(",");
            } else {
                isFirst = false;
            }

            // Build each order JSON object
            jsonResponse.append("{")
                        .append("\"orderId\":").append(rs.getInt("ORDERID")).append(",")
                        .append("\"userId\":\"").append(rs.getString("USERID")).append("\",")
                        .append("\"itemName\":\"").append(rs.getString("ITEMNAME")).append("\",")
                        .append("\"price\":").append(rs.getDouble("PRICE")).append(",")
                        .append("\"status\":\"").append(rs.getString("STATUS")).append("\",");

            Timestamp timestamp = rs.getTimestamp("TIMESTAMP");
            String timestampStr = timestamp != null ? timestamp.toString() : "";
            jsonResponse.append("\"timestamp\":\"").append(timestampStr).append("\"}");

        }
        jsonResponse.append("]}");

        // Debug: Log the generated JSON
        System.out.println("JSON Response: " + jsonResponse.toString());

        return jsonResponse.toString();

    } catch (Exception e) {
        e.printStackTrace();
        return "{\"status\":\"error\",\"message\":\"Internal server error.\"}";
    }
}

}
