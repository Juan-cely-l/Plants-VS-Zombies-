package test;

import domain.*;
import org.junit.jupiter.api.Test;
import presentation.GamePanel;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class RecursosTest {

    @Test
    void testIncrementarYDisminuirSoles() {
        GamePanel gamePanel = new GamePanel(GameMode.SUPERVIVENCIA);

        // Incrementar soles
        gamePanel.aumentarSoles(50); // Añadir 50 soles
        assertEquals(2000, gamePanel.getSoles(), "Los soles deben incrementarse correctamente."); // 100 + 50 = 150

        // Disminuir soles
        gamePanel.reducirSoles(50); // Reducir 30 soles
        assertEquals(1950, gamePanel.getSoles(), "Los soles deben disminuirse correctamente."); // 150 - 30 = 120

        // Intentar reducir soles a un valor negativo
        gamePanel.reducirSoles(2000); // Reducir más de lo disponible
        assertEquals(1950, gamePanel.getSoles(), "Los soles no deben ser negativos."); // Resultado esperado: 0
    }


    @Test
    void testIncrementarYDisminuirCerebros() {
        GamePanel gamePanel = new GamePanel(GameMode.ZOMBIES); // Instancia del panel con cerebros iniciales

        // Incrementar cerebros
        gamePanel.aumentarCerebros(100); // Añadir 100 cerebros
        assertEquals(200, gamePanel.getCerebros(), "Los cerebros deben incrementarse correctamente.");

        // Disminuir cerebros
        gamePanel.reducirCerebros(50); // Reducir 50 cerebros
        assertEquals(150, gamePanel.getCerebros(), "Los cerebros deben disminuirse correctamente.");

        // Intentar reducir cerebros a un valor negativo
        gamePanel.reducirCerebros(300); // Reducir más de lo disponible
        assertEquals(150, gamePanel.getCerebros(), "Los cerebros no deben ser negativos.");
    }

    @Test
    void testGeneracionDeSolesPorGirasol() {
        // Crear un tablero y un Girasol
        Tablero tablero = new Tablero(5, 9, new ArrayList<>());
        Girasol girasol = new Girasol(3, 3, 100, 50);
        tablero.agregarEntidad(girasol);

        Sol solGenerado = girasol.generarSol();


        // Verificar que se generaron soles

        assertNotNull(solGenerado, "Girasol debe generar un sol.");
        assertEquals(25, solGenerado.getValor(), "El valor del sol generado por girasol debe ser 25.");
    }


    @Test
    void testGeneracionDeSolesPorECIPlant() {
        // Crear un tablero y una ECIPlant
        Tablero tablero = new Tablero(5, 9, new ArrayList<>());
        ECIPlant eciPlant = new ECIPlant(4, 4, 100, 75);
        tablero.agregarEntidad(eciPlant);

        // Generar un sol grande manualmente
        Sol solGenerado = eciPlant.generarSol();

        // Verificar las propiedades del sol generado
        assertNotNull(solGenerado, "La ECIPlant debe generar un sol.");
        assertEquals(50, solGenerado.getValor(), "El valor del sol generado por la ECIPlant debe ser 50.");
        assertEquals(4, solGenerado.getX(), "La posición X del sol debe coincidir con la posición de la ECIPlant.");
        assertEquals(4, solGenerado.getY(), "La posición Y del sol debe coincidir con la posición de la ECIPlant.");
    }
}
