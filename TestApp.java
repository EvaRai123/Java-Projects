import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.sqlite.SQLiteDataSource;
import java.util.Random;

public class TestApp {

    private static SQLiteDataSource ds;
    private static Connection conn;

    public static void main(String[] args) {
        try {
            // Setting up the database connection
            ds = new SQLiteDataSource();
            ds.setUrl("jdbc:sqlite:flashcards.db");
            conn = ds.getConnection();
            createTableIfNotExists(conn); // Ensure the table is created

            // Check if the app should review a random flashcard
            int reviewFirst = JOptionPane.showConfirmDialog(null, "Do you want to review a random flashcard first?", "Start Review", JOptionPane.YES_NO_OPTION);
            if (reviewFirst == JOptionPane.YES_OPTION) {
                showRandomFlashcardReview(conn);
            }

            // Swing UI setup
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JFrame frame = new JFrame("🌟 Flashcard App");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(650, 600);
                    frame.setLayout(new BorderLayout());

                    // Mascot panel (Customizable for your needs)
                    JTextArea mascotArea = createMascotPanel();
                    frame.add(mascotArea, BorderLayout.NORTH);

                    // Main content panel
                    JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
                    panel.setBackground(new Color(224, 255, 255)); // light blue
                    frame.add(panel, BorderLayout.CENTER);

                    JLabel label = new JLabel("📚 Enter Flashcard Details", JLabel.CENTER);
                    label.setFont(new Font("Arial", Font.BOLD, 20));
                    panel.add(label);

                    // Input fields for question, answer, and category
                    JPanel inputPanel = createInputFields(panel);
                    panel.add(inputPanel);

                    // Button panel with actions
                    JPanel buttonPanel = createButtonPanel(frame);
                    panel.add(buttonPanel);

                    // Display area for flashcards
                    JTextArea flashcardDisplayArea = createFlashcardDisplayArea();
                    JScrollPane scrollPane = new JScrollPane(flashcardDisplayArea);
                    frame.add(scrollPane, BorderLayout.SOUTH);

                    // Make the frame visible
                    frame.setVisible(true);
                }
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database setup failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JTextArea createMascotPanel() {
        JTextArea mascotArea = new JTextArea();
        mascotArea.setEditable(false);
        mascotArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        mascotArea.setBackground(new Color(245, 245, 255));
        mascotArea.setText(
                "      [^_^]\n" +
                "     [|   |]\n" +
                "    /[|___|]\\\n" +
                "     /   \\\n" +
                "   []     []\n" +
                "  FlashBot: Ready to help!"
        );
        return mascotArea;
    }

    private static JPanel createInputFields(JPanel panel) {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(new Color(224, 255, 255));
        
        inputPanel.add(new JLabel("Question:"));
        JTextField questionField = new JTextField();
        inputPanel.add(questionField);

        inputPanel.add(new JLabel("Answer:"));
        JTextField answerField = new JTextField();
        inputPanel.add(answerField);

        inputPanel.add(new JLabel("Category:"));
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"Math", "Science", "History", "General"});
        inputPanel.add(categoryComboBox);

