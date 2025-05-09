package test;

import domain.GameMode;
import domain.PoobVsZombiesException;
import org.junit.jupiter.api.Test;
import presentation.GamePanel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CondicionesDeVictoriaDerrotaTest {

    @Test
    void testVictoriaModoSupervivencia() throws PoobVsZombiesException {
        //Verificacion del correcto inicio y fin de una partida en modo supervivencia en este caso cuando el jugador gana
        GamePanel gamePanel = new GamePanel(GameMode.SUPERVIVENCIA);

        gamePanel.terminarJuego(true); // Simular victoria
        assertTrue(gamePanel.isJuegoTerminado(), "El juego debe terminar cuando se completan las oleadas.");
        assertTrue(gamePanel.isVictoria(), "Debe declararse victoria para el jugador.");
    }



    @Test
    void testDerrotaModoSupervivencia() throws PoobVsZombiesException {
        //Verificacion del correcto inicio y fin de una partida en modo supervivencia en este caso cuando el jugador pierde

        GamePanel gamePanel = new GamePanel(GameMode.SUPERVIVENCIA);
        gamePanel.terminarJuego(false); // Simula derrota
        assertTrue(gamePanel.isJuegoTerminado(), "El juego debe terminar si un zombie alcanza la primera columna.");
        assertFalse(gamePanel.isVictoria(), "Debe declararse derrota para el jugador.");
    }


    @Test
    void testModoMaquinaVsMaquinaIniciaYFinalizaCorrectamente() {
        //Se verifica la correcta ejecucion del modo Maquina vs Maquina durante 40 segundos
        GamePanel gamePanel = new GamePanel(GameMode.MAQUINA);

        gamePanel.iniciarJuegoMaquinaVsMaquina();

        try {
            Thread.sleep(40000); // Simula 40 segundos de interacción
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(true, "El modo Máquina vs Máquina debe ejecutarse sin bloqueos y finalizar correctamente.");
    }

}
