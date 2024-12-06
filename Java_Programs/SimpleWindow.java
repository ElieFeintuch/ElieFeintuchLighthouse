import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
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
import java.io.FileInputStream;
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
        JPanel viewSalesPanel = new JPanel();
        viewSalesPanel.add(new JLabel("View Sales Menu"));

        GridBagConstraints gbc = new GridBagConstraints();
        JPanel printLabelsPanel = new JPanel(new GridBagLayout());
        gbc.insets = new Insets(5, 5, 5, 5);  // Padding around components
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        gbc.gridy = 0;
        printLabelsPanel.add(new JLabel("Print Labels Menu"));
        // Print Sample Button
        gbc.gridx = 1;
        gbc.gridy = 1;
        JButton printButton = new JButton("Print Sample");
        printLabelsPanel.add(printButton, gbc);
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	printLabel("C:\\Users\\benst\\Downloads\\label.png");
            }
        });
        
        // Create Listings panel with image upload
        JPanel createListingsPanel = new JPanel(new GridBagLayout());
        gbc.insets = new Insets(5, 5, 5, 5);  // Padding around components
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        // Title field
        gbc.gridx = 0;
        gbc.gridy = 0;
        createListingsPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        JTextField titleField = new JTextField(30);  // Smaller width
        createListingsPanel.add(titleField, gbc);

        // Price field
        gbc.gridx = 0;
        gbc.gridy = 1;
        createListingsPanel.add(new JLabel("Price:"), gbc);

        gbc.gridx = 1;
        JTextField priceField = new JTextField(30);
        createListingsPanel.add(priceField, gbc);
        
        //Original cost field
        gbc.gridx = 0;
        gbc.gridy = 2;
        createListingsPanel.add(new JLabel("Original Cost:"), gbc);
        
        gbc.gridx = 1;
        JTextField costField = new JTextField(30);
        createListingsPanel.add(costField, gbc);

        // Description field
        gbc.gridx = 0;
        gbc.gridy = 3;
        createListingsPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        JTextArea descriptionField = new JTextArea(3, 30);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionField);
        createListingsPanel.add(descriptionScrollPane, gbc);

        // Image upload area
        gbc.gridx = 0;
        gbc.gridy = 4;
        createListingsPanel.add(new JLabel("Images:"), gbc);

        gbc.gridx = 1;
        JTextArea imageListArea = new JTextArea(3, 30);
        imageListArea.setEditable(false);
        JScrollPane imageScrollPane = new JScrollPane(imageListArea);
        createListingsPanel.add(imageScrollPane, gbc);

        // Upload button
        gbc.gridx = 1;
        gbc.gridy = 5;
        JButton uploadImageButton = new JButton("Select Images");
        createListingsPanel.add(uploadImageButton, gbc);

        ArrayList<File> selectedImages = new ArrayList<>();
        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File[] files = fileChooser.getSelectedFiles();
                    for (File file : files) {
                        selectedImages.add(file);
                        imageListArea.append(file.getAbsolutePath() + "\n");
                    }
                }
            }
        });
        
        
        // Back button
        gbc.gridx = 1;
        gbc.gridy = 6;
        JButton backButton = new JButton("Back to Main Menu");
        createListingsPanel.add(backButton, gbc);
        
        //Submit button
        gbc.gridx = 1;
        gbc.gridy = 7;
        JButton submitButton = new JButton("Submit");
        createListingsPanel.add(submitButton, gbc);

        button1.addActionListener(e -> cardLayout.show(cardPanel, "CreateListings"));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));


        // Action listener for the Submit button
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
                      printLabel(filePath);
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
    public static void printLabel(String filePath) {
        FileInputStream fileInputStream = null;

        try {
            // Load the label file as a FileInputStream
            File labelFile = new File(filePath);
            fileInputStream = new FileInputStream(labelFile);

            // Determine the file type (MIME type)
            DocFlavor flavor = DocFlavor.INPUT_STREAM.PNG;

            // Locate available print services
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, null);

            if (printServices.length == 0) {
                System.out.println("No compatible print services found. Please check your printer settings.");
                return;
            }

            // Set up print attributes
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new Copies(1)); // Number of copies
            attributes.add(MediaSizeName.ISO_A4); // Paper size
            attributes.add(OrientationRequested.PORTRAIT); // Orientation

            // Show the print dialog to the user
            PrintService selectedService = ServiceUI.printDialog(
                    null, // Parent component (null means center on screen)
                    200, // X position of the dialog
                    200, // Y position of the dialog
                    printServices, // List of available print services
                    PrintServiceLookup.lookupDefaultPrintService(), // Default print service
                    flavor, // Doc flavor
                    attributes // Print request attributes
            );

            // If the user cancels the dialog, selectedService will be null
            if (selectedService == null) {
                System.out.println("Print job canceled by the user.");
                return;
            }

            // Create a print job for the selected printer
            DocPrintJob printJob = selectedService.createPrintJob();

            // Create a Doc object to send to the printer
            Doc doc = new SimpleDoc(fileInputStream, flavor, null);

            // Print the document
            printJob.print(doc, attributes);

            System.out.println("Print job sent successfully!");
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        } catch (PrintException e) {
            System.out.println("Error printing the document: " + e.getMessage());
        } finally {
            // Close the file input stream
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    System.out.println("Error closing the file stream: " + e.getMessage());
                }
            }
        }
    }
    public static Connection connect() {
        Connection conn = null;
        try {
        	String url = "jdbc:sqlite:salesdata.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    public static void createTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS sales (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                item_title TEXT NOT NULL,
                item_quant INTEGER NOT NULL,
                item_price REAL NOT NULL,
                original_cost REAL NOT NULL,
                profit REAL NOT NULL,
                sale_date TEXT NOT NULL
            )
        """;

        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
               stmt.execute(createTableSQL);
               System.out.println("Table created or already exists.");
           } catch (SQLException e) {
               e.printStackTrace();
           }
    }
    // Sample sales data
    public static void insertSampleData() {
        insertSale("Sample Item 1", 2, 50.00, 30.00, "2024-12-01");
        insertSale("Sample Item 2", 1, 75.00, 40.00, "2024-12-01");
        insertSale("Sample Item 3", 1, 15.00, 4.00, "2024-12-02");
        insertSale("Sample Item 4", 1, 30.00, 10.00, "2024-12-03");
        insertSale("Sample Item 5", 1, 12.00, 6.00, "2024-12-04");
    }
    //Method to take sales data and place in the database
    public static void insertSale(String itemTitle, int itemQuant, double itemPrice, double originalCost, String saleDate) {
        String insertSQL = """
            INSERT INTO sales (item_title, item_quant, item_price, original_cost, profit, sale_date)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        double profit = itemPrice - originalCost; // Calculate profit

        
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, itemTitle);
            pstmt.setInt(2, itemQuant);
            pstmt.setDouble(3, itemPrice);
            pstmt.setDouble(4, originalCost);
            pstmt.setDouble(5, profit);
            pstmt.setString(6, saleDate);

            pstmt.executeUpdate();
            System.out.println("Sale data inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
