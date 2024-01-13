package GameofUr;

import java.awt.event.ActionListener;

/**
 * Inteface for central class that controls the game.
 */
public interface IReferee {
    IPlayer activePlayer();
    void startGame();
    void setListener(ActionListener listener);
    boolean isGameRunning();
    boolean isGameFinished();

}

