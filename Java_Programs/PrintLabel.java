import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PrintLabel {

    public static void main(String[] args) {
        // Path to the sample label file
        String labelFilePath = "C:\\Users\\benst\\Downloads\\label.png";

        printLabel(labelFilePath);
    }

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
}
