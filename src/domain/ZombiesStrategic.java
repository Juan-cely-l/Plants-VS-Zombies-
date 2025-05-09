package domain;

import presentation.GamePanel;

import javax.swing.*;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ZombiesStrategic implements Serializable {
    private List<Entidad> entidades;
    private EntidadesActualizables entidadesActualizables;
    private EntidadesAccesibles entidadesAccesibles;
    private transient Timer timer;
    private int intervaloGeneracion = 10000;  // Inicialmente 10 segundos
    private int cantidadZombies = 1;
    private boolean generacionPausada = false;// Cantidad de zombies por oleada

    public ZombiesStrategic(List<Entidad> entidades, EntidadesActualizables entidadesActualizables, EntidadesAccesibles entidadesAccesibles) {
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
                if (!entidadesAccesibles.isJuegoPausado()) { // Solo generar zombis si no está pausado
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
                ((BasicZombie) entidad).iniciarHiloMovimiento();
            } else if (entidad instanceof ConeheadZombie) {
                ((ConeheadZombie) entidad).iniciarHiloMovimiento();
            } else if (entidad instanceof BucketheadZombie) {
                ((BucketheadZombie) entidad).iniciarHiloMovimiento();
            }
        }
    }

    public void generarOleadaZombies() {
        for (int i = 0; i < cantidadZombies; i++) {
            generarZombie();
        }
    }

    private void generarZombie() {
        int fila = (int) (Math.random() * 5);
        int tipoZombie = (int) (Math.random() * 3);  // Elige aleatoriamente el tipo de zombie

        Zombie zombie = null;
        switch (tipoZombie) {
            case 0:
                zombie = new ConeheadZombie(9, fila, 380, 0.05f, 100, entidadesAccesibles);
                zombie.setImagen(new ImageIcon(getClass().getResource("/resources/ConeheadZombie.gif")));
                break;
            case 1:
                zombie = new BucketheadZombie(9, fila, 800, 0.05f, 100, entidadesAccesibles);
                zombie.setImagen(new ImageIcon(getClass().getResource("/resources/BucketheadZombie.gif")));
                break;
            case 2:
                zombie = new BasicZombie(9, fila, 100, 0.05f, 100, entidadesAccesibles);
                zombie.setImagen(new ImageIcon(getClass().getResource("/resources/Zombie.gif")));
                break;
        }

        entidades.add(zombie);
        entidadesActualizables.actualizarEntidades(entidades);
    }


    private void aumentarDificultad() {
        if (intervaloGeneracion > 3000) {  // Límite mínimo de 3 segundos
            intervaloGeneracion -= 500;
            reprogramarTimer();  // Reinicia el timer con el nuevo intervalo
        }
        cantidadZombies++;  // Aumenta la cantidad de zombies por oleada
    }

    private void reprogramarTimer() {
        timer.cancel();  // Cancela el timer anterior
        iniciarGeneracionAutomatica();  // Reinicia el timer con el nuevo intervalo
    }

    public void iniciarGeneracionAutomaticaMaquina() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!entidadesAccesibles.isJuegoPausado()) { // Solo generar zombis si no está pausado
                    generarOleadaZombies();
                    aumentarDificultad(); // Incrementa la dificultad
                }
            }
        }, 20000, intervaloGeneracion); // Comienza después de 20s, se repite según el intervalo
    }
}
