package domain;

/**
 * Clase de excepción personalizada para el juego Poob vs Zombies.
 * Esta excepción se utiliza para manejar errores específicos del juego,
 * proporcionando mensajes descriptivos para facilitar el diagnóstico y la resolución de problemas.
 */
public class PoobVsZombiesException extends Exception {

    /**
     * Constructor por defecto.
     * Crea una nueva instancia de PoobVsZombiesException sin mensaje ni causa.
     */
    public PoobVsZombiesException() {
        super();
    }

    /**
     * Constructor que recibe un mensaje de error.
     *
     * @param message Mensaje detallado de la excepción.
     */
    public PoobVsZombiesException(String message) {
        super(message);
    }

    /**
     * Constructor que recibe un mensaje de error y una causa.
     *
     * @param message Mensaje detallado de la excepción.
     * @param cause   Causa original de la excepción.
     */
    public PoobVsZombiesException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor que recibe una causa.
     *
     * @param cause Causa original de la excepción.
     */
    public PoobVsZombiesException(Throwable cause) {
        super(cause);
    }
}
