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
                case "viewOrders":
                    outputJson = handleViewOrders(requestData.username);
                    break;
                case "fetchProfile":
                      HttpSession session = req.getSession(false); // Get the session, do not create a new one
                      if (session != null && session.getAttribute("USERID") != null) {
                     int userId = (int) session.getAttribute("USERID");
                    outputJson = fetchProfile(userId); // Pass the userId to the fetchProfile method
                          } else {
                    outputJson = "{\"status\":\"error\",\"message\":\"User not logged in\"}";
                      }
                     break;
                case "addToCart":
                    outputJson = addToCart(requestData.userId, requestData.productId, requestData.productName, requestData.productPrice);
                    break;

                case "fetchCart":
                    outputJson = fetchCart(requestData.userId);
                    break;

                case "deleteCartItem":
                    System.out.println("user id:"+ requestData.userId +" cartid:"+requestData.cartId);
                     int userId = Integer.parseInt(requestData.userId);
                     int cartId = Integer.parseInt(requestData.cartId);
                    outputJson = deleteCartItem(userId, cartId);
                     break;

                case "placeOrder":
                     userId = Integer.parseInt(requestData.userId);
                    outputJson = placeOrder(userId);
                    break;

                case "fetchOrderHistory":
                    userId = Integer.parseInt(requestData.userId);
                    outputJson = fetchOrderHistory(userId);
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
                System.out.println(session.getAttribute("USERID"));
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

    private String fetchProfile(int userId) {
    try {
        String sql = "SELECT FIRSTNAME, LASTNAME, EMAIL, PHONENUMBER, ADDRESS FROM USER WHERE USERID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1,userId);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Map<String, String> profile = new HashMap<>();
            profile.put("firstName", rs.getString("FIRSTNAME"));
            profile.put("lastName", rs.getString("LASTNAME"));
            profile.put("email", rs.getString("EMAIL"));
            profile.put("phoneNumber", rs.getString("PHONENUMBER"));
            profile.put("address", rs.getString("ADDRESS"));

            Gson gson = new Gson();
            return gson.toJson(Map.of("status", "success", "profile", profile));
        } else {
            return "{\"status\":\"failure\",\"message\":\"User not found\"}";
        }
    } catch (Exception e) {
        e.printStackTrace();
        return "{\"status\":\"error\",\"message\":\"Internal server error\"}";
    }
}
private String deleteCartItem(int userId, int cartId) {
    try {
        String sql = "DELETE FROM CART WHERE CARTID = ? AND USERID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, cartId);
        ps.setInt(2, userId);

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0) {
            return "{\"status\":\"success\"}";
        } else {
            return "{\"status\":\"failure\",\"message\":\"Item not found or not authorized\"}";
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





// Add this method to handle adding items to the cart
private String addToCart(String userId, String productId, String productName, String productPrice) {
    try {
        String sql = "INSERT INTO CART (USERID, ITEMNAME, PRICE) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, Integer.parseInt(userId));
        ps.setString(2, productName);
        ps.setBigDecimal(3, new BigDecimal(productPrice));
        ps.executeUpdate();

        // Fetch updated cart count
        sql = "SELECT COUNT(*) AS itemCount FROM CART WHERE USERID = ?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, Integer.parseInt(userId));
        ResultSet rs = ps.executeQuery();
        int itemCount = 0;
        if (rs.next()) {
            itemCount = rs.getInt("itemCount");
        }
        return "{\"status\":\"success\",\"cartItemCount\":" + itemCount + "}";
    } catch (Exception e) {
        e.printStackTrace();
        return "{\"status\":\"error\",\"message\":\"Failed to add to cart.\"}";
    }
}
private String fetchCart(String userId) {
    try {
        String sql = "SELECT CARTID, ITEMNAME, PRICE FROM CART WHERE USERID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, Integer.parseInt(userId));
        ResultSet rs = ps.executeQuery();

        List<Map<String, String>> items = new ArrayList<>();
        while (rs.next()) {
            Map<String, String> item = new HashMap<>();
            item.put("itemName", rs.getString("ITEMNAME"));
            item.put("price", rs.getString("PRICE"));
            item.put("cartId", rs.getString("CARTID"));
            items.add(item);
        }

        Gson gson = new Gson();
        return gson.toJson(Map.of("status", "success", "items", items));
    } catch (Exception e) {
        e.printStackTrace();
        return "{\"status\":\"error\",\"message\":\"Failed to fetch cart items.\"}";
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
