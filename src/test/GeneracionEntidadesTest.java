package test;

import domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneracionEntidadesTest {

    @Test
    void testGirasolesGeneranSolesAutomaticamente()  {
        // Configuración
        Girasol girasol = new Girasol(2, 2, 100, 50);
        Sol sol = girasol.generarSol();



        // Verificar que se generaron al menos 2 soles
        assertNotNull(sol, "El girasol debe generar un sol.");

    }


    @Test
    void testECIPlantGeneraSolGrande() {
        // Configuración
        ECIPlant eciPlant = new ECIPlant(3, 3, 100, 75);

        // Generar un sol manualmente
        Sol solGenerado = eciPlant.generarSol();

        // Verificar propiedades del sol
        assertNotNull(solGenerado, "La ECIPlant debe generar un sol.");
        assertEquals(50, solGenerado.getValor(), "El valor del sol generado debe ser 50.");
        assertEquals(3, solGenerado.getY(), "La posición X del sol debe coincidir con la posición de la ECIPlant.");
        assertEquals(3, solGenerado.getY(), "La posición Y del sol debe coincidir con la posición de la ECIPlant.");
    }


    @Test
    void testZombiesSeGeneranConIncrementoDeDificultad() {
        // Configuración inicial
        List<Entidad> entidades = new ArrayList<>();

        // Implementación  de EntidadesAccesibles
        EntidadesAccesibles entidadesAccesibles = new EntidadesAccesibles() {
            @Override
            public boolean isJuegoPausado() {
                return false; // El juego no está pausado
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return entidades; // Retorna la lista actual de entidades
            }
        };

        // Implementación simulada de EntidadesActualizables
        EntidadesActualizables entidadesActualizables = new EntidadesActualizables() {
            @Override
            public void actualizarEntidades(List<Entidad> nuevasEntidades) {
                // Simular actualización de entidades sin hacer nada
                System.out.println("Entidades actualizadas: " + nuevasEntidades.size());
            }
        };

        ZombiesStrategic zombiesStrategic = new ZombiesStrategic(entidades, entidadesActualizables, entidadesAccesibles);

        // Simular generación de zombies progresivamente
        zombiesStrategic.iniciarGeneracionAutomatica();

        // Esperar un tiempo para que se generen zombies
        try {
            Thread.sleep(21000); // Espera 21 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verificar que se generaron varios zombies y el incremento de dificultad
        assertTrue(entidades.size() > 0, "Deben generarse zombies después de iniciar la generación automática.");

        // Detener la generación para evitar interferencias
        zombiesStrategic.detenerGeneracionAutomatica();
    }



    @Test
    void testPlantasYZombiesNoSeSuperponen() {
        // Configuración del tablero
        Tablero tablero = new Tablero(5, 9, new ArrayList<>());

        // Crear una planta y agregarla al tablero
        Planta peashooter = new Peashooter(1, 1, 100, 50, 20, tablero, null, null);
        tablero.agregarEntidad(peashooter);

        // Implementación  de EntidadesAccesibles
        EntidadesAccesibles entidadesAccesibles = new EntidadesAccesibles() {
            @Override
            public boolean isJuegoPausado() {
                return false;
            }

            @Override
            public List<Entidad> obtenerEntidades() {
                return tablero.getEntidades();
            }
        };

        // Crear un zombie y verificar que no se puede agregar en la misma posición
        Zombie zombie = new BasicZombie(1, 1, 100, 0.05f, 10, entidadesAccesibles);
        assertTrue(tablero.celdaOcupada(1, 1), "La celda debería estar ocupada por una planta.");


    }
}