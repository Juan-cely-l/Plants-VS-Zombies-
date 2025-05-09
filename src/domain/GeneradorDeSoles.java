package domain;

public interface GeneradorDeSoles {
    /**
     * Genera un nuevo sol.
     * @return El sol generado.
     */
    Sol generarSol();

    /**
     * Maneja un nuevo sol generado.
     * @param sol El sol generado.
     */
    void manejarGeneracionSol(Sol sol);
}
