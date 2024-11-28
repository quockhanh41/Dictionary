import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class deleteSlangWord extends Component {
    public deleteSlangWord(Map<String, String> slangWords, String modifiedFilePath) {
        // Create a text field for slang input with a more aesthetically pleasing design
        JTextField slangField = new JTextField();
        slangField.setPreferredSize(new Dimension(200, 30));  // Adjust the size to fit the dialog nicely

        // Create a panel with a better layout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Enter Slang Word: "), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(slangField, gbc);

        // Show the dialog with improved layout and better UI elements
        int result = JOptionPane.showConfirmDialog(this, panel, "Delete Slang Word", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String slang = slangField.getText().trim();  // Trim whitespace to avoid issues

            if (slangWords.containsKey(slang)) {
                // Ask user for confirmation with a better confirmation dialog
                int confirmDelete = JOptionPane.showConfirmDialog(this,
                        "<html><b>Are you sure you want to delete the slang word: <font color='red'>" + slang + "</font>?</b></html>",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirmDelete == JOptionPane.YES_OPTION) {
                    // Remove the slang word from the dictionary
                    slangWords.remove(slang);

                    // Read the file contents into a list while excluding the deleted slang word
                    List<String> lines = new ArrayList<>();
                    try (BufferedReader reader = new BufferedReader(new FileReader(modifiedFilePath))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split("`", 2);
                            if (parts.length == 2 && !parts[0].equals(slang)) {
                                lines.add(line);
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // Write the updated contents back to the file
                    try (FileWriter writer = new FileWriter(modifiedFilePath)) {
                        for (String line : lines) {
                            writer.write(line + "\n");
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // Notify user of successful deletion with a nice confirmation dialog
                    JOptionPane.showMessageDialog(this, "<html><b>Slang word <font color='green'>" + slang + "</font> deleted successfully!</b></html>",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // If slang word doesn't exist, notify the user in a clearer way
                JOptionPane.showMessageDialog(this, "<html><b>Slang word <font color='red'>" + slang + "</font> not found!</b></html>",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
