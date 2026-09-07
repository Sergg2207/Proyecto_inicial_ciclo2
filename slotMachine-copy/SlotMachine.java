import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Simula una maquina tragamonedas. La maquina tiene un catalogo de
 * simbolos disponibles (colores) y un conjunto de ruedas. Los simbolos
 * del catalogo se colocan en las ruedas, las ruedas se pueden girar o
 * fijar, se pueden intercambiar entre si, y se puede consultar la
 * configuracion actual (simbolo al frente de cada rueda) para ver si
 * hay una combinacion ganadora.
 *
 * @author Sergio Gonzalez, Juan Alvarez
 * @version 2.0
 */


 public class SlotMachine{
 
    private ArrayList<Wheel> wheels;
    private ArrayList<String> symbolCatalog;
    private boolean lastOperationOk;
    private boolean visible;
    private Random random;
 
    /**
     * Crea una maquina nueva, sin ruedas ni simbolos.
     */
    public SlotMachine(){
        wheels = new ArrayList<Wheel>();
        symbolCatalog = new ArrayList<String>();
        lastOperationOk = true;
        visible = false;
        random = new Random();
    }
 
    /**
     * Indica si la ultima operacion realizada se pudo completar.
     * @return true si la ultima operacion fue exitosa, false si no.
     */
    public boolean ok(){
        return lastOperationOk;
    }
 
    /**
     * Agrega una rueda vacia en la posicion indicada. Las posiciones
     * empiezan en 1; si pos es menor a 1 se usa 1, y si es mayor al
     * maximo posible se usa el maximo.
     * @param pos posicion deseada para la nueva rueda.
     */
    public void addWheel(int pos){
        int validPos = clamp(pos, wheels.size() + 1);
        wheels.add(validPos - 1, new Wheel());
        lastOperationOk = true;
        refreshVisuals();
    }
 
    /**
     * Elimina la rueda en la posicion indicada (posiciones desde 1,
     * ajustadas igual que en addWheel).
     * @param pos posicion de la rueda a eliminar.
     */
    public void delWheel(int pos){
        if(wheels.isEmpty()){
            lastOperationOk = false;
            reportError("No hay ruedas para eliminar.");
            return;
        }
        int validPos = clamp(pos, wheels.size());
        Wheel removed = wheels.remove(validPos - 1);
        removed.hide();
        lastOperationOk = true;
        refreshVisuals();
    }
 
    /**
     * Intercambia entre si las ruedas en las dos posiciones indicadas
     * (posiciones desde 1, ajustadas igual que en addWheel). El estado
     * de cada rueda (simbolos y si esta fija) viaja con ella.
     * @param wheel1 posicion de la primera rueda.
     * @param wheel2 posicion de la segunda rueda.
     */
    public void swap(int wheel1, int wheel2){
        if(wheels.size() < 2){
            lastOperationOk = false;
            reportError("Se necesitan al menos dos ruedas para intercambiar.");
            return;
        }
        int pos1 = clamp(wheel1, wheels.size());
        int pos2 = clamp(wheel2, wheels.size());
        Wheel temp = wheels.get(pos1 - 1);
        wheels.set(pos1 - 1, wheels.get(pos2 - 1));
        wheels.set(pos2 - 1, temp);
        lastOperationOk = true;
        refreshVisuals();
    }
 
    /**
     * Fija la rueda en la posicion indicada: mientras este fija no gira,
     * ni con spin(wheel), spin(wheel,steps) ni con spin().
     * @param wheel posicion de la rueda a fijar (desde 1).
     */
    public void lock(int wheel){
        if(wheels.isEmpty()){
            lastOperationOk = false;
            reportError("No hay ruedas para fijar.");
            return;
        }
        int validPos = clamp(wheel, wheels.size());
        wheels.get(validPos - 1).lock();
        lastOperationOk = true;
    }
 
    /**
     * Suelta la rueda en la posicion indicada, previamente fijada.
     * @param wheel posicion de la rueda a soltar (desde 1).
     */
    public void unlock(int wheel){
        if(wheels.isEmpty()){
            lastOperationOk = false;
            reportError("No hay ruedas para soltar.");
            return;
        }
        int validPos = clamp(wheel, wheels.size());
        wheels.get(validPos - 1).unlock();
        lastOperationOk = true;
    }
 
    /**
     * Agrega un simbolo nuevo al catalogo de la maquina, en la posicion
     * indicada (ajustada igual que en addWheel).
     * @param pos posicion deseada para el nuevo simbolo.
     * @param color color del nuevo simbolo (nombre valido en CSS).
     */
    public void addSymbol(int pos, String color){
        int validPos = clamp(pos, symbolCatalog.size() + 1);
        symbolCatalog.add(validPos - 1, color);
        lastOperationOk = true;
    }
 
    /**
     * Elimina un simbolo del catalogo de la maquina.
     * @param symbol color del simbolo a eliminar.
     */
    public void delSymbol(String symbol){
        boolean removed = symbolCatalog.remove(symbol);
        lastOperationOk = removed;
        if(!removed){
            reportError("El simbolo \"" + symbol + "\" no existe en el catalogo.");
        }
    }
 
    /**
     * Coloca un simbolo del catalogo en una rueda. El simbolo queda
     * al frente de esa rueda.
     * @param wheel posicion de la rueda (desde 1).
     * @param symbol color del simbolo a colocar (debe existir en el catalogo).
     */
    public void placeSymbol(int wheel, String symbol){
        if(wheels.isEmpty() || !symbolCatalog.contains(symbol)){
            lastOperationOk = false;
            reportError("No se pudo colocar el simbolo \"" + symbol + "\".");
            return;
        }
        int validPos = clamp(wheel, wheels.size());
        Wheel selectedWheel = wheels.get(validPos - 1);
        selectedWheel.place(symbol);
        lastOperationOk = true;
        refreshVisuals();
    }
 
    /**
     * Ajusta una posicion (base 1) para que quede entre 1 y max.
     * @param pos posicion solicitada.
     * @param max posicion maxima valida.
     * @return 1 si pos es menor a 1, max si pos es mayor a max, o pos.
     */
    private int clamp(int pos, int max){
        if(pos < 1){
            return 1;
        }
        if(pos > max){
            return max;
        }
        return pos;
    }
 
    /**
     * Retorna los colores del catalogo de simbolos de la maquina,
     * en el orden en que fueron agregados (empezando por la posicion 1).
     */
    public String[] symbols(){
        return symbolCatalog.toArray(new String[0]);
    }
 
    /**
     * Retorna cuantos colores distintos hay en el catalogo de simbolos.
     */
    public int distinctSymbols(){
        ArrayList<String> distinct = new ArrayList<String>();
        for(int i = 0; i < symbolCatalog.size(); i++){
            String color = symbolCatalog.get(i);
            if(!distinct.contains(color)){
                distinct.add(color);
            }
        }
        return distinct.size();
    }
 
    /**
     * Retorna el color que esta al frente de cada rueda, de izquierda
     * a derecha. Si una rueda no tiene simbolos, su posicion queda null.
     */
    public String[] configuration(){
        ArrayList<String> current = new ArrayList<String>();
        for(int i = 0; i < wheels.size(); i++){
            current.add(wheels.get(i).getFrontColor());
        }
        return current.toArray(new String[0]);
    }
 
    /**
     * Indica si la configuracion actual es ganadora. Regla simple para
     * esta entrega: hay premio si hay al menos una rueda, todas tienen
     * un simbolo al frente, y todas muestran el mismo color.
     */
    public boolean isJackpot(){
        if(wheels.isEmpty()){
            return false;
        }
        String firstColor = wheels.get(0).getFrontColor();
        if(firstColor == null){
            return false;
        }
        for(int i = 1; i < wheels.size(); i++){
            String color = wheels.get(i).getFrontColor();
            if(color == null || !color.equals(firstColor)){
                return false;
            }
        }
        return true;
    }
 
    /**
     * Gira una rueda: elige al azar un simbolo del catalogo y lo deja
     * al frente de esa rueda. No hace nada si la rueda esta fija.
     * @param wheel posicion de la rueda a girar (desde 1).
     */
    public void spin(int wheel){
        if(wheels.isEmpty() || symbolCatalog.isEmpty()){
            lastOperationOk = false;
            reportError("No se puede girar: faltan ruedas o simbolos.");
            return;
        }
        int validPos = clamp(wheel, wheels.size());
        Wheel selectedWheel = wheels.get(validPos - 1);
        if(selectedWheel.isLocked()){
            lastOperationOk = false;
            reportError("La rueda " + validPos + " esta fija y no puede girar.");
            return;
        }
        String randomColor = symbolCatalog.get(random.nextInt(symbolCatalog.size()));
        selectedWheel.place(randomColor);
        lastOperationOk = true;
        refreshVisuals();
    }
 
    /**
     * Gira todas las ruedas de la maquina. Las ruedas fijas se
     * mantienen sin cambios.
     */
    public void spin(){
        if(wheels.isEmpty() || symbolCatalog.isEmpty()){
            lastOperationOk = false;
            reportError("No se puede girar: faltan ruedas o simbolos.");
            return;
        }
        for(int i = 0; i < wheels.size(); i++){
            Wheel currentWheel = wheels.get(i);
            if(!currentWheel.isLocked()){
                String randomColor = symbolCatalog.get(random.nextInt(symbolCatalog.size()));
                currentWheel.place(randomColor);
            }
        }
        lastOperationOk = true;
        refreshVisuals();
    }
 
    /**
     * Gira una rueda dando la cantidad de pasos indicada. En cada paso
     * se elige un simbolo al azar del catalogo; si el simulador esta
     * visible, cada paso se muestra en pantalla antes de continuar con
     * el siguiente (requisito de usabilidad). No hace nada si la rueda
     * esta fija.
     * @param wheel posicion de la rueda a girar (desde 1).
     * @param steps cantidad de pasos a girar (debe ser mayor a 0).
     */
    public void spin(int wheel, int steps){
        if(wheels.isEmpty() || symbolCatalog.isEmpty() || steps < 1){
            lastOperationOk = false;
            reportError("No se puede girar: faltan ruedas, simbolos o el numero de pasos no es valido.");
            return;
        }
        int validPos = clamp(wheel, wheels.size());
        Wheel selectedWheel = wheels.get(validPos - 1);
        if(selectedWheel.isLocked()){
            lastOperationOk = false;
            reportError("La rueda " + validPos + " esta fija y no puede girar.");
            return;
        }
        for(int i = 0; i < steps; i++){
            String randomColor = symbolCatalog.get(random.nextInt(symbolCatalog.size()));
            selectedWheel.place(randomColor);
            if(visible){
                selectedWheel.showAt(validPos);
            }
        }
        lastOperationOk = true;
        refreshVisuals();
    }
 
    /**
     * Deja la maquina en la configuracion dada: coloca cada color del
     * arreglo al frente de la rueda de la misma posicion (empezando en
     * la posicion 1). Las ruedas fijas no se modifican. Si el arreglo
     * trae mas posiciones que ruedas tiene la maquina, las posiciones
     * sobrantes se ignoran.
     * @param setSymbols colores a dejar al frente de cada rueda, en orden.
     */
    public void spin(String[] setSymbols){
        if(wheels.isEmpty() || setSymbols == null || setSymbols.length == 0){
            lastOperationOk = false;
            reportError("No se pudo dejar la maquina en la configuracion indicada.");
            return;
        }
        boolean allOk = true;
        int total = Math.min(setSymbols.length, wheels.size());
        for(int i = 0; i < total; i++){
            Wheel currentWheel = wheels.get(i);
            String color = setSymbols[i];
            if(currentWheel.isLocked()){
                continue;
            }
            if(symbolCatalog.contains(color)){
                currentWheel.place(color);
            } else {
                allOk = false;
            }
        }
        lastOperationOk = allOk;
        if(!allOk){
            reportError("Algunos simbolos de la configuracion no existen en el catalogo.");
        }
        refreshVisuals();
    }
 
    /**
     * Hace visible el simulador: muestra cada rueda con su color actual.
     */
    public void makeVisible(){
        visible = true;
        refreshVisuals();
        lastOperationOk = true;
    }
 
    /**
     * Hace invisible el simulador. La maquina sigue funcionando igual,
     * solo que no se ve nada en pantalla.
     */
    public void makeInvisible(){
        for(int i = 0; i < wheels.size(); i++){
            wheels.get(i).hide();
        }
        visible = false;
        lastOperationOk = true;
    }

    /**
     * Vuelve a dibujar todas las ruedas con su color actual, y las
     * resalta si la configuracion actual es ganadora. No hace nada si
     * el simulador esta invisible.
     */
    private void refreshVisuals(){
        if(!visible){
            return;
        }
        for(int i = 0; i < wheels.size(); i++){
            wheels.get(i).showAt(i + 1);
        }
        boolean jackpot = isJackpot();
        for(int i = 0; i < wheels.size(); i++){
            if(jackpot){
                wheels.get(i).highlight();
            } else {
                wheels.get(i).unhighlight();
            }
        }
    }

    /**
     * Muestra un mensaje de error al usuario, solo si el simulador
     * esta visible.
     * @param message mensaje a mostrar.
     */
    private void reportError(String message){
        if(visible){
            JOptionPane.showMessageDialog(null, message);
        }
    }
 
    /**
     * Termina el simulador: oculta las ruedas y deja la maquina vacia.
     */
    public void exit(){
        makeInvisible();
        wheels.clear();
        symbolCatalog.clear();
        lastOperationOk = true;
    }
}