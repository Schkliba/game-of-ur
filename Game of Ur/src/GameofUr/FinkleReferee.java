package GameofUr;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Central class of the game. Running in accordance with version of Ur proposed by prof. Irving Finkel.
 */
public class FinkleReferee implements IReferee, Runnable {
    EqualDice dice = new EqualDice(4,2);
    IPlayer firstplayer;
    IPlayer secondplayer;
    IBoard  board;
    Thread current_thread ;

    final int figure_count = 7;

    boolean game_finished = false;
    boolean game_running = false;

    List<ActionListener> listener = new ArrayList<ActionListener>();

    public FinkleReferee(IBoard board,IPlayer player1, IPlayer player2){
        this.board = board;
        firstplayer = player1;
        firstplayer.setColor(Figure.WHITE);
        secondplayer = player2;
        secondplayer.setColor(Figure.BLACK);


    }

    /**
     * LSets listener. Listeners are set-off on begining of each round.
     * @param listener
     */
    public void setListener(ActionListener listener) {
        this.listener.add(listener);
    }

    /**
     * Internaly changes place of active ad passive player
    */
    private void setoffAction(){
        for(ActionListener a:listener){
            a.actionPerformed(new ActionEvent(this,a.hashCode(),"RoundStrts"));
        }
    }
    private void swapPlayers(){
        IPlayer change = firstplayer;
        firstplayer = secondplayer;
        secondplayer = change;
    }

    /**
     *  Actual game thread. Human Player has to temporaly interrupt it.
     */
    public void run(){

        while (board.figuresHome(firstplayer.getColor()) != figure_count ||
            board.figuresHome(secondplayer.getColor()) != figure_count){

            board.setPlaying(firstplayer.getColor());
            GameStream.println("Active player: "+firstplayer.getName());
            int sum = dice.throwDice();
            GameStream.println("You've thrown: "+sum);
            GameStream.putOnDisplay(Integer.toString(sum));
            if (board.availableFigures(firstplayer.getColor(),sum).size()==0){
                GameStream.println("No available actions. Turn lost!");
                swapPlayers();
                continue;
            }
            setoffAction();
            int figure = firstplayer.myTurn(board,sum);

            boolean where = board.move(firstplayer.getColor(),figure,sum);
            GameStream.println("Chosen: "+figure);
            if (where){
                GameStream.println("Go again!");
                continue;
            }
            swapPlayers();

        }

        if (board.figuresHome(firstplayer.getColor()) == figure_count) GameStream.println("The Winner is: "+ firstplayer.getName());
        else GameStream.println("The Winner is: "+ secondplayer.getName());
        game_running = false;
        game_finished = true;
    }

    /**
     *  Starts Game thread.
     */
    @Override
    public void startGame() {
        game_running = true;
        current_thread = new Thread(this);
        current_thread.setDaemon(true);
        current_thread.start();
    }

    @Override
    public boolean isGameFinished() {
        return game_finished;
    }

    /**
     *
     * @return Player currently active (playing)
     */
    @Override
    public IPlayer activePlayer() {
        return firstplayer;
    }

    /**
     * Checks if current thread is Alive
     * @return Thread.isAlive
     */
    @Override
    public boolean isGameRunning() {
        return game_running;
    }
}
