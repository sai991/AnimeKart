import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.gson.*;


public class ProductServlet extends HttpServlet {

    static Connection conn;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(
                "jdbc:h2:~/Desktop/myservers/databases/shoppingdb", 
                "sa", 
                ""
            );
            System.out.println("ProductServlet: Connection to DB successful.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try (PrintWriter out = resp.getWriter()) {
            String sql = "SELECT ID, NAME, PRICE, IMAGE_PATH FROM PRODUCTS";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            Gson gson = new Gson();
            JsonArray productsArray = new JsonArray();

            while (rs.next()) {
                JsonObject product = new JsonObject();
                product.addProperty("id", rs.getInt("ID"));
                product.addProperty("name", rs.getString("NAME"));
                product.addProperty("price", rs.getDouble("PRICE"));
                product.addProperty("imagePath", rs.getString("IMAGE_PATH"));
                productsArray.add(product);
            }

            out.print(gson.toJson(productsArray));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
