import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class addSlangWord {
    public  addSlangWord(Map<String, String> slangWords, String modifiedFilePath) {
        // Create a custom dialog panel with better alignment and spacing
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add "Slang" label and text field
        JLabel slangLabel = new JLabel("Slang:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(slangLabel, gbc);

        JTextField slangField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(slangField, gbc);

        // Add "Definition" label and text field
        JLabel definitionLabel = new JLabel("Definition:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(definitionLabel, gbc);

        JTextField definitionField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(definitionField, gbc);

        // Show dialog for adding a slang word
        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Slang Word", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // Process the result of the dialog
        if (result == JOptionPane.OK_OPTION) {
            String slang = slangField.getText().trim();
            String definition = definitionField.getText().trim();

            if (slang.isEmpty() || definition.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Both fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (slangWords.containsKey(slang)) {
                // Warn the user if the slang already exists
                int overwrite = JOptionPane.showConfirmDialog(null, "Slang word already exists. Overwrite?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (overwrite == JOptionPane.YES_OPTION) {
                    slangWords.put(slang, definition);

                    // Update the file by overwriting the existing slang word
                    try (BufferedReader reader = new BufferedReader(new FileReader(modifiedFilePath)); FileWriter writer = new FileWriter(modifiedFilePath)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split("`", 2);
                            if (parts.length == 2 && !parts[0].equals(slang)) {
                                writer.write(line + "\n");
                            }
                        }
                        writer.write(slang + "`" + definition + "\n");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error updating slang file.", "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            } else {
                // Add the new slang word
                slangWords.put(slang, definition);

                try (FileWriter writer = new FileWriter(modifiedFilePath, true)) {
                    writer.write(slang + "`" + definition + "\n");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error saving slang file.", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }

                JOptionPane.showMessageDialog(null, "Slang word added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
