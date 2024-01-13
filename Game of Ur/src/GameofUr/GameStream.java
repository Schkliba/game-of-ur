package GameofUr;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for easy ingame comunication. Methods are sychronised. Related TextArea serves as a lock.
 */
public final class GameStream {
    /**Central TextArea*/
    public final static JTextArea text = new JTextArea();
    public final static JLabel display = new JLabel();
    /**Listeners alerted on update*/
    static List<ActionListener> listeners = new ArrayList<ActionListener>();
    /**How many lines have been printed*/
    static int linecount =0;

    private GameStream(){

    }

    public static void putOnDisplay(String text){
        display.setText(text);
    }

    /**
     * Adds line to the stream.
     * @param line String to be printed in line.
     */
    public static void println(String line){
        synchronized (text) {
            text.append(line + "\n");
            linecount++;
            for (ActionListener l : listeners) {
                l.actionPerformed(new ActionEvent(text, 0, "Game Stream Updated"));
            }
        }
    }

    /**
     * Clears the stream.
     */
    public static void clear(){
        synchronized (text){
            text.setText("");
        }
    }
    

}
