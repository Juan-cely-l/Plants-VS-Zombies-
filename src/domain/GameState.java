package domain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Clase que representa el estado completo del juego.
 * Implementa Serializable para permitir su guardado y carga.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L; // Versión de serialización

    private List<Entidad> entidades;
    private int soles;
    private int cerebros;
    private int tiempoRestante;
    private boolean modoPala;
    private boolean juegoPausado;
    private boolean juegoTerminado;
    private int duracionJuego;
    private boolean enPreparacion;
    private boolean primeraRondaTerminada;
    private boolean turnoPlantas;
    private GameMode gameMode;

    private ZombiesOriginal zombiesOriginal;
    private ZombiesStrategic zombiesStrategic;
    private PlantsIntelligent plantsIntelligent;
    private PlantsStrategic plantsStrategic;
    private int  tiempoPorRonda;

    /**
     * Constructor completo que inicializa todas las propiedades del estado del juego.
     */
    public GameState(List<Entidad> entidades, int soles, int cerebros, int tiempoRestante, boolean modoPala,
                     boolean juegoPausado, boolean juegoTerminado, int duracionJuego, boolean enPreparacion,
                     boolean primeraRondaTerminada, boolean turnoPlantas, ZombiesOriginal zombiesOriginal,
                     ZombiesStrategic zombiesStrategic, PlantsIntelligent plantsIntelligent, PlantsStrategic plantsStrategic,GameMode gameMode,int tiempoPorRonda) {
        this.entidades = entidades;
        this.soles = soles;
        this.cerebros = cerebros;
        this.tiempoRestante = tiempoRestante;
        this.modoPala = modoPala;
        this.juegoPausado = juegoPausado;
        this.juegoTerminado = juegoTerminado;
        this.duracionJuego = duracionJuego;
        this.enPreparacion = enPreparacion;
        this.primeraRondaTerminada = primeraRondaTerminada;
        this.turnoPlantas = turnoPlantas;
        this.zombiesOriginal = zombiesOriginal;
        this.zombiesStrategic = zombiesStrategic;
        this.plantsIntelligent = plantsIntelligent;
        this.plantsStrategic = plantsStrategic;
        this.gameMode = gameMode;
        this.tiempoPorRonda = tiempoPorRonda;
    }

    // Getters y Setters para todas las propiedades

    public GameMode getGameMode() {
        return gameMode;
    }
    public int getTiempoPorRonda() {
        return tiempoPorRonda;
    }

    public void setTiempoPorRonda(int tiempoPorRonda) {
        this.tiempoPorRonda = tiempoPorRonda;
    }


    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public List<Entidad> getEntidades() {
        return entidades;
    }

    public void setEntidades(List<Entidad> entidades) {
        this.entidades = entidades;
    }

    public int getSoles() {
        return soles;
    }

    public void setSoles(int soles) {
        this.soles = soles;
    }

    public int getCerebros() {
        return cerebros;
    }

    public void setCerebros(int cerebros) {
        this.cerebros = cerebros;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    public boolean isModoPala() {
        return modoPala;
    }

    public void setModoPala(boolean modoPala) {
        this.modoPala = modoPala;
    }

    public boolean isJuegoPausado() {
        return juegoPausado;
    }

    public void setJuegoPausado(boolean juegoPausado) {
        this.juegoPausado = juegoPausado;
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    public void setJuegoTerminado(boolean juegoTerminado) {
        this.juegoTerminado = juegoTerminado;
    }

    public int getDuracionJuego() {
        return duracionJuego;
    }

    public void setDuracionJuego(int duracionJuego) {
        this.duracionJuego = duracionJuego;
    }

    public boolean isEnPreparacion() {
        return enPreparacion;
    }

    public void setEnPreparacion(boolean enPreparacion) {
        this.enPreparacion = enPreparacion;
    }

    public boolean isPrimeraRondaTerminada() {
        return primeraRondaTerminada;
    }

    public void setPrimeraRondaTerminada(boolean primeraRondaTerminada) {
        this.primeraRondaTerminada = primeraRondaTerminada;
    }

    public boolean isTurnoPlantas() {
        return turnoPlantas;
    }

    public void setTurnoPlantas(boolean turnoPlantas) {
        this.turnoPlantas = turnoPlantas;
    }

    public ZombiesOriginal getZombiesOriginal() {
        return zombiesOriginal;
    }

    public void setZombiesOriginal(ZombiesOriginal zombiesOriginal) {
        this.zombiesOriginal = zombiesOriginal;
    }

    public ZombiesStrategic getZombiesStrategic() {
        return zombiesStrategic;
    }

    public void setZombiesStrategic(ZombiesStrategic zombiesStrategic) {
        this.zombiesStrategic = zombiesStrategic;
    }

    public PlantsIntelligent getPlantsIntelligent() {
        return plantsIntelligent;
    }

    public void setPlantsIntelligent(PlantsIntelligent plantsIntelligent) {
        this.plantsIntelligent = plantsIntelligent;
    }

    public PlantsStrategic getPlantsStrategic() {
        return plantsStrategic;
    }

    public void setPlantsStrategic(PlantsStrategic plantsStrategic) {
        this.plantsStrategic = plantsStrategic;
    }

    /**
     * Método llamado automáticamente durante la deserialización.
     * Reestablece los campos transitorios y asegura la consistencia de las referencias.
     *
     * @param ois ObjectInputStream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        // Reestablecer referencias si es necesario
        // Por ejemplo, si alguna de las entidades necesita referencias específicas
    }

    /**
     * Reinicia los hilos de las entidades deserializadas.
     */
    public void reiniciarHilosEntidades() {

        for (Entidad entidad : entidades) {
            if (entidad instanceof BasicZombie) {
                ((BasicZombie) entidad).iniciarHiloMovimiento();
            } else if (entidad instanceof BucketheadZombie) {
                ((BucketheadZombie) entidad).iniciarHiloMovimiento();
            } else if (entidad instanceof ConeheadZombie) {
                ((ConeheadZombie) entidad).iniciarHiloMovimiento();
            } else if (entidad instanceof Peashooter) {
                ((Peashooter) entidad).iniciarAtaquePeriodico();
            }else if (entidad instanceof Guisante) {
                ((Guisante) entidad).iniciarMovimiento();

            }
            // Añadir más casos según sea necesario para otras entidades con hilos
        }
    }
}
