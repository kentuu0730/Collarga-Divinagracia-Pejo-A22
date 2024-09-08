import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class tildeTokenizer {

    // Enum for token types
    private enum TokenType {
        WORD,
        NUMBER,
        PUNCTUATION,
        ALPHANUMERIC,
        WHITESPACE,
        END_OF_LINE,
        UNKNOWN
    }


    // Method to classify tokens based on regex patterns
    private TokenType classifyToken(String value) {
        if (value.matches("[a-zA-Z]+")) {
            return TokenType.WORD;
        } else if (value.matches("\\d+")) {
            return TokenType.NUMBER;
        } else if (value.matches("-?\\d+(\\.\\d+)?")) {
            return TokenType.NUMBER;  // Updated to recognize decimal numbers
        }else if (value.matches("[a-zA-Z0-9]+")) {
            return TokenType.ALPHANUMERIC;
        } else if (value.matches("\\W+")) {
            return TokenType.PUNCTUATION;
        } else {
            return TokenType.UNKNOWN;  // fallback case for unclassified tokens
        }
    }

    // Token class
    private class Token {
        String value;
        TokenType type;

        Token(String value, TokenType type) {
            this.value = value;
            this.type = type;
        }

        @Override
        public String toString() {
            if (type == TokenType.END_OF_LINE) {
                return "Token: '\\n', Type: " + type;
            }
            return "Token: '" + value + "', Type: " + type;
        }
    }

    private boolean isPunctuation(char c) {
        int charType = Character.getType(c);
        return charType == Character.CONNECTOR_PUNCTUATION ||
               charType == Character.DASH_PUNCTUATION ||
               charType == Character.START_PUNCTUATION ||
               charType == Character.END_PUNCTUATION ||
               charType == Character.OTHER_PUNCTUATION ||
               charType == Character.MATH_SYMBOL ||
               charType == Character.CURRENCY_SYMBOL;
    }


    // Method to tokenize input string based on '~' delimiter
    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();

        // Split the input string by the tilde (~)
        String[] parts = input.split("~");

        for (String part : parts) {
            int length = part.length();
            StringBuilder token = new StringBuilder();
            TokenType type;

            type = classifyToken(part);
            if (type == TokenType.NUMBER) {
                tokens.add(new Token(part, type));
                continue;
            }
            
            
            for (int i = 0; i < length; i++) {
                char c = part.charAt(i);
                
                // If the character is a newline, treat it as a separate token
                if (c == '\n') {
                    if (token.length() > 0) {
                        type = classifyToken(token.toString());
                        tokens.add(new Token(token.toString(), type));
                        token.setLength(0);  // Clear the buffer for the next token
                    }
                    tokens.add(new Token("\\n", TokenType.END_OF_LINE));
                }
                // If the character is whitespace (but not newline), treat it as a separate token
                else if (Character.isWhitespace(c)) {
                    if (token.length() > 0) {
                        type = classifyToken(token.toString());
                        tokens.add(new Token(token.toString(), type));
                        token.setLength(0);  // Clear the buffer for the next token
                    }
                    tokens.add(new Token(Character.toString(c), TokenType.WHITESPACE));
                }
                // If the character is a punctuation or symbol, treat it as its own token
                else if (isPunctuation(c)) {
                    if (token.length() > 0) {
                        type = classifyToken(token.toString());
                        tokens.add(new Token(token.toString(), type));
                        token.setLength(0);  // Clear the buffer for the next token
                    }
                    // Add the punctuation token
                    
                    tokens.add(new Token(Character.toString(c), TokenType.PUNCTUATION));
                }
                // Otherwise, add the character to the current token (for words/numbers/alphanumerics)
                else {
                    token.append(c);
                }
            }
            // Add any remaining token at the end
            if (token.length() > 0) {
                type = classifyToken(token.toString());
                tokens.add(new Token(token.toString(), type));
            }
        }

        return tokens;
    }

    // Method to provide granular breakdown of tokens with 2 or more characters
    public List<String> granularBreakdown(List<Token> tokens) {
        List<String> breakdown = new ArrayList<>();
        for (Token token : tokens) {
            if (token.value.length() >= 2) {
                StringBuilder sb = new StringBuilder();
                sb.append("Token: '").append(token.value).append("' -> ");
                for (int i = 0; i < token.value.length(); i++) {
                    sb.append("'").append(token.value.charAt(i)).append("'");
                    if (i < token.value.length() - 1) {
                        sb.append(", ");
                    }
                }
                breakdown.add(sb.toString());
            }
        }
        return breakdown;
    }

    // GUI logic - Separate input and output windows
    private static class TokenizerGUI {

        private JFrame inputFrame, outputFrame;
        private JTextArea inputArea, outputArea;
        private JButton tokenizeButton, returnButton;

        public TokenizerGUI() {
            // Initial Input Window setup
            inputFrame = new JFrame("Tokenizer Input");
            inputFrame.setSize(600, 300);
            inputFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            inputFrame.setLayout(new BorderLayout());

            inputArea = new JTextArea(5, 50);
            inputArea.setFont(new Font("Arial", Font.PLAIN, 16));
            inputArea.setLineWrap(true);
            inputArea.setWrapStyleWord(true);
            JScrollPane inputScroll = new JScrollPane(inputArea);
            inputScroll.setBorder(BorderFactory.createTitledBorder("Input Text"));

            tokenizeButton = new JButton("Tokenize");
            tokenizeButton.setFont(new Font("Arial", Font.BOLD, 16));
            tokenizeButton.setPreferredSize(new Dimension(150, 50));
            tokenizeButton.setBackground(Color.BLUE);
            tokenizeButton.setForeground(Color.WHITE);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.LIGHT_GRAY);
            buttonPanel.add(tokenizeButton);

            inputFrame.add(inputScroll, BorderLayout.CENTER);
            inputFrame.add(buttonPanel, BorderLayout.SOUTH);

            // Output Window Setup
            outputFrame = new JFrame("Tokenizer Output");
            outputFrame.setSize(600, 400);
            outputFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            outputFrame.setLayout(new BorderLayout());

            outputArea = new JTextArea(10, 50);
            outputArea.setFont(new Font("Courier New", Font.PLAIN, 14));
            outputArea.setEditable(false);
            JScrollPane outputScroll = new JScrollPane(outputArea);
            outputScroll.setBorder(BorderFactory.createTitledBorder("Output"));

            returnButton = new JButton("Return");
            returnButton.setFont(new Font("Arial", Font.BOLD, 16));
            returnButton.setPreferredSize(new Dimension(150, 50));
            returnButton.setBackground(Color.RED);
            returnButton.setForeground(Color.WHITE);

            JPanel outputButtonPanel = new JPanel();
            outputButtonPanel.setBackground(Color.LIGHT_GRAY);
            outputButtonPanel.add(returnButton);

            outputFrame.add(outputScroll, BorderLayout.CENTER);
            outputFrame.add(outputButtonPanel, BorderLayout.SOUTH);

            // Tokenize Action - Open Output Window
            tokenizeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tildeTokenizer tokenizer = new tildeTokenizer();
                    String input = inputArea.getText();

                    // Phase 1: Tokenization
                    List<Token> tokens = tokenizer.tokenize(input);
                    StringBuilder phase1Output = new StringBuilder("Phase 1 Output:\n");
                    for (Token token : tokens) {
                        phase1Output.append(token).append("\n");
                    }

                    // Separator for Phase 1 and Phase 2
                    phase1Output.append("\n~~~~~\n");

                    // Phase 2: Granular Breakdown (only tokens with 2 or more characters)
                    List<String> breakdown = tokenizer.granularBreakdown(tokens);
                    StringBuilder phase2Output = new StringBuilder("\nPhase 2 Output (Granular Breakdown):\n");
                    for (String line : breakdown) {
                        phase2Output.append(line).append("\n");
                    }

                    // Display both phase outputs in output area
                    outputArea.setText(phase1Output.toString() + phase2Output.toString());

                    // Show output window and hide input window
                    inputFrame.setVisible(false);
                    outputFrame.setVisible(true);
                }
            });

            // Return Action - Close Output Window, Show Input Window
            returnButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    outputFrame.setVisible(false);
                    inputFrame.setVisible(true);
                }
            });
        }

        public void show() {
            inputFrame.setVisible(true);
        }
    }

    // Main method to run the GUI
    public static void main(String[] args) {
        // Launch the GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TokenizerGUI().show();
            }
        });
    }
}
