package furniture_app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.awt.Color;
import javax.swing.JOptionPane;

public class DBConnection {
    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "furniture_db";
    private static final String USER = "root"; // Update this if your mysql username is different
    private static final String PASS = "anu1245A@";     // Update this if your mysql password is different

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // First, connect to MySQL server without database to create the database if it doesn't exist
            Connection tempConn = DriverManager.getConnection(URL, USER, PASS);
            Statement stmt = tempConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            stmt.close();
            tempConn.close();

            // Connect to the specific database
            conn = DriverManager.getConnection(URL + DB_NAME, USER, PASS);
            createTables(conn);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database Connection Error: " + e.getMessage() + "\nMake sure XAMPP/MySQL is running!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return conn;
    }

    private static void createTables(Connection conn) throws SQLException {
        String createDesignsTable = "CREATE TABLE IF NOT EXISTS designs ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) NOT NULL, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";
                
        String createItemsTable = "CREATE TABLE IF NOT EXISTS furniture_items ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "design_id INT, "
                + "type VARCHAR(100), "
                + "x INT, "
                + "y INT, "
                + "width INT, "
                + "height INT, "
                + "color_rgb INT, "
                + "rotation DOUBLE, "
                + "FOREIGN KEY (design_id) REFERENCES designs(id) ON DELETE CASCADE"
                + ")";

        Statement stmt = conn.createStatement();
        stmt.execute(createDesignsTable);
        stmt.execute(createItemsTable);
        stmt.close();
    }

    public static void saveDesignToDatabase(String designName, ArrayList<FurnitureItem> items) {
        Connection conn = getConnection();
        if (conn == null) return;

        try {
            conn.setAutoCommit(false);
            
            // Check if design already exists, if so delete it (or overwrite)
            String delSql = "DELETE FROM designs WHERE name = ?";
            PreparedStatement delStmt = conn.prepareStatement(delSql);
            delStmt.setString(1, designName);
            delStmt.executeUpdate();
            
            // Insert Design
            String insertDesign = "INSERT INTO designs (name) VALUES (?)";
            PreparedStatement pstmt = conn.prepareStatement(insertDesign, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, designName);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            int designId = -1;
            if (rs.next()) {
                designId = rs.getInt(1);
            }

            // Insert Items
            String insertItem = "INSERT INTO furniture_items (design_id, type, x, y, width, height, color_rgb, rotation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(insertItem);
            
            for (FurnitureItem item : items) {
                itemStmt.setInt(1, designId);
                itemStmt.setString(2, item.type);
                itemStmt.setInt(3, item.x);
                itemStmt.setInt(4, item.y);
                itemStmt.setInt(5, item.width);
                itemStmt.setInt(6, item.height);
                itemStmt.setInt(7, item.color.getRGB());
                itemStmt.setDouble(8, item.rotation);
                itemStmt.addBatch();
            }
            
            itemStmt.executeBatch();
            conn.commit();
            
            JOptionPane.showMessageDialog(null, "Design Saved to Database Successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            JOptionPane.showMessageDialog(null, "Error Saving to DB: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
    }

    public static ArrayList<FurnitureItem> loadDesignFromDatabase(String designName) {
        ArrayList<FurnitureItem> items = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) return items;

        try {
            String sql = "SELECT * FROM furniture_items fi JOIN designs d ON fi.design_id = d.id WHERE d.name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, designName);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String type = rs.getString("type");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                int colorRGB = rs.getInt("color_rgb");
                double rotation = rs.getDouble("rotation");
                
                FurnitureItem item = new FurnitureItem(type, x, y, width, height, new Color(colorRGB));
                item.rotation = rotation;
                items.add(item);
            }
            
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No design found with the name '" + designName + "'", "Info", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Design Loaded from Database Successfully!", "Loaded", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Loading from DB: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
        return items;
    }
}
