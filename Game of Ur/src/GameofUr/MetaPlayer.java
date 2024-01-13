package GameofUr;

/**
 * Base class for Players to inherit basic functionality
 */
public abstract class MetaPlayer {
    Figure color;
    String name;
    public String getName(){ return name; }
    public void setColor(Figure color) {
        this.color = color;
    }
    public Figure getColor(){
        return this.color;
    }
}
