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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;


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
        JPanel createListingsPanel = new JPanel(new GridBagLayout());
        JPanel printLabelsPanel = new JPanel();
        JPanel viewSalesPanel = new JPanel();

        printLabelsPanel.add(new JLabel("Print Labels Menu"));
        viewSalesPanel.add(new JLabel("View Sales Menu"));

        // Add form fields for "Create Listings" panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel skuLabel = new JLabel("SKU:");
        JTextField skuField = new JTextField(20);
        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField(20);
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField(20);
        JLabel priceLabel = new JLabel("Price (USD):");
        JTextField priceField = new JTextField(20);
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        createListingsPanel.add(skuLabel, gbc);
        gbc.gridx = 1;
        createListingsPanel.add(skuField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        createListingsPanel.add(titleLabel, gbc);
        gbc.gridx = 1;
        createListingsPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        createListingsPanel.add(descriptionLabel, gbc);
        gbc.gridx = 1;
        createListingsPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        createListingsPanel.add(priceLabel, gbc);
        gbc.gridx = 1;
        createListingsPanel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        createListingsPanel.add(quantityLabel, gbc);
        gbc.gridx = 1;
        createListingsPanel.add(quantityField, gbc);

        // Create Submit button for "Create Listings"
        JButton submitButton = new JButton("Submit");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        createListingsPanel.add(submitButton, gbc);

        // Action listener for the Submit button
        submitButton.addActionListener(e -> {
            String sku = skuField.getText();
            String title = titleField.getText();
            String description = descriptionField.getText();
            String price = priceField.getText();
            String quantity = quantityField.getText();

            // Trigger the API call for creating a listing
            try {
                createListing(sku, title, description, price, quantity);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Create Back button for the "Create Listings" panel
        JButton backButton1 = new JButton("Back to Main Menu");
        gbc.gridy = 6;
        createListingsPanel.add(backButton1, gbc);

        backButton1.addActionListener(e -> {
            cardLayout.show(cardPanel, "MainMenu");
        });

        // Back buttons for the other panels
        JButton backButton2 = new JButton("Back to Main Menu");
        JButton backButton3 = new JButton("Back to Main Menu");

        printLabelsPanel.add(backButton2);
        viewSalesPanel.add(backButton3);

        // Add all panels to the cardPanel
        cardPanel.add(mainMenuPanel, "MainMenu");
        cardPanel.add(createListingsPanel, "CreateListings");
        cardPanel.add(printLabelsPanel, "PrintLabels");
        cardPanel.add(viewSalesPanel, "ViewSales");

        // Add functionality to switch menus when buttons are pressed
        button1.addActionListener(e -> cardLayout.show(cardPanel, "CreateListings"));
        button2.addActionListener(e -> cardLayout.show(cardPanel, "PrintLabels"));
        button3.addActionListener(e -> cardLayout.show(cardPanel, "ViewSales"));

        // Add functionality to the "Back" buttons to return to the main menu
        backButton2.addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));
        backButton3.addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));

        // Add the card panel to the frame
        frame.add(cardPanel);

        // Make the window visible
        frame.setVisible(true);
    }

    // Method to make the API request for creating a listing
    public static void createListing(String sku, String title, String description, String price, String quantity) throws Exception {
        String oauthToken = "YOUR_ACCESS_TOKEN";  // Replace with your OAuth token
        String url = "https://api.ebay.com/sell/inventory/v1/inventory_item";  // eBay API endpoint

        Map<String, Object> listingData = new HashMap<>();
        listingData.put("sku", sku);
        listingData.put("product", Map.of("title", title, "description", description));
        listingData.put("price", Map.of("value", price, "currency", "USD"));
        listingData.put("availability", Map.of("shipToLocationAvailability", Map.of("quantity", Integer.parseInt(quantity))));

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
    // Method to download and print the shipping label with a unique name
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

            // Check if the desktop is supported on the current platform
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

}
