package GameofUr;

import java.util.*;

/**
 * Root class for non-graphical tile represantation
 */
abstract class MetaTile{
    volatile Figure occupaton;
    Tile type;
    int x;
    int y;
    public MetaTile(int x, int y){
        this.x = x;
        this.y = y;
        this.occupaton = Figure.VACANT;
    }

    /**
     * Places figure of certain type on the tile. Returns what is taken.
     * @param figure
     * @return VACANT if previously empty otherwise type of figure being removed.
     */
    abstract public Figure putFigure(Figure figure);

    /**
     * Lifts a figure from the tile. Leaves tile VACANT.
     * @return type previously occupying the tile.
     */
    abstract  public Figure popFigure();

    /**
     * Tell us if tile is rossete
     * @return is tile safe
     */
    abstract public boolean isSafe();

    public boolean isVacant(){
        return occupaton==Figure.VACANT;
    }

    /**
     * Method answers who is on the tile
     * @return type of figure on the tile
     */
    abstract public Figure getOccupation();

    /**
     * Checks conditions for movement
     * @param figure
     * @return
     */
    public boolean isVacantFor(Figure figure){
        return figure==Figure.VACANT || occupaton ==Figure.VACANT||(occupaton!=figure && !isSafe());
    }
}

/**
 *  Start and Home GameofUr.Tile. Is permanently owned
 */
class OwnedTile extends  MetaTile{
    int fig_count;

    public OwnedTile(int x,int y, int figures, Figure occupation){
        super(x,y);
        this.occupaton = occupation;
        this.fig_count = figures;
        type = Tile.HOME;
    }
    public synchronized int getCount(){
        return fig_count;
    }
    @Override
    public synchronized Figure getOccupation() {
        if(fig_count>0)return occupaton;
        return Figure.VACANT;
    }


    @Override
    public synchronized Figure putFigure(Figure figure) {
        if(figure == occupaton){
            fig_count++;
            return Figure.VACANT;
        }else return figure;

    }
    @Override
    public synchronized boolean isVacant(){
        return fig_count==0;
    }

    @Override
    public synchronized boolean isVacantFor(Figure color){
        return occupaton == color;
    }

    @Override
    public synchronized Figure popFigure() {
        if (fig_count ==0) return Figure.VACANT;
        fig_count--;
        Figure fig = occupaton;
        return fig;
    }

    @Override
    public boolean isSafe() {
        return false;
    }
}

/**
 * GameofUr.Tile with no pemanent owner.
 */
abstract class UnownedTile extends  MetaTile{
    public UnownedTile(int x, int y){
        super(x,y);
    }
    public synchronized Figure putFigure(Figure figure) {
        Figure fig = occupaton;
        occupaton = figure;
        return fig;
    }
    @Override
    public synchronized Figure getOccupation() {
        return occupaton;
    }
    public synchronized Figure popFigure(){
        Figure fig = occupaton;
        occupaton = Figure.VACANT;
        return fig;
    }
}
/**
 *  Representing non-special tile.
 */
class StandardTile extends UnownedTile{
    public StandardTile(int x, int y){
        super(x,y);
        type = Tile.STANDARD;
    }
    @Override
    public boolean isSafe() {
        return false;
    }
}

/**
 *  Representing Rossete Type GameofUr.Tile.
 */
class RosseteTile extends UnownedTile{
    public RosseteTile(int x, int y){
        super(x,y);
        type = Tile.ROSETTE;
    }
    @Override
    public boolean isSafe() {
        return true;
    }
}

/**
 * Representation of single track in the game
 */
class CounterTrack{
    OwnedTile home;
    OwnedTile start;
    MetaTile[] track;
    Figure aliegence;
    boolean repeat_last_move =false;

    public CounterTrack(MetaTile[] track){
        this.track = track;
        this.start = (OwnedTile) track[0];
        aliegence = start.occupaton;
        this.home = (OwnedTile) track[track.length-1];
    }

    /**
     * Given a diceroll which position is able to move.
     * @param diceRoll
     * @return
     */
    public synchronized List<Integer> activeFigures(int diceRoll){
        List<Integer> figures = new ArrayList<Integer>();
        if (diceRoll<1) return figures;
        for(int i =0; i< track.length-diceRoll; i++){
            if (track[i].getOccupation()==aliegence && track[i+diceRoll].isVacantFor(aliegence)){
                figures.add(i);

            }
        }

        return figures;
    }

    /**
     * Moves content of a tile to the next. Returns what was replaced.
     * @param from
     * @param where
     * @return Type of figure replaced during movement
     */
    public synchronized Figure moveAndTake(int from, int where){
        Figure taken = track[where].getOccupation();
        Figure moving = track[from].popFigure();


        track[where].putFigure(moving);
        if (track[where].type == Tile.ROSETTE) repeat_last_move = true;
        else repeat_last_move = false;

        return taken;
    }

