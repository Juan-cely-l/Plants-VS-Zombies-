package domain;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Clase ECIPlant: Planta especial que genera 50 soles en su posición cada 20 segundos.
 */
public class ECIPlant extends Planta implements GeneradorDeSoles {
    private transient  ImageIcon imagen;
    private int solesGenerados;

    /**
     * Constructor de ECIPlant.
     *
     * @param x Posición X en el tablero.
     * @param y Posición Y en el tablero.
     */
    public ECIPlant(int x, int y, int salud, int coste) {
        super(x, y, 150, 75); // Salud: 150, Coste: 75
        this.imagen = new ImageIcon(getClass().getResource("/resources/ECIplant.png"));
        this.solesGenerados = 0; // Inicializamos en 0
    }

    @Override
    public Sol generarSol() {
        // Crear un sol grande con un valor de 50 unidades
        Sol solGrande = new Sol(getxPos(), getyPos(), 50);
        System.out.println("ECIPlant generó un sol grande en (" + getxPos() + ", " + getyPos() + ") con valor 50.");
        return solGrande;
    }

    @Override
    public void manejarGeneracionSol(Sol sol) {
        // Manejo adicional si es necesario (opcional)
        System.out.println("Se manejó el sol generado por ECIPlant.");
    }

    @Override
    public void actualizar() {
        // Aquí podrías agregar lógica de actualización específica si es necesario
    }

    @Override
    public void interactuar(Entidad otra) {
        // ECIPlant no interactúa directamente con otras entidades
    }

    @Override
    public ImageIcon getImagen() {
        return imagen;
    }

    @Override
    public int getyPos() {
        return super.getyPos();
    }

    @Override
    public int getxPos() {
        return super.getxPos();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Deserializa los campos que no son 'transient' automáticamente
        ois.defaultReadObject();

        // Restauramos el campo 'imagen' manualmente
        this.imagen = new ImageIcon(getClass().getResource("/resources/ECIplant.png"));


    }

    public int getSolesGenerados() {
        return solesGenerados;
    }
}
