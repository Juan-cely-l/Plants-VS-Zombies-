package domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import presentation.GamePanel;

public class ZombiesOriginal implements Serializable {
    private List<Entidad> entidades;
    private EntidadesActualizables entidadesActualizables;
    private EntidadesAccesibles entidadesAccesibles;
    private transient Timer timer;
    private int intervaloGeneracion = 10000; // Inicialmente 10 segundos
    private int cantidadZombies = 1;         // Zombies iniciales por generación
    private boolean generacionPausada = false;

    public ZombiesOriginal(List<Entidad> entidades, EntidadesActualizables entidadesActualizables, EntidadesAccesibles entidadesAccesibles) {
        this.entidadesAccesibles = entidadesAccesibles;
        this.entidadesActualizables = entidadesActualizables;
        this.entidades = entidades;
    }

    // Pausar la generación de zombis
    public void pausarGeneracionZombies() {
        generacionPausada = true;
    }

    // Reanudar la generación de zombis
    public void reanudarGeneracionZombies() {
        generacionPausada = false;
    }

    public void iniciarGeneracionAutomatica() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!generacionPausada) { // Solo generar zombis si no está pausado
                    generarOleadaZombies();
                    aumentarDificultad(); // Incrementa la dificultad
                }
            }
        }, 20000, intervaloGeneracion); // Comienza después de 20s, se repite según el intervalo
    }

    public void detenerGeneracionAutomatica() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void iniciarMovimientoZombies() {
        for (Entidad entidad : entidades) {
            if (entidad instanceof BasicZombie) {
                BasicZombie zombie = (BasicZombie) entidad;
                zombie.iniciarHiloMovimiento();
            }
        }
    }

    private void generarOleadaZombies() {
        for (int i = 0; i < cantidadZombies; i++) {
            generarZombie();
        }
    }

    private void generarZombie() {
        int fila = (int) (Math.random() * 5);
        BasicZombie zombie = new BasicZombie(9, fila, 100, 0.03f, 100, entidadesAccesibles);
        entidades.add(zombie);
        entidadesActualizables.actualizarEntidades(entidades);
    }

    private void aumentarDificultad() {
        if (intervaloGeneracion > 3000) {  // Reduce el intervalo hasta un límite
            intervaloGeneracion -= 500;
            reprogramarTimer();
        }
        cantidadZombies++;  // Aumenta la cantidad de zombies por generación
    }

    private void reprogramarTimer() {
        detenerGeneracionAutomatica();  // Cancela el timer actual
        iniciarGeneracionAutomatica();  // Reinicia el timer con el nuevo intervalo
    }
}
