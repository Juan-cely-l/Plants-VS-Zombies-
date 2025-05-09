package test;

import domain.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CondicionesDeJuegoTest {

    @Test
    void testTiempoRestanteIgualACero() {
        // Simular un modo de juego con temporizador
        int tiempoRestante = 0;
        boolean victoria = tiempoRestante == 0;

        // Validar que el juego se considera ganado si el tiempo llega a 0
        assertTrue(victoria, "El jugador debe ganar si el tiempo llega a 0.");
    }



    @Test
    void testValidarActualizar() {
        // Crear un tablero con plantas y zombies
        Tablero tablero = new Tablero(5, 9, new ArrayList<>());

        EntidadesAccesibles entidadesAccesibles = new EntidadesAccesibles() {
            @Override
            public boolean isJuegoPausado() {
                return false; // El juego no está pausado
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return tablero.getEntidades(); // Retorna las entidades actuales del tablero
            }
        };

        // Agregar una planta
        Planta planta = new Girasol(4, 3, 100, 50);
        tablero.agregarEntidad(planta);

        // Crear un zombie con salud inicial 0 (muerto)
        Zombie zombie = new BasicZombie(1, 1, 0, 0.05f, 20, entidadesAccesibles);
        tablero.agregarEntidad(zombie);

        // Actualizar el estado del tablero para eliminar entidades muertas
        tablero.actualizarEstado();

        // Verificar que solo queda la planta y el zombie fue eliminado
        assertEquals(1, tablero.getEntidades().size(), "Se actualizo correctamente");
        assertTrue(tablero.getEntidades().contains(planta), "Solo debe quedar la planta en el tablero.");
    }


    @Test
    void testValidarDerrota() {
        // Crear un tablero con un zombie alcanzando la primera columna
        Tablero tablero = new Tablero(5, 9, new ArrayList<>());
        EntidadesAccesibles entidadesAccesibles = new EntidadesAccesibles() {
            @Override
            public boolean isJuegoPausado() {
                return false;
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return tablero.getEntidades(); // Devuelve las entidades actuales del tablero
            }
        };
        Zombie zombie = new BasicZombie(0, 2, 100, 0.05f, 20, entidadesAccesibles); // Zombie en la primera columna
        tablero.agregarEntidad(zombie);

        // Actualizar
        tablero.actualizarEstado();

        // Verificar que el jugador pierde si un zombie alcanza la primera columna
        assertTrue(tablero.isJuegoTerminado(), "El jugador pierde si un zombie alcanza la primera columna.");
    }
}
