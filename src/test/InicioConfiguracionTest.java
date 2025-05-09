package test;

import domain.Entidad;
import domain.GameMode;
import org.junit.jupiter.api.Test;
import presentation.GamePanel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InicioConfiguracionTest {

    @Test
    void testInicioModoSupervivencia() {
        // Configurar el juego en Modo Supervivencia
        GamePanel gamePanel = new GamePanel(GameMode.SUPERVIVENCIA);

        // Verificar que los recursos iniciales son correctos
        assertEquals(2000, gamePanel.getSoles(), "En Modo Supervivencia, los soles iniciales deben ser 2000.");
        assertEquals(2000, gamePanel.getCerebros(), "En Modo Supervivencia, los cerebros iniciales deben ser 2000.");

        // Verificar que el tablero está vacío
        List<Entidad> entidades = gamePanel.getEntidades();
        assertTrue(entidades.isEmpty(), "El tablero debe estar vacío al inicio en Modo Supervivencia.");
    }

    @Test
    void testInicioModoZombies() {
        // Configurar el juego en Modo Zombies
        GamePanel gamePanel = new GamePanel(GameMode.ZOMBIES);

        // Verificar que los recursos iniciales son correctos

        assertEquals(100, gamePanel.getCerebros(), "En Modo Zombies, los cerebros iniciales deben ser 100.");

        // Verificar que el tablero está vacío
        List<Entidad> entidades = gamePanel.getEntidades();
        assertTrue(entidades.isEmpty(), "El tablero debe estar vacío al inicio en Modo Zombies.");
    }

    @Test
    void testInicioModoMaquinaVsMaquina() {
        // Configurar el juego en Modo Máquina vs Máquina
        GamePanel gamePanel = new GamePanel(GameMode.MAQUINA);

        // Verificar que los recursos iniciales son correctos
        assertEquals(200, gamePanel.getSoles(), "En Modo Máquina, los soles iniciales deben ser 200.");
        assertEquals(200, gamePanel.getCerebros(), "En Modo Máquina, los cerebros iniciales deben ser 200.");

        // Verificar que el tablero está vacío
        List<Entidad> entidades = gamePanel.getEntidades();
        assertTrue(entidades.isEmpty(), "El tablero debe estar vacío al inicio en Modo Máquina vs Máquina.");
    }


    @Test
    void testInicioModoOriginal() {
        // Configurar el juego en Modo Original
        GamePanel gamePanel = new GamePanel(GameMode.ORIGINAL);

        // Verificar que los recursos iniciales son correctos

        assertEquals(100, gamePanel.getSoles(), "En Modo Original, los soles iniciales deben ser 100.");

        // Verificar que el tablero está vacío
        List<Entidad> entidades = gamePanel.getEntidades();
        assertTrue(entidades.isEmpty(), "El tablero debe estar vacío al inicio en Modo Original.");
    }

    @Test
    void testInicioModoPersonalizado() {
        // Configurar el juego en Modo Personalizado
        GamePanel gamePanel = new GamePanel(GameMode.PERSONALIZADO);

        // Verificar que los recursos iniciales son correctos

        assertEquals(100, gamePanel.getCerebros(), "En Modo Personalizado, los soles iniciales deben ser 100.");

        // Verificar que el tablero está vacío
        List<Entidad> entidades = gamePanel.getEntidades();
        assertTrue(entidades.isEmpty(), "El tablero debe estar vacío al inicio en Modo Zombies.");
    }
}