package presentation;

public interface TurnChangeListener {
    /**
     * Método llamado cuando el turno cambia.
     * @param isTurnoPlantas true si es el turno de las plantas, false si es el turno de los zombies.
     */
    void onTurnChange(boolean isTurnoPlantas);
}
