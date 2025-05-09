package presentation;

import domain.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePanel extends JPanel implements EntidadesActualizables,EntidadesAccesibles    {
    public enum Gamemode {
        ORIGINAL,
        PERSONALIZADO,
        ZOMBIES,
        SUPERVIVENCIA,
        MAQUINA
    }
    private GameMode gameMode;

    private Tablero tablero;
    private static List<Entidad> entidades = new CopyOnWriteArrayList<>(); // Lista de entidades en el juegos en el juego
    private ImageIcon fondoTablero; // Imagen del tablero de fondo
    private ImageIcon menuJuego; // Imagen del menú de juego
    private int soles = 100; // Contador de soles iniciales
    private int cerebros = 100; // Contador de cerebros iniciales
    private String plantaSeleccionada = null; // Planta actualmente seleccionada
    private String zombieSeleccionado = null; // Zombie actualmente seleccionado
    private Timer globalTimer; // Timer global para el incremento de soles
    private Timer girasolTimer; // Timer para el funcionamiento de los Girasoles
    private Timer gameLoopTimer; // Timer para el loop del juego
    private Timer countdownTimer; // Temporizador para la cuenta regresiva
    private Timer cerebrosTimer; // Timer para incrementar cerebros en modo Zombies
    private int tiempoRestante; // Tiempo restante en milisegundos
    private boolean modoPala = false;
    private volatile boolean juegoPausado = false; // Variable para controlar el estado de pausa
    private boolean juegoTerminado = false; // Estado del juego
    private JButton botonPausa;
    private ZombiesOriginal zombiesOriginal;
    private ZombiesStrategic zombiesStrategic;
    private PlantsIntelligent plantsIntelligent;
    private PlantsStrategic plantsStrategic;
    private int duracionJuego; // Duración del juego en milisegundos
    private boolean victoria ;

    // Variables para supervivencia
    private boolean enPreparacion = false;
    private boolean primeraRondaTerminada = false;
    private Timer preparacionTimer;
    private int tiempoPreparacionRestante = 120000; // 2 minutos
    private int tiempoPorRonda; // Se setea en supervivencia
    // Variable para alternar turnos en Supervivencia
    private boolean turnoPlantas = true; // true si es turno del jugador de plantas, false si es del jugador de zombies

    // Recursos iniciales para Supervivencia
    private static final int RECURSOS_INICIALES_SOL = 2000;
    private static final int RECURSOS_INICIALES_CEREBROS = 2000;
    // Lista de listeners para cambios de turno
    private List<TurnChangeListener> turnChangeListeners = new ArrayList<>();



    public GamePanel(GameMode gameMode) {
        this.gameMode = gameMode;
        if (gameMode == GameMode.ZOMBIES) {
            cerebros = 100; // Valor inicial de cerebros
        } else if (gameMode == GameMode.SUPERVIVENCIA) {
            soles = RECURSOS_INICIALES_SOL;
            cerebros = RECURSOS_INICIALES_CEREBROS;
        } else if (gameMode == GameMode.MAQUINA) {
            soles = 200; // Configuración inicial para el modo máquina
            cerebros = 200;
        } else {
            soles = 100;
        }


        this.tablero = new Tablero(5, 9, new ArrayList<>());
        setBackground(Color.GREEN);
        fondoTablero = new ImageIcon(getClass().getResource("/resources/Background.jpg"));

        // Botón de pausa
        botonPausa = new JButton("Pausar");
        botonPausa.setPreferredSize(new Dimension(80, 30));
        botonPausa.addActionListener(e -> {
            try {
                pausarJuego();
            } catch (PoobVsZombiesException ex) {
                throw new RuntimeException(ex);
            }
        });
        this.add(botonPausa);

        // MouseListener para el manejo de eventos
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (juegoPausado || juegoTerminado) return;  // No hacer nada si el juego está pausado o terminado

                try {
                    if (modoPala) {
                        eliminarPlanta(e.getX(), e.getY());
                        modoPala = false; // Desactiva el modo pala después de eliminar la planta
                    } else {
                        if (gameMode == GameMode.ZOMBIES) {
                            colocarZombie(e.getX(), e.getY());
                        } else if (gameMode == GameMode.PERSONALIZADO || gameMode == GameMode.ORIGINAL) {
                            colocarPlanta(e.getX(), e.getY());
                        } else if (gameMode == GameMode.SUPERVIVENCIA) {
                            if (enPreparacion) {
                                colocarPlanta(e.getX(), e.getY());
                            } else {
                                if (turnoPlantas) {
                                    colocarPlanta(e.getX(), e.getY());
                                    turnoPlantas = false; // Cambiar turno
                                    notifyTurnChange(turnoPlantas);
                                    JOptionPane.showMessageDialog(GamePanel.this, "Turno del jugador de zombies.", "Cambio de Turno", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    colocarZombie(e.getX(), e.getY());
                                }
                            }
                        }
                    }
                } catch (PoobVsZombiesException ex) {
                    JOptionPane.showMessageDialog(GamePanel.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    // Manejo de excepciones inesperadas
                    JOptionPane.showMessageDialog(GamePanel.this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });



        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                actualizarPosicionBotonPausa();
            }
        });

        if (gameMode != GameMode.SUPERVIVENCIA) {
            iniciarTimers();
        }
    }


    /**
     * Configura la duración del juego y comienza la cuenta regresiva.
     *
     * @param duracionMilis Duración del juego en milisegundos.
     */
    public void setDuracionJuego(int duracionMilis) {
        this.duracionJuego = duracionMilis;
        if (gameMode == GameMode.SUPERVIVENCIA) {
            // Establecer la fase de preparación de 2 minutos (120,000 ms)
            tiempoPreparacionRestante = 120000;
            iniciarFasePreparacion();
        } else {
            this.tiempoRestante = duracionMilis;
            iniciarCuentaRegresiva();
        }
    }


    /**
     * Inicia el temporizador de cuenta regresiva.
     */
    private void iniciarCuentaRegresiva() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }

        countdownTimer = new Timer(1000, e -> {
            if (!juegoPausado && !juegoTerminado) {
                tiempoRestante -= 1000;
                if (tiempoRestante <= 0) {
                    tiempoRestante = 0;
                    if (gameMode == GameMode.SUPERVIVENCIA) {
                        if (!primeraRondaTerminada) {
                            // Termina primera ronda
                            primeraRondaTerminada = true;
                            pausarJuegoForzado();
                            JOptionPane.showMessageDialog(this, "Primera ronda finalizada. Reorganiza tus plantas, tienes 2 min.");
                            iniciarFasePreparacion();
                        } else {
                            // Segunda ronda terminada
                            try {
                                terminarJuego(true);
                            } catch (PoobVsZombiesException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    } else {
                        if (gameMode == GameMode.ZOMBIES) {
                            try {
                                terminarJuego(false);
                            } catch (PoobVsZombiesException ex) {
                                throw new RuntimeException(ex);
                            }
                        } else {
                            try {
                                terminarJuego(true);
                            } catch (PoobVsZombiesException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }
                repaint();
            }
        });
        countdownTimer.start();
    }






    @Override
    public List<Entidad> obtenerEntidades() {
        return entidades;
    }



    /**
     * Termina el juego y muestra el resultado.
     * @param victoria Indica si el jugador ha ganado.
     */
    public void terminarJuego(boolean victoria) throws PoobVsZombiesException {
        if (juegoTerminado) return;
        juegoTerminado = true;
        this.victoria = victoria;

        // Verificar si la duración del juego es 0
        if (duracionJuego == 0) {
            // Si la duración es 0, no mostramos el mensaje ni continuamos el juego
            return;
        }

        // Detener temporizadores
        if (countdownTimer != null) countdownTimer.stop();
        if (gameLoopTimer != null) gameLoopTimer.stop();
        if (globalTimer != null) globalTimer.stop();
        if (girasolTimer != null) girasolTimer.stop();
        if (cerebrosTimer != null && (gameMode == GameMode.ZOMBIES || gameMode == GameMode.SUPERVIVENCIA)) cerebrosTimer.stop();

        // Detener generación de zombies y movimiento
        if (zombiesOriginal != null) zombiesOriginal.detenerGeneracionAutomatica();
        if (zombiesStrategic != null) zombiesStrategic.detenerGeneracionAutomatica();
        for (Entidad entidad : entidades) {
            if (entidad instanceof Zombie) ((Zombie) entidad).detenerMovimiento();
            if (entidad instanceof Peashooter) ((Peashooter) entidad).detenerAtaquePeriodico();
            if (entidad instanceof Guisante) ((Guisante) entidad).detenerMovimiento();
        }

        double puntajeFinal = calcularPuntaje();
        String puntajeStr = String.format("Puntaje: %.2f", puntajeFinal);

        String mensaje = "";

        // Solo mostrar mensaje de victoria/derrota si la duración no es 0
        if (gameMode == GameMode.SUPERVIVENCIA) {
            if (victoria) {
                mensaje = "¡Felicidades! El jugador de plantas ha ganado la partida.";
            } else {
                mensaje = "¡Felicidades! El jugador de zombies ha ganado la partida.";
            }
        } else if (gameMode == GameMode.ZOMBIES) {
            if (victoria) {
                mensaje = "¡Felicidades! Has ganado el juego.";
            } else {
                mensaje = "¡Has perdido! Un zombie ha alcanzado la primera columna.";
            }
        } else {
            if (victoria) {
                mensaje = "¡Felicidades! Has ganado el juego.";
            } else {
                mensaje = "¡Has perdido! Un zombie ha llegado a la primera fila.";
            }
        }

        // Solo añadir puntaje si la duración es válida
        mensaje += "\n" + puntajeStr;

        // Mostrar mensaje solo si la duración es válida
        if (duracionJuego != 0) {
            JOptionPane.showMessageDialog(this, mensaje, "Resultado del Juego", JOptionPane.INFORMATION_MESSAGE);
        }

        // Preguntar si se quiere jugar de nuevo
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Deseas jugar de nuevo?", "Reiniciar Juego", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            reiniciarJuego();
        } else {
            confirmarSalida();
        }
    }



    /**
     * Inicia los timers para el contador global, la producción de soles y el loop del juego.
     */

    /**
     * Inicia los timers para el contador global, la producción de soles/cerebros y el loop del juego.
     */
    private void iniciarTimers() {
        gameLoopTimer = new Timer(16, e -> { // Aproximadamente 60 FPS
            if (!juegoPausado && !juegoTerminado) { // Solo actualizar si el juego no está pausado ni terminado
                List<Entidad> entidadesParaEliminar = new ArrayList<>();

                synchronized (entidades) {
                    // Actualizar entidades
                    for (Entidad entidad : new ArrayList<>(entidades)) {
                        entidad.actualizar();
                    }

                    // Eliminar entidades muertas
                    for (Entidad entidad : new ArrayList<>(entidades)) {
                        if ((entidad instanceof Planta && entidad.getSalud() <= 0) ||
                                (entidad instanceof Zombie && entidad.getSalud() <= 0) ||
                                (entidad instanceof Guisante && !((Guisante) entidad).isEnMovimiento())) {
                            entidadesParaEliminar.add(entidad);
                            System.out.println("Entidad eliminada del tablero en posición (" + entidad.getxPos() + ", " + entidad.getyPos() + ")");
                        }
                    }

                    entidades.removeAll(entidadesParaEliminar);

                    // Verificar si algún zombie ha llegado a la primera columna
                    for (Entidad entidad : new ArrayList<>(entidades)) {
                        if (entidad instanceof Zombie) {
                            if (entidad.getxPos() <= 0) {
                                if (gameMode == GameMode.ZOMBIES || gameMode == GameMode.SUPERVIVENCIA) {
                                    try {
                                        terminarJuego(false); // El jugador de zombies gana
                                    } catch (PoobVsZombiesException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                } else {
                                    try {
                                        terminarJuego(false); // El jugador de plantas pierde
                                    } catch (PoobVsZombiesException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                                break; // Salir del loop para evitar múltiples llamadas
                            }
                        }
                    }

                }
                repaint(); // Redibuja el tablero después de actualizar
            }
        });

        gameLoopTimer.start();

        if (gameMode != GameMode.ZOMBIES && gameMode != GameMode.SUPERVIVENCIA) {
            // Timer para aumentar los soles globales cada 10 segundos
            globalTimer = new Timer(10000, e -> {
                if (!juegoPausado && !juegoTerminado) {
                    aumentarSoles(25); // Incrementar el contador global
                }
            });
            globalTimer.start();
        }

        // Timer para producir soles o cerebros con girasoles cada 20 segundos
        girasolTimer = new Timer(20000, e -> {
            if (!juegoPausado && !juegoTerminado) {
                synchronized (entidades) {
                    producirSolesPorGirasoles();
                }
            }
        });
        girasolTimer.start();

        if (gameMode == GameMode.ZOMBIES || gameMode == GameMode.SUPERVIVENCIA) {
            // Timer para aumentar cerebros cada 15 segundos
            cerebrosTimer = new Timer(15000, e -> {
                if (!juegoPausado && !juegoTerminado) {
                    aumentarCerebros(50); // Incrementar cerebros globales
                }
            });
            cerebrosTimer.start();
        }
    }



    /**
     * Actualiza la lista de entidades en el juego.
     *
     * @param nuevasEntidades Lista de nuevas entidades a añadir.
     */
    @Override
    public void actualizarEntidades(List<Entidad> nuevasEntidades) {
        synchronized (entidades) {
            entidades.addAll(nuevasEntidades); // Añade solo las nuevas entidades
            SwingUtilities.invokeLater(this::repaint);  // Redibuja el panel para mostrar las entidades actualizadas
        }
    }

    // Métodos para pausar y reanudar entidades específicas
    private void pausarTodasLasEntidades() {
        for (Entidad entidad : entidades) {
            if (entidad instanceof Peashooter) {
                ((Peashooter) entidad).detenerAtaquePeriodico();
            }
            // Agrega más condiciones si tienes otras entidades que necesitan pausar
        }
    }

    private void reanudarTodasLasEntidades() {
        for (Entidad entidad : entidades) {
            if (entidad instanceof Peashooter) {
                ((Peashooter) entidad).reanudarAtaquePeriodico();
            }
            // Agrega más condiciones si tienes otras entidades que necesitan reanudar
        }
    }
    /**
     * Calcula la puntuación actual del juego.
     *
     * @return La puntuación calculada.
     */
    private double calcularPuntaje() throws PoobVsZombiesException {
        int costoTotal = 0;
        synchronized (entidades) {
            for (Entidad entidad : entidades) {
                if (entidad instanceof Planta) {
                    Planta planta = (Planta) entidad;
                    costoTotal += getCostoPlanta(planta.getNombre()); // Asegúrate de tener getNombre()
                }
                if (gameMode == GameMode.ZOMBIES || gameMode == GameMode.SUPERVIVENCIA) {
                    if (entidad instanceof Zombie) {
                        Zombie zombie = (Zombie) entidad;
                        costoTotal += getCostoZombie(zombie.getNombre());
                    }
                }
            }
        }
        double puntaje;
        if (gameMode == GameMode.ZOMBIES || gameMode == GameMode.SUPERVIVENCIA) {
            puntaje = (cerebros + costoTotal) * 1.5;
        } else {
            puntaje = (soles + costoTotal) * 1.5;
        }
        return puntaje;
    }




    /**
     * Pausa o reanuda el juego.
     */
    private void pausarJuego() throws PoobVsZombiesException {
        juegoPausado = !juegoPausado; // Cambia el estado de pausa
        System.out.println("Juego pausado: " + juegoPausado); // Depuración

        if (juegoPausado) {
            // Detener los timers
            if (gameLoopTimer != null) gameLoopTimer.stop();
            if (globalTimer != null) globalTimer.stop();
            if (girasolTimer != null) girasolTimer.stop();
            if (cerebrosTimer != null && gameMode == GameMode.ZOMBIES) cerebrosTimer.stop();
            if (countdownTimer != null) countdownTimer.stop(); // Detener la cuenta regresiva también

            // Pausar la generación de zombies
            if (zombiesOriginal != null) {
                zombiesOriginal.pausarGeneracionZombies();
            }
            if (zombiesStrategic != null) {
                zombiesStrategic.pausarGeneracionZombies();
            }

            // Pausar todas las entidades
            pausarTodasLasEntidades();

            System.out.println("Timers detenidos."); // Depuración

            // Cambiar el texto del botón de pausa
            botonPausa.setText("Reanudar");

            // Mostrar el menú de pausa
            mostrarMenuPausa();
        } else {
            // Reiniciar los timers
            if (gameLoopTimer != null) gameLoopTimer.start();
            if (globalTimer != null) globalTimer.start();
            if (girasolTimer != null) girasolTimer.start();
            if (cerebrosTimer != null &&(gameMode != GameMode.ZOMBIES && gameMode != GameMode.SUPERVIVENCIA)) cerebrosTimer.start();
            if (countdownTimer != null) countdownTimer.start(); // Reiniciar la cuenta regresiva

            // Reanudar la generación de zombies
            if (zombiesOriginal != null) {
                zombiesOriginal.reanudarGeneracionZombies();
            }
            if (zombiesStrategic != null) {
                zombiesStrategic.reanudarGeneracionZombies();
            }

            // Reanudar todas las entidades
            reanudarTodasLasEntidades();

            System.out.println("Timers reiniciados."); // Depuración

            // Cambiar el texto del botón de pausa
            botonPausa.setText("Pausar");
        }
    }

    private void pausarJuegoForzado() {
        juegoPausado = true;
        if (gameLoopTimer != null) gameLoopTimer.stop();
        if (globalTimer != null) globalTimer.stop();
        if (girasolTimer != null) girasolTimer.stop();
        if (cerebrosTimer != null && gameMode == GameMode.ZOMBIES) cerebrosTimer.stop();
        if (countdownTimer != null) countdownTimer.stop();

        if (zombiesOriginal != null) zombiesOriginal.pausarGeneracionZombies();
        if (zombiesStrategic != null) zombiesStrategic.pausarGeneracionZombies();
        pausarTodasLasEntidades();
    }
    /**
     * Muestra el menú de pausa con opciones.
     */
    private void mostrarMenuPausa() throws PoobVsZombiesException {
        String[] opciones = {"Reanudar", "Guardar partida", "Abrir juego guardado", "Reiniciar", "Salir"};
        int opcionSeleccionada = JOptionPane.showOptionDialog(
                this,
                "Juego Pausado",
                "Menú Pausa",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        switch (opcionSeleccionada) {
            case 0:
                pausarJuego();
                break;
            case 1:
                guardarPartida();
                break;
            case 2:
                abrirJuegoGuardado();
                break;
            case 3:
                reiniciarJuego();
                break;
            case 4:
                confirmarSalida();
                break;
            default:
                // Manejar si el usuario cierra el diálogo sin seleccionar una opción
                break;
        }
    }

    /**
     * Confirma la salida del juego.
     */
    private void confirmarSalida() {
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas salir del juego?",
                "Confirmar Salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            limpiarRecursos();
            System.exit(0);
        } else {
            // Opcional: Puedes llamar a mostrarMenuPausa() nuevamente si deseas que el menú reaparezca
        }
    }


    /**
     * Limpia los recursos antes de salir del juego.
     */
    private void limpiarRecursos() {
        // Detener todos los hilos activos
        if (zombiesOriginal != null) {
            zombiesOriginal.detenerGeneracionAutomatica();
        }

        for (Entidad entidad : entidades) {
            if (entidad instanceof BasicZombie) {
                ((BasicZombie) entidad).detenerMovimiento();
            }
            if (entidad instanceof Peashooter) {
                ((Peashooter) entidad).detenerAtaquePeriodico();
            }
            if (entidad instanceof Guisante) {
                ((Guisante) entidad).detenerMovimiento();
            }
            // Agrega más condiciones si tienes otras entidades con recursos que limpiar
        }

        // Cerrar cualquier otro recurso si es necesario
    }

    /**
     * Devuelve el estado de pausa del juego.
     *
     * @return true si el juego está pausado, false de lo contrario.
     */
    public boolean isJuegoPausado() {
        return juegoPausado;
    }

    /**
     * Actualiza la posición del botón de pausa al redimensionar la ventana.
     */
    private void actualizarPosicionBotonPausa() {
        botonPausa.setBounds(getWidth() - 100, 10, 80, 30);
    }

    /**
     * Guarda la partida actual.
     */
    /**
     * Guarda la partida actual.
     */
    // presentation/GamePanel.java

    public void guardarPartida() throws PoobVsZombiesException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Partida");
        fileChooser.setAcceptAllFileFilterUsed(false);

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try {
                if (!fileToSave.getName().endsWith(".dat")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".dat");
                }

                // Determinar tiempoPorRonda según el modo de juego
                int tiempoPorRondaActual = 0;
                if (gameMode == GameMode.SUPERVIVENCIA) {
                    tiempoPorRondaActual = this.tiempoPorRonda;
                }

                GameState gameState = new GameState(
                        new ArrayList<>(entidades),
                        soles,
                        cerebros,
                        tiempoRestante,
                        modoPala,
                        juegoPausado,
                        juegoTerminado,
                        duracionJuego,
                        enPreparacion,
                        primeraRondaTerminada,
                        turnoPlantas,
                        zombiesOriginal,
                        zombiesStrategic,
                        plantsIntelligent,
                        plantsStrategic,
                        this.gameMode, // **Añadido**
                        tiempoPorRondaActual // **Añadido**
                );

                try (FileOutputStream fileOut = new FileOutputStream(fileToSave);
                     ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

                    out.writeObject(gameState);
                }

                System.out.println("Partida guardada con éxito en: " + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                throw new PoobVsZombiesException("Error al guardar la partida en el archivo: " + fileToSave.getAbsolutePath(), e);
            }
        } else {
            System.out.println("Guardado cancelado.");
        }
    }






    /**
     * Abre una partida guardada.
     */
    public void abrirJuegoGuardado() throws PoobVsZombiesException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir Partida Guardada");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de Partida", "dat"));

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();

            try {
                FileInputStream fileIn = new FileInputStream(archivoSeleccionado);
                ObjectInputStream in = new ObjectInputStream(fileIn);

                GameState partidaCargada = (GameState) in.readObject();
                in.close();
                fileIn.close();

                // **Cambios realizados aquí**
                this.entidades.clear(); // Limpia la lista original
                this.entidades.addAll(partidaCargada.getEntidades()); // Añade todas las entidades deserializadas

                this.soles = partidaCargada.getSoles();
                this.cerebros = partidaCargada.getCerebros();
                this.tiempoRestante = partidaCargada.getTiempoRestante();
                this.modoPala = partidaCargada.isModoPala();
                this.juegoPausado = partidaCargada.isJuegoPausado();
                this.juegoTerminado = partidaCargada.isJuegoTerminado();
                this.duracionJuego = partidaCargada.getDuracionJuego();
                this.enPreparacion = partidaCargada.isEnPreparacion();
                this.primeraRondaTerminada = partidaCargada.isPrimeraRondaTerminada();
                this.turnoPlantas = partidaCargada.isTurnoPlantas();
                this.zombiesOriginal = partidaCargada.getZombiesOriginal();
                this.zombiesStrategic = partidaCargada.getZombiesStrategic();
                this.plantsIntelligent = partidaCargada.getPlantsIntelligent();
                this.plantsStrategic = partidaCargada.getPlantsStrategic();
                this.gameMode = partidaCargada.getGameMode(); // **Asignación**
                this.tiempoPorRonda = partidaCargada.getTiempoPorRonda(); // **Añadido**

                // Asignar referencias a entidades
                for (Entidad entidad : entidades) {
                    if (entidad instanceof Peashooter) {
                        Peashooter peashooter = (Peashooter) entidad;
                        peashooter.setEntidadesActualizables(this);
                        peashooter.setEntidadesAccesibles(this);
                    } else if (entidad instanceof Guisante) {
                        Guisante guisante = (Guisante) entidad;
                        guisante.setEntidadesActualizables(this);
                        guisante.setEntidadesAccesibles(this);
                    }
                    // Añadir más condiciones si tienes otras entidades que requieren referencias
                }

                // Restaurar los timers y el estado del juego
                restaurarTimersDesdeEstado(partidaCargada);

                // Iniciar la generación automática si es necesario
                if (gameMode == GameMode.ORIGINAL) {
                    this.zombiesOriginal.iniciarGeneracionAutomatica();
                } else if (gameMode == GameMode.PERSONALIZADO) {
                    this.zombiesStrategic.iniciarGeneracionAutomatica();
                } else if (gameMode == GameMode.SUPERVIVENCIA) {
                    // Iniciar temporizadores específicos de Supervivencia
                    if (enPreparacion) {
                        iniciarFasePreparacion();
                    } else {
                        iniciarCuentaRegresiva();
                        iniciarTimers();
                    }
                }

                // Actualizar entidades en el GamePanel
                this.actualizarEntidades(this.entidades);

                // Reiniciar hilos y schedulers de las entidades
                partidaCargada.reiniciarHilosEntidades();

                // **Forzar eliminación de Guisantes que no están en movimiento**
                List<Entidad> entidadesParaEliminar = new ArrayList<>();
                for (Entidad entidad : entidades) {
                    if (entidad instanceof Guisante) {
                        Guisante guisante = (Guisante) entidad;
                        if (!guisante.isEnMovimiento()) {
                            entidadesParaEliminar.add(guisante);
                        }
                    }
                }
                entidades.removeAll(entidadesParaEliminar);
                repaint();

                System.out.println("Partida cargada con éxito desde: " + archivoSeleccionado.getAbsolutePath());
            } catch (IOException | ClassNotFoundException e) {
                throw new PoobVsZombiesException("Error al cargar la partida desde el archivo: " + archivoSeleccionado.getAbsolutePath(), e);
            }
        } else {
            System.out.println("Selección de archivo cancelada.");
        }}



    private void restaurarTimersDesdeEstado(GameState gameState) {
        this.duracionJuego = gameState.getDuracionJuego();
        this.tiempoRestante = gameState.getTiempoRestante();
        this.enPreparacion = gameState.isEnPreparacion();
        this.primeraRondaTerminada = gameState.isPrimeraRondaTerminada();
        this.turnoPlantas = gameState.isTurnoPlantas();
        this.tiempoPorRonda = gameState.getTiempoPorRonda(); // **Añadido**

        if (gameMode == GameMode.SUPERVIVENCIA) {
            if (enPreparacion) {
                iniciarFasePreparacion();
            } else {
                iniciarCuentaRegresiva();
                iniciarTimers();
                // Notificar al listener sobre el turno actual
                notifyTurnChange(turnoPlantas);
            }
        } else {
            iniciarCuentaRegresiva();
            iniciarTimers();
        }

        // Restaurar el estado de pausa
        if (gameState.isJuegoPausado()) {
            pausarJuegoForzado();
        }
    }











    /**
     * Reinicia el juego restableciendo todos los parámetros iniciales.
     */
    private void reiniciarJuego() {
        if (gameMode == GameMode.ZOMBIES) {
            cerebros = 100;
        } else if (gameMode == GameMode.SUPERVIVENCIA) {
            soles = 2000;
            cerebros = 2000;
        } else {
            soles = 100;
        }

        entidades.clear();
        juegoPausado = false;
        juegoTerminado = false;
        tiempoRestante = duracionJuego;

        // Reiniciar los timers
        if (gameLoopTimer != null) gameLoopTimer.restart();
        if (globalTimer != null) globalTimer.restart();
        if (girasolTimer != null) girasolTimer.restart();
        if (countdownTimer != null) {
            countdownTimer.stop();
            if (gameMode != GameMode.SUPERVIVENCIA) {
                iniciarCuentaRegresiva();
            } else {
                iniciarFasePreparacion();
            }
        }

        if (zombiesOriginal != null) {
            zombiesOriginal.detenerGeneracionAutomatica();
            zombiesOriginal.iniciarGeneracionAutomatica();
        }
        if (zombiesStrategic != null) {
            zombiesStrategic.detenerGeneracionAutomatica();
            zombiesStrategic.iniciarGeneracionAutomatica();
        }


        //repintar nuevo tablero
        inicializarPodadoras(entidades, tablero, this);
        System.out.println("Juego reiniciado y timers reiniciados.");
        repaint();
    }

    private void inicializarPodadoras(List<Entidad> entidades, Tablero tablero, EntidadesAccesibles entidadesAccesibles) {
        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            // Crear una nueva podadora en la primera columna de cada fila
            Podadora podadora = new Podadora(0, fila, entidadesAccesibles);
            entidades.add(podadora); // Añadir la podadora a la lista de entidades
            tablero.agregarEntidad(podadora); // Añadir la podadora al tablero
        }
    }



    /**
     * Asigna el generador de zombies original.
     *
     * @param zombiesOriginal Instancia de ZombiesOriginal.
     */
    public void setZombiesOriginal(ZombiesOriginal zombiesOriginal) {
        this.zombiesOriginal = zombiesOriginal;
    }

    /**
     * Asigna el generador de zombies estratégicos.
     *
     * @param zombiesStrategic Instancia de ZombiesStrategic.
     */
    public void setZombiesStrategic(ZombiesStrategic zombiesStrategic) {
        this.zombiesStrategic = zombiesStrategic;
    }

    /**
     * Genera soles y cerebros de todas las entidades correspondientes presentes en el tablero.
     */
    private void producirSolesPorGirasoles() {
        synchronized (entidades) {
            for (Entidad entidad : entidades) {
                if (entidad instanceof Girasol) {
                    if (gameMode == GameMode.ORIGINAL || gameMode == GameMode.PERSONALIZADO || gameMode == GameMode.SUPERVIVENCIA) {
                        aumentarSoles(25); // Cada Girasol produce 25 soles
                    }
                } else if (entidad instanceof ECIPlant) {
                    if (gameMode == GameMode.ORIGINAL || gameMode == GameMode.PERSONALIZADO || gameMode == GameMode.SUPERVIVENCIA) {
                        aumentarSoles(50); // Cada ECIPlant produce 50 soles
                    }
                } else if (entidad instanceof Brainstein) {
                    if (gameMode == GameMode.ZOMBIES || gameMode == GameMode.SUPERVIVENCIA) {
                        aumentarCerebros(25);
                    }
                }
            }
        }
    }


    public void reducirCerebros(int cantidad) {
        if (gameMode == GameMode.ZOMBIES || gameMode == GameMode.SUPERVIVENCIA) {
            if (cerebros >= cantidad) {
                cerebros -= cantidad;
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "No tienes suficientes cerebros.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void aumentarCerebros(int cantidad) {
        if (gameMode == GameMode.ZOMBIES || gameMode == GameMode.SUPERVIVENCIA) {
            cerebros += cantidad;
            if (cerebros > 2000) cerebros = 2000; // Limitar a 2000
            repaint();
        }
    }

    public void reducirSoles(int cantidad) {
        if (soles >= cantidad) {
            soles -= cantidad;
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "No tienes suficientes soles.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void aumentarSoles(int cantidad) {
        if (gameMode == GameMode.PERSONALIZADO || gameMode == GameMode.ORIGINAL || gameMode == GameMode.SUPERVIVENCIA) {
            soles += cantidad;
            if (soles > 2000) soles = 2000; // Limitar a 2000
            repaint();
        }
    }

    /**
     * Asigna la planta seleccionada para ser colocada.
     *
     * @param plantaSeleccionada Nombre de la planta seleccionada.
     */
    public void setPlantaSeleccionada(String plantaSeleccionada) {
        this.plantaSeleccionada = plantaSeleccionada;
    }

    public void setZombieSeleccionado(String zombieSeleccionado) {
        this.zombieSeleccionado = zombieSeleccionado;
    }

    public void addTurnChangeListener(TurnChangeListener listener) {
        turnChangeListeners.add(listener);
    }

    /**
     * Elimina un listener previamente registrado.
     * @param listener El listener a eliminar.
     */
    public void removeTurnChangeListener(TurnChangeListener listener) {
        turnChangeListeners.remove(listener);
    }

    /**
     * Notifica a todos los listeners registrados sobre un cambio de turno.
     * @param isTurnoPlantas true si es el turno de las plantas, false si es el turno de los zombies.
     */
    private void notifyTurnChange(boolean isTurnoPlantas) {
        for (TurnChangeListener listener : turnChangeListeners) {
            listener.onTurnChange(isTurnoPlantas);
        }
    }


    /**
     * Lógica para colocar una planta en la cuadrícula.
     *
     * @param mouseX Coordenada X del mouse.
     * @param mouseY Coordenada Y del mouse.
     */
    public void colocarPlanta(int mouseX, int mouseY) throws PoobVsZombiesException {
        if (plantaSeleccionada == null) {
            throw new PoobVsZombiesException("No has seleccionado una planta.");
        }

        if (soles < getCostoPlanta(plantaSeleccionada)) {
            throw new PoobVsZombiesException("No tienes suficientes soles para colocar esta planta.");
        }

        int filas = 5;
        int columnas = 9;

        // Calcular dimensiones de la cuadrícula
        int tileWidth = (int) (getWidth() * 0.73) / columnas;
        int tileHeight = (int) (getHeight() * 0.8) / filas;

        // Calcular offsets para centrar la cuadrícula
        int offsetX = (int) ((getWidth() - (tileWidth * columnas)) / 2 + (getWidth() * 0.1));
        int offsetY = (getHeight() - (tileHeight * filas)) / 2;

        // Convertir las coordenadas del mouse en la celda de la cuadrícula
        int xGrid = (mouseX - offsetX) / tileWidth;
        int yGrid = (mouseY - offsetY) / tileHeight;

        if (xGrid < 0 || yGrid < 0 || xGrid >= columnas || yGrid >= filas) {
            throw new PoobVsZombiesException("Has hecho clic fuera de la cuadrícula.");
        }

        if (esCeldaOcupada(xGrid, yGrid)) {
            throw new PoobVsZombiesException("Esta celda ya está ocupada.");
        }

        // Crear y añadir la planta seleccionada
        Planta nuevaPlanta;
        switch (plantaSeleccionada) {
            case "Peashooter":
                nuevaPlanta = new Peashooter(xGrid, yGrid, 300, 100, 20, tablero, this, this);
                ((Peashooter) nuevaPlanta).iniciarAtaquePeriodico();
                break;
            case "Girasol":
                nuevaPlanta = new Girasol(xGrid, yGrid, 300, 50);
                break;
            case "WallNut":
                nuevaPlanta = new WallNut(xGrid, yGrid, 4000, 50);
                break;
            case "PotatoMine":
                nuevaPlanta = new PotatoMine(xGrid, yGrid, 100, 25);
                break;
            case "ECIPlant":
                nuevaPlanta = new ECIPlant(xGrid, yGrid, 150, 75);
                break;
            default:
                throw new PoobVsZombiesException("Planta desconocida seleccionada.");
        }

        entidades.add(nuevaPlanta);
        reducirSoles(getCostoPlanta(plantaSeleccionada));
        plantaSeleccionada = null; // Deseleccionar planta tras colocarla
        repaint();

        // Alternar turno en Supervivencia después de colocar planta
        if (gameMode == GameMode.SUPERVIVENCIA && !enPreparacion) {
            turnoPlantas = false; // Cambiar turno al jugador de zombies
            notifyTurnChange(turnoPlantas);

            JOptionPane.showMessageDialog(this, "Turno del jugador de zombies.", "Cambio de Turno", JOptionPane.INFORMATION_MESSAGE);
        }
    }



    /**
     * Lógica para colocar un zombie en la cuadrícula.
     *
     * @param mouseX Coordenada X del mouse.
     * @param mouseY Coordenada Y del mouse.
     */
    public void colocarZombie(int mouseX, int mouseY) throws PoobVsZombiesException {
        if (gameMode != GameMode.ZOMBIES && gameMode != GameMode.SUPERVIVENCIA) {
            JOptionPane.showMessageDialog(this, "En este modo no se pueden colocar zombies.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (gameMode == GameMode.SUPERVIVENCIA && enPreparacion) {
            JOptionPane.showMessageDialog(this, "Actualmente estás en la fase de preparación. Solo puedes colocar plantas.", "Fase de Preparación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (gameMode == GameMode.SUPERVIVENCIA && !turnoPlantas) {
            // Es el turno del jugador de zombies
        } else if (gameMode == GameMode.ZOMBIES) {
            // En modo Zombies, cualquier jugador puede colocar zombies
        } else {
            // En otros modos, control normal
        }

        // En modo Supervivencia, verificar si es el turno de zombies
        if (gameMode == GameMode.SUPERVIVENCIA && turnoPlantas) {
            JOptionPane.showMessageDialog(this, "Es el turno del jugador de plantas.", "Turno de Zombies", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (zombieSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "No has seleccionado un zombie.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cerebros < getCostoZombie(zombieSeleccionado)) {
            JOptionPane.showMessageDialog(this, "No tienes suficientes cerebros para colocar este zombie.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int filas = 5;
        int columnas = 9;

        // Calcular dimensiones de la cuadrícula
        int tileWidth = (int) (getWidth() * 0.73) / columnas;
        int tileHeight = (int) (getHeight() * 0.8) / filas;

        // Calcular offsets para centrar la cuadrícula
        int offsetX = (int) ((getWidth() - (tileWidth * columnas)) / 2 + (getWidth() * 0.1));
        int offsetY = (getHeight() - (tileHeight * filas)) / 2;

        // Convertir las coordenadas del mouse en la celda de la cuadrícula
        int xGrid = (mouseX - offsetX) / tileWidth;
        int yGrid = (mouseY - offsetY) / tileHeight;

        if (xGrid < 0 || yGrid < 0 || xGrid >= columnas || yGrid >= filas) {
            JOptionPane.showMessageDialog(this, "Has hecho clic fuera de la cuadrícula.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (esCeldaOcupada(xGrid, yGrid)) {
            JOptionPane.showMessageDialog(this, "Esta celda ya está ocupada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear y añadir el zombie seleccionado
        Zombie nuevoZombie;
        switch (zombieSeleccionado) {
            case "BasicZombie":
                nuevoZombie = new BasicZombie(xGrid, yGrid, 100, 0.05f, 100, this, 100);
                ((BasicZombie) nuevoZombie).iniciarHiloMovimiento();
                break;
            case "BucketHeadZombie":
                nuevoZombie = new BucketheadZombie(xGrid, yGrid, 100, 0.05f, 100, this, 100);
                ((BucketheadZombie) nuevoZombie).iniciarHiloMovimiento();
                break;
            case "ConeHeadZombie":
                nuevoZombie = new ConeheadZombie(xGrid, yGrid, 100, 0.05f, 100, this, 100);
                ((ConeheadZombie) nuevoZombie).iniciarHiloMovimiento();
                break;
            case "Brainstein":
                nuevoZombie = new Brainstein(xGrid, yGrid);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Zombie desconocido seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        entidades.add(nuevoZombie);
        reducirCerebros(getCostoZombie(zombieSeleccionado));
        zombieSeleccionado = null; // Deseleccionar zombie tras colocar
        repaint();


    }



    /**
     * Verifica si una celda está ocupada por una planta.
     *
     * @param xGrid Coordenada X en la cuadrícula.
     * @param yGrid Coordenada Y en la cuadrícula.
     * @return true si está ocupada, false de lo contrario.
     */
    private boolean esCeldaOcupada(int xGrid, int yGrid) {
        for (Entidad entidad : entidades) {
            if (entidad instanceof Planta && entidad.getxPos() == xGrid && entidad.getyPos() == yGrid) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene el costo de una planta.
     *
     * @param planta Nombre de la planta.
     * @return Costo en soles.
     */
    private int getCostoPlanta(String planta) throws PoobVsZombiesException {
        switch (planta) {
            case "Peashooter":
                return 100;
            case "Girasol":
                return 50;
            case "WallNut":
                return 50;
            case "PotatoMine":
                return 25;
            case "ECIPlant":
                return 75;
            default:
                throw new PoobVsZombiesException("Planta desconocida: " + planta);
        }
    }

    private int getCostoZombie(String zombie) throws PoobVsZombiesException {
        switch (zombie) {
            case "BasicZombie":
                return 100;
            case "ConeHeadZombie":
                return 150;
            case "BucketHeadZombie":
                return 200;

            case "Brainstein":
                return 50;
            default:
                throw new PoobVsZombiesException("Zombie desconocido: " + zombie);
        }
    }


    /**
     * Elimina una planta en la cuadrícula basada en las coordenadas del mouse.
     *
     * @param mouseX Coordenada X del mouse.
     * @param mouseY Coordenada Y del mouse.
     */
    private void eliminarPlanta(int mouseX, int mouseY) throws PoobVsZombiesException {
        int filas = 5;
        int columnas = 9;

        // Calcular dimensiones de la cuadrícula
        int tileWidth = (int) (getWidth() * 0.73) / columnas;
        int tileHeight = (int) (getHeight() * 0.8) / filas;

        // Calcular offsets para centrar la cuadrícula
        int offsetX = (int) ((getWidth() - (tileWidth * columnas)) / 2 + (getWidth() * 0.1));
        int offsetY = (getHeight() - (tileHeight * filas)) / 2;

        // Convertir las coordenadas del mouse en la celda de la cuadrícula
        int xGrid = (mouseX - offsetX) / tileWidth;
        int yGrid = (mouseY - offsetY) / tileHeight;

        if (xGrid < 0 || yGrid < 0 || xGrid >= columnas || yGrid >= filas) {
            throw new PoobVsZombiesException("Has hecho clic fuera de la cuadrícula.");
        }

        boolean plantaEncontrada = false;
        for (Entidad entidad : entidades) {
            if (entidad instanceof Planta &&
                    entidad.getxPos() == xGrid &&
                    entidad.getyPos() == yGrid) {
                entidad.setSalud(0);  // Establecer la salud en 0
                plantaEncontrada = true;
                break;  // Terminar el bucle una vez encontrada
            }
        }

        if (!plantaEncontrada) {
            throw new PoobVsZombiesException("No hay ninguna planta en esta celda para eliminar.");
        }

        repaint();
    }


    /**
     * Activa el modo pala para eliminar plantas.
     */
    public void activarModoPala() {
        modoPala = true;
        plantaSeleccionada = null;  // Anula la selección de plantas
        System.out.println("Modo Pala Activado");
    }

    public void iniciarFasePreparacion() {
        enPreparacion = true;
        tiempoPreparacionRestante = 120000; // 2 minutos

        preparacionTimer = new Timer(1000, e -> {
            tiempoPreparacionRestante -= 1000;
            if (tiempoPreparacionRestante <= 0) {
                ((Timer)e.getSource()).stop();
                finalizarFasePreparacion();
            }
            repaint();
        });
        preparacionTimer.start();
    }

    private void finalizarFasePreparacion() {
        enPreparacion = false;
        if (preparacionTimer != null) preparacionTimer.stop();

        tiempoRestante = duracionJuego;
        iniciarTimers(); // Iniciar los timers del juego
        iniciarCuentaRegresiva(); // Iniciar la ronda
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (fondoTablero != null) {
            g.drawImage(fondoTablero.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
        if (gameMode == GameMode.SUPERVIVENCIA && !enPreparacion) {
            String turnoStr = turnoPlantas ? "Turno del Jugador de Plantas" : "Turno del Jugador de Zombies";
            dibujarTextoConContorno(g, turnoStr, getWidth() / 2 - 100, 30, turnoPlantas ? Color.GREEN : Color.RED);
        }


        // Mostrar soles y cerebros en modo SUPERVIVENCIA
        if (gameMode == GameMode.ZOMBIES || gameMode == GameMode.SUPERVIVENCIA) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String cerebrosStr = "Cerebros: " + cerebros;
            dibujarTextoConContorno(g, cerebrosStr, 10, 130, Color.RED);
        }
        if (gameMode == GameMode.PERSONALIZADO || gameMode == GameMode.ORIGINAL || gameMode == GameMode.SUPERVIVENCIA) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String solesStr = "Soles: " + soles;
            dibujarTextoConContorno(g, solesStr, 10, 150, Color.GREEN);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String tiempoStr = String.format("Tiempo: %02d:%02d",
                (tiempoRestante / 1000) / 60,
                (tiempoRestante / 1000) % 60);
        dibujarTextoConContorno(g, tiempoStr, 10, 170, Color.WHITE);

        double puntaje = 0;
        try {
            puntaje = calcularPuntaje();
        } catch (PoobVsZombiesException e) {
            throw new RuntimeException(e);
        }
        String puntajeStr = String.format("Puntaje: %.2f", puntaje);
        dibujarTextoConContorno(g, puntajeStr, 10, 190, Color.ORANGE);

        if (enPreparacion && gameMode == GameMode.SUPERVIVENCIA) {
            String prepStr = String.format("Tiempo de preparación: %02d:%02d",
                    (tiempoPreparacionRestante / 1000) / 60,
                    (tiempoPreparacionRestante / 1000) % 60);
            dibujarTextoConContorno(g, prepStr, getWidth() - 300, 130, Color.BLUE);
        }

        // Configuración de la cuadrícula
        int filas = 5;
        int columnas = 9;
        int tileWidth = (int) (getWidth() * 0.73) / columnas;
        int tileHeight = (int) (getHeight() * 0.8) / filas;
        int offsetX = (int) ((getWidth() - (tileWidth * columnas)) / 2 + (getWidth() * 0.1));
        int offsetY = (getHeight() - (tileHeight * filas)) / 2;

        // Dibujar todas las entidades en el tablero
        for (Entidad entidad : entidades) {
            int xReal = offsetX + (int) (entidad.getxPos() * tileWidth);
            int yReal = offsetY + (int) (entidad.getyPos() * tileHeight);

            if (entidad instanceof Planta) {
                Planta planta = (Planta) entidad;
                ImageIcon imagenPlanta = planta.getImagen();
                if (imagenPlanta != null) {
                    g.drawImage(imagenPlanta.getImage(), xReal, yReal, tileWidth, tileHeight, this);
                } else {
                    System.out.println("Imagen de la planta es nula en: (" + planta.getxPos() + ", " + planta.getyPos() + ")");
                    dibujarRectanguloMarcador(g, xReal, yReal, tileWidth, tileHeight, Color.GREEN);
                }
            } else if (entidad instanceof BasicZombie) {
                BasicZombie basiczombie = (BasicZombie) entidad;
                ImageIcon imagenbasiczombie = basiczombie.getImagen();
                if (imagenbasiczombie != null) {
                    g.drawImage(imagenbasiczombie.getImage(), xReal, yReal, tileWidth, tileHeight, this);
                } else {
                    System.out.println("Imagen del zombie es nula en: (" + basiczombie.getxPos() + ", " + basiczombie.getyPos() + ")");
                    dibujarRectanguloMarcador(g, xReal, yReal, tileWidth, tileHeight, Color.RED);
                }
            } else if (entidad instanceof BucketheadZombie) {
                BucketheadZombie bucketheadZombie = (BucketheadZombie) entidad;
                ImageIcon imagenbucketheadZombie = bucketheadZombie.getImagen();
                if (imagenbucketheadZombie != null) {
                    g.drawImage(imagenbucketheadZombie.getImage(), xReal, yReal, tileWidth, tileHeight, this);
                } else {
                    System.out.println("Imagen del zombie es nula en: (" + bucketheadZombie.getxPos() + ", " + bucketheadZombie.getyPos() + ")");
                    dibujarRectanguloMarcador(g, xReal, yReal, tileWidth, tileHeight, Color.RED);
                }
            } else if (entidad instanceof ConeheadZombie) {
                ConeheadZombie coneheadZombie = (ConeheadZombie) entidad;
                ImageIcon imagenconeheadZombie= coneheadZombie.getImagen();
                if (imagenconeheadZombie != null) {
                    g.drawImage(imagenconeheadZombie.getImage(), xReal, yReal, tileWidth, tileHeight, this);
                } else {
                    System.out.println("Imagen del zombie es nula en: (" + coneheadZombie.getxPos() + ", " + coneheadZombie.getyPos() + ")");
                    dibujarRectanguloMarcador(g, xReal, yReal, tileWidth, tileHeight, Color.RED);
                }
            } else if (entidad instanceof Guisante) {  // Aquí añadimos la lógica para dibujar los guisantes
                Guisante guisante = (Guisante) entidad;
                ImageIcon imagenGuisante = guisante.getImagen();
                if (imagenGuisante != null) {
                    g.drawImage(imagenGuisante.getImage(), xReal, yReal, tileWidth / 5, tileHeight / 4, this); // Ajustar el tamaño si es necesario
                } else {
                    System.out.println("Imagen del guisante es nula en: (" + guisante.getxPos() + ", " + guisante.getyPos() + ")");
                    dibujarRectanguloMarcador(g, xReal, yReal, tileWidth, tileHeight, Color.GREEN);  // Dibuja un rectángulo en su lugar
                }
            } else if (entidad instanceof Podadora) {

                Podadora podadora = (Podadora) entidad;
                ImageIcon imagenPodadora = podadora.getImagen();
                if (imagenPodadora != null) {
                    g.drawImage(imagenPodadora.getImage(), xReal, yReal, tileWidth, tileHeight, this);
                } else {
                    System.out.println("Imagen de la podadora es nula en: (" + podadora.getxPos() + ", " + podadora.getyPos() + ")");
                    dibujarRectanguloMarcador(g, xReal, yReal, tileWidth, tileHeight, Color.BLUE);
                }
            } else if (entidad instanceof WallNut) {
                WallNut wallNut = (WallNut) entidad;
                ImageIcon imagenWallNut = wallNut.getImagen();
                if (imagenWallNut != null) {
                    g.drawImage(imagenWallNut.getImage(), xReal, yReal, tileWidth, tileHeight, this);
                } else {
                    System.out.println("Imagen del wallnut es nula en: (" + wallNut.getxPos() + ", " + wallNut.getyPos() + ")");
                    dibujarRectanguloMarcador(g, xReal, yReal, tileWidth, tileHeight, Color.BLUE);
                }

            } else if (entidad instanceof PotatoMine) {
                PotatoMine potatoMine = (PotatoMine) entidad;
                ImageIcon imagenPotatoMine = potatoMine.getImagen();
                if (imagenPotatoMine != null) {
                    g.drawImage(imagenPotatoMine.getImage(), xReal, yReal, tileWidth, tileHeight, this);
                } else {
                    System.out.println("Imagen de la PotatoMine es nula en: (" + potatoMine.getxPos() + ", " + potatoMine.getyPos() + ")");
                    dibujarRectanguloMarcador(g, xReal, yReal, tileWidth, tileHeight, Color.BLUE);
                }
            } else if (entidad instanceof ECIPlant) {
                ECIPlant eciPlant = (ECIPlant) entidad;
                ImageIcon imagenECIPlant = eciPlant.getImagen();
                if (imagenECIPlant != null) {
                    g.drawImage(imagenECIPlant.getImage(), xReal, yReal, tileWidth, tileHeight, this);
                } else {
                    System.out.println("Imagen de la ECIPlant es nula en :(" + eciPlant.getxPos() + "," + eciPlant.getyPos() + ")");
                    dibujarRectanguloMarcador(g, xReal, yReal, tileWidth, tileHeight, Color.PINK);
                }
            } else if (entidad instanceof Brainstein) {
                Brainstein brainstein=(Brainstein) entidad;
                ImageIcon imagenBrainstein = brainstein.getImagen();
                if (imagenBrainstein != null) {
                    g.drawImage(imagenBrainstein.getImage(), xReal, yReal, tileWidth, tileHeight, this);
                } else {
                    System.out.println("Imagen del Brainstein es nula en: (" + brainstein.getxPos() + ", " + brainstein.getyPos() + ")");
                    dibujarRectanguloMarcador(g, xReal, yReal, tileWidth, tileHeight, Color.BLUE);
                }

            }
        }
    }

    private void dibujarTextoConContorno(Graphics g, String texto, int x, int y, Color colorTexto) {
        g.setColor(Color.BLACK);
        g.drawString(texto, x+1, y+1);
        g.drawString(texto, x-1, y+1);
        g.drawString(texto, x+1, y-1);
        g.drawString(texto, x-1, y-1);
        g.drawString(texto, x+2, y);
        g.drawString(texto, x-2, y);
        g.drawString(texto, x, y+2);
        g.drawString(texto, x, y-2);

        g.setColor(colorTexto);
        g.drawString(texto, x, y);
    }


    /**
     * Dibuja un rectángulo marcador en lugar de una imagen faltante.
     *
     * @param g      Objeto Graphics
     * @param x      Coordenada x
     * @param y      Coordenada y
     * @param width  Ancho del rectángulo
     * @param height Altura del rectángulo
     * @param color  Color del rectángulo
     */
    private void dibujarRectanguloMarcador(Graphics g, int x, int y, int width, int height, Color color) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    /**
     * Obtiene una copia segura de la lista de entidades.
     *
     * @return Lista de entidades.
     */
    public List<Entidad> getEntidades() {
        synchronized (entidades) {
            return new ArrayList<>(entidades); // Devuelve una copia segura
        }
    }

    /**
     * Obtiene la cantidad de soles disponibles.
     *
     * @return Cantidad de soles.
     */
    public int getSoles() {
        return soles;
    }

    /**
     * Actualiza el estado del juego eliminando entidades muertas y actualizando las vivas.
     */
    public void actualizarEstadoJuego() {
        List<Entidad> entidadesParaEliminar = new ArrayList<>();
        for (Entidad entidad : entidades) {
            if (entidad.getSalud() <= 0) {
                entidadesParaEliminar.add(entidad);
            }
        }
        entidades.removeAll(entidadesParaEliminar);
        repaint();
    }

    public boolean isTurnoPlantas() {
        return turnoPlantas;
    }
    public static Planta obtenerPlantaEnCelda(int x, int y) {
        for (Entidad entidad : entidades) { // Suponiendo que `entidades` es una lista en GamePanel
            if (entidad instanceof Planta && entidad.getxPos() == x && entidad.getyPos() == y) {
                return (Planta) entidad;
            }
        }
        return null;
    }
    public int getDuracionJuego() {
        return duracionJuego;
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }
    public boolean isVictoria() {
        return victoria;
    }
    public void iniciarJuegoMaquinaVsMaquina() {
        PoobVsZombiesGUI.iniciarJuegoMaquinaVsMaquina();
        // Lógica mínima o inicialización del juego automático
    }
    public int getCerebros(){
        return cerebros;
    }
}
