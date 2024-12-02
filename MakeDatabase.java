import java.sql.*;

public class MakeDatabase {

    static Statement statement;

    public static void main(String[] argv) {
        try {
            // Load the H2 driver and connect to the database
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/Desktop/myservers/databases/shoppingdb", "sa", "");

            statement = conn.createStatement();

            // Create the user table
            makeUserTable();

            // Create the order table
            makeOrderTable();

            makeCartTable();

            makeProductTable();

            // Print the user table to verify
            printTable("USER", 7);

            // Print the order table to verify
            printTable("ORDERS", 5);


  printTable("PRODUCTS", 3);

  printTable("CART", 4);

            // Close the connection
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void printTable(String tableName, int numColumns) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        ResultSet rs = statement.executeQuery(sql);

        System.out.println("\nRows from " + tableName + ":");
        while (rs.next()) {
            StringBuilder row = new StringBuilder("Row: ");
            for (int i = 1; i <= numColumns; i++) {
                row.append(rs.getString(i)).append(" ");
            }
            System.out.println(row);
        }
    }

    static void makeCartTable() throws SQLException {
    // Drop the table if it already exists
    String sql = "DROP TABLE IF EXISTS CART";
    statement.executeUpdate(sql);

    // Create the cart table with the required fields
    sql = "CREATE TABLE CART (" +
            "CARTID INT AUTO_INCREMENT PRIMARY KEY, " +
            "USERID INT, " +
            "ITEMNAME VARCHAR(100), " +
            "PRICE DECIMAL(10, 2))";
    statement.executeUpdate(sql);

    // Insert sample rows
    sql = "INSERT INTO CART (USERID, ITEMNAME, PRICE) VALUES " +
            "(1, 'Demon Slayer', 29.99)";
    statement.executeUpdate(sql);
}


    static void makeUserTable() throws SQLException {
        // Drop the table if it already exists
        String sql = "DROP TABLE IF EXISTS USER";
        statement.executeUpdate(sql);

        // Create the user table with the required fields
        sql = "CREATE TABLE USER (" +
                "USERID INT AUTO_INCREMENT PRIMARY KEY, " +
                "FIRSTNAME VARCHAR(50), " +
                "LASTNAME VARCHAR(50), " +
                "EMAIL VARCHAR(100), " +
                "PHONENUMBER VARCHAR(15), " +
                "ADDRESS VARCHAR(255), " +
                "PASSWORD VARCHAR(50))";
        statement.executeUpdate(sql);

        // Insert sample rows
        sql = "INSERT INTO USER (FIRSTNAME, LASTNAME, EMAIL, PHONENUMBER, ADDRESS, PASSWORD) VALUES " +
                "('John', 'Doe', 'john.doe@example.com', '1234567890', '123 Main St, Cityville', 'password123'), " +
                "('Jane', 'Smith', 'jane.smith@example.com', '9876543210', '456 Elm St, Townsville', 'password456')";
        statement.executeUpdate(sql);
    }

     static void makeProductTable() throws SQLException {
        // Drop the table if it already exists
        String sql = "DROP TABLE IF EXISTS PRODUCTS";
        statement.executeUpdate(sql);

        // Create the products table
        sql = "CREATE TABLE PRODUCTS (" +
              "ID INT AUTO_INCREMENT PRIMARY KEY, " +
              "NAME VARCHAR(100), " +
              "PRICE DECIMAL(10, 2), " +
              "IMAGE_PATH VARCHAR(255))";
        statement.executeUpdate(sql);

        // Insert sample data
        sql = "INSERT INTO PRODUCTS (NAME, PRICE, IMAGE_PATH) VALUES " +
              "('Demon Slayer ', 29.99, 'Images/demonslayer.jpeg'), " +
              "('Naruto Uzumaki ', 39.99, 'Images/naruto.jpeg'), " +
              "('Sasuke Uchiha ', 35.99, 'Images/sasuke.jpeg'), " +
              "('Goku ', 49.99, 'Images/goku.png'), " +
              "('Vegeta ', 45.99, 'Images/vegeta.jpeg'), " +
              "('One Piece Luffy', 39.99, 'Images/luffy.jpeg')";
        statement.executeUpdate(sql);

        System.out.println("Product table created and populated.");
    }

    static void makeOrderTable() throws SQLException {
    // Drop the table if it already exists
    String sql = "DROP TABLE IF EXISTS ORDERS";
    statement.executeUpdate(sql);

    // Create the order table with the required fields
    sql = "CREATE TABLE ORDERS (" +
            "ORDERID INT AUTO_INCREMENT PRIMARY KEY, " +
            "USERID INT, " +
            "ITEMNAME VARCHAR(100), " +
            "PRICE DECIMAL(10, 2), " + // Individual price of each item
            "STATUS VARCHAR(20), " + // Status of the order (e.g., Pending, Shipped)
            "TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP, " + // Time the order was placed
            "FOREIGN KEY (USERID) REFERENCES USER(USERID))";
    statement.executeUpdate(sql);

    // Insert sample rows
    sql = "INSERT INTO ORDERS (USERID, ITEMNAME, PRICE, STATUS, TIMESTAMP) VALUES " +
            "(1, 'Naruto Uzumaki Figure', 59.98, 'Shipped', '2024-11-29 10:00:00'), " +
            "(2, 'Sasuke Uchiha Figure', 79.98, 'Pending', '2024-11-30 12:00:00')";
    statement.executeUpdate(sql);
}

}
