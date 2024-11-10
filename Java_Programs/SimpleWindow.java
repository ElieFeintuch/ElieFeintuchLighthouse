import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

//Class with the main method and main body of code
public class SimpleWindow {

    public static void main(String[] args) {
  	
    	// Create a new JFrame (window)
        JFrame frame = new JFrame("Lighthouse");

        // Set the size of the window
        frame.setSize(2000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Create a CardLayout to switch between different panels (menus)
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        // Create the main menu panel
        JPanel mainMenuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 200, 400));

        // Create buttons for the main menu
        JButton button1 = new JButton("Create Listings");
        button1.setPreferredSize(new Dimension(160, 40));

        JButton button2 = new JButton("Print Labels");
        button2.setPreferredSize(new Dimension(160, 40));

        JButton button3 = new JButton("View Sales");
        button3.setPreferredSize(new Dimension(160, 40));

        // Add buttons to the main menu panel
        mainMenuPanel.add(button1);
        mainMenuPanel.add(button2);
        mainMenuPanel.add(button3);

        // Create additional panels (sub-menus) for each button
        JPanel printLabelsPanel = new JPanel();
        JPanel viewSalesPanel = new JPanel();

        printLabelsPanel.add(new JLabel("Print Labels Menu"));
        viewSalesPanel.add(new JLabel("View Sales Menu"));

