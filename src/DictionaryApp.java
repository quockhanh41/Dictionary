import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryApp extends JFrame {
    Map<String, String> slangWords = new HashMap<>();
    final String filePath = "slang.txt";
    final String modifiedFilePath = "modified_slang.txt";
    JTextArea contentArea;
    JTextField searchDefinitionField;
    JScrollPane listScrollPane;
    JTextField searchField;
    JButton addButton;
    JButton editButton;
    JButton deleteButton;
    JButton resetButton;
    JButton randomButton;
    JButton quizButton;
    JButton historyButton;
    JButton searchButton;

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

    // Tìm kiếm theo từ lóng
    public String searchBySlang(String slang) {
        return slangWords.getOrDefault(slang, "Not found.");
    }

    // Tìm kiếm theo định nghĩa
    public List<String> searchByDefinition(String definition) {
        List<String> results = new ArrayList<>();
        for (Map.Entry<String, String> entry : slangWords.entrySet()) {
            if (entry.getValue().toLowerCase().contains(definition.toLowerCase())) {
                results.add(entry.getKey());
            }
        }
        return results;
    }

    public DictionaryApp() {

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

        // Cài đặt JFrame
        setTitle("Dictionary");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(null); // Hiển thị giữa màn hình

        // Sử dụng GridBagConstraints để bố trí giao diện
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding giữa các thành phần

        // 1.1 Thanh tìm kiếm từ
        searchField = new JTextField(20); // Replaced with standard JTextField
        // trigger searchButton when Enter key is pressed
        searchField.addActionListener(e -> searchButton.doClick());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(searchField, gbc);

        // 1.2 Thanh tìm kiếm định nghĩa
        searchDefinitionField = new JTextField(20); // Replaced with standard JTextField
        // trigger searchButton when Enter key is pressed
        searchDefinitionField.addActionListener(e -> searchButton.doClick());
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(searchDefinitionField, gbc);

        // 2. Danh sách từ
        String[] words = slangWords.keySet().toArray(new String[0]);
        JList<String> wordList = new JList<>(words);
        wordList.setFont(new Font("Arial", Font.PLAIN, 16));
        listScrollPane = new JScrollPane(wordList); // Updated JScrollPane
        // display clicked word's definition in contentArea
        wordList.addListSelectionListener(e -> {
            String slang = wordList.getSelectedValue();
            String definition = slangWords.get(slang);
            if (definition != null) {
                contentArea.setText(definition);
            }
        });
//        wordList.addListSelectionListener(e -> searchField.setText(wordList.getSelectedValue()));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        add(listScrollPane, gbc);

        // 3. Khu vực hiển thị nội dung
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setPreferredSize(new Dimension(400, 300));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 3;
        gbc.weightx = 0.7;
        gbc.weighty = 0.7;
        add(contentScrollPane, gbc);

        // 4. Các nút chức năng
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        addButton = new JButton("Add");
        // open new frame to add new slang word and definition using 2 text fields in a JOptionPane
        addButton.addActionListener(e -> {
            JTextField slangField = new JTextField();
            JTextField definitionField = new JTextField();
            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            panel.add(new JLabel("Slang:"));
            panel.add(slangField);
            panel.add(new JLabel("Definition:"));
            panel.add(definitionField);
            int result = JOptionPane.showConfirmDialog(this, panel, "Add new slang word", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String slang = slangField.getText();
                String definition = definitionField.getText();
                if (!slang.isEmpty() && !definition.isEmpty()) {
                    slangWords.put(slang, definition);
                    try (FileWriter writer = new FileWriter(modifiedFilePath, true)) {
                        writer.write(slang + "`" + definition + "\n");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        editButton = new JButton("Edit");
        editButton.addActionListener(e -> {
            // enter slang word and check exist using JOptionPane
            JTextField slangField = new JTextField();
            JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
            panel.add(new JLabel("Slang:"));
            panel.add(slangField);
            int result = JOptionPane.showConfirmDialog(this, panel, "Edit slang word", JOptionPane.OK_CANCEL_OPTION);
            // if slang word exists, open new frame to edit definition
            // display slang and definition in 4 labels using JOptionPane
            if (result == JOptionPane.OK_OPTION) {
                String slang = slangField.getText();
                String definition = slangWords.get(slang);
                if (definition != null) {

                    JTextField newDefinitionField = new JTextField(definition);
                    JTextField newSlangField = new JTextField(slang);
                    JPanel editPanel = new JPanel(new GridLayout(2, 2, 5, 5));
                    editPanel.add(new JLabel("Slang:"));
                    editPanel.add(newDefinitionField);
                    editPanel.add(new JLabel("Definition:"));
                    editPanel.add(newSlangField);
                    int editResult = JOptionPane.showConfirmDialog(this, editPanel, "Edit definition", JOptionPane.OK_CANCEL_OPTION);
                    if (editResult == JOptionPane.OK_OPTION) {
                        String newSlang = newSlangField.getText();
                        String newDefinition = newDefinitionField.getText();
                        // delete old slang word and definition, then add new slang word and definition
                        slangWords.remove(slang);
                        slangWords.put(newSlang, newDefinition);
                        // delete line containing old slang word and definition, then add new slang word and definition
                        try (BufferedReader reader = new BufferedReader(new FileReader(modifiedFilePath));
                             FileWriter writer = new FileWriter(modifiedFilePath)) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                String[] parts = line.split("`", 2);
                                if (parts.length == 2 && !parts[0].equals(slang)) {
                                    writer.write(line + "\n");
                                }
                            }
                            writer.write(newSlang + "`" + newDefinition + "\n");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                    }
                }
            }

        });


        deleteButton = new JButton("Delete");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(buttonPanel, gbc);

        // 5. Nút Reset
        resetButton = new JButton("Reset");
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(resetButton, gbc);

        // 6. Các nút chức năng khác (Random, Quiz, History)
        JPanel rightPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        randomButton = new JButton("Random");
        quizButton = new JButton("Quiz");
        historyButton = new JButton("History");
        rightPanel.add(randomButton);
        rightPanel.add(quizButton);
        rightPanel.add(historyButton);
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        add(rightPanel, gbc);

        // 1.3 Nút tìm kiếm
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 16));
        searchButton.addActionListener(e -> {
            String slang = searchField.getText();
            String definition = searchDefinitionField.getText();
            if (!slang.isEmpty()) {
                String result = searchBySlang(slang);
                if (result.equals("Not found.")) {
                    JOptionPane.showMessageDialog(this, "No definition found.", "Message", JOptionPane.INFORMATION_MESSAGE);
                } else contentArea.setText(result);
            } else if (!definition.isEmpty()) {
                List<String> results = searchByDefinition(definition);
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No slang found.", "Message", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // display in JScrollPane listScrollPane
                    JList<String> resultList = new JList<>(results.toArray(new String[0]));
                    resultList.setFont(new Font("Arial", Font.PLAIN, 16));
                    listScrollPane.setViewportView(resultList);
                }
            }
        });
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(searchButton, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DictionaryApp app = new DictionaryApp();
            app.setVisible(true);
        });
    }
}
