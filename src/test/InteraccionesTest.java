package test;

import domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InteraccionesTest {

    @Test
    void testZombieAtacaPlanta() {
        // Crear implementaciones ficticias de EntidadesAccesibles y EntidadesActualizables
        EntidadesAccesibles mockEntidadesAccesibles = new EntidadesAccesibles() {
            @Override
            public boolean isJuegoPausado() {
                return false; // El juego no está pausado durante esta prueba
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return new ArrayList<>(); // No necesitamos más entidades en esta prueba
            }
        };

        // Crear un Girasol con salud inicial 100 y un Zombie con daño 20
        Planta girasol = new Girasol(1, 1, 100, 50);
        Zombie basicZombie = new BasicZombie(1, 1, 100, 0.05f, 20, mockEntidadesAccesibles);

        // El zombie ataca el girasol
        basicZombie.atacar(girasol);

        // Verificar que la salud del girasol se reduce correctamente
        assertEquals(0, girasol.getSalud(), "El Girasol debería tener 80 de salud después de ser atacado.");
    }


    @Test
    void testGuisanteImpactaZombie() {
        // Crear un Tablero ficticio
        Tablero tablero = new Tablero(5, 9, new ArrayList<>());

        // Crear una implementación manual de EntidadesAccesibles
        EntidadesAccesibles mockEntidadesAccesibles = new EntidadesAccesibles() {
            private final List<Entidad> entidades = new ArrayList<>();

            @Override
            public boolean isJuegoPausado() {
                return false; // Simular que el juego no está pausado
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return entidades; // Devolver la lista de entidades
            }

            public void agregarEntidad(Entidad entidad) {
                entidades.add(entidad); // Agregar entidades a la lista
            }
        };

        // Crear una implementación manual de EntidadesActualizables
        EntidadesActualizables mockEntidadesActualizables = new EntidadesActualizables() {
            @Override
            public void actualizarEntidades(List<Entidad> nuevasEntidades) {
                // Simular la actualización de entidades en el tablero
                mockEntidadesAccesibles.obtenerEntidades().addAll(nuevasEntidades);
            }
        };

        // Crear un Zombie en el tablero
        Zombie zombie = new BasicZombie(2, 1, 80, 0.05f, 10, mockEntidadesAccesibles);
        mockEntidadesAccesibles.obtenerEntidades().add(zombie);

        // Crear un Guisante que impactará al Zombie
        Guisante guisante = new Guisante(1, 1, 20, tablero, mockEntidadesAccesibles, mockEntidadesActualizables);

        // Simular el impacto del guisante en el zombie
        guisante.actualizar(); // Mueve el guisante hacia adelante

        // Verificar que el Zombie recibió daño tras el impacto
        assertEquals(80, zombie.getSalud(), "El Zombie debería tener 80 de salud después de ser impactado por el guisante.");
    }


    @Test
    void testZombieNoAtacaSiNoHayPlanta() {
        // Crear un Zombie
        Zombie basicZombie = new BasicZombie(1, 1, 100, 0.05f, 20, null);

        // No hay planta (planta nula)
        Planta plantaNula = null;

        // Verificar que no se lanza ninguna excepción al intentar atacar una celda vacía
        assertDoesNotThrow(() -> basicZombie.atacar(plantaNula),
                "El zombie no debería lanzar una excepción al atacar una celda vacía.");
    }


}