        // Add form fields for "Create Listings" panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create Listings panel with image upload
        JPanel createListingsPanel = new JPanel();
        createListingsPanel.setLayout(new BoxLayout(createListingsPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField(20);
        createListingsPanel.add(titleLabel);
        createListingsPanel.add(titleField);

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(20);
        createListingsPanel.add(priceLabel);
        createListingsPanel.add(priceField);

        JLabel descriptionLabel = new JLabel("Description:");
        JTextArea descriptionField = new JTextArea(5, 20);
        createListingsPanel.add(descriptionLabel);
        createListingsPanel.add(new JScrollPane(descriptionField));

        JLabel imageLabel = new JLabel("Images:");
        JTextArea imageListArea = new JTextArea(5, 20);
        imageListArea.setEditable(false);
        createListingsPanel.add(imageLabel);
        createListingsPanel.add(new JScrollPane(imageListArea));

        JButton uploadImageButton = new JButton("Select Images");
        ArrayList<File> selectedImages = new ArrayList<>();

        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int option = fileChooser.showOpenDialog(frame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File[] files = fileChooser.getSelectedFiles();
                    for (File file : files) {
                        selectedImages.add(file);
                        imageListArea.append(file.getAbsolutePath() + "\n");
                    }
                }
            }
        });

        createListingsPanel.add(uploadImageButton);

        // Action listener for the Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            String title = titleField.getText();
            String description = descriptionField.getText();
            String price = priceField.getText();

            // Trigger the API call for creating a listing
            try {
                createListing(title, description, price);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Create Back button for the "Create Listings" panel
        JButton backButton1 = new JButton("Back to Main Menu");
        createListingsPanel.add(backButton1);

        backButton1.addActionListener(e -> {
            cardLayout.show(cardPanel, "MainMenu");
        });

        // Back buttons for the other panels
        JButton backButton2 = new JButton("Back to Main Menu");
        JButton backButton3 = new JButton("Back to Main Menu");

        printLabelsPanel.add(backButton2);
        //The view sales back button has been deprecated since the table appears in a new window
        viewSalesPanel.add(backButton3);

        // Add all panels to the cardPanel
        cardPanel.add(mainMenuPanel, "MainMenu");
        cardPanel.add(createListingsPanel, "CreateListings");
        cardPanel.add(printLabelsPanel, "PrintLabels");
        cardPanel.add(viewSalesPanel, "ViewSales");

        // Add functionality to switch menus when buttons are pressed
        button1.addActionListener(e -> cardLayout.show(cardPanel, "CreateListings"));
        button2.addActionListener(e -> cardLayout.show(cardPanel, "PrintLabels"));
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	SalesDataDisplay.initializeDatabase();
            	createTable();
            	insertSampleData();
                SalesDataDisplay.showSalesData(); // Show sales data on button click
            }
        });

        // Add functionality to the "Back" buttons to return to the main menu
        backButton2.addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));
        backButton3.addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));

        // Add the card panel to the frame
        frame.add(cardPanel);

        // Make the window visible
        frame.setVisible(true);
    }

    // Method to make the API request for creating a listing
    public static void createListing(String title, String description, String price) throws Exception {
        String oauthToken = "YOUR_ACCESS_TOKEN";  // Replace with OAuth token
        String url = "https://api.ebay.com/sell/inventory/v1/inventory_item";  // eBay API endpoint

        Map<String, Object> listingData = new HashMap<>();
        listingData.put("product", Map.of("title", title, "description", description));
        listingData.put("price", Map.of("value", price, "currency", "USD"));
        

        // Convert the listing data to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(listingData);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + oauthToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        // Send the request asynchronously
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    System.out.println("API Response: " + response);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
    //Get order details to help with printing a shipping label
    public static void getOrderDetails(String orderId, String oauthToken) {
        String endpointUrl = "https://api.ebay.com/sell/fulfillment/v1/order/" + orderId;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointUrl))
                .header("Authorization", "Bearer " + oauthToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
              .thenApply(HttpResponse::body)
              .thenAccept(response -> {
                  System.out.println("Order Details: " + response);
              })
              .exceptionally(e -> {
                  e.printStackTrace();
                  return null;
              });
    }
    //Given some order details, I would fill in the necessary info to create a shipping label. The response would include a url for the label
    public static void purchaseShippingLabel(String orderId, String oauthToken) {
        String endpointUrl = "https://api.ebay.com/post-order/v2/return_shipping_label";

        String requestBody = "{"
                + "\"orderId\": \"" + orderId + "\","
                + "\"labelRequest\": {"
                + "    \"carrierEnum\": \"USPS\","
                + "    \"serviceTypeEnum\": \"USPS_PRIORITY\","
                + "    \"labelFormatEnum\": \"PDF\","
                + "    \"dimensions\": {"
                + "        \"height\": 10,"
                + "        \"length\": 15,"
                + "        \"width\": 3"
                + "    },"
                + "    \"weight\": {"
                + "        \"unitOfMeasure\": \"POUND\","
                + "        \"value\": 2.0"
                + "    }"
                + "}"
                + "}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointUrl))
                .header("Authorization", "Bearer " + oauthToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
              .thenApply(HttpResponse::body)
              .thenAccept(response -> {
                  System.out.println("Shipping Label Response: " + response);
              })
              .exceptionally(e -> {
                  e.printStackTrace();
                  return null;
              });
    }
    // Method to download and print the shipping label with a unique name. Do not run, test in PrintLabel.java.
    public static void downloadAndPrintLabel(String labelUrl, String orderId) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(labelUrl))
                .header("Content-Type", "application/pdf")
                .GET()
                .build();

        // Download the label
        client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
              .thenApply(HttpResponse::body)
              .thenAccept(labelBytes -> {
                  try {
                      // Generate a unique file name using orderId and timestamp
                      String fileName = generateUniqueFileName(orderId);
                      String filePath = "C:/Downloads/" + fileName;

                      // Save the label as a PDF file
                      Files.write(Paths.get(filePath), labelBytes);
                      System.out.println("Label downloaded successfully: " + filePath);

                      // Open the print dialog to print the label
                      printFile(filePath);
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              })
              .exceptionally(e -> {
                  e.printStackTrace();
                  return null;
              });
    }

    // Method to generate a unique file name based on order ID and timestamp
    public static String generateUniqueFileName(String orderId) {
        // Get current timestamp in a readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        
        // Create a unique file name
        return "shipping_label_" + orderId + "_" + timestamp + ".pdf";
    }

    // Method to open the print dialog for a file
    public static void printFile(String filePath) {
        try {
            // Create a file object from the downloaded file path
            File file = new File(filePath);

            // Check if the desktop is supported on the current platform. Mobile would not work here.
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.PRINT)) {
                    // Open the print dialog
                    desktop.print(file);
                } else {
                    System.out.println("Printing is not supported on this platform.");
                }
            } else {
                System.out.println("Desktop is not supported on this platform.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void connect() {
        Connection conn = null;
        try {
            // Connect to SQLite database, or create the file if it doesn't exist
            String url = "jdbc:sqlite:salesdata.db";
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public static void createTable() {
        // Database connection string (file location)
        String url = "jdbc:sqlite:salesdata.db";

        // SQL statement to create the table
        String sql = "CREATE TABLE IF NOT EXISTS sales ("
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " item_id TEXT NOT NULL,"
                    + " item_title TEXT NOT NULL,"
                    + " price REAL,"
                    + " quantity INTEGER,"
                    + " sale_date TEXT"
                    + ");";

        // Establish connection and execute the SQL
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);  // Execute the create table statement
            System.out.println("Sales table created or already exists.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    // Sample sales data
    public static void insertSampleData() {
        insertSalesData("12345", "Sample Item 1", 19.99, 1, "2024-10-10");
        insertSalesData("12346", "Sample Item 2", 49.99, 2, "2024-10-11");
        insertSalesData("12347", "Sample Item 3", 29.99, 1, "2024-10-12");
    }
    //Method to take sales data and place in the database
    public static void insertSalesData(String itemId, String itemTitle, double price, int quantity, String saleDate) {
        String url = "jdbc:sqlite:salesdata.db";
        String sql = "INSERT INTO sales(item_id, item_title, price, quantity, sale_date) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, itemId);
            pstmt.setString(2, itemTitle);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, quantity);
            pstmt.setString(5, saleDate);

            pstmt.executeUpdate();
            System.out.println("Inserted sales data.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
