import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;

/**
 * Pruebas unitarias de metodos agregados en ciclo 2 de SlotMachine:
 * swap, lock, unlock, spin(wheel,steps) y spin(setSymbols). 
 * Las pruebas se ejecutan en modo invisible (la maquina no se hace visible)
 *
 * @author Sergio Gonzalez Alba, Juan Andres Alvarez Silva
 * @version 2.0
 */
public class SlotMachineC2Test{

    private SlotMachine machine;

    /**
     * Crea una maquina con 3 ruedas vacias y 3 simbolos en el catalogo,
     * lista para cada prueba. **se mantiene invisible
     */
    @Before
    public void setUp(){
        machine = new SlotMachine();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");
    }

    // ---------- swap ----------

    @Test
    public void swapShouldExchangeTheFrontSymbolsOfTwoWheels(){
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.swap(1, 2);
        assertTrue(machine.ok());
        String[] config = machine.configuration();
        assertEquals("blue", config[0]);
        assertEquals("red", config[1]);
    }

    @Test
    public void swapShouldMoveTheLockedStateAlongWithTheWheel(){
        machine.lock(1);
        machine.swap(1, 2);
        assertTrue(machine.ok());
        machine.spin(2, 3);
        assertFalse("La rueda que antes era la 1 sigue fija, ahora en la posicion 2", machine.ok());
        machine.spin(1, 3);
        assertTrue("La rueda que antes era la 2 no estaba fija, ahora en la posicion 1", machine.ok());
    }

    @Test
    public void swapShouldFailWhenThereAreLessThanTwoWheels(){
        SlotMachine oneWheelMachine = new SlotMachine();
        oneWheelMachine.addWheel(1);
        oneWheelMachine.swap(1, 1);
        assertFalse(oneWheelMachine.ok());
    }

    @Test
    public void swapShouldClampPositionsOutOfRange(){
        machine.swap(0, 100);
        assertTrue(machine.ok());
    }

    // ---------- lock / unlock ----------

    @Test
    public void lockShouldPreventTheWheelFromSpinning(){
        machine.placeSymbol(1, "red");
        machine.lock(1);
        machine.spin(1);
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[0]);
    }

    @Test
    public void lockShouldFailWhenThereAreNoWheels(){
        SlotMachine emptyMachine = new SlotMachine();
        emptyMachine.lock(1);
        assertFalse(emptyMachine.ok());
    }

    @Test
    public void unlockShouldAllowTheWheelToSpinAgain(){
        machine.lock(1);
        machine.unlock(1);
        machine.spin(1);
        assertTrue(machine.ok());
    }

    @Test
    public void lockShouldClampPositionsOutOfRange(){
        machine.lock(100);
        machine.spin(3);
        assertFalse("La posicion 100 se ajusta a la ultima rueda (3), que queda fija", machine.ok());
    }

    // ---------- spin(wheel, steps) ----------

    @Test
    public void spinWithStepsShouldLeaveACatalogColorAtTheFront(){
        machine.spin(1, 5);
        assertTrue(machine.ok());
        String frontColor = machine.configuration()[0];
        assertTrue(Arrays.asList(machine.symbols()).contains(frontColor));
    }

    @Test
    public void spinWithStepsShouldFailWhenTheWheelIsLocked(){
        machine.placeSymbol(1, "red");
        machine.lock(1);
        machine.spin(1, 5);
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[0]);
    }

    @Test
    public void spinWithStepsShouldFailWhenStepsIsNotPositive(){
        machine.spin(1, 0);
        assertFalse(machine.ok());
        machine.spin(1, -2);
        assertFalse(machine.ok());
    }

    @Test
    public void spinWithStepsShouldFailWhenThereAreNoSymbols(){
        SlotMachine noSymbolsMachine = new SlotMachine();
        noSymbolsMachine.addWheel(1);
        noSymbolsMachine.spin(1, 3);
        assertFalse(noSymbolsMachine.ok());
    }

    // ---------- spin(setSymbols) ----------

    @Test
    public void spinWithConfigurationShouldSetTheGivenColors(){
        machine.spin(new String[]{"red", "blue", "green"});
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "blue", "green"}, machine.configuration());
    }

    @Test
    public void spinWithConfigurationShouldIgnoreLockedWheels(){
        machine.placeSymbol(2, "green");
        machine.lock(2);
        machine.spin(new String[]{"red", "blue", "red"});
        assertTrue(machine.ok());
        assertEquals("green", machine.configuration()[1]);
    }

    @Test
    public void spinWithConfigurationShouldFailWithAColorOutsideTheCatalog(){
        machine.spin(new String[]{"red", "purple", "green"});
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[0]);
        assertEquals("green", machine.configuration()[2]);
    }

    @Test
    public void spinWithConfigurationShouldIgnorePositionsBeyondTheWheelCount(){
        machine.spin(new String[]{"red", "blue", "green", "red", "blue"});
        assertTrue(machine.ok());
        assertEquals(3, machine.configuration().length);
    }

    @Test
    public void spinWithConfigurationShouldFailWhenTheArrayIsNull(){
        machine.spin((String[]) null);
        assertFalse(machine.ok());
    }

    // ---------- spin() y spin(wheel) ahora respetan ruedas fijas ----------

    @Test
    public void spinAllShouldSkipLockedWheels(){
        machine.placeSymbol(2, "green");
        machine.lock(2);
        machine.spin();
        assertTrue(machine.ok());
        assertEquals("green", machine.configuration()[1]);
    }
}