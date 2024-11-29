import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class startQuiz extends Component {
    public startQuiz(Map< String, String > slangWords) {
        // Select a random slang word from the dictionary
        int randomIndex = (int) (Math.random() * slangWords.size());
        String randomSlang = slangWords.keySet().toArray(new String[0])[randomIndex];
        String randomDefinition = slangWords.get(randomSlang);

        // Prepare multiple random definitions (including the correct one)
        List<String> randomDefinitions = new ArrayList<>();
        randomDefinitions.add(randomDefinition);
        while (randomDefinitions.size() < 4) {
            int randomIndex2 = (int) (Math.random() * slangWords.size());
            String randomDefinition2 = slangWords.get(slangWords.keySet().toArray(new String[0])[randomIndex2]);
            if (!randomDefinitions.contains(randomDefinition2)) {
                randomDefinitions.add(randomDefinition2);
            }
        }

        // Shuffle the options to randomize the order of the answer choices
        Collections.shuffle(randomDefinitions);

        // Create a custom JPanel to display the question and options
        JPanel quizPanel = new JPanel();
        quizPanel.setLayout(new BoxLayout(quizPanel, BoxLayout.Y_AXIS));
        quizPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around the panel

        // Question label
        JLabel questionLabel = new JLabel("What is the definition of: " + randomSlang + "?");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        quizPanel.add(questionLabel);

        quizPanel.add(Box.createVerticalStrut(20));  // Space between question and options

        // Option buttons with padding around each
        List<JButton> optionButtons = new ArrayList<>();
        for (String option : randomDefinitions) {
            JButton optionButton = new JButton(option);
            optionButton.setFont(new Font("Arial", Font.PLAIN, 16));
            optionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            optionButton.setPreferredSize(new Dimension(500, 40));

            // Add padding to each button by setting margins
            optionButton.setMargin(new Insets(10, 20, 10, 20)); // Top, Left, Bottom, Right padding

            // Add action listener to handle answer selection
            optionButton.addActionListener(event -> {
                // Check if the selected answer is correct
                if (option.equals(randomDefinition)) {
                    optionButton.setBackground(Color.GREEN);

                } else {
                    optionButton.setBackground(Color.RED);  // Incorrect answer, red background
                    optionButtons.stream().filter(button -> button.getText().equals(randomDefinition)).findFirst().ifPresent(button -> button.setBackground(Color.GREEN));
                }
                // Disable all options after an answer is selected
                optionButtons.forEach(button -> button.setEnabled(false));
            });

            // Add the button to the panel
            quizPanel.add(optionButton);
            optionButtons.add(optionButton);

            quizPanel.add(Box.createVerticalStrut(10));  // Add space between options
        }
        // Show the custom panel in a dialog box
        JOptionPane.showConfirmDialog(this, quizPanel, "Slang Quiz", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}
