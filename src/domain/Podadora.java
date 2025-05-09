package domain;

import presentation.GamePanel;
import javax.swing.*;
import java.util.List;
import java.util.Iterator;

/**
 * Clase Podadora: Se activa al detectar un zombie en su posición
 * y elimina todos los zombies en la línea.
 */
public class Podadora extends Entidad {
    private boolean activa; // Indica si la podadora ya se activó
    private EntidadesAccesibles entidadesAccesibles; // Referencia al tablero donde se encuentra
    private ImageIcon imagen;

    /**
     * Constructor de Podadora.
     *
     * @param xPos    Posición X en el tablero (siempre será 0).
     * @param yPos    Posición Y en el tablero.
     * @param entidadesAccesibles Referencia al tablero donde se encuentra la podadora.
     */
    public Podadora(int xPos, int yPos, EntidadesAccesibles entidadesAccesibles) {
        super(xPos, yPos, Integer.MAX_VALUE); // Salud infinita para que no pueda ser destruida
        this.activa = false; // Inicialmente inactiva
        this.entidadesAccesibles=entidadesAccesibles;
        this.imagen = new ImageIcon(getClass().getResource("/resources/Podadora.png"));
    }

    @Override
    public void actualizar() {
        // La podadora no hace nada por sí misma; espera a ser activada.
    }

    @Override
    public void interactuar(Entidad otra) {
        if (otra instanceof Zombie && !activa && ((Zombie) otra).getxPos() == 0) {
            activar(); // Activar podadora si un zombie llega a la primera columna
        }
    }

    /**
     * Activa la podadora y elimina todos los zombies en la fila.
     */
    private void activar() {
        activa = true;
        System.out.println("Podadora en posición (" + xPos + ", " + yPos + ") activada.");

        // Eliminar todos los zombies en la misma fila
        List<Entidad> entidades = entidadesAccesibles.obtenerEntidades(); // Usar la instancia del tablero
        Iterator<Entidad> iterator = entidades.iterator();
        while (iterator.hasNext()) {
            Entidad entidad = iterator.next();
            if (entidad instanceof Zombie && entidad.getyPos() == this.yPos) {
                Zombie zombi = (Zombie) entidad;
                zombi.recibirDaño(10000);
                zombi.actualizar();// Eliminar el zombie de la lista
                System.out.println("Zombie eliminado en la posición (" + entidad.getxPos() + ", " + entidad.getyPos() + ")");
            }
        }

        // Después de eliminar los zombies, la podadora desaparece (se elimina del tablero)
        entidades.remove(this);
        System.out.println("Podadora eliminada después de ser activada.");
    }

    /**
     * Verifica si la podadora intersecta con un zombie.
     *
     * @param zombie Zombie que podría estar en la posición de la podadora.
     * @return Verdadero si están en la misma posición.
     */


    private boolean intersectaConZombie(Zombie zombie) {
        return zombie.getxPos() == this.xPos && zombie.getyPos() == this.yPos;
    }


    public boolean isActiva() {
        return activa;
    }

    public ImageIcon getImagen() {
        return imagen;
    }
}
