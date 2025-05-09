package domain;

import javax.swing.ImageIcon;

/**
 * Clase base abstracta para representar plantas en el juego.
 * Extiende la clase Entidad e introduce el costo y la imagen como atributos adicionales.
 */
public abstract class Planta extends Entidad {
    protected int coste; // Costo en soles para colocar la planta
    private ImageIcon imagen; // Imagen asociada a la planta

    /**
     * Constructor para inicializar una planta.
     *
     * @param xPos  Posición X en el tablero.
     * @param yPos  Posición Y en el tablero.
     * @param salud Cantidad inicial de salud.
     * @param coste Costo en soles para colocar la planta.
     */
    public Planta(int xPos, int yPos, int salud, int coste) {
        super(xPos, yPos, salud);
        this.coste = coste;
    }

    /**
     * Retorna el costo de la planta.
     *
     * @return Costo en soles.
     */
    public int getCoste() {
        return coste;
    }

    /**
     * Retorna la imagen de la planta.
     *
     * @return ImageIcon de la planta.
     */
    public ImageIcon getImagen() {
        return imagen;
    }

    /**
     * Configura la imagen de la planta.
     *
     * @param imagen La imagen a asociar con la planta.
     */
    public void setImagen(ImageIcon imagen) {
        this.imagen = imagen;
    }

    @Override
    public int getxPos() {
        return super.getxPos();
    }

    @Override
    public int getyPos() {
        return super.getyPos();
    }

    public boolean estaMuerta() {
        return this.salud <= 0;
    }

    @Override
    public void recibirDaño(int daño) {
        this.salud -= daño;
        if (this.salud < 0) {
            this.salud = 0; // No permitir salud negativa
        }
    }
    public String getNombre() {
        return this.getClass().getSimpleName();
    }


}
