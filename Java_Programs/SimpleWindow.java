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
        JPanel createListingsPanel = new JPanel();
        createListingsPanel.add(new JLabel("Create Listings Menu"));

        JPanel printLabelsPanel = new JPanel();
        printLabelsPanel.add(new JLabel("Print Labels Menu"));

        JPanel viewSalesPanel = new JPanel();
        viewSalesPanel.add(new JLabel("View Sales Menu"));

        // Add "Back" buttons to each submenu
        JButton backButton1 = new JButton("Back to Main Menu");
        JButton backButton2 = new JButton("Back to Main Menu");
        JButton backButton3 = new JButton("Back to Main Menu");

        // Add "Back" buttons to each respective submenu
        createListingsPanel.add(backButton1);
        printLabelsPanel.add(backButton2);
        viewSalesPanel.add(backButton3);

        // Add all panels to the cardPanel
        cardPanel.add(mainMenuPanel, "MainMenu");
        cardPanel.add(createListingsPanel, "CreateListings");
        cardPanel.add(printLabelsPanel, "PrintLabels");
        cardPanel.add(viewSalesPanel, "ViewSales");

        // Add functionality to switch menus when buttons are pressed
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "CreateListings");
                // Trigger API request when this button is pressed
                sendApiRequest();
            }
        });

        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "PrintLabels");
            }
        });

        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "ViewSales");
            }
        });

        // Add functionality to the "Back" buttons to return to the main menu
        backButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "MainMenu");
            }
        });

        backButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "MainMenu");
            }
        });

        backButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "MainMenu");
            }
        });

        // Add the card panel to the frame
        frame.add(cardPanel);

        // Make the window visible
        frame.setVisible(true);
    }

    // Method to make an API request when a button is pressed
    public static void sendApiRequest() {
        // API endpoint and OAuth token placeholder
        String endpointUrl = "https://api.ebay.com/buy/browse/v1/item_summary/search?q=iphone";
        String oauthToken = "YOUR_ACCESS_TOKEN"; // Replace with OAuth token

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointUrl))
                .header("Authorization", "Bearer " + oauthToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        // Asynchronous API call to prevent freezing the GUI
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
              .thenApply(HttpResponse::body)
              .thenAccept(response -> {
                  // Handle response here
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
