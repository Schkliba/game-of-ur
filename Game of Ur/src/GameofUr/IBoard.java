package GameofUr;

import java.util.*;

/**
 * Interface for Boards of Ur
 */
public interface IBoard {
    boolean move(Figure player, int figure, int diceroll);
    int figuresHome(Figure player);
    Figure isPlaying();
    void setPlaying(Figure player);
    MetaTile lookAt(Figure player, int position);
    List<Integer> availableFigures(Figure player,int diceroll);
    Figure[][] getFigureLayout();
}
