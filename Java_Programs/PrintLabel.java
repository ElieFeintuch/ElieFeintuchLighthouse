import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrintLabel {

    public static void main(String[] args) {
        // The URL of the shipping label
        String labelUrl = "https://example.com/path-to-shipping-label.pdf";
        // Example order ID
        String orderId = "1234567890";

        // Download and then print the label
        //downloadAndPrintLabel(labelUrl, orderId);
        printFile("C:\\Users\\benst\\Downloads\\test2.txt");

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
                      //String filePath = "C:/Downloads/" + fileName;
                      //For now, just insert a file for testing
                      String filePath = "C:\\Users\\benst\\Downloads\\testLabel.png";

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

