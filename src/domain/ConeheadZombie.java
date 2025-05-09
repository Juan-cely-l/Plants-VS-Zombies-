package domain;

import presentation.GamePanel;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConeheadZombie extends Zombie implements Atacante,Movible {
    private transient ImageIcon imagen;
    private float velocidadBase; // Velocidad inicial configurable
    private float velocidadActual; // Velocidad actual
    private long ultimoTiempoActualizacion; // Última marca de tiempo para movimiento
    private boolean atacando = false; // Controla si el zombi está atacando
    private transient Thread hiloMovimiento;
    private static final float FACTOR_VELOCIDAD_GLOBAL = 0.09f; // 50% de la velocidad base
    private float xPosFlotante;
    private EntidadesAccesibles entidadesAccesibles; // Referencia a EntidadesAccesibles
    private transient ScheduledExecutorService scheduler;
    private int costo;



    public ConeheadZombie(int xPos, int yPos, int salud, float velocidad, int daño, EntidadesAccesibles entidadesAccesibles,int costo) {
        super(xPos, yPos, salud, velocidad, daño);
        this.imagen = new ImageIcon (getClass().getResource("/resources/ConeheadZombie.gif"));
        this.entidadesAccesibles = entidadesAccesibles;
        this.velocidadBase = velocidad; // Configurar la velocidad base
        this.velocidadActual = this.velocidadBase;
        this.ultimoTiempoActualizacion = System.currentTimeMillis(); // Inicializar el tiempo de referencia
        this.xPosFlotante = xPos;
        iniciarHiloMovimiento();
        this.costo = costo;

    }
    public ConeheadZombie(int xPos, int yPos, int salud, float velocidad, int daño, EntidadesAccesibles entidadesAccesibles) {
        super(xPos, yPos, salud, velocidad, daño);
        this.imagen = new ImageIcon(getClass().getResource("/resources/ConeheadZombie.gif"));
        this.entidadesAccesibles = entidadesAccesibles;
        this.velocidadBase = velocidad; // Configurar la velocidad base
        this.velocidadActual = this.velocidadBase;
        this.ultimoTiempoActualizacion = System.currentTimeMillis(); // Inicializar el tiempo de referencia
        this.xPosFlotante = xPos;
        iniciarHiloMovimiento();

    }


    @Override
    public void actualizar() {
        if (!atacando) { // Solo moverse si no está atacando
            mover();
        }
        for (Entidad otra : entidadesAccesibles.obtenerEntidades()) { // Acceder a otras entidades del tablero
            interactuar(otra); // Verificar interacciones
        }
    }
    public void iniciarHiloMovimiento() {
        hiloMovimiento = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) { // Mejora: verifica si el hilo ha sido interrumpido
                if (!atacando && !entidadesAccesibles.isJuegoPausado()) { // Verifica si el juego está pausado
                    mover();
                }
                try {
                    Thread.sleep(100); // Mantiene la fluidez del movimiento
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break; // Termina el hilo si es interrumpido
                }
            }
        });
        hiloMovimiento.start();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Deserializa los campos que no son 'transient' automáticamente
        ois.defaultReadObject();

        // Restauramos el campo 'imagen' manualmente
        this.imagen = new ImageIcon(getClass().getResource("/resources/ConeheadZombie.gif"));

        // Restauramos el campo 'hiloMovimiento' manualmente, aunque no se reinicia hasta que se llame a iniciarHiloMovimiento
        this.hiloMovimiento = null; // El hilo se inicia cuando se llama a iniciarHiloMovimiento()

        // Restauramos el campo 'scheduler' manualmente
        this.scheduler = null; // El scheduler debe ser reiniciado cuando sea necesario

        // Si deseas reiniciar el hilo o scheduler después de la deserialización, puedes hacerlo aquí,
        // pero en general no es recomendable restablecer estos objetos en la deserialización directamente.
    }

    @Override
    public void mover() {

        xPosFlotante -= velocidad* 0.05f ;  // 0.05f para suavizar el movimiento

        // Si el zombi ha cruzado el límite, lo podemos detener o hacer algo más
        if (xPosFlotante < 0) {
            xPosFlotante = 0;
        }

        // Actualizamos la posición en la clase padre (Zombie) utilizando un valor entero
        setxPos((int) xPosFlotante);}

    @Override
    public void interactuar(Entidad otra) {
        if (otra instanceof Planta && intersectaConPlanta((Planta) otra)) {
            atacando = true; // Detener movimiento mientras ataca
            iniciarAtaqueRepetido((Planta) otra);

            if (((Planta) otra).estaMuerta()) {
                entidadesAccesibles.obtenerEntidades().remove(otra); // Eliminar planta del tablero
                System.out.println("Planta eliminada del tablero en posición (" + otra.getxPos() + ", " + otra.getyPos() + ")");
                atacando = false;
            }

        }

    }

    /**
     * Realiza el ataque a una planta, reduciendo su salud.
     *
     * @param planta La planta a atacar.
     */
    @Override
    public void atacar(Planta planta) {
        planta.recibirDaño(100);
        System.out.println("Zombie atacando planta en posición (" + planta.getxPos() + ", " + planta.getyPos() + ")");
    }
    public void iniciarAtaqueRepetido(Planta planta) {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> atacar(planta), 0, 500, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void recibirDaño(int daño) {
        this.salud -= daño;
        if (this.salud <= 0) {
            this.salud = 0;
            detenerMovimiento();
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdownNow();
            }
            System.out.println("¡Zombi eliminado!");
        }
    }

    public void detenerAtaque() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        atacando = false;
    }

    @Override
    public boolean intersectaConPlanta(Planta planta) {
        return Math.abs(this.getxPos() - planta.getxPos()) < 1 && this.getyPos() == planta.getyPos();
    }




    public void detenerMovimiento() {
        if (hiloMovimiento != null) {
            hiloMovimiento.interrupt();  // Detener el hilo de movimiento
        }
    }

    public ImageIcon getImagen() {
        return imagen;
    }
    public int getCosto() {
        return costo;
    }
    public void setImagen(ImageIcon imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return "ConeHeadZombie";
    }
}
