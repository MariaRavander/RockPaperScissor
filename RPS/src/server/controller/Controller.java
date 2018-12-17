package server.controller;

import server.model.Game;
import java.io.IOException;
import server.net.PlayerHandler;
/**
 *
 * @author yuchen
 */
public class Controller {
    private final Game game = new Game();
  
    public void appendToHistory(String msg) {
       game.appendEntry(msg);
    }
    
    public String[] getGameStatus() {
        return game.getGameStatus();
    }
    
    public boolean prepareGame() {
        return game.prepareGame();
    }

    public String showCurrentState() { return game.showCurrentState(); }
    
    public int remainingGuesses() { return game.remainingGuesses(); }
    
    public boolean correctWord() { return game.correctWord(); }
    
    public String getWord() { return game.word; }
    
    public int score() { return game.score; }
    
    public boolean sendChoice(String choice, PlayerHandler handler) {
        return game.playGame(choice, handler);
    }

    public String getResult() {
        return game.gameResult();
    }
    public void selectedWord() {
        try {
           game.selectedWord();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
