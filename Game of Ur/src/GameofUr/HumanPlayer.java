package GameofUr;

import java.util.List;

/**
 * Functions like a simple Thread lock to give Player time to think.
 */
public class HumanPlayer extends MetaPlayer implements IHuman, IPlayer {
    int select;
    int current_diceroll;
    BoardView my_view;
    IBoard board;
    boolean onTurn = false;
    public  HumanPlayer(BoardView my_view,String name){
        this.name = name;
        this.my_view = my_view;
    }

    /**
     * Start turn and locks Game Thread.
     * @param board
     * @param diceroll
     * @return What figure we move on the board.
     */
    @Override
    public synchronized int myTurn(IBoard board, int diceroll) {

        this.board = board;
        this.current_diceroll = diceroll;

        onTurn = true;

        try{
            while(onTurn){
                wait();
            }
        } catch (InterruptedException e){
            GameStream.println("Game thread interrupted!");
            return board.availableFigures(getColor(),diceroll).get(0);
        }
        return select;
    }


    /**
     * Unlocks the Game Thread
     * @param figure decision on which figue to move
     * @return True if this figure can be selected. Otherwise False.
     */
    @Override
    public synchronized boolean select(int figure) {
            List<Integer> actions = board.availableFigures(color,current_diceroll);
            if(actions.contains(figure)){
                select = figure;
                onTurn = false;
                notify();
                return true;
            }


        return false;
    }

}
