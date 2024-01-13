package GameofUr;

import javax.swing.*;

/**
 *@author František Dostál
 *
 */


public class Main {

    public static void main(String[] args) {


        final boolean hotseat = (args.length>0)&&(args[0].compareTo("-hotseat") == 0);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GameFrame game=new GameFrame("The Royal Game of Ur",hotseat);
            }
        });


    }
}
