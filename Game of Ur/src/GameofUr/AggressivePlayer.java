package GameofUr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI opponent realised with simple algrithm. Tries selecting the best move by evaluating nea surrounding of
 * each possible tile.
 */
public class AggressivePlayer extends MetaPlayer implements IPlayer {

    int[] tile_values = {1,4,6,4,1};
    Map<Figure,Integer> prefix_vals = new HashMap<Figure, Integer>();
    Map<Figure,Integer> sufix_vals = new HashMap<Figure, Integer>();
    Map<Figure,Integer> thistile = new HashMap<Figure, Integer>();
    final int rossete_value = 2;
    IBoard board;
    Figure enemy;
    public AggressivePlayer(String name){
        this.name = name;
    }
    @Override
    public void setColor(Figure color) {
        this.color = color;
        ;
        switch (color){
            case BLACK:
                enemy = Figure.WHITE;
                break;
            default:
                enemy = Figure.BLACK;
                break;
        }
        prefix_vals.put(enemy,3);
        prefix_vals.put(color, -1);
        prefix_vals.put(Figure.VACANT,1);
        sufix_vals.put(enemy,-2);
        sufix_vals.put(color, 1);
        sufix_vals.put(Figure.VACANT,0);
        thistile.put(enemy,5);
        thistile.put(color,0);
        thistile.put(Figure.VACANT,0);

    }
    @Override
    public int myTurn(IBoard board, int diceroll) {
        GameStream.println("Calculating!");
        this.board = board;
        List<Integer> actions = board.availableFigures(getColor(),diceroll);

        int[] scoreboard = new int[actions.size()];

        for(int i=0; i<actions.size();i++){

            int from = actions.get(i);
            int where = from + diceroll;
            scoreboard[i] = evaluateField(where);
        }

        int maxscore = scoreboard[0];
        int max_i = 0;
        for(int i=0; i<actions.size();i++){
           if(scoreboard[i]>maxscore){
               maxscore = scoreboard[i];
               max_i = i;
           }
        }

        return actions.get(max_i);
    }

    /**
     * Evaluation function based on 4 tile radius in front and behind of the tile.
     * @param position
     * @return
     */
    int evaluateField(int position){
        int prefix_sum = 0;
        int sufix_sum = 0;
        int this_tile = 0;
        for (int i = 1 ; i <5; i++){
            if (position+i<AbstractBoard.trackSize) {
                MetaTile tile = board.lookAt(getColor(), position+i);
                int fig_val= prefix_vals.get(tile.getOccupation());
                if (tile.type == Tile.HOME) fig_val+=4;
                if (tile.isSafe()&&tile.getOccupation()==enemy) fig_val=0;
                if (tile.isSafe()&&tile.getOccupation()==Figure.VACANT) fig_val+=2;
                prefix_sum += fig_val * tile_values[i];
            }
            if(position-i>0){
                MetaTile tile = board.lookAt(getColor(), position-i);
                MetaTile e_tile = board.lookAt(enemy, position-i);
                int fig_val= sufix_vals.get(tile.getOccupation())+sufix_vals.get(e_tile.getOccupation());
                sufix_sum += fig_val * tile_values[i];
            }
        }
        MetaTile tile = board.lookAt(getColor(), position);
        int fig_val = thistile.get(tile.getOccupation());
        if (tile.isSafe()) fig_val+=rossete_value;

        this_tile = fig_val * tile_values[0];

        return prefix_sum+sufix_sum+this_tile;

    }

}
