package domain;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Representa un Girasol en el juego.
 * Genera soles periódicamente.
 */
public class Girasol extends Planta implements GeneradorDeSoles {
    private transient Timer temporizador;
    private GeneradorDeSoles manejadorDeSoles;
    private transient ImageIcon imagen;
    private int solesGenerados; // Registro del total de soles generados

    /**
     * Constructor para inicializar un Girasol.
     *
     * @param x      Posición X en el tablero.
     * @param y      Posición Y en el tablero.
     * @param salud  Salud inicial del Girasol.
     * @param coste  Costo en soles para colocar la planta.
     */
    public Girasol(int x, int y, int salud, int coste) {
        super(x, y, salud, coste);
        this.temporizador = new Timer();
        this.imagen=new ImageIcon(getClass().getResource("/resources/Sunflower.gif"));
        this.solesGenerados = 0; // Inicializamos en 0
    }

    /**
     * Vincula un manejador externo para gestionar los soles generados.
     *
     * @param manejador Manejador de soles implementado en la capa de presentación.
     */
    public void setManejadorDeSoles(GeneradorDeSoles manejador) {
        this.manejadorDeSoles = manejador;
    }

    @Override
    public Sol generarSol() {
        return new Sol(getxPos(), getyPos());
    }

    @Override
    public void manejarGeneracionSol(Sol sol) {
        // Notifica al manejador de la capa de presentación.
        if (manejadorDeSoles != null) {
            manejadorDeSoles.manejarGeneracionSol(sol);
        }
    }
    @Override
    public void recibirDaño(int daño) {
        this.salud -= daño;
        if (this.salud < 0) {
            this.salud = 0; // No permitir salud negativa
        }
    }

    /**
     * Inicia la generación automática de soles.
     */
    public void iniciarGeneracionAutomatica() {
        temporizador.schedule(new TimerTask() {
            @Override
            public void run() {
                Sol nuevoSol = generarSol();
                manejarGeneracionSol(nuevoSol);
            }
        }, 0, 5000); // Generar soles cada 5 segundos
    }

    /**
     * Detiene la generación automática de soles.
     */
    public void detenerGeneracionAutomatica() {
        temporizador.cancel();
    }

    @Override
    public void actualizar() {
        // Aquí puedes incluir lógica adicional en el futuro.
    }

    @Override
    public void interactuar(Entidad otra) {
        if (otra instanceof Zombie) {
            System.out.println("¡Cuidado! Un zombie está cerca del Girasol.");
        }
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
    /**
     * Método de deserialización para restaurar los objetos 'transient'.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Deserializa los campos que no son 'transient' automáticamente
        ois.defaultReadObject();

        // Restauramos el campo 'imagen' manualmente
        this.imagen = new ImageIcon(getClass().getResource("/resources/Sunflower.gif"));


    }
    public int getSolesGenerados() {
        return solesGenerados;
    }

}
