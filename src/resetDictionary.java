import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class resetDictionary extends Component {
    public resetDictionary(Map<String, String> slangWords, JList<String> wordList, JTextArea contentArea, JTextField searchField, String filePath, String modifiedFilePath) {
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
                DictionaryApp.loadSlangWords();
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
}
