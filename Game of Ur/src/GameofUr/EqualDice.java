package GameofUr;

import java.util.Random;

/**
 * Crates number of unbiased dice with number of faces.
 */
public class EqualDice {
    Random rand;
    int count;
    int faces;
    int[] last_throw;
    int last_sum;

    public EqualDice(int count, int faces){
        this.count = count;
        this.faces = faces;
        last_throw = new int[count];
        rand = new Random();
    }

    /**
     * Generates sum of dice thrown.
     * @return
     */
    public int throwDice(){
        last_sum = 0;
        for(int i = 0; i<count; i++){
            last_throw[i] = rand.nextInt(this.faces);
            last_sum += last_throw[i];
        }
        return last_sum;
    }
    public int[] lastDice(){
        return last_throw;
    }
    public int lastThrow(){
        return last_sum;
    }
}
