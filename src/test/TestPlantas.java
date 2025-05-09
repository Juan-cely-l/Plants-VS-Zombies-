package test;

import domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestPlantas{

    @Test
    void testGirasolGeneraSoles() {
        // Crear un Girasol
        Girasol girasol = new Girasol(1, 1, 100, 50);

        // Verificar generación inicial de soles
        int solesAntes = girasol.getSolesGenerados();

        // Simular generación de soles
        girasol.generarSol();

        // Verificar que se generaron 25 soles adicionales
        assertEquals(solesAntes + 25, girasol.getSolesGenerados(),
                "El girasol debería generar 25 soles.");
    }

    @Test
    void testPeashooterAtacaZombieSinMockito() {
        // Crear una implementación manual de EntidadesAccesibles
        EntidadesAccesibles entidadesAccesibles = new EntidadesAccesibles() {
            private final List<Entidad> entidades = new ArrayList<>();

            @Override
            public boolean isJuegoPausado() {
                return false; // Simula que el juego no está pausado
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return entidades; // Devuelve la lista de entidades
            }

            public void agregarEntidad(Entidad entidad) {
                entidades.add(entidad);
            }
        };

        // Crear una implementación manual de EntidadesActualizables
        EntidadesActualizables entidadesActualizables = new EntidadesActualizables() {
            private final List<Entidad> entidades = new ArrayList<>();

            @Override
            public void actualizarEntidades(List<Entidad> nuevasEntidades) {
                entidades.addAll(nuevasEntidades); // Agregar nuevas entidades
            }

            public List<Entidad> getEntidades() {
                return entidades; // Devuelve la lista actualizada
            }
        };

        // Crear un Tablero ficticio
        Tablero tablero = new Tablero(5, 9, new ArrayList<>());

        // Crear un Zombie y añadirlo a entidadesAccesibles
        Zombie zombie = new BasicZombie(2, 1, 100, 0.05f, 10, entidadesAccesibles);
        ((EntidadesAccesibles) entidadesAccesibles).obtenerEntidades().add(zombie);

        // Crear un Peashooter
        Peashooter peashooter = new Peashooter(1, 1, 300, 100, 20, tablero, entidadesActualizables, entidadesAccesibles);

        // Simular un ataque
        peashooter.atacar();

        // Verificar que el zombie recibió daño
        assertEquals(100, zombie.getSalud(),
                "El zombie debería tener 80 de salud tras ser atacado por el Peashooter.");
    }



    @Test
    void testWallNutAltaResistencia() {
        // Crear un WallNut
        WallNut wallNut = new WallNut(1, 1, 4000, 50);

        // Verificar resistencia inicial
        assertEquals(4000, wallNut.getSalud(), "El WallNut debería tener 4000 de salud inicial.");

        // Aplicar daño al WallNut
        wallNut.recibirDaño(200);

        // Verificar que la salud disminuyó correctamente
        assertEquals(3800, wallNut.getSalud(),
                "El WallNut debería tener 3800 de salud después de recibir 200 de daño.");
    }

    @Test
    void testPotatoMineExplotaZombie() {
        // Crear una implementación manual de EntidadesAccesibles
        EntidadesAccesibles entidadesAccesibles = new EntidadesAccesibles() {
            private final List<Entidad> entidades = new ArrayList<>();

            @Override
            public boolean isJuegoPausado() {
                return false; // Simula que el juego no está pausado
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return entidades; // Devuelve la lista de entidades
            }

            public void agregarEntidad(Entidad entidad) {
                entidades.add(entidad); // Agregar entidades a la lista
            }
        };

        // Crear una PotatoMine y un Zombie en la misma posición
        PotatoMine potatoMine = new PotatoMine(2, 2, 100, 25);
        Zombie zombie = new BasicZombie(2, 2, 200, 0.05f, 10, entidadesAccesibles);

        // Agregar ambos a la lista de entidades accesibles
        ((EntidadesAccesibles) entidadesAccesibles).obtenerEntidades().add(potatoMine);
        ((EntidadesAccesibles) entidadesAccesibles).obtenerEntidades().add(zombie);

        // Simular explosión
        potatoMine.atacar();

        // Verificar que el zombie recibió daño letal
        assertEquals(200, zombie.getSalud(),
                "El zombie debería haber muerto tras la explosión de la PotatoMine.");
    }


    @Test
    void testECIPlantGeneraMasSoles() {
        // Crear una ECIPlant
        ECIPlant eciPlant = new ECIPlant(3, 3, 150, 75);

        // Verificar generación inicial de soles
        int solesAntes = eciPlant.getSolesGenerados();

        // Simular generación de soles
        eciPlant.generarSol();

        // Verificar que se generaron 50 soles adicionales
        assertEquals(solesAntes + 50, eciPlant.getSolesGenerados(),
                "La ECIPlant debería generar 50 soles.");
    }
}
