import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DictionaryApp extends JFrame {
    private Map<String, String> slangWords = new HashMap<>();
    private final String filePath = "slang.txt";
    private final String modifiedFilePath = "modified_slang.txt";

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
        historyPanel.add(new JLabel("History functionality coming soon!", SwingConstants.CENTER), BorderLayout.CENTER);
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

        addButton.addActionListener(e -> addSlangWord());
        editButton.addActionListener(e -> editSlangWord());
        deleteButton.addActionListener(e -> deleteSlangWord());
        resetButton.addActionListener(e -> resetDictionary());

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

        JButton quizButton = new JButton("Take a Quiz");
        quizButton.setFont(new Font("Arial", Font.BOLD, 16));
        quizButton.setBackground(new Color(46, 204, 113));
        quizButton.setForeground(Color.WHITE);
        quizButton.setFocusPainted(false);
        quizButton.addActionListener(e -> startQuiz());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        buttonPanel.add(randomButton);
        buttonPanel.add(quizButton);

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
    public void loadSlangWords() throws IOException {
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

    private void addSlangWord() {
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

    private void editSlangWord() {
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
    private void deleteSlangWord() {
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


    private void resetDictionary() {
        // Ask user for confirmation before resetting the dictionary
        int confirmReset = JOptionPane.showConfirmDialog(this,
                "<html><b>Are you sure you want to reset the dictionary to its original state?</b></html>",
                "Confirm Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmReset == JOptionPane.YES_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
                 FileWriter writer = new FileWriter(modifiedFilePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Clear the current slangWords dictionary
            slangWords.clear();

            try {
                // Load the slang words again after reset
                loadSlangWords();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Update the word list display
            String[] newWords = slangWords.keySet().toArray(new String[0]);
            wordList.setListData(newWords);

            // Clear the content area and search field
            contentArea.setText("");
            searchField.setText("");

            // Notify user that the dictionary has been reset successfully
            JOptionPane.showMessageDialog(this,
                    "<html><b>Dictionary has been reset to its original state!</b></html>",
                    "Reset Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // If user cancels the reset action
            JOptionPane.showMessageDialog(this,
                    "<html><b>Dictionary reset has been cancelled.</b></html>",
                    "Reset Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void showRandomWord() {
        // Implement Random Word functionality here.
    }

    private void startQuiz() {
        // Implement Quiz functionality here.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DictionaryApp app = new DictionaryApp();
            app.setVisible(true);
        });
    }
}
