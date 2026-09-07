import java.util.ArrayList;

/**
 * Representa una rueda (reel) de la maquina tragamonedas. Guarda los
 * simbolos (colores) que se han colocado, cual queda al frente, y si
 * la rueda esta fija (no debe girar).
 *
 * @author Sergio Gonzalez, Juan Alvarez
 * @version 2.0
 */
public class Wheel{

    private ArrayList<String> symbols;
    private int front;
    private Circle circle;
    private int shownPosition;
    private boolean locked;

    /**
     * Crea una rueda vacia, sin simbolos y sin fijar.
     */
    public Wheel(){
        symbols = new ArrayList<String>();
        front = -1;
        circle = null;
        shownPosition = -1;
        locked = false;
    }

    /**
     * Coloca un simbolo en la rueda. Queda como el simbolo al frente.
     * @param color color del simbolo a colocar.
     */
    public void place(String color){
        symbols.add(color);
        front = symbols.size() - 1;
    }

    /**
     * Retorna el color del simbolo que esta al frente de la rueda,
     * o null si la rueda no tiene simbolos.
     */
    public String getFrontColor(){
        if(front == -1){
            return null;
        }
        return symbols.get(front);
    }

    /**
     * Retorna cuantos simbolos se han colocado en la rueda.
     */
    public int size(){
        return symbols.size();
    }

    /**
     * Fija la rueda: mientras este fija no debe girar.
     */
    public void lock(){
        locked = true;
    }

    /**
     * Suelta la rueda previamente fijada.
     */
    public void unlock(){
        locked = false;
    }

    /**
     * Indica si la rueda esta fija.
     */
    public boolean isLocked(){
        return locked;
    }

    /**
     * Muestra esta rueda en el Canvas, en la posicion indicada (1, 2, 3...
     * de izquierda a derecha), con el color del simbolo al frente. Si la
     * rueda ya se estaba mostrando en esa misma posicion, no se recrea ni
     * se reposiciona el circulo, solo se actualiza su color; la posicion
     * solo se recalcula cuando realmente cambia (por ejemplo al agregar,
     * eliminar o intercambiar ruedas).
     * @param position posicion de esta rueda entre las demas (desde 1).
     */
    public void showAt(int position){
        if(circle == null){
            circle = new Circle();
            circle.moveHorizontal((position - 1) * 60);
            circle.moveVertical(85);
            shownPosition = position;
        } else if(position != shownPosition){
            circle.moveHorizontal((position - shownPosition) * 60);
            shownPosition = position;
        }
        String frontColor = getFrontColor();
        if(frontColor != null){
            circle.changeColor(frontColor);
        }
        circle.makeVisible();
    }

    /**
     * Oculta la representacion visual de esta rueda.
     */
    public void hide(){
        if(circle != null){
            circle.makeInvisible();
        }
    }

    /**
     * Resalta esta rueda (la hace mas grande) para mostrar un estado ganador.
     */
    public void highlight(){
        if(circle != null){
            circle.changeSize(50);
        }
    }

    /**
     * Vuelve esta rueda a su tamano normal.
     */
    public void unhighlight(){
        if(circle != null){
            circle.changeSize(30);
        }
    }
}