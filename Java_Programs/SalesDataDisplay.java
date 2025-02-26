import javax.swing.*;
import java.sql.*;

//Helper class for database related operations, currently shows the table
public class SalesDataDisplay {
	//Reset the table to a blank slate
    public static void initializeDatabase() {
        String url = "jdbc:sqlite:salesdata.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                conn.setAutoCommit(true);  // Enable auto-commit if it’s disabled by default

                Statement stmt = conn.createStatement();
                // Clear the sales data table
                String clearTableSql = "DELETE FROM sales;";
                stmt.executeUpdate(clearTableSql);

                System.out.println("Sales data table cleared on program start.");
            }
        } catch (SQLException e) {
            System.out.println("Error clearing table: " + e.getMessage());
        }
    }
	public static int getRowCount(String tableName) {
	    String url = "jdbc:sqlite:salesdata.db";
	    String countSql = "SELECT COUNT(*) FROM " + tableName;
	    int rowCount = 0;

	    try (Connection conn = DriverManager.getConnection(url);
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(countSql)) {

	        if (rs.next()) {
	            rowCount = rs.getInt(1); // Get the count
	        }

	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	    }

	    return rowCount;
	}
    public static void showSalesData() {
        String[] columnNames = {"ID", "Title", "Quantity", "Price", "Cost", "Profit", "Sale Date"};
        
        String url = "jdbc:sqlite:salesdata.db";
        String sql = "SELECT * FROM sales";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Get row data
            int rowCount = getRowCount("sales");
            Object[][] data = new Object[rowCount][7];
            int rowIndex = 0;
            while (rs.next()) {
            	System.out.println("Displaying item " + rowIndex);
                data[rowIndex][0] = rs.getInt("id");
                data[rowIndex][1] = rs.getString("item_title");
                data[rowIndex][2] = rs.getInt("item_quant");
                data[rowIndex][3] = rs.getDouble("item_price");
                data[rowIndex][4] = rs.getDouble("original_cost");
                data[rowIndex][5] = rs.getDouble("profit");
                data[rowIndex][6] = rs.getString("sale_date");
                rowIndex++;
            }

            // Create JTable and display data
            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);

            JFrame frame = new JFrame("Sales Data");
            frame.setSize(800, 600);
            frame.add(scrollPane);
            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