        return inputPanel;
    }

    private static JPanel createButtonPanel(JFrame frame) {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(224, 255, 255));

        JButton addButton = new JButton("Add Flashcard");
        JButton displayButton = new JButton("Display Flashcards");
        JButton deleteButton = new JButton("Delete Flashcard");
        JButton editButton = new JButton("Edit Flashcard");
        JButton reviewButton = new JButton("Review Random");

        Color btnColor = new Color(173, 216, 230); // Light blue buttons
        for (JButton btn : new JButton[]{addButton, displayButton, deleteButton, editButton, reviewButton}) {
            btn.setBackground(btnColor);
            btn.setFocusPainted(false);
        }

        buttonPanel.add(addButton);
        buttonPanel.add(displayButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(reviewButton);

        addActionListeners(addButton, displayButton, deleteButton, editButton, reviewButton, frame);

        return buttonPanel;
    }

    private static JTextArea createFlashcardDisplayArea() {
        JTextArea flashcardDisplayArea = new JTextArea(10, 40);
        flashcardDisplayArea.setEditable(false);
        flashcardDisplayArea.setBackground(new Color(255, 255, 204)); // light yellow
        return flashcardDisplayArea;
    }

    private static void addActionListeners(JButton addButton, JButton displayButton, JButton deleteButton, JButton editButton, JButton reviewButton, JFrame frame) {
        addButton.addActionListener(e -> {
            try {
                String question = JOptionPane.showInputDialog(frame, "Enter Question:");
                String answer = JOptionPane.showInputDialog(frame, "Enter Answer:");
                String category = (String) JOptionPane.showInputDialog(frame, "Select Category:", "Category", JOptionPane.QUESTION_MESSAGE, null, new String[]{"Math", "Science", "History", "General"}, "Math");

                if (question != null && answer != null && !question.isEmpty() && !answer.isEmpty()) {
                    insertFlashcard(conn, question, answer, category);
                    JOptionPane.showMessageDialog(frame, "🎉 Flashcard Added!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Both fields are required!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error adding flashcard: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        displayButton.addActionListener(e -> {
            try {
                String flashcards = getFlashcards(conn);
                JTextArea flashcardDisplayArea = createFlashcardDisplayArea();
                flashcardDisplayArea.setText(flashcards);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error displaying flashcards: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            String idToDelete = JOptionPane.showInputDialog(frame, "Enter Flashcard ID to delete:");
            try {
                deleteFlashcard(conn, idToDelete);
                JOptionPane.showMessageDialog(frame, "🗑️ Flashcard Deleted!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error deleting flashcard: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        editButton.addActionListener(e -> {
            String idToEdit = JOptionPane.showInputDialog(frame, "Enter Flashcard ID to edit:");
            String newQuestion = JOptionPane.showInputDialog(frame, "Enter new question:");
            String newAnswer = JOptionPane.showInputDialog(frame, "Enter new answer:");
            try {
                updateFlashcard(conn, idToEdit, newQuestion, newAnswer);
                JOptionPane.showMessageDialog(frame, "✏️ Flashcard Updated!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error updating flashcard: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        reviewButton.addActionListener(e -> showRandomFlashcardReview(conn));
    }

    private static void createTableIfNotExists(Connection conn) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS flashcards (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT NOT NULL, answer TEXT NOT NULL, category TEXT);";
        try (PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
            pstmt.executeUpdate();
        }
    }

    private static void insertFlashcard(Connection conn, String question, String answer, String category) throws SQLException {
        String insertSQL = "INSERT INTO flashcards (question, answer, category) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, question);
            pstmt.setString(2, answer);
            pstmt.setString(3, category);
            pstmt.executeUpdate();
        }
    }

    private static String getFlashcards(Connection conn) throws SQLException {
        String selectSQL = "SELECT * FROM flashcards";
        StringBuilder flashcards = new StringBuilder();
        try (PreparedStatement pstmt = conn.prepareStatement(selectSQL); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String question = rs.getString("question");
                String answer = rs.getString("answer");
                String category = rs.getString("category");
                flashcards.append("🎯 ID: ").append(id).append("\n");
                flashcards.append("❓ Question: ").append(question).append("\n");
                flashcards.append("✅ Answer: ").append(answer).append("\n");
                flashcards.append("📂 Category: ").append(category).append("\n");
                flashcards.append("━━━━━━━━━━━━━━━━━━━━━━\n");
            }
        }
        return flashcards.toString();
    }

    private static void deleteFlashcard(Connection conn, String id) throws SQLException {
        String deleteSQL = "DELETE FROM flashcards WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, Integer.parseInt(id));
            pstmt.executeUpdate();
        }
    }

    private static void updateFlashcard(Connection conn, String id, String newQuestion, String newAnswer) throws SQLException {
        String updateSQL = "UPDATE flashcards SET question = ?, answer = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, newQuestion);
            pstmt.setString(2, newAnswer);
            pstmt.setInt(3, Integer.parseInt(id));
            pstmt.executeUpdate();
        }
    }

    private static void showRandomFlashcardReview(Connection conn) {
        try {
            String countSQL = "SELECT COUNT(*) AS count FROM flashcards";
            try (PreparedStatement pstmt = conn.prepareStatement(countSQL);
                 ResultSet rs = pstmt.executeQuery()) {

                if (rs.next() && rs.getInt("count") > 0) {
                    int count = rs.getInt("count");
                    int randomId = new Random().nextInt(count) + 1;

                    String fetchSQL = "SELECT * FROM flashcards WHERE id = ?";
                    try (PreparedStatement fetchStmt = conn.prepareStatement(fetchSQL)) {
                        fetchStmt.setInt(1, randomId);
                        ResultSet flashcard = fetchStmt.executeQuery();
                        if (flashcard.next()) {
                            String question = flashcard.getString("question");
                            String answer = flashcard.getString("answer");

                            String userAnswer = JOptionPane.showInputDialog(null, "🤔 Question:\n" + question + "\n\nYour Answer:");
                            if (userAnswer != null) {
                                if (userAnswer.trim().equalsIgnoreCase(answer.trim())) {
                                    JOptionPane.showMessageDialog(null, "✅ Correct! Well done!");
                                } else {
                                    JOptionPane.showMessageDialog(null, "❌ Incorrect! The correct answer is: " + answer);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching flashcard: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
