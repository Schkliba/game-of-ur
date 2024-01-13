package GameofUr;

/**
 * Inteface allowing basic behaviour as a player in Game Thread.
 */
public interface IPlayer {
    Figure getColor();
    String getName();
    void setColor(Figure color);

    /**
     * Executing calculations needed for player's turn
     * @param board
     * @param diceroll
     * @return position of figure to be moved.
     */
    int myTurn(IBoard board, int diceroll);

}
