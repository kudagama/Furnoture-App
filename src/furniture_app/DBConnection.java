package furniture_app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.awt.Color;
import java.util.List;
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
        
        // Add new columns to designs
        try { stmt.execute("ALTER TABLE designs ADD COLUMN room_w INT DEFAULT 10;"); } catch(Exception e){}
        try { stmt.execute("ALTER TABLE designs ADD COLUMN room_l INT DEFAULT 10;"); } catch(Exception e){}
        try { stmt.execute("ALTER TABLE designs ADD COLUMN wall_color INT DEFAULT -657931;"); } catch(Exception e){}
        try { stmt.execute("ALTER TABLE designs ADD COLUMN shadows BOOLEAN DEFAULT true;"); } catch(Exception e){}

        // Add new column to furniture_items
        try { stmt.execute("ALTER TABLE furniture_items ADD COLUMN second_color_rgb INT DEFAULT 0;"); } catch(Exception e){}

        stmt.close();
    }

    public static void saveDesignToDatabase(String designName, DesignCanvas canvas) {
        Connection conn = getConnection();
        if (conn == null) return;
        
        ArrayList<FurnitureItem> items = canvas.getItems();

        try {
            conn.setAutoCommit(false);
            
            // Check if design already exists, if so delete it (or overwrite)
            String delSql = "DELETE FROM designs WHERE name = ?";
            PreparedStatement delStmt = conn.prepareStatement(delSql);
            delStmt.setString(1, designName);
            delStmt.executeUpdate();
            
            // Insert Design
            String insertDesign = "INSERT INTO designs (name, room_w, room_l, wall_color, shadows) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertDesign, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, designName);
            pstmt.setInt(2, canvas.roomWidthMeters);
            pstmt.setInt(3, canvas.roomLengthMeters);
            pstmt.setInt(4, canvas.getWallColor().getRGB());
            pstmt.setBoolean(5, canvas.isApplyShadows());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            int designId = -1;
            if (rs.next()) {
                designId = rs.getInt(1);
            }

            // Insert Items
            String insertItem = "INSERT INTO furniture_items (design_id, type, x, y, width, height, color_rgb, rotation, second_color_rgb) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                itemStmt.setInt(9, item.secondaryColor != null ? item.secondaryColor.getRGB() : item.color.brighter().getRGB());
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

    public static boolean loadDesignFromDatabase(String designName, DesignCanvas canvas) {
        ArrayList<FurnitureItem> items = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) return false;

        boolean found = false;
        try {
            // Load Room Properties
            String sqlRoom = "SELECT * FROM designs WHERE name = ?";
            PreparedStatement pRoom = conn.prepareStatement(sqlRoom);
            pRoom.setString(1, designName);
            ResultSet rsRoom = pRoom.executeQuery();
            if (rsRoom.next()) {
                canvas.roomWidthMeters = rsRoom.getInt("room_w");
                canvas.roomLengthMeters = rsRoom.getInt("room_l");
                canvas.setWallColor(new Color(rsRoom.getInt("wall_color")));
                canvas.setApplyShadows(rsRoom.getBoolean("shadows"));
                found = true;
            }

            // Load Items
            String sql = "SELECT fi.* FROM furniture_items fi JOIN designs d ON fi.design_id = d.id WHERE d.name = ?";
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
                int secondColorRGB = rs.getInt("second_color_rgb");
                if (secondColorRGB == 0) secondColorRGB = new Color(colorRGB).brighter().getRGB();
                double rotation = rs.getDouble("rotation");
                
                FurnitureItem item = new FurnitureItem(type, x, y, width, height, new Color(colorRGB));
                item.secondaryColor = new Color(secondColorRGB);
                item.rotation = rotation;
                items.add(item);
            }
            
            if (!found && items.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No design found with the name '" + designName + "'", "Info", JOptionPane.WARNING_MESSAGE);
                return false;
            } else {
                canvas.setItems(items);
                canvas.repaint();
                JOptionPane.showMessageDialog(null, "Design Loaded from Database Successfully!", "Loaded", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Loading from DB: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
    }
    
    public static List<String> getSavedDesigns() {
        List<String> designs = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) return designs;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM designs ORDER BY created_at DESC");
            while(rs.next()) {
                designs.add(rs.getString("name"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
        return designs;
    }
    
    public static boolean deleteDesign(String designName) {
        Connection conn = getConnection();
        if (conn == null) return false;
        try {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM designs WHERE name = ?");
            pstmt.setString(1, designName);
            return pstmt.executeUpdate() > 0;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
    }
}
