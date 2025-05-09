package domain;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Zombie especial Brainstein: No se mueve, genera recursos cada 20 segundos
 * y puede atacar si es necesario.
 */
public class Brainstein extends Zombie  {
    private transient Timer temporizador;
    private int recursosGenerados; // Recursos generados (cerebros)
    private transient ImageIcon imagen;

    /**
     * Constructor de Brainstein.
     *
     * @param xPos Posición X en el tablero.
     * @param yPos Posición Y en el tablero.
     */
    public Brainstein(int xPos, int yPos) {
        super(xPos, yPos, 300, 0, 0); // Salud: 300, Velocidad: 0 (no se mueve), Daño: 0 (no ataca cuerpo a cuerpo)
        this.recursosGenerados = 0;
        this.temporizador = new Timer();
        this.imagen = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/Brainstein-.png")));

        // Configurar temporizador para generar recursos cada 20 segundos
        temporizador.schedule(new TimerTask() {
            @Override
            public void run() {
                generarRecursos();
            }
        }, 0, 20000); // 20 segundos en milisegundos
    }

    /**
     * Genera recursos (cerebros) cada 20 segundos.
     */
    private void generarRecursos() {
        recursosGenerados += 25; // Generar 25 cerebros
        System.out.println("Brainstein en posición (" + xPos + ", " + yPos + ") generó 25 cerebros. Total acumulado: " + recursosGenerados + ".");
    }

    @Override
    public void atacar(Planta planta) {
        // Aunque implementa la interfaz Atacante, este zombie no ataca directamente
        System.out.println("Brainstein no ataca directamente a las plantas.");
    }

    @Override
    public void actualizar() {
        // Brainstein no se mueve ni tiene lógica adicional en cada ciclo
    }

    @Override
    public void interactuar(Entidad otra) {
        if (otra instanceof Planta && intersectaConPlanta((Planta) otra)) {
            atacar((Planta) otra); // Ejecuta un ataque (aunque no ataca directamente)
        }
    }

    /**
     * Devuelve la cantidad total de recursos generados.
     *
     * @return Recursos generados (cerebros).
     */
    public int getRecursosGenerados() {
        return recursosGenerados;
    }

    /**
     * Detiene la generación automática de recursos.
     */
    public void detenerGeneracion() {
        temporizador.cancel();
        System.out.println("Brainstein dejó de generar recursos.");
    }

    public ImageIcon getImagen() {
        return imagen;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Deserializa los campos que no son 'transient' automáticamente
        ois.defaultReadObject();

        // Restauramos el campo 'imagen' manualmente
        this.imagen = new ImageIcon(getClass().getResource("/resources/Brainstein-.png"));


    }
}
