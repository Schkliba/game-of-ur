package GameofUr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class representing the game as a whole.
 */
public class GameFrame implements ActionListener{
    JFrame frame;
    IReferee judge;
    JButton doit;
    BoardView view;
    String name;
    boolean hotspot;
    public GameFrame (String name, boolean hotspot){
        this.name=name;
        this.hotspot=hotspot;
        restart();

    }

    /**
     * Defines functionality of GO! button
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e){
        if (judge.isGameFinished()){
            frame.dispose();
            GameStream.clear();
            restart();
        }
        if (!judge.isGameRunning()){
            GameStream.println("New Game Begins!");
            this.judge.startGame();
            return;
        }

        IPlayer current = judge.activePlayer();
        if (current instanceof IHuman){
            if (((view.selected.side < 2)&&(current.getColor()==Figure.WHITE))||
                    ((view.selected.side > 0)&&(current.getColor()==Figure.BLACK))){
                if(!((IHuman)current).select(view.selected.number))GameStream.println("Inactive field");
            } else GameStream.println("Inactive field");


        }
        view.repaint();

    }

    /**
     * Defines preparation for the game to run.
     */
    public void restart(){

        frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        doit = new JButton("GO!");
        frame.add(doit);
        Dimension size = doit.getPreferredSize();
        Insets insets = frame.getInsets();

        doit.setBounds(insets.left+20,insets.top+200,200,100);
        doit.setFont(new Font("Arial", Font.PLAIN, 50));
        JTextArea log = GameStream.text;
        JLabel display = GameStream.display;
        display.setBounds(70,50,100,100);
        display.setFont(new Font("Arial", Font.PLAIN, 40));
        display.setHorizontalAlignment(SwingConstants.CENTER);
        display.setBorder(BorderFactory.createLineBorder(Color.black));
        log.setEditable(false);
        JScrollPane scroll = new JScrollPane(log);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setBounds(insets.left+20,insets.top+350,200,400);
        log.setBorder(BorderFactory.createLineBorder(Color.black));
        log.setBackground(Color.white);
        frame.add(display);
        frame.add(scroll);
        doit.addActionListener(this);
        IBoard metaboard = new UrBoard(7); //Creating Boards

        view = new BoardView(metaboard);
        frame.add(view);
        view.setBounds(insets.left+300,insets.top+50,300, 800);
        //inicialisation of players and judge
        IPlayer player2;
        if(hotspot) player2 = new HumanPlayer(view,"Chakal");
        else player2 = new AggressivePlayer("Hippo");

        this.judge =  new FinkleReferee(metaboard, new HumanPlayer(view,"Lion"),player2);
        judge.setListener(view);


        frame.setSize(900,900);
        frame.setVisible(true);
        frame.repaint();
    }
}
