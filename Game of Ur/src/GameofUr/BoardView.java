package GameofUr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Represantation of tiles useful for rendering.
 */
class PhysicalTile{
    int left;
    int top;
    int width;
    int height;
    int number;
    int side;
    boolean selected = false;
    Tile type;

    public PhysicalTile(int number,int side,int left, int top, int width, int height, Tile type){
        this.number = number;
        this.side = side;
        this.height=height;
        this.top = top;
        this.left=left;
        this.width = width;
        this.type = type;
    }

    /**
     * Sets wheteher the tile is seleceted or not
     * @param value
     */
    public void select(boolean value){
        selected = value;
    }

    /**
     * Changes type of tile to the type in parametr.
     * @param type
     */
    public void reType(Tile type){
        this.type = type;
    }

    /**
     * Paints the tile with g.
     * @param g
     */
    public void paintTile(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        switch (type){
            case ROSETTE:
                g2.setColor(Color.red);
                break;
            case HOME:
                g2.setColor(Color.darkGray);
                break;
            case START:
                g2.setColor(Color.lightGray);
                break;
            default:
                g2.setColor(Color.pink);
        }
        if (selected) g2.setColor(Color.yellow);
        g2.fillRect(left,top,width,height);
        g2.setColor(Color.black);
        g2.drawRect(left,top,width,height);

    }

}


/**
 *  Class for rendering the gameboard.
 */
public class BoardView extends JPanel implements ActionListener{
    PhysicalTile[][] p_layout;
    PhysicalTile selected;
    IBoard abstract_board;
    int t_size = 100;

    public BoardView(IBoard board)
    {

        setBorder(BorderFactory.createLineBorder(Color.black,4));
        this.abstract_board = board;
        p_layout = new PhysicalTile[3][8];
        for (int x=0;x<3;x++){
            for (int y = 0; y<8;y++){
                int num=Integer.parseInt(""+AbstractBoard.track_layout[x].charAt(y),AbstractBoard.trackSize);
                p_layout[x][y]=new PhysicalTile(num, x,(x* t_size),(y* t_size), t_size, t_size, Tile.STANDARD);
                if ((x!=1&&(y%6)==0)||(x==1&&y==3)) p_layout[x][y].reType(Tile.ROSETTE);
                if(x!=1&&y==4) p_layout[x][y].reType(Tile.START);
                if(x!=1&&y==5) p_layout[x][y].reType(Tile.HOME);
            }
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                select(e.getX(),e.getY());
                repaint();
            }
        });
        selected = p_layout[0][4];
        selected.select(true);
    }

    /**
     * Translates real coordinates to selecting one tile.
     * @param Bx real co-ordinate x
     * @param By real co-ordinate y
     */
    private void select(int Bx, int By){

        int Tx = (Bx)/ t_size;
        int Ty = (By)/ t_size;
        //System.out.println(Tx+"-"+Ty);
        if (Tx>=0 && Tx<3 && Ty>=0 && Ty < 8){
            if (!(selected == null)) selected.select(false);
            selected = p_layout[Tx][Ty];
            selected.select(true);
        }


    }

    /**
     * Repaints the board on action.
     * @param e An ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    /**
     * Method drawing a figure with Graphics g.
     * @param x Where should it be drawn
     * @param y Where should it be drawn
     * @param type What should be drawn
     * @param g
     */
    private void paintFigure(int x, int y, Figure type, Graphics g){
        switch (type){
            case WHITE:
                g.setColor(Color.white);
                break;
            case BLACK:
                g.setColor(Color.black);
                break;
            default:
                return;
        }
        g.fillOval((x* t_size),(y* t_size),this.t_size, t_size);
        g.setColor(Color.black);
        g.drawOval((x* t_size),(y* t_size),this.t_size, t_size);
    }

    /**
     * Repaints the whole thing.
     * @param g
     */
    protected void paintComponent(Graphics g){

        Figure[][] f_layout = abstract_board.getFigureLayout();

        for (int x=0;x<3;x++){
            for (int y = 0; y<8;y++){
                p_layout[x][y].paintTile(g);
                paintFigure(x,y,f_layout[x][y],g);
            }
        }

    }


}

