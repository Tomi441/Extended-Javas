import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Timer;
import javax.swing.*;

public class Main {
    private static final int MAX_GUESSES = 7;
    private static final String WORDLIST_FILENAME = "wordlist.txt";
    private static ArrayList<String> targetWords = new ArrayList<>();
    private static String targetWord;
    private static int remainingGuesses;
    private static Set<Character> guessedLetters = new HashSet<>();
    private static JButton submitButton;
    private static JLabel incorrectGuessesLabel;
    private static StringBuilder incorrectGuesses = new StringBuilder();
    private static Timer timer;
    private static int timeLeft = 60;
    private static JLabel timerLabel;
    private static JLabel wordLabel;
    private static JLabel remainingLabel;
    private static JTextField inputField;

    public static void main(String[] args) throws FileNotFoundException {
        loadWordList();
        selectTargetWord();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                startTimer();
            }
        });
    }

    private static void loadWordList() throws FileNotFoundException {
        Scanner in = new Scanner(new File(WORDLIST_FILENAME));
        while (in.hasNext()) {
            String word = in.next();
            if (word.length() >= 7 && word.length() <= 10) {
                targetWords.add(word);
            }
        }
        in.close();
    }

    private static void selectTargetWord() {
        Random r = new Random();
        targetWord = targetWords.get(r.nextInt(targetWords.size()));
        remainingGuesses = MAX_GUESSES;
        guessedLetters.clear();
        System.out.println("The target word is: " + targetWord);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Hangman Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wordLabel = new JLabel(createHiddenWordString());
        wordLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        JPanel wordPanel = new JPanel();
        wordPanel.add(wordLabel);
        remainingLabel = new JLabel("Guesses remaining: " + remainingGuesses);
        remainingLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JPanel remainingPanel = new JPanel();
        remainingPanel.add(remainingLabel);
        inputField = new JTextField(1);
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processInput(frame);
            }
        });
        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processInput(frame);
            }
        });
        incorrectGuessesLabel = new JLabel("Incorrect guesses: ");
        incorrectGuessesLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JPanel incorrectGuessesPanel = new JPanel();
        incorrectGuessesPanel.add(incorrectGuessesLabel);
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Guess a letter: "));
        inputPanel.add(inputField);
        inputPanel.add(submitButton);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.getContentPane().add(wordPanel);
        frame.getContentPane().add(Box.createVerticalStrut(10));
        frame.getContentPane().add(remainingPanel);
        frame.getContentPane().add(Box.createVerticalStrut(10));
        frame.getContentPane().add(incorrectGuessesPanel);
        frame.getContentPane().add(Box.createVerticalStrut(10));
        frame.getContentPane().add(inputPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private static void processInput(JFrame frame) {
        String inputText = inputField.getText();
        if (inputText.length() == 1) {
            char inputChar = inputText.charAt(0);
            if (Character.isLetter(inputChar)) {
                inputChar = Character.toLowerCase(inputChar);
                if (guessedLetters.contains(inputChar)) {
                    JOptionPane.showMessageDialog(frame, "You already guessed this letter.");
                } else {
                    guessedLetters.add(inputChar);
                    if (targetWord.indexOf(inputChar) == -1) {
                        remainingGuesses--;
                        incorrectGuesses.append(inputChar).append(" ");
                        incorrectGuessesLabel.setText("Incorrect guesses: " + incorrectGuesses.toString());
                        if (remainingGuesses == 0) {
                            JOptionPane.showMessageDialog(frame, "You lose. The word was " + targetWord + ".");
                            selectTargetWord();
                            resetGame();
                        } else {
                            remainingLabel.setText("Guesses remaining: " + remainingGuesses);
                        }
                    } else {
                        wordLabel.setText(createHiddenWordString());
                        if (targetWord.equals(getCurrentWord())) {
                            JOptionPane.showMessageDialog(frame, "You win!");
                            selectTargetWord();
                            resetGame();
                        }
                    }
                }
                inputField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a letter.");
                inputField.setText("");
            }
        }
    }

    private static String createHiddenWordString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < targetWord.length(); i++) {
            char c = targetWord.charAt(i);
            if (guessedLetters.contains(c)) {
                sb.append(c);
            } else {
                sb.append("_");
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    private static String getCurrentWord() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < targetWord.length(); i++) {
            char c = targetWord.charAt(i);
            if (guessedLetters.contains(c)) {
                sb.append(c);
            } else {
                sb.append("_");
            }
        }
        return sb.toString();
    }

    private static void startTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timeLeft--;
                timerLabel.setText("Time left: " + timeLeft + "s");
                if (timeLeft == 0) {
                    timer.cancel();
                    JOptionPane.showMessageDialog(null, "Time's up! You lose. The word was " + targetWord + ".");
                    selectTargetWord();
                    resetGame();
                }
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }


    private static void resetGame() {
        wordLabel.setText(createHiddenWordString());
        remainingLabel.setText("Guesses remaining: " + remainingGuesses);
        incorrectGuesses.setLength(0);
        incorrectGuessesLabel.setText("Incorrect guesses: ");
        inputField.requestFocusInWindow();
    }
}

