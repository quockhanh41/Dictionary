import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class showSlangHistoryInPanel extends Component {
    // Function to display history in the given JPanel (historyPanel)
    public showSlangHistoryInPanel(JPanel historyPanel) {
        // Read the history.txt file and get the content
        StringBuilder historyContent = new StringBuilder();
        String historyFilePath = "history.txt";  // Adjust the file path if necessary

        try (BufferedReader reader = new BufferedReader(new FileReader(historyFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                historyContent.append(line).append("\n");  // Add each line from history.txt
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading history file.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Create a JTextArea to display the history content
        JTextArea historyTextArea = new JTextArea();
        historyTextArea.setText(historyContent.toString());
        historyTextArea.setEditable(false);  // Make it read-only
        historyTextArea.setFont(new Font("Arial", Font.PLAIN, 14));  // Optional: Customize font

        // Wrap the JTextArea inside a JScrollPane
        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        scrollPane.setPreferredSize(new Dimension(450, 300));  // Set the preferred size of the scrollable area

        // Clear any previous components and add the scrollPane to the historyPanel
        historyPanel.removeAll();
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        // Revalidate and repaint the panel to reflect the changes
        historyPanel.revalidate();
        historyPanel.repaint();
    }
}
