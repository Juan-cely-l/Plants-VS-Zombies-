package test;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import presentation.GamePanel;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GamePanelTest {
    private Tablero tablero;
    private List<Entidad> entidades;
    private GamePanel gamePanel;

    @BeforeEach
    void setUp() {
        entidades = new ArrayList<>();
        tablero = new Tablero(5, 9, entidades); // Tablero de 5 filas y 9 columnas
        gamePanel = new GamePanel(GameMode.ORIGINAL);
        gamePanel.actualizarEntidades(entidades);
    }

    @Test
    void testEliminarEntidadesMuertas() {
        EntidadesAccesibles entidadesAccesibles = new EntidadesAccesibles() {
            @Override
            public boolean isJuegoPausado() {
                return false;
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return tablero.getEntidades(); // Devuelve las entidades del tablero
            }
        };

        // Crear entidades de prueba
        Zombie zombie = new BasicZombie(8, 2, 0, 0.05f, 100, entidadesAccesibles); // Salud inicial 0
        Planta planta = new Girasol(4, 3, 0, 50); // Salud inicial 0

        // Agregar entidades al tablero
        tablero.agregarEntidad(zombie);
        tablero.agregarEntidad(planta);

        // Verificar que las entidades están inicialmente en el tablero
        assertEquals(2, tablero.getEntidades().size(), "Deberían haber 2 entidades en el tablero");

        // Actualizar el estado del tablero (eliminar entidades muertas)
        tablero.actualizarEstado();

        // Verificar que las entidades muertas fueron eliminadas
        assertEquals(0, tablero.getEntidades().size(), "Las entidades muertas deben ser eliminadas del tablero");
    }

    @Test
    void testZombieEnPrimeraColumnaTerminaElJuego() {
        // Crear un mock de EntidadesAccesibles
        EntidadesAccesibles accesibles = new EntidadesAccesibles() {
            @Override
            public boolean isJuegoPausado() {
                return false; // El juego no está pausado
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return tablero.getEntidades(); // Delegar al tablero real
            }
        };

        // Crear un zombie usando el mock
        Zombie zombie = new BasicZombie(0, 2, 100, 0.05f, 100, accesibles);
        tablero.agregarEntidad(zombie);

        // Actualizar el estado del tablero
        tablero.actualizarEstado();

        // Verificar que el juego termina si un zombie llega a la primera columna
        assertTrue(tablero.isJuegoTerminado(), "El juego debe terminar si un zombie llega a la primera columna");
    }


    @Test
    void testMantenerEntidadesVivas() {
        // Crear una planta con salud positiva
        Planta planta = new Girasol(4, 3, 50, 50); // Salud inicial 50
        tablero.agregarEntidad(planta);

        // Actualizar el estado del tablero
        tablero.actualizarEstado();

        // Verificar que la planta viva no se elimina
        assertEquals(1, tablero.getEntidades().size(), "La planta viva debe permanecer en el tablero");
        assertEquals(planta, tablero.getEntidades().get(0), "La planta viva debe coincidir con la planta inicial");
    }

    @Test
    void testZombieRecibeDañoYSeElimina() {

        EntidadesAccesibles entidadesAccesibles = new EntidadesAccesibles() {
            @Override
            public boolean isJuegoPausado() {
                return false; // Simular que el juego no está pausado
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return tablero.getEntidades(); // Delegar al tablero real
            }
        };

        Zombie zombie = new BasicZombie(8, 2, 50, 0.05f, 100, entidadesAccesibles);
        tablero.agregarEntidad(zombie);

        // Aplicar daño al zombie
        zombie.recibirDaño(50); // Reducir la salud a 0

        // Actualizar el estado del tablero
        tablero.actualizarEstado();

        // Verificar que el zombie es eliminado
        assertTrue(zombie.estaMuerta(), "El zombie debe estar muerto después de recibir suficiente daño");
        assertEquals(0, tablero.getEntidades().size(), "El zombie muerto debe ser eliminado del tablero");
    }

}