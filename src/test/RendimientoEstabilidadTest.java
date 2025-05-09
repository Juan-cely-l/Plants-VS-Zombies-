package test;

import domain.*;
import org.junit.jupiter.api.Test;
import presentation.GamePanel;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RendimientoEstabilidadTest {

    @Test
    void testSimulacionPartidaLarga() {
        Tablero tablero = new Tablero(5, 9, new ArrayList<>());
        EntidadesActualizables entidadesActualizablesMock = new EntidadesActualizables() {
            @Override
            public void actualizarEntidades(List<Entidad> entidades) {
                // Simular actualización de entidades
            }
        };

        PlantsStrategic plantsStrategic = new PlantsStrategic(tablero, entidadesActualizablesMock);

        // Ejecutar múltiples iteraciones de la estrategia para simular una partida larga
        for (int i = 0; i < 100; i++) {
            plantsStrategic.ejecutarEstrategiaLimitada();
        }

        // Verificar que las entidades generadas están dentro de los límites del tablero
        for (Entidad entidad : tablero.getEntidades()) {
            assertTrue(entidad.getxPos() >= 0 && entidad.getxPos() < tablero.getColumnas(),
                    "Entidad fuera de los límites en la posición X: " + entidad.getxPos());
            assertTrue(entidad.getyPos() >= 0 && entidad.getyPos() < tablero.getFilas(),
                    "Entidad fuera de los límites en la posición Y: " + entidad.getyPos());
        }

        // Verificar que no haya entidades duplicadas en la misma celda
        boolean[][] ocupacion = new boolean[tablero.getFilas()][tablero.getColumnas()];
        for (Entidad entidad : tablero.getEntidades()) {
            int x = entidad.getxPos();
            int y = entidad.getyPos();
            assertFalse(ocupacion[y][x], "La celda (" + x + ", " + y + ") ya está ocupada.");
            ocupacion[y][x] = true; // Marcar la celda como ocupada
        }
    }


    @Test
    void testManejoDeHilos() {
        // Configuración inicial
        GamePanel gamePanel = new GamePanel(GameMode.SUPERVIVENCIA);
        Tablero tablero = new Tablero(5, 9, new ArrayList<>());

        ZombiesStrategic zombiesStrategic = new ZombiesStrategic(tablero.getEntidades(), gamePanel, gamePanel);

        // Iniciar generación de zombies en un hilo
        zombiesStrategic.iniciarGeneracionAutomatica();

        // Verificar que el hilo se inicia correctamente
        assertDoesNotThrow(() -> zombiesStrategic.iniciarGeneracionAutomatica(), "Los hilos deben iniciarse correctamente.");

        // Esperar un tiempo para simular el funcionamiento del juego
        try {
            Thread.sleep(5000); // Esperar 5 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Detener la generación automática
        zombiesStrategic.detenerGeneracionAutomatica();

        // Verificar que el hilo no arroja excepciones al detenerse
        assertDoesNotThrow(() -> zombiesStrategic.detenerGeneracionAutomatica(), "Los hilos deben detenerse correctamente.");
    }

    @Test
    void testInterfazGraficaResponsiva() {
        // Crear un GamePanel para el modo Máquina vs Máquina
        GamePanel gamePanel = new GamePanel(GameMode.MAQUINA);

        // Simular la ejecución del juego
        assertDoesNotThrow(() -> gamePanel.repaint(), "La interfaz gráfica debe responder a repaint() sin errores.");

        // Simular interacciones con la interfaz gráfica
        assertDoesNotThrow(() -> {
            gamePanel.aumentarSoles(100); // Incrementar recursos
            gamePanel.reducirSoles(50);  // Reducir recursos
            gamePanel.repaint();
        }, "La interfaz gráfica debe seguir respondiendo durante interacciones.");
    }
}
