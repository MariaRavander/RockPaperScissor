package server.model;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import server.net.PlayerHandler;

/**
 *
 * @author yuchen
 */
public class Game {
    private final List<String> entries = Collections.synchronizedList(new ArrayList<>());
    private boolean gameRound = false;
    private int noOfPlayers = 0;
    private int noOfChoices = 0;
    public String word;
    public String currentState = "";
    public char[] letterArray;
    public char[] dashes;
    private boolean first = true;
    int remainingGuesses = 0;
    public int score = 0;
    
    PlayerHandler player1;
    PlayerHandler player2;
    PlayerHandler player3;
    String choice1;
    String choice2;
    String choice3;

    public void appendEntry(String msg) {
        entries.add(msg);
    }

    public String[] getGameStatus() {
        return entries.toArray(new String[0]);
    }

    public void selectedWord() throws IOException {
        first = true;
        Path path = Paths.get("C:\\Users\\maria\\Desktop\\Nätverksprogrammering\\RPS\\src\\resources\\words.txt");
        Stream<String> lines = Files.lines(path);
        long numberOfLines = lines.count();
        word = "";
        Random ran = new Random();
        int wordLine = ran.nextInt((int) numberOfLines);
        //word = Files.lines(path).skip(wordLine - 1).findFirst().get().toUpperCase();
        letterArray = word.toCharArray();
        remainingGuesses = word.length();
        System.out.println("word = " + word);
        System.out.println();
        showCurrentState();
    }

    public void emptyWord() {
        first = false;
        dashes = new char[letterArray.length];
        for (int i = 0; i < dashes.length; i++)
            dashes[i] = '_';
    }
    
    public synchronized boolean prepareGame() {
        boolean startGame = false;
        noOfPlayers++;
        if(noOfPlayers == 3) {
            startGame = true;
            noOfPlayers = 0;
        }
        return startGame;
    }

    public synchronized boolean playGame(String choice, PlayerHandler handler) {
        noOfChoices++;
        if(noOfChoices == 1) {
            player1 = handler;
            choice1 = choice;
        }
        else if(noOfChoices == 2) {
            player2 = handler;
            choice2 = choice;
        }
        else {
            player3 = handler;
            choice3 = choice;
        }
        if(noOfChoices == 3) {
            System.out.println("in nuber of choice");
            calculateScore();
            noOfChoices = 0;
            return true;
        }
        else
            return false;
    }
    
    public String gameResult() {
        StringJoiner join = new StringJoiner("##");
        join.add(player1.getUsername());
        join.add(player1.roundScore + "");
        join.add(player1.totalScore + "");
        join.add(player2.getUsername());
        join.add(player2.roundScore + "");
        join.add(player2.totalScore + "");
        join.add(player3.getUsername());
        join.add(player3.roundScore + "");
        join.add(player3.totalScore + "");
        
        player1.roundScore = 0;
        player2.roundScore = 0;
        player3.roundScore = 0;
        return join.toString();
    }
    
    private void calculateScore() {
        if(choice1.equals("paper")) {
            if(choice2.equals("rock")) {
                player1.roundScore++;
                }
            if(choice3.equals("rock"))
                player1.roundScore++;
        }
        else if(choice1.equals("rock")) {
            if(choice2.equals("scissor"))
                player1.roundScore++;
            if(choice3.equals("scissor"))
                player1.roundScore++;
        } 
        else {
            if(choice2.equals("paper"))
                player1.roundScore++;
            if(choice3.equals("paper"))
                player1.roundScore++;
        }
        
        if(choice2.equals("paper")) {
            if(choice1.equals("rock"))
                player2.roundScore++;
            if(choice3.equals("rock"))
                player2.roundScore++;
        }
        else if(choice2.equals("rock")) {
            if(choice1.equals("scissor"))
                player2.roundScore++;
            if(choice3.equals("scissor"))
                player2.roundScore++;
        } 
        else {
            if(choice1.equals("paper"))
                player2.roundScore++;
            if(choice3.equals("paper"))
                player2.roundScore++;
        }
        
        if(choice3.equals("paper")) {
            if(choice1.equals("rock"))
                player3.roundScore++;
            if(choice2.equals("rock"))
                player3.roundScore++;
        }
        else if(choice3.equals("rock")) {
            if(choice1.equals("scissor"))
                player3.roundScore++;
            if(choice2.equals("scissor"))
                player3.roundScore++;
        } 
        else {
            if(choice1.equals("paper"))
                player3.roundScore++;
            if(choice2.equals("paper"))
                player3.roundScore++;
        }
        
        player1.totalScore = player1.totalScore + player1.roundScore;
        player2.totalScore = player2.totalScore + player2.roundScore;
        player3.totalScore = player3.totalScore + player3.roundScore;
    }   
    
    public boolean correctWord() {
        String dash = new String(dashes);

        if(dash.toUpperCase().compareTo(word) == 0) {
            score++;
            return true;
        }
        else 
            return false;
    }
    
    public String showCurrentState() {
        currentState = "";
        if (first == true) emptyWord();
        
        for(int i=0; i<dashes.length; i++) {
            currentState = currentState + dashes[i] + " ";
        }
        return currentState;
    }
    
    public int remainingGuesses() {
        if(remainingGuesses == 0)
            score--;
        return remainingGuesses;
    }
        
    private boolean checkAndUpdateLetter (char[] letters, char letter) {
        boolean right = false;
        System.out.println(letter);
        for (int i = 0; i < letters.length; i++) {
            if (letters[i] == letter) {
               right = true;
               dashes[i] = letter;
            } 
        }
        return right;
    }
}
