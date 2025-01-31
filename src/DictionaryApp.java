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
                new showSlangHistoryInPanel(historyPanel);
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
        resetButton.addActionListener(e -> new resetDictionary(slangWords, wordList, contentArea, searchField, filePath, modifiedFilePath));

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
        randomButton.addActionListener(e -> new showRandomWord(slangWords));

        JButton quizButton = new JButton("Take a Quiz with Definitions");
        quizButton.setFont(new Font("Arial", Font.BOLD, 16));
        quizButton.setBackground(new Color(46, 204, 113));
        quizButton.setForeground(Color.WHITE);
        quizButton.setFocusPainted(false);
        quizButton.addActionListener(e -> new startQuiz(slangWords));

        JButton slangQuizButton = new JButton("Take a Quiz with Slang Words");
        slangQuizButton.setFont(new Font("Arial", Font.BOLD, 16));
        slangQuizButton.setBackground(new Color(231, 76, 60));
        slangQuizButton.setForeground(Color.WHITE);
        slangQuizButton.setFocusPainted(false);
        slangQuizButton.addActionListener(e -> new startSlangQuiz(slangWords));

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
        if (!slang.isEmpty() && !definition.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter only one search criteria.");
        }
       else if (!slang.isEmpty()) {
            String result = slangWords.getOrDefault(slang, "Not found.");
            contentArea.setText(result);

        } else if (!definition.isEmpty()) {
            List<String> results = searchByDefinition(definition);
            wordList.setListData(results.toArray(new String[0]));
            contentArea.setText("");
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DictionaryApp app = new DictionaryApp();
            app.setVisible(true);
        });
    }
}
