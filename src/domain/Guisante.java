package domain;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Guisante extends Entidad {
    private int velocidad;
    private int daño;
    private transient EntidadesActualizables entidadesActualizables; // Marcado como transient
    private transient EntidadesAccesibles entidadesAccesibles;     // Marcado como transient
    private transient ImageIcon imagen;
    private boolean enMovimiento; // Variable de control de movimiento
    private transient Thread hiloMovimiento;
    private Tablero tablero;

    /**
     * Constructor para la clase Guisante.
     *
     * @param xPos                Posición inicial en X.
     * @param yPos                Posición inicial en Y.
     * @param daño                Daño que causa el guisante.
     * @param tablero             Referencia al tablero.
     * @param entidadesAccesibles Referencia a EntidadesAccesibles.
     * @param entidadesActualizables Referencia a EntidadesActualizables.
     */
    public Guisante(int xPos, int yPos, int daño, Tablero tablero, EntidadesAccesibles entidadesAccesibles, EntidadesActualizables entidadesActualizables) {
        super(xPos, yPos, 1); // Llama al constructor de Entidad con salud = 1
        this.velocidad = 1;    // Velocidad del movimiento del guisante
        this.daño = daño;
        this.tablero = tablero;
        this.imagen = new ImageIcon(getClass().getResource("/resources/Pea_p.png"));
        this.enMovimiento = true; // Inicia el guisante en movimiento
        this.entidadesAccesibles = entidadesAccesibles;
        this.entidadesActualizables = entidadesActualizables;
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
     * Método que inicia el movimiento del guisante con un hilo.
     */
    public void iniciarMovimiento() {
        // Si ya está en movimiento, no iniciamos un nuevo hilo
        if (enMovimiento && (hiloMovimiento == null || !hiloMovimiento.isAlive())) {
            hiloMovimiento = new Thread(() -> {
                while (enMovimiento) {
                    if (!entidadesAccesibles.isJuegoPausado()) {
                        mover(); // Mueve el guisante solo si el juego no está pausado
                    }
                    try {
                        Thread.sleep(100); // Espera 100ms antes de mover nuevamente
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break; // Termina el hilo si es interrumpido
                    }
                }
            });
            hiloMovimiento.start();
        }
    }

    /**
     * Mueve el guisante hacia la derecha.
     */
    public void mover() {
        if (enMovimiento) {
            xPos += velocidad; // Mueve el guisante hacia la derecha

            // Verificar si el guisante ha alcanzado algún zombi
            for (Entidad entidad : entidadesAccesibles.obtenerEntidades()) {
                if (entidad instanceof Zombie) {
                    Zombie zombi = (Zombie) entidad;
                    if (this.xPos == zombi.getxPos() && this.yPos == zombi.getyPos()) {
                        // Si el guisante colisiona con el zombi, aplica el daño
                        zombi.recibirDaño(daño);
                        zombi.actualizar();  // Actualizar el estado del zombi (por ejemplo, eliminarlo si está muerto)
                        System.out.println("Zombi recibió daño en posición (" + zombi.getxPos() + ", " + zombi.getyPos() + ") Salud: " + zombi.getSalud());

                        this.enMovimiento = false;  // Detener el movimiento del guisante
                        break;  // Salir del bucle después de que el guisante haya impactado a un zombi
                    }
                }
            }

            // Verificar si el guisante ha cruzado el tablero sin impactar nada, y eliminarlo
            if (this.xPos > 8) {
                this.enMovimiento = false;  // Detener el movimiento
                entidadesAccesibles.obtenerEntidades().remove(this);  // Eliminar el guisante si se sale del panel
                entidadesActualizables.actualizarEntidades(new ArrayList<>()); // Notificar al GamePanel
            }
        }
    }

    /**
     * Método de deserialización para restaurar los objetos 'transient'.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject(); // Deserializa los campos normales

        // Restauramos el campo 'imagen' manualmente
        this.imagen = new ImageIcon(getClass().getResource("/resources/Pea_p.png"));

        // Restauramos el campo 'hiloMovimiento' manualmente
        this.hiloMovimiento = null; // El hilo se inicia cuando se llama a iniciarMovimiento()
    }

    /**
     * Detiene el movimiento del guisante.
     */
    public void detenerMovimiento() {
        enMovimiento = false;
        if (hiloMovimiento != null) {
            hiloMovimiento.interrupt(); // Interrumpe el hilo de movimiento
        }
    }

    public boolean isEnMovimiento() {
        return enMovimiento;
    }

    public void setEnMovimiento(boolean enMovimiento) {
        this.enMovimiento = enMovimiento;
    }

    @Override
    public void actualizar() {
        // Actualiza el estado del guisante si es necesario
    }

    @Override
    public void interactuar(Entidad otra) {
        if (otra instanceof Zombie) {
            Zombie zombi = (Zombie) otra;
            if (zombi.isMuerto()) {
                entidadesAccesibles.obtenerEntidades().remove(otra);
                System.out.println("Zombie eliminado del tablero en posición (" + otra.getxPos() + ", " + otra.getyPos() + ")");
            }
        }
    }

    @Override
    public void recibirDaño(int daño) {
        this.salud -= daño;
        if (this.salud < 0) {
            this.salud = 0; // No permitir salud negativa
        }
    }

    // Getters para la posición del guisante
    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public ImageIcon getImagen() {
        return imagen;
    }

    public int getDaño() {
        return daño;
    }
}
