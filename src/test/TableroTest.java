package test;

import domain.Girasol;
import domain.Planta;
import domain.Tablero;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TableroTest {

    @Test
    void testObtenerPlantaEnCelda() {
        Tablero tablero = new Tablero(5, 8, new ArrayList<>());
        Planta girasol = new Girasol(1, 1, 100, 50);
        tablero.agregarEntidad(girasol);

        Planta plantaObtenida = tablero.obtenerPlantaEnCelda(1, 1);
        assertNotNull(plantaObtenida, "Debería haber una planta en la celda (1,1)");
        assertEquals(girasol, plantaObtenida, "La planta obtenida debería ser el Girasol agregado.");
    }

    @Test
    void testCeldaOcupada() {
        Tablero tablero = new Tablero(5, 8, new ArrayList<>());
        Planta girasol = new Girasol(2, 2, 100, 50);
        tablero.agregarEntidad(girasol);

        assertTrue(tablero.celdaOcupada(2, 2), "La celda (2,2) debería estar ocupada.");
        assertFalse(tablero.celdaOcupada(0, 0), "La celda (0,0) no debería estar ocupada.");
    }

    @Test
    void testAgregarEntidad() {
        Tablero tablero = new Tablero(5, 8, new ArrayList<>());
        Planta girasol = new Girasol(1, 1, 100, 50);

        tablero.agregarEntidad(girasol);
        assertEquals(1, tablero.getEntidades().size(), "Debería haber una entidad en el tablero.");
        assertTrue(tablero.getEntidades().contains(girasol), "El tablero debería contener el Girasol agregado.");
    }
}
