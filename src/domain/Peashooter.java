package domain;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Peashooter extends Planta implements Defender {
    private int daño;
    private Tablero tablero; // Referencia al tablero donde se encuentra
    private transient ImageIcon imagen;
    private List<Guisante> guisantes; // Lista de guisantes disparados
    private transient ScheduledExecutorService scheduler; // Para tareas periódicas
    private transient EntidadesActualizables entidadesActualizables; // Marcado como transient
    private transient EntidadesAccesibles entidadesAccesibles; // Marcado como transient

    // Constructor
    public Peashooter(int x, int y, int salud, int coste, int daño, Tablero tablero, EntidadesActualizables entidadesActualizables, EntidadesAccesibles entidadesAccesibles) {
        super(x, y, salud, coste);
        this.daño = daño;
        this.tablero = tablero;
        this.imagen = new ImageIcon(getClass().getResource("/resources/Peashooter-.gif"));
        this.guisantes = new ArrayList<>(); // Inicializamos la lista de guisantes
        this.entidadesActualizables = entidadesActualizables;
        this.entidadesAccesibles = entidadesAccesibles;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(); // Inicializa el scheduler
        iniciarAtaquePeriodico(); // Inicia el ataque periódico
    }

    /**
     * Setters para entidadesActualizables y entidadesAccesibles
     */
    public void setEntidadesActualizables(EntidadesActualizables entidadesActualizables) {
        this.entidadesActualizables = entidadesActualizables;
    }

    public void setEntidadesAccesibles(EntidadesAccesibles entidadesAccesibles) {
        this.entidadesAccesibles = entidadesAccesibles;
    }

    /**
     * Método que inicia el ataque periódico cada 1.5 segundos.
     */
    public void iniciarAtaquePeriodico() {
        // Se ejecutará cada 1.5 segundos
        scheduler.scheduleAtFixedRate(() -> {
            if (entidadesAccesibles != null && !entidadesAccesibles.isJuegoPausado() && getSalud() > 0) {
                atacar();
            }
        }, 0, 1500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void atacar() {
        // Verificar si la salud del Peashooter es mayor que cero
        if (getSalud() <= 0) {
            // Si la salud es 0 o menos, detener la generación de guisantes
            for (Guisante guisante : guisantes) {
                guisante.setEnMovimiento(false); // Detener el movimiento del guisante
            }
            guisantes.clear(); // Limpiar la lista de guisantes disparados
            System.out.println("Peashooter ha muerto, ya no se generan más guisantes.");
            return; // Terminar la ejecución del método sin disparar más guisantes
        }

        // Verificar si hay zombis en la misma fila
        boolean hayZombiEnLaFila = false;
        for (Entidad entidad : entidadesAccesibles.obtenerEntidades()) {
            if (entidad instanceof Zombie) {
                // Verificar si el zombi está en la misma fila (misma posición Y)
                if (entidad.getyPos() == getyPos()) {
                    hayZombiEnLaFila = true;
                    System.out.println("Zombi detectado en la fila " + getyPos() + " en posición X: " + entidad.getxPos());
                    break; // Si encontramos un zombi en la fila, dejamos de buscar
                }
            }
        }

        // Depuración: Verificar si hay algún zombi en la fila
        if (hayZombiEnLaFila) {
            System.out.println("Disparando guisante, zombi en la fila " + getyPos());
            // Crear un nuevo guisante
            Guisante guisante = new Guisante(getxPos(), getyPos(), daño, tablero, entidadesAccesibles, entidadesActualizables);
            guisantes.add(guisante); // Añadir el guisante a la lista de disparados
            guisante.iniciarMovimiento(); // Iniciar el movimiento del guisante

            // Crear una lista con solo el nuevo guisante y actualizar las entidades
            List<Entidad> nuevosGuisantes = new ArrayList<>();
            nuevosGuisantes.add(guisante);
            entidadesActualizables.actualizarEntidades(nuevosGuisantes);
        } else {
            System.out.println("No hay zombis en la fila " + getyPos() + " para disparar.");
        }
    }

    /**
     * Método de deserialización para restaurar los objetos 'transient'.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();  // Deserializa los campos normales

        // Restauramos la imagen manualmente después de la deserialización
        this.imagen = new ImageIcon(getClass().getResource("/resources/Peashooter-.gif"));

        // Restauramos el scheduler manualmente
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        iniciarAtaquePeriodico();  // Reiniciamos las tareas periódicas después de la restauración
    }

    /**
     * Detiene el ataque periódico si es necesario.
     */
    public void detenerAtaquePeriodico() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown(); // Detiene el scheduler
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow(); // Forzar detención si no se cierra en 1 segundo
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Reanuda el ataque periódico si es necesario.
     */
    public void reanudarAtaquePeriodico() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            iniciarAtaquePeriodico();
        }
    }

    @Override
    public void actualizar() {
        // Actualiza el estado del Peashooter si es necesario
        // Se puede agregar más lógica aquí si es necesario
    }

    @Override
    public void interactuar(Entidad otra) {
        // No interactúa directamente con otras entidades
    }

    public ImageIcon getImagen() {
        return imagen;
    }

    @Override
    public void setxPos(int xPos) {
        super.setxPos(xPos);
    }

    @Override
    public void setyPos(int yPos) {
        super.setyPos(yPos);
    }
}
