package domain;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Clase Wall-nut, una planta defensiva que actúa como un escudo.
 */
public class WallNut extends Planta {
    private transient ImageIcon imagen= new ImageIcon(getClass().getResource("/resources/wall-nut.gif"));

    public WallNut(int x, int y, int salud, int coste) {
        super(x, y, salud, coste);
    }

    @Override
    public void actualizar() {
        // La Wall-nut no realiza ninguna acción activa durante el juego
    }

    @Override
    public void interactuar(Entidad otra) {
        // La Wall-nut no interactúa directamente con otras entidades
        // Solo sirve como un escudo pasivo que los zombies atacan
    }
    @Override
    public void recibirDaño(int daño) {
        this.salud -= daño;
        if (this.salud < 0) {
            this.salud = 0; // No permitir salud negativa
        }
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
        this.imagen = new ImageIcon(getClass().getResource("/resources/wall-nut.gif"));


    }
}