    /**
     * Giving back the figure on start
     * @param fig
     */
    public synchronized void returnFigure(Figure fig){
        start.putFigure(fig);
    }

    /**
     * How many figures are secured in the Home
     * @return
     */
    public synchronized int homeCount(){
        return home.getCount();
    }
}

/**
 * Representation of figures on the board.
 */
enum  Figure{
    VACANT,WHITE,BLACK
}

/**
 * Container for important constants. Also base for possible future Board types
 */
abstract class AbstractBoard{
        int figures;
        static final int boardHeight = 8;
        static final int trackSize = 16;
        static final int boardWidht = 3;
        static final String[] track_layout = {
                "43210FED",
                "56789ABC",
                "43210FED"
        };
        static final String[] safe_layout = {
            "R---wWR-",
            "---R---T",
            "R---bBR-"
        };
}

/**
 * Representation of the "abstract(non-graphical) board".
 */
public class UrBoard extends AbstractBoard implements IBoard {
    Figure playing;
    MetaTile[][] layout;
    Map<Figure,CounterTrack> tracks;

    public UrBoard(int figures){
        this.figures = figures;
        getTileLayout();
        makeCounters();
    }

    @Override
    public void setPlaying(Figure playing) {
        this.playing = playing;
    }

    public Figure isPlaying(){
        return playing;
    }

    /**
     * Transforms constant into useful layout.
     * @return
     */
    private MetaTile[][] getTileLayout(){
        this.layout = new MetaTile[boardWidht][boardHeight];
        for(int x = 0; x<boardWidht; x++){
            for (int y = 0; y<boardHeight;y++){
                switch (safe_layout[x].charAt(y)){
                    case 'w':
                        this.layout[x][y] = new OwnedTile(x,y,figures,Figure.WHITE);
                        break;
                    case 'b':
                        this.layout[x][y] = new OwnedTile(x,y,figures,Figure.BLACK);
                        break;
                    case 'W':
                        this.layout[x][y] = new OwnedTile(x,y,0,Figure.WHITE);
                        break;
                    case 'B':
                        this.layout[x][y] = new OwnedTile(x,y,0,Figure.BLACK);
                        break;
                    case 'R':
                        this.layout[x][y] = new RosseteTile(x,y);
                        break;
                    default:
                        this.layout[x][y] = new StandardTile(x,y);
                        break;
                }
            }
        }
        return this.layout;
    }

    /**
     * Puts layout into two distinct linear tracks that can be accesed separately.
     */
    private void makeCounters(){

        MetaTile[] left_track = new MetaTile[trackSize];
        MetaTile[] right_track = new MetaTile[trackSize];
        tracks = new HashMap<Figure, CounterTrack>();
        for(int x = 0; x<boardWidht; x++){
            for (int y = 0; y<boardHeight;y++){
                int num = Integer.parseInt(""+track_layout[x].charAt(y),trackSize);
                if (x<2) left_track[num] = this.layout[x][y];
                if (x>0) right_track[num] = this.layout[x][y];
            }
        }
        CounterTrack left_c = new CounterTrack(left_track);
        CounterTrack right_c = new CounterTrack(right_track);
        tracks.put(right_c.aliegence,right_c);
        tracks.put(left_c.aliegence,left_c);
    }

    /**
     * Method to get current layout outside of the instance.
     * @return Current figure placement on board.
     */
    @Override
    public synchronized Figure[][] getFigureLayout() {
        Figure[][] ret_val = new Figure[boardWidht][boardHeight];
        for(int x = 0; x<boardWidht; x++){
            for (int y = 0; y<boardHeight;y++){
               ret_val[x][y] = this.layout[x][y].getOccupation();

            }
        }
        return ret_val;
    }

    /**
     * Gives acces to particular tiles.
     * @param player Owner of the track
     * @param position Position in he track
     * @return
     */
    @Override
    public synchronized MetaTile lookAt(Figure player, int position) {
        if (position>tracks.get(player).track.length || position<0) return null;
        return tracks.get(player).track[position];
    }

    /**
     * Moves figre in player's track by amout of diceroll
     * @param player
     * @param figure
     * @param diceroll
     * @return
     */
    @Override
    public synchronized boolean move(Figure player, int figure, int diceroll) {
        Figure taken = tracks.get(player).moveAndTake(figure,figure+diceroll);
        if (taken != Figure.VACANT) tracks.get(taken).returnFigure(taken);
        return tracks.get(player).repeat_last_move;
    }

    /**
     * How many figures has player already secured in his home.
     * @param player
     * @return
     */
    @Override
    public int figuresHome(Figure player) {
        return tracks.get(player).homeCount();
    }

    /**
     * Given a diceroll which figures (positions) in player's track can move
     * @param player
     * @param diceroll
     * @return
     */
    @Override
    public synchronized List<Integer> availableFigures(Figure player, int diceroll) {
        return tracks.get(player).activeFigures(diceroll);
    }
}


