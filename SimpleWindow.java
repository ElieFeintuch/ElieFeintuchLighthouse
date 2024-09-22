import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
}
