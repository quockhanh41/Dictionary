import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class editSlangWord {
    public editSlangWord(Map<String, String> slangWords, String modifiedFilePath) {
        // Create a custom dialog to input the slang word
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel slangLabel = new JLabel("Slang:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(slangLabel, gbc);

        JTextField slangField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(slangField, gbc);

        // Show dialog for entering slang word
        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Slang Word", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // If slang word exists, prompt for new definition
        if (result == JOptionPane.OK_OPTION) {
            String slang = slangField.getText().trim();

            // Check if the slang word exists in the dictionary
            String definition = slangWords.get(slang);
            if (definition != null) {
                // Create an edit panel with pre-filled definition
                JTextField newSlangField = new JTextField(slang);
                JTextField newDefinitionField = new JTextField(definition);

                JPanel editPanel = new JPanel(new GridBagLayout());
                GridBagConstraints editGbc = new GridBagConstraints();
                editGbc.insets = new Insets(10, 10, 10, 10);
                editGbc.fill = GridBagConstraints.HORIZONTAL;

                // Add labels and text fields for editing
                JLabel editSlangLabel = new JLabel("Slang:");
                editGbc.gridx = 0;
                editGbc.gridy = 0;
                editPanel.add(editSlangLabel, editGbc);

                editGbc.gridx = 1;
                editPanel.add(newSlangField, editGbc);

                JLabel editDefinitionLabel = new JLabel("Definition:");
                editGbc.gridx = 0;
                editGbc.gridy = 1;
                editPanel.add(editDefinitionLabel, editGbc);

                editGbc.gridx = 1;
                editPanel.add(newDefinitionField, editGbc);

                // Show dialog to edit the slang word's definition
                int editResult = JOptionPane.showConfirmDialog(null, editPanel, "Edit Slang Definition", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user confirms, update the slang word and definition
                if (editResult == JOptionPane.OK_OPTION) {
                    String newSlang = newSlangField.getText().trim();
                    String newDefinition = newDefinitionField.getText().trim();

                    // Validate the inputs
                    if (newSlang.isEmpty() || newDefinition.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Both fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Remove the old slang word and add the updated one
                    slangWords.remove(slang);
                    slangWords.put(newSlang, newDefinition);

                    // Update the file by deleting the old slang word and definition, then adding the new one
                    try (BufferedReader reader = new BufferedReader(new FileReader(modifiedFilePath))) {
                        List<String> lines = new ArrayList<>();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split("`", 2);
                            if (parts.length == 2 && !parts[0].equals(slang)) {
                                lines.add(line);
                            }
                        }
                        lines.add(newSlang + "`" + newDefinition);

                        try (FileWriter writer = new FileWriter(modifiedFilePath)) {
                            for (String l : lines) {
                                writer.write(l + "\n");
                            }
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error updating slang file.", "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }

                    // Inform the user that the slang word was successfully updated
                    JOptionPane.showMessageDialog(null, "Slang word updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Slang word not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
