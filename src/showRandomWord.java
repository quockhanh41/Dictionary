import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class showRandomWord {
    public showRandomWord(Map<String, String> slangWords) {
// Select a random slang word from the dictionary
        int randomIndex = (int) (Math.random() * slangWords.size());
        String randomSlang = slangWords.keySet().toArray(new String[0])[randomIndex];
        String randomDefinition = slangWords.get(randomSlang);

        // Create a custom JPanel to display the random word and its definition
        JPanel randomPanel = new JPanel();
        randomPanel.setLayout(new BoxLayout(randomPanel, BoxLayout.Y_AXIS));
        randomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around the panel
        randomPanel.setBackground(new Color(255, 255, 204)); // Light yellow background

        // Create a label for the random slang word
        JLabel slangLabel = new JLabel("Random Slang Word:");
        slangLabel.setFont(new Font("Arial", Font.BOLD, 18));
        slangLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        randomPanel.add(slangLabel);

        randomPanel.add(Box.createVerticalStrut(10));  // Space between label and slang word

        // Create a label for the random slang word
        JLabel randomSlangLabel = new JLabel(randomSlang);
        randomSlangLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        randomSlangLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        randomSlangLabel.setForeground(new Color(0, 102, 204)); // Blue color for slang word
        randomPanel.add(randomSlangLabel);

        randomPanel.add(Box.createVerticalStrut(20));  // Space between slang word and definition

        // Create a label for the definition of the slang word
        JLabel definitionLabel = new JLabel("Definition:");
        definitionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        definitionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        randomPanel.add(definitionLabel);

        randomPanel.add(Box.createVerticalStrut(10));  // Space between label and definition

        // Create a label for the definition
        JLabel randomDefinitionLabel = new JLabel(randomDefinition);
        randomDefinitionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        randomDefinitionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        randomPanel.add(randomDefinitionLabel);

        // Show the custom panel without the information icon
        JOptionPane.showMessageDialog(this, randomPanel, "Random Slang Word", JOptionPane.PLAIN_MESSAGE);
    }
}
