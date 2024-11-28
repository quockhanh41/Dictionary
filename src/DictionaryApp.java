import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;


public class DictionaryApp extends JFrame {
    private static Map<String, String> slangWords = new HashMap<>();
    private final String filePath = "slang.txt";
    private static final String modifiedFilePath = "modified_slang.txt";
    private final String historyFilePath = "history.txt";
    private JTextField searchField, searchDefinitionField;
    private JTextArea contentArea;
    private JList<String> wordList;
    private JScrollPane listScrollPane;


    public DictionaryApp() {
        setTitle("Slang Dictionary");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // check if historyFilePath exists, then delete all its data
        try {
            FileWriter writer = new FileWriter(historyFilePath);
            writer.write("");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if modifiedFilePath doesn't exist, then create it by copying from filePath
        try {
            BufferedReader reader = new BufferedReader(new FileReader(modifiedFilePath));
            reader.close();
        } catch (IOException e) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath)); FileWriter writer = new FileWriter(modifiedFilePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // Load slang words into the map
        try {
            loadSlangWords();  // Call to load slang words from file
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Search Panel
        JPanel searchPanel = createSearchPanel();
        tabbedPane.addTab("Search", searchPanel);


        // Random and Quiz Panel
        JPanel randomQuizPanel = createRandomQuizPanel();
        tabbedPane.addTab("Fun Zone", randomQuizPanel);

        // History Panel
        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BorderLayout());
//        historyPanel.add(new JLabel("History functionality coming soon!", SwingConstants.CENTER), BorderLayout.CENTER);
        // read history.txt file and display the content in the panel
        // add listener to run showSlangHistoryInPanel() when the History tab is selected
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2) {
                showSlangHistoryInPanel(historyPanel);
            }
        });
        tabbedPane.addTab("History", historyPanel);


        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Search Bar
        JPanel searchBar = new JPanel(new GridLayout(1, 3, 10, 10));
        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createTitledBorder("Search by Slang"));
        searchBar.add(searchField);

        searchDefinitionField = new JTextField();
        searchDefinitionField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchDefinitionField.setBorder(BorderFactory.createTitledBorder("Search by Definition"));
        searchBar.add(searchDefinitionField);

        JButton searchButton = new JButton("Search");


        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        searchButton.setBackground(new Color(59, 89, 182));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setToolTipText("Click to search");
        searchButton.addActionListener(e -> performSearch());
        searchBar.add(searchButton);

        panel.add(searchBar, BorderLayout.NORTH);

        // Word List and Content Area
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createWordListPane(), createContentPane());
        splitPane.setDividerLocation(300);
        panel.add(splitPane, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton resetButton = new JButton("Reset");

        addButton.setFont(new Font("Arial", Font.PLAIN, 16));
        editButton.setFont(new Font("Arial", Font.PLAIN, 16));
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 16));
        resetButton.setFont(new Font("Arial", Font.PLAIN, 16));

        addButton.addActionListener(e -> new addSlangWord(slangWords, modifiedFilePath));
        editButton.addActionListener(e -> new editSlangWord(slangWords, modifiedFilePath));
        deleteButton.addActionListener(e -> new deleteSlangWord(slangWords, modifiedFilePath));
        resetButton.addActionListener(e -> new resetDictionary( slangWords, wordList, contentArea, searchField, filePath, modifiedFilePath));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRandomQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton randomButton = new JButton("Random Word");
        randomButton.setFont(new Font("Arial", Font.BOLD, 16));
        randomButton.setBackground(new Color(59, 89, 182));
        randomButton.setForeground(Color.WHITE);
        randomButton.setFocusPainted(false);
        randomButton.addActionListener(e -> showRandomWord());

        JButton quizButton = new JButton("Take a Quiz with Definitions");
        quizButton.setFont(new Font("Arial", Font.BOLD, 16));
        quizButton.setBackground(new Color(46, 204, 113));
        quizButton.setForeground(Color.WHITE);
        quizButton.setFocusPainted(false);
        quizButton.addActionListener(e -> startQuiz());

        JButton slangQuizButton = new JButton("Take a Quiz with Slang Words");
        slangQuizButton.setFont(new Font("Arial", Font.BOLD, 16));
        slangQuizButton.setBackground(new Color(231, 76, 60));
        slangQuizButton.setForeground(Color.WHITE);
        slangQuizButton.setFocusPainted(false);
        slangQuizButton.addActionListener(e -> startSlangQuiz());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 10));
        buttonPanel.add(randomButton);
        buttonPanel.add(quizButton);
        buttonPanel.add(slangQuizButton);

        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createWordListPane() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Slang Words"));

        wordList = new JList<>(slangWords.keySet().toArray(new String[0]));
        wordList.setFont(new Font("Arial", Font.PLAIN, 16));
        wordList.addListSelectionListener(e -> {
            String selectedSlang = wordList.getSelectedValue();
            if (selectedSlang != null) {
                contentArea.setText(slangWords.get(selectedSlang));
            }
        });

        listScrollPane = new JScrollPane(wordList);
        panel.add(listScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createContentPane() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Definition"));

        contentArea = new JTextArea();
        contentArea.setFont(new Font("Arial", Font.PLAIN, 16));
        contentArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void performSearch() {
        String slang = searchField.getText();
        String definition = searchDefinitionField.getText();

        // add listener to append write text in searchField to history.txt file when press searchButton
        try {
            FileWriter writer = new FileWriter("history.txt", true);
            writer.write(slang + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!slang.isEmpty()) {
            String result = slangWords.getOrDefault(slang, "Not found.");
            contentArea.setText(result);
        } else if (!definition.isEmpty()) {
            List<String> results = searchByDefinition(definition);
            wordList.setListData(results.toArray(new String[0]));
        }
    }

    private List<String> searchByDefinition(String definition) {
        List<String> results = new ArrayList<>();
        for (Map.Entry<String, String> entry : slangWords.entrySet()) {
            if (entry.getValue().toLowerCase().contains(definition.toLowerCase())) {
                results.add(entry.getKey());
            }
        }
        return results;
    }

    // Load dữ liệu từ file
    public static void loadSlangWords() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(modifiedFilePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("`", 2);
            if (parts.length == 2) {
                slangWords.put(parts[0], parts[1]);
            }
        }
        reader.close();
    }




    private void showRandomWord() {
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

    private void startQuiz() {
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

    private void startSlangQuiz() {
        // Select a random definition from the dictionary
        int randomIndex = (int) (Math.random() * slangWords.size());
        String randomSlang = slangWords.keySet().toArray(new String[0])[randomIndex];
        String randomDefinition = slangWords.get(randomSlang);

        // Prepare 4 random slang words (including the correct one)
        List<String> randomSlangs = new ArrayList<>();
        randomSlangs.add(randomSlang);
        while (randomSlangs.size() < 4) {
            int randomIndex2 = (int) (Math.random() * slangWords.size());
            String randomSlang2 = slangWords.keySet().toArray(new String[0])[randomIndex2];
            if (!randomSlangs.contains(randomSlang2)) {
                randomSlangs.add(randomSlang2);
            }
        }

        // Shuffle the options to randomize the order of the slang words
        Collections.shuffle(randomSlangs);

        // Create a custom JPanel to display the question and options
        JPanel quizPanel = new JPanel();
        quizPanel.setLayout(new BoxLayout(quizPanel, BoxLayout.Y_AXIS));
        quizPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around the panel

        // Question label
        JLabel questionLabel = new JLabel("Which slang word means: " + randomDefinition + "?");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        quizPanel.add(questionLabel);

        quizPanel.add(Box.createVerticalStrut(20));  // Space between question and options

        // Option buttons with padding around each
        List<JButton> optionButtons = new ArrayList<>();
        for (String option : randomSlangs) {
            JButton optionButton = new JButton(option);
            optionButton.setFont(new Font("Arial", Font.PLAIN, 16));
            optionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            optionButton.setPreferredSize(new Dimension(500, 40));

            // Add padding to each button by setting margins
            optionButton.setMargin(new Insets(10, 20, 10, 20)); // Top, Left, Bottom, Right padding

            // Add action listener to handle answer selection
            optionButton.addActionListener(event -> {
                // Check if the selected answer is correct
                if (option.equals(randomSlang)) {
                    optionButton.setBackground(Color.GREEN); // Correct answer
                } else {
                    optionButton.setBackground(Color.RED);  // Incorrect answer, red background
                    optionButtons.stream()
                            .filter(button -> button.getText().equals(randomSlang))
                            .findFirst()
                            .ifPresent(button -> button.setBackground(Color.GREEN)); // Highlight correct answer
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

    // Function to display history in the given JPanel (historyPanel)
    private void showSlangHistoryInPanel(JPanel historyPanel) {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DictionaryApp app = new DictionaryApp();
            app.setVisible(true);
        });
    }
}
