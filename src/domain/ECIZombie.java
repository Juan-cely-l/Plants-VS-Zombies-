package domain;

import java.util.Timer;
import java.util.TimerTask;

public class ECIZombie extends Zombie implements Atacante {
    private int dañoPOOmBas; // Daño que causan las POOmBas
    private  transient Timer temporizador;
    private Tablero tablero; // Referencia al tablero

    public ECIZombie(int xPos, int yPos, int dañoPOOmBas, Tablero tablero) {
        super(xPos, yPos, 200, 1.0f, 50); // Salud: 200, Velocidad: 1.0, Daño cuerpo a cuerpo: 50
        this.dañoPOOmBas = dañoPOOmBas;
        this.tablero = tablero; // Asigna la referencia del tablero

        // Configuración del temporizador para disparar POOmBas cada 3 segundos
        this.temporizador = new Timer();
        temporizador.schedule(new TimerTask() {
            @Override
            public void run() {
                dispararPOOmBa(); // Dispara POOmBas cada 3 segundos
            }
        }, 0, 3000); // Intervalo de disparos: 3 segundos
    }

    private void dispararPOOmBa() {
        System.out.println("ECIZombie en (" + xPos + ", " + yPos + ") dispara una POOmBa causando " + dañoPOOmBas + " de daño.");
        Planta objetivo = buscarPlantaEnFila();
        if (objetivo != null) {
            atacar(objetivo); // Utiliza el método atacar para aplicar el daño
        }
    }

    private Planta buscarPlantaEnFila() {
        // Utiliza la instancia del tablero para buscar plantas
        for (Entidad entidad : tablero.getEntidades()) {
            if (entidad instanceof Planta && entidad.getyPos() == this.yPos) {
                return (Planta) entidad;
            }
        }
        return null; // No hay plantas en la fila
    }

    @Override
    public void atacar(Planta planta) {
        if (planta != null) {
            System.out.println("ECIZombie ataca a la planta en (" + planta.getxPos() + ", " + planta.getyPos() + ") con " + dañoPOOmBas + " de daño.");
            planta.recibirDaño(dañoPOOmBas); // Aplica el daño de la POOmBa a la planta
        }
    }

    @Override
    public void actualizar() {
        mover(); // El zombie avanza hacia la izquierda
    }

    @Override
    public void interactuar(Entidad otra) {
        if (otra instanceof Planta && intersectaConPlanta((Planta) otra)) {
            System.out.println("ECIZombie ataca cuerpo a cuerpo a la planta.");
            atacar((Planta) otra); // Ataque cuerpo a cuerpo
        }
    }

    public void detenerDisparos() {
        temporizador.cancel();
        System.out.println("ECIZombie dejó de disparar POOmBas.");
    }
}
