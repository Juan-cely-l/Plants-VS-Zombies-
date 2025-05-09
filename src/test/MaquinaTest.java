package test;


import domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MaquinaTest {

    @Test
    void testGeneracionEnPosicionesValidas() {
        int filas = 5, columnas = 9;
        List<Entidad> entidades = new ArrayList<>();
        EntidadesActualizables entidadesActualizables = updatedEntidades  -> {}; // Mock simple
        EntidadesAccesibles entidadesAccesibles = new EntidadesAccesibles() {
            @Override
            public boolean isJuegoPausado() {
                return false;
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return entidades;
            }
        };
        Tablero tablero = new Tablero(filas, columnas, new ArrayList<>());

        // Generar algunas plantas
        Planta girasol = new Girasol(3, 1, 100, 50);
        Planta peashooter = new Peashooter(4, 2, 100, 50, 20, tablero, entidadesActualizables, null);
        Zombie zombie = new BasicZombie(8, 3, 100, 0.05f, 20, entidadesAccesibles);

        tablero.agregarEntidad(girasol);
        tablero.agregarEntidad(peashooter);
        tablero.agregarEntidad(zombie);

        // Verificar que todas las entidades están dentro de los límites del tablero
        for (Entidad entidad : tablero.getEntidades()) {
            assertTrue(entidad.getxPos() >= 0 && entidad.getxPos() < columnas,
                    "La posición X de la entidad está fuera de los límites.");
            assertTrue(entidad.getyPos() >= 0 && entidad.getyPos() < filas,
                    "La posición Y de la entidad está fuera de los límites.");
        }
    }

    @Test
    void testNoSuperposicionDeEntidades() {
        Tablero tablero = new Tablero(5, 9, new ArrayList<>());

        // Generar una entidad en una posición específica
        Planta girasol = new Girasol(1, 1, 100, 50);
        tablero.agregarEntidad(girasol); // Agregar la entidad al tablero

        // Intentar agregar otra entidad en la misma posición
        Planta peashooter = new Peashooter(1, 1, 100, 50, 20, tablero, null, null);
        boolean celdaOcupadaAntesDeAgregar = tablero.celdaOcupada(peashooter.getyPos(), peashooter.getxPos());

        if (!celdaOcupadaAntesDeAgregar) {
            tablero.agregarEntidad(peashooter);
        }

        // Verificar que la celda está ocupada y no se permite superposición
        assertTrue(celdaOcupadaAntesDeAgregar, "No se debería poder agregar una entidad en una celda ocupada.");
        assertEquals(1, tablero.getEntidades().size(), "Solo debe haber una entidad en la celda.");
    }


    @Test
    void testGenerarOleadaZombies() {
        List<Entidad> entidades = new ArrayList<>();
        EntidadesActualizables mockEntidadesActualizables = updatedEntidades  -> {}; // Mock simple
        EntidadesAccesibles mockEntidadesAccesibles = new EntidadesAccesibles() {
            @Override
            public boolean isJuegoPausado() {
                return false;
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return entidades;
            }
        };

        // Crear una instancia de ZombiesStrategic
        ZombiesStrategic zombiesStrategic = new ZombiesStrategic(entidades, mockEntidadesActualizables, mockEntidadesAccesibles);

        // Llamar al método generarOleadaZombies
        zombiesStrategic.generarOleadaZombies();

        // Verificar que los zombies se generaron
        assertFalse(entidades.isEmpty(), "Debe haber zombies generados.");
    }


}
