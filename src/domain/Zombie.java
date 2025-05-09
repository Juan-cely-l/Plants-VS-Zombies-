package domain;



import presentation.GamePanel;

import javax.swing.*;

public abstract class Zombie extends Entidad implements Movible, Atacante {
    private static int contadorZombis = 0;
    private Thread hiloMovimiento;
    protected float velocidad;
    protected int daño;
    protected int lane; // Carril en el que está el zombie
    protected boolean muerto;
    protected EntidadesAccesibles entidadesAccesibles;

    public Zombie(int xPos, int yPos, int salud, float velocidad, int daño) {
        super(xPos, yPos, salud);
        this.velocidad = velocidad;
        this.daño = daño;
        this.muerto = false;
    }
    public Zombie(int xPos, int yPos, int salud, float velocidad, int daño,EntidadesAccesibles entidadesAccesibles) {
        super(xPos, yPos, salud);
        this.velocidad = velocidad;
        this.daño = daño;
        this.muerto = false;
        this.entidadesAccesibles = entidadesAccesibles;
    }

    @Override
    public void mover() {
        this.xPos -= velocidad; // Movimiento básico hacia la izquierda
    }

    @Override
    public void atacar(Planta planta) {
        planta.recibirDaño(daño); // Reducir la salud de la planta
    }

    public boolean intersectaConPlanta(Planta planta) {
        return this.xPos == planta.getxPos() && this.yPos == planta.getyPos();
    }

    public boolean gameOver() {
        // Si el zombi cruza un límite (por ejemplo, xPos <= 0), se considera que alcanzó la base
        return xPos <= 0;
    }

    public static int getN() {
        return contadorZombis; // Retorna el número total de zombis generados
    }

    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }

    public void recibirDaño(int daño) {
        this.salud -= daño;
        if (this.salud <= 0) {
            this.salud = 0;
            detenerMovimiento(); // Detener el movimiento cuando el zombie muere
            entidadesAccesibles.obtenerEntidades().remove(this); // Eliminar del juego
            System.out.println("¡Zombie eliminado! Posición (" + getxPos() + ", " + getyPos() + ")");
        }
    }

    public void actualizar() {
        if (muerto=true) {
            entidadesAccesibles.obtenerEntidades().remove(this);  // Eliminar el zombi del tablero// Actualizar el tablero
            System.out.println("Zombi eliminado del tablero en posición (" + getxPos() + ", " + getyPos() + ")");
        }
    }
    public void detenerMovimiento() {
        if (hiloMovimiento != null && hiloMovimiento.isAlive()) {
            hiloMovimiento.interrupt(); // Detener el hilo de movimiento
            hiloMovimiento = null; // Liberar el hilo
        }
    }

    public boolean isMuerto() {

        return this.salud <= 0;
    }
    // Getter para el nombre de la clase
    public String getNombre() {
        return this.getClass().getSimpleName(); // Retorna el nombre simple de la clase

    }
    public void setImagen(ImageIcon imagen) {
        this.imagen = imagen;
    }

    public void setEntidadesAccesibles(EntidadesAccesibles entidadesAccesibles) {
        this.entidadesAccesibles=entidadesAccesibles;
    }

    public void setEntidadesActualizables(EntidadesActualizables entidadesActualizables) {

    }
}






