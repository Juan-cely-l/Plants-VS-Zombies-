package domain;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clase PotatoMine: Planta explosiva que tarda 14 segundos en activarse.
 * Una vez activa, al contacto con un zombie, le inflige 5000 de daño
 * (asegurando su muerte) y la PotatoMine se destruye a sí misma.
 * Si no está activa, el zombie puede comerla sin que explote.
 */
public class PotatoMine extends Planta implements Defender {
    private boolean activa; // Indica si la mina está lista para explotar
    private transient Timer temporizador;
    private transient  ImageIcon imagen = new ImageIcon(getClass().getResource("/resources/Potato Mine.gif"));
    private static final int DAÑO_EXPLOSION = 5000; // Daño suficiente para matar a cualquier zombie

    /**
     * Constructor de PotatoMine.
     *
     * @param xPos  Posición X en el tablero.
     * @param yPos  Posición Y en el tablero.
     * @param salud Salud inicial de la mina (ej: 100)
     * @param coste Coste en soles (ej: 25)
     */
    public PotatoMine(int xPos, int yPos, int salud, int coste) {
        super(xPos, yPos, salud, coste);
        this.activa = false; // Inicialmente no está activa
        this.salud = salud;
        this.coste = coste;
        this.temporizador = new Timer();

        // Configurar temporizador para activar la mina después de 14 segundos (14000 ms)
        temporizador.schedule(new TimerTask() {
            @Override
            public void run() {
                activar(); // Cambiar el estado a activa
            }
        }, 14000);
    }

    /**
     * Activa la mina, permitiéndole explotar al tocar un zombie.
     */
    private void activar() {
        activa = true;
        System.out.println("PotatoMine en (" + xPos + ", " + yPos + ") está activa y lista para explotar.");
        // Si se desea, aquí podría cambiarse la imagen a una de la mina lista
        // this.imagen = new ImageIcon(getClass().getResource("/resources/PotatoMineActive.gif"));
    }

    @Override
    public void atacar() {
        // La PotatoMine no ataca de forma continua, su ataque se produce al contacto con un zombie estando activa.
    }

    @Override
    public void interactuar(Entidad otra) {
        // La interacción se da cuando un zombie ocupa la misma celda que la PotatoMine
        if (otra instanceof Zombie) {
            Zombie zombie = (Zombie) otra;
            // Verificar posición
            if (zombie.getxPos() == this.xPos && zombie.getyPos() == this.yPos) {
                if (this.isActiva()) {
                    // Mina activa: explota
                    // Hacemos daño masivo al zombie y destruimos la PotatoMine
                    zombie.recibirDaño(DAÑO_EXPLOSION);
                    this.setSalud(0);
                    System.out.println("¡PotatoMine ha explotado y matado al zombie en (" + xPos + ", " + yPos + ")!");
                } else {
                    // Mina inactiva: el zombie puede comerla sin que explote
                    // Por ejemplo, el zombie podría hacerle daño cada vez que interactúa.
                    // Ajustar el daño según la lógica del juego. Aquí como ejemplo:
                    this.recibirDaño(20);
                    if (this.getSalud() <= 0) {
                        System.out.println("PotatoMine en (" + xPos + ", " + yPos + ") fue comida antes de activarse.");
                    }
                }
            }
        }
    }

    @Override
    public void actualizar() {
        // La PotatoMine no realiza acciones activas además de esperar para activarse
        // Su lógica principal se da en la interacción con zombies.
    }

    @Override
    public void recibirDaño(int daño) {
        this.salud -= daño;
        if (this.salud < 0) {
            this.salud = 0; // No permitir salud negativa
        }
    }

    /**
     * Indica si la mina está activa.
     * @return true si está activa, false en caso contrario.
     */
    public boolean isActiva() {
        return activa;
    }

    @Override
    public ImageIcon getImagen() {
        return imagen;
    }

    /**
     * Método de deserialización para restaurar los objetos 'transient'.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Deserializa los campos que no son 'transient' automáticamente
        ois.defaultReadObject();

        // Restauramos el campo 'imagen' manualmente
        this.imagen = new ImageIcon(getClass().getResource("/resources/Potato Mine.gif"));

    }
}
