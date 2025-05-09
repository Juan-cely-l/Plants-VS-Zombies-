// PoobVsZombiesGUI.java
package presentation;

import domain.*;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PoobVsZombiesGUI extends JFrame implements TurnChangeListener {

    private transient Clip clip; // Controlador para la música de fondo
    private boolean isMuted = false; // Estado de audio (silenciado o no)
    private String dificultadSeleccionada = "Fácil"; // Valor por defecto
    private int duracionJuego; // Duración del juego en milisegundos
    private String modoDeJuegoActual;
    private JButton botonModalidad;
    private JButton botonDificultad;
    private JButton botonModoJuego;
    private CardLayout cardLayout;

    private final String PLANTAS = "Plantas";
    private final String ZOMBIES = "Zombis";

    // Referencia al ButtonPanel en modo Supervivencia
    private JPanel buttonPanel;

    public PoobVsZombiesGUI() {
        configurarVentana(); // Configura las propiedades de la ventana
        JPanel panel = crearPanelConFondo(); // Crea el panel con fondo
        inicializarComponentes(panel); // Inicializa los componentes gráficos (botones)
        setJMenuBar(crearMenuAudio()); // Configura el menú de audio
        reproducirMusica("/resources/Plants_vs_Zombies_Soundtrack___Main_Menu.wav"); // Reproduce la música de fondo

        setVisible(true); // Muestra la ventana
    }

    /**
     * Configura las propiedades de la ventana principal.
     */
    private void configurarVentana() {
        setTitle("Poob vs Zombies"); // Título de la ventana
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Obtener tamaño de la pantalla
        setSize(screenSize.width / 2, screenSize.height / 2); // Configurar tamaño de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Acción al cerrar la ventana
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla
    }

    /**
     * Crea un panel con una imagen de fondo personalizada.
     *
     * @return JPanel con la imagen de fondo.
     */
    private JPanel crearPanelConFondo() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Cargar y dibujar la imagen de fondo
                ImageIcon imagen = new ImageIcon(getClass().getResource("/resources/1Menu.jpg"));
                if (imagen != null) {
                    g.drawImage(imagen.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    System.err.println("No se pudo cargar la imagen de fondo.");
                }
            }
        };
        panel.setLayout(null); // Posicionamiento manual de los componentes
        add(panel); // Añadir el panel a la ventana
        return panel;
    }

    /**
     * Inicializa los componentes gráficos (botones) y los agrega al panel.
     *
     * @param panel El panel al que se agregarán los botones.
     */
    private void inicializarComponentes(JPanel panel) {
        // Crear los botones para las opciones del menú
        botonModalidad = crearBotonPersonalizado("Modalidad");
        botonDificultad = crearBotonPersonalizado("Dificultad");
        botonModoJuego = crearBotonPersonalizado("Modo De Juego");

        // Crear el menú desplegable para la dificultad
        JPopupMenu menuDificultad = new JPopupMenu();

        JMenuItem itemFacil = new JMenuItem("Fácil");
        JMenuItem itemMedio = new JMenuItem("Medio");
        JMenuItem itemDificil = new JMenuItem("Difícil");

        // Añadir los ítems al menú
        menuDificultad.add(itemFacil);
        menuDificultad.add(itemMedio);
        menuDificultad.add(itemDificil);

        // Añadir acción al botón "Dificultad" para mostrar el menú desplegable
        botonDificultad.addActionListener(e -> {
            menuDificultad.show(botonDificultad, 0, botonDificultad.getHeight());
        });

        // Manejar la selección de "Fácil"
        itemFacil.addActionListener(e -> {
            dificultadSeleccionada = "Fácil";
            botonDificultad.setText("Dificultad: Fácil -> 3 minutos");
            duracionJuego = 3 * 60 * 1000; // 3 minutos en milisegundos
            System.out.println("Dificultad seleccionada: Fácil -> 3 minutos");
            // Si ya se eligió modo Supervivencia, iniciar el juego
            if ("Supervivencia".equals(modoDeJuegoActual)) {
                iniciarJuegoSupervivencia();
            }
        });

        // Manejar la selección de "Medio"
        itemMedio.addActionListener(e -> {
            dificultadSeleccionada = "Medio";
            botonDificultad.setText("Dificultad: Medio -> 5 minutos");
            duracionJuego = 5 * 60 * 1000; // 5 minutos en milisegundos
            System.out.println("Dificultad seleccionada: Medio -> 5 minutos");
            if ("Supervivencia".equals(modoDeJuegoActual)) {
                iniciarJuegoSupervivencia();
            }
        });

        // Manejar la selección de "Difícil"
        itemDificil.addActionListener(e -> {
            dificultadSeleccionada = "Difícil";
            botonDificultad.setText("Dificultad: Difícil -> 7 minutos");
            duracionJuego = 7 * 60 * 1000; // 7 minutos en milisegundos
            System.out.println("Dificultad seleccionada: Difícil -> 7 minutos");
            if ("Supervivencia".equals(modoDeJuegoActual)) {
                iniciarJuegoSupervivencia();
            }
        });

        // Crear el menú desplegable para la modalidad
        JPopupMenu menuModalidad = new JPopupMenu();
        JMenuItem itemMaquinaVsMaquina = new JMenuItem("Máquina vs Máquina");
        JMenuItem itemSupervivencia = new JMenuItem("Supervivencia");

        menuModalidad.add(itemMaquinaVsMaquina);
        menuModalidad.add(itemSupervivencia);

        botonModalidad.addActionListener(e -> {
            menuModalidad.show(botonModalidad, 0, botonModalidad.getHeight());
        });

        itemMaquinaVsMaquina.addActionListener(e -> {
            // Cambiar el modo de juego actual
            modoDeJuegoActual = "MaquinaVsMaquina";

            // Mostrar un mensaje de confirmación
            JOptionPane.showMessageDialog(this, "Modo Máquina vs Máquina seleccionado. ¡Comenzando el juego!");

            // Iniciar el juego en modo Máquina vs Máquina
            iniciarJuegoMaquinaVsMaquina(); // Llamar al método que implementa el modo
        });

        itemSupervivencia.addActionListener(e -> {
            modoDeJuegoActual = "Supervivencia";
            JOptionPane.showMessageDialog(this, "Modo Supervivencia seleccionado.\n"
                    + "Primero selecciona la dificultad.\n"
                    + "Cuando inicies el juego, tendrás 2 minutos de preparación antes de la primera ronda.\n"
                    + "Luego la partida se dividirá en 2 rondas, cada una con la mitad del tiempo.\n"
                    + "Entre rondas, volverás a tener 2 minutos para reorganizar las plantas.");
        });

        // Agregar acción al botón "Modo de Juego"
        botonModoJuego.addActionListener(e -> mostrarOpcionesDeModoDeJuego());

        // Agregar los botones al panel
        panel.add(botonModalidad);
        panel.add(botonDificultad);
        panel.add(botonModoJuego);

        // Ajustar posiciones dinámicamente cuando se redimensiona la ventana
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int x = (panel.getWidth() / 2) - (botonDificultad.getWidth() / 2);
                int y = (panel.getHeight() / 2) - (botonDificultad.getHeight() / 2);
                botonDificultad.setLocation(x, y);
                botonModalidad.setLocation(x - 200, y);
                botonModoJuego.setLocation(x + 200, y);
            }
        });
    }

    /**
     * Muestra un cuadro de diálogo con las opciones de modo de juego.
     */
    private void mostrarOpcionesDeModoDeJuego() {
        String[] opciones = {"Juego Original", "Personalizado", "Zombies"};
        int seleccion = JOptionPane.showOptionDialog(
                this,
                "Selecciona el modo de juego:",
                "Modo de Juego",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccion == 0) { // Juego Original
            modoDeJuegoActual = "Juego Original";
            abrirVentanaJuegoOriginal();
        } else if (seleccion == 1) { // Personalizado
            modoDeJuegoActual = "Personalizado";
            mostrarPantallaSeleccionPlantas();
        } else if (seleccion == 2) { // Zombies
            modoDeJuegoActual = "Zombies";
            mostrarPantallaSeleccionZombies();
        }
    }

    public String obtenerModoDeJuego() {
        return modoDeJuegoActual;
    }

    private void mostrarPantallaSeleccionPlantas() {
        // Pantalla de selección de plantas
        JFrame ventanaSeleccionPlantas = new JFrame("Selecciona tus Plantas");
        ventanaSeleccionPlantas.setSize(800, 600);
        ventanaSeleccionPlantas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaSeleccionPlantas.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/MenuSeleccion.png")));

        JPanel panelSeleccion = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imagen = new ImageIcon(getClass().getResource("/resources/MenuSeleccion.png"));
                if (imagen != null) {
                    g.drawImage(imagen.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    System.err.println("No se pudo cargar la imagen de fondo.");
                }
            }
        };

        panelSeleccion.setLayout(new FlowLayout(FlowLayout.CENTER));
        List<String> plantasSeleccionadas = new ArrayList<>();

        JButton botonPeashooter = crearBotonConImagen("/resources/botonPeashooter.png");
        botonPeashooter.addActionListener(e -> {
            if (!plantasSeleccionadas.contains("Peashooter")) plantasSeleccionadas.add("Peashooter");
        });
        panelSeleccion.add(botonPeashooter);

        JButton botonWallNut = crearBotonConImagen("/resources/Wallnutmenu.png");
        botonWallNut.addActionListener(e -> {
            if (!plantasSeleccionadas.contains("WallNut")) plantasSeleccionadas.add("WallNut");
        });
        panelSeleccion.add(botonWallNut);

        JButton botonMine = crearBotonConImagen("/resources/Potatominemenu.png");
        botonMine.addActionListener(e -> {
            if (!plantasSeleccionadas.contains("PotatoMine")) plantasSeleccionadas.add("PotatoMine");
        });
        panelSeleccion.add(botonMine);

        JButton botonGirasol = crearBotonConImagen("/resources/Girasolboton.png");
        botonGirasol.addActionListener(e -> {
            if (!plantasSeleccionadas.contains("Girasol")) plantasSeleccionadas.add("Girasol");
        });
        panelSeleccion.add(botonGirasol);

        JButton botonECIplant = crearBotonConImagen("/resources/ECIplantBoton.png");
        botonECIplant.addActionListener(e -> {
            if (!plantasSeleccionadas.contains("ECIPlant")) plantasSeleccionadas.add("ECIPlant");
        });
        panelSeleccion.add(botonECIplant);

        JButton botonIniciarJuego = new JButton("Confirmar Selección");
        botonIniciarJuego.addActionListener(e -> {
            if (plantasSeleccionadas.isEmpty()) {
                JOptionPane.showMessageDialog(ventanaSeleccionPlantas, "Por favor selecciona al menos una planta.");
            } else {
                iniciarJuegoConPlantas(plantasSeleccionadas);
                ventanaSeleccionPlantas.dispose();
            }
        });
        panelSeleccion.add(botonIniciarJuego);

        ventanaSeleccionPlantas.add(panelSeleccion);
        ventanaSeleccionPlantas.setVisible(true);
    }

    private void mostrarPantallaSeleccionZombies() {
        JFrame ventanaSeleccionZombies = new JFrame("Selecciona tus Zombies");
        ventanaSeleccionZombies.setSize(800, 600);
        ventanaSeleccionZombies.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaSeleccionZombies.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/MenuSeleccion.png")));

        JPanel panelSeleccion = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imagen = new ImageIcon(getClass().getResource("/resources/MenuSeleccion.png"));
                if (imagen != null) {
                    g.drawImage(imagen.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    System.err.println("No se pudo cargar la imagen de fondo.");
                }
            }
        };

        panelSeleccion.setLayout(new FlowLayout(FlowLayout.CENTER));
        List<String> ZombiesSeleccionados = new ArrayList<>();

        JButton botonBasicZombie = crearBotonConImagen("/resources/BotonBasicZombie.png");
        botonBasicZombie.addActionListener(e -> {
            if (!ZombiesSeleccionados.contains("BasicZombie")) ZombiesSeleccionados.add("BasicZombie");
        });
        panelSeleccion.add(botonBasicZombie);

        JButton botonBucketHeadZombie = crearBotonConImagen("/resources/BotonBucketheadZombie.png");
        botonBucketHeadZombie.addActionListener(e -> {
            if (!ZombiesSeleccionados.contains("BucketHeadZombie")) ZombiesSeleccionados.add("BucketHeadZombie");
        });
        panelSeleccion.add(botonBucketHeadZombie);

        JButton botonConeHeadZombie = crearBotonConImagen("/resources/BotonConeheadZombie.png");
        botonConeHeadZombie.addActionListener(e -> {
            if (!ZombiesSeleccionados.contains("ConeHeadZombie")) ZombiesSeleccionados.add("ConeHeadZombie");
        });
        panelSeleccion.add(botonConeHeadZombie);

        JButton botonBrainstein = crearBotonConImagen("/resources/BrainsteinBoton.png");
        botonBrainstein.addActionListener(e -> {
            if(!ZombiesSeleccionados.contains("Brainstein")) ZombiesSeleccionados.add("Brainstein");
        });
        panelSeleccion.add(botonBrainstein);

        JButton botonIniciarJuego = new JButton("Confirmar Selección");
        botonIniciarJuego.addActionListener(e -> {
            if (ZombiesSeleccionados.isEmpty()) {
                JOptionPane.showMessageDialog(ventanaSeleccionZombies, "Por favor selecciona al menos un zombie.");
            } else {
                iniciarJuegoConZombies(ZombiesSeleccionados);
                ventanaSeleccionZombies.dispose();
            }
        });
        panelSeleccion.add(botonIniciarJuego);

        ventanaSeleccionZombies.add(panelSeleccion);
        ventanaSeleccionZombies.setVisible(true);
    }

    private void iniciarJuegoConPlantas(List<String> plantasSeleccionadas) {
        // Aquí se debe abrir la ventana del juego con las plantas seleccionadas
        JFrame ventanaJuego = new JFrame("Juego Personalizado - Poob vs Zombies");
        ventanaJuego.setSize(800, 600);
        ventanaJuego.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Crear el panel de juego
        GamePanel gamePanel = new GamePanel(GameMode.PERSONALIZADO);
        gamePanel.setDuracionJuego(duracionJuego);

        // Verificar si la duración del juego es 0, lanzar una excepción si es el caso
        if(gamePanel.getDuracionJuego() == 0){
            try {
                throw new PoobVsZombiesException("Error: La dificultad del juego no ha sido establecida.");
            } catch (PoobVsZombiesException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error de Duración", JOptionPane.ERROR_MESSAGE);

            }
        }else{

            // Crear el panel de botones (en modo Personalizado)
            JPanel buttonPanelPersonalizado = crearPanelDeBotones(gamePanel, plantasSeleccionadas);

            List<Entidad> entidades = new ArrayList<>();
            Tablero tablero = new Tablero(8, 5, entidades);
            inicializarPodadoras(entidades, tablero, gamePanel);

            gamePanel.actualizarEntidades(entidades);

            // Iniciar los zombies
            ZombiesStrategic zombiesStrategic = new ZombiesStrategic(entidades, gamePanel, gamePanel);
            zombiesStrategic.iniciarGeneracionAutomatica();
            zombiesStrategic.iniciarMovimientoZombies();

            gamePanel.setZombiesStrategic(zombiesStrategic);

            ventanaJuego.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    zombiesStrategic.detenerGeneracionAutomatica();

                    for (Entidad entidad : entidades) {
                        if (entidad instanceof Zombie) ((Zombie) entidad).detenerMovimiento();
                        if (entidad instanceof Peashooter) ((Peashooter) entidad).detenerAtaquePeriodico();
                        if (entidad instanceof Guisante) ((Guisante) entidad).detenerMovimiento();
                    }
                    super.windowClosing(e);
                }
            });

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(gamePanel, BorderLayout.CENTER);

            JRootPane rootPane = ventanaJuego.getRootPane();
            rootPane.setGlassPane(buttonPanelPersonalizado);
            buttonPanelPersonalizado.setVisible(true);

            ventanaJuego.add(mainPanel);
            ventanaJuego.setVisible(true);}
    }


    private void iniciarJuegoConZombies(List<String> ZombiesSeleccionados) {
        JFrame ventanaJuego = new JFrame("Juego Zombies - Poob vs Zombies");
        ventanaJuego.setSize(800, 600);
        ventanaJuego.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GamePanel gamePanel = new GamePanel(GameMode.ZOMBIES);
        gamePanel.setDuracionJuego(duracionJuego);
        // Verificar si la duración del juego es 0, lanzar una excepción si es el caso
        if(gamePanel.getDuracionJuego() == 0){
            try {
                throw new PoobVsZombiesException("Error: La dificultad del juego no ha sido establecida.");
            } catch (PoobVsZombiesException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error de Duración", JOptionPane.ERROR_MESSAGE);
                return; // Terminar la ejecución si la duración es inválida
            }
        }

        // Crear el panel de botones (en modo Zombies)
        JPanel buttonPanelZombies = crearPanelDeBotonesZombies(gamePanel, ZombiesSeleccionados);

        List<Entidad> entidades = new ArrayList<>();
        Tablero tablero = new Tablero(8, 5, entidades);
        inicializarPodadoras(entidades, tablero, gamePanel);

        PlantsStrategic plantsStrategic = new PlantsStrategic(tablero,  gamePanel);
        plantsStrategic.ejecutarEstrategiaLimitada();


        PlantsStrategic plantsStrategic1 = new PlantsStrategic(tablero,  gamePanel);
        plantsStrategic1.ejecutarEstrategiaLimitada();

        PlantsIntelligent plantsIntelligent = new PlantsIntelligent(tablero, gamePanel, gamePanel);
        plantsIntelligent.ejecutarDefensaEnFila(4);
        plantsIntelligent.ejecutarDefensaEnFila(3);
        plantsIntelligent.ejecutarDefensaEnFila(1);
        gamePanel.actualizarEntidades(entidades);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(gamePanel, BorderLayout.CENTER);

        JRootPane rootPane = ventanaJuego.getRootPane();
        rootPane.setGlassPane(buttonPanelZombies);
        buttonPanelZombies.setVisible(true);

        ventanaJuego.add(mainPanel);
        ventanaJuego.setVisible(true);
    }

    private void abrirVentanaJuegoOriginal() {
        JFrame ventanaJuego = new JFrame("Juego Original - Poob vs Zombies");
        ventanaJuego.setSize(800, 600);
        ventanaJuego.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GamePanel gamePanel = new GamePanel(GameMode.ORIGINAL);
        gamePanel.setDuracionJuego(duracionJuego);
        // Verificar si la duración del juego es 0, lanzar una excepción si es el caso
        if(gamePanel.getDuracionJuego() == 0){
            try {
                throw new PoobVsZombiesException("Error: La dificultad del juego no ha sido establecida.");
            } catch (PoobVsZombiesException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error de Duración", JOptionPane.ERROR_MESSAGE);
                return; // Terminar la ejecución si la duración es inválida
            }
        }

        JPanel buttonPanelOriginal = crearPanelDeBotonesOriginal(gamePanel);

        List<Entidad> entidades = new ArrayList<>();
        Tablero tablero = new Tablero(8, 5, entidades);
        inicializarPodadoras(entidades, tablero, gamePanel);

        gamePanel.actualizarEntidades(entidades);

        ZombiesOriginal zombiesOriginal = new ZombiesOriginal(entidades, gamePanel, gamePanel);
        zombiesOriginal.iniciarGeneracionAutomatica();
        zombiesOriginal.iniciarMovimientoZombies();
        gamePanel.setZombiesOriginal(zombiesOriginal);

        ventanaJuego.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                zombiesOriginal.detenerGeneracionAutomatica();
                for (Entidad entidad : entidades) {
                    if (entidad instanceof BasicZombie) ((BasicZombie) entidad).detenerMovimiento();
                    if (entidad instanceof Peashooter) ((Peashooter) entidad).detenerAtaquePeriodico();
                    if (entidad instanceof Guisante) ((Guisante) entidad).detenerMovimiento();
                }
                super.windowClosing(e);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(gamePanel, BorderLayout.CENTER);

        JRootPane rootPane = ventanaJuego.getRootPane();
        rootPane.setGlassPane(buttonPanelOriginal);
        buttonPanelOriginal.setVisible(true);

        ventanaJuego.add(mainPanel);
        ventanaJuego.setVisible(true);
    }

    /**
     * Implementación del método TurnChangeListener para actualizar el panel de botones en Supervivencia.
     * @param isTurnoPlantas true si es el turno de las plantas, false si es el turno de los zombies.
     */
    @Override
    public void onTurnChange(boolean isTurnoPlantas) {
        if (buttonPanel != null && cardLayout != null) {
            if (isTurnoPlantas) {
                cardLayout.show(buttonPanel, PLANTAS);
            } else {
                cardLayout.show(buttonPanel, ZOMBIES);
            }
        }
    }

    /**
     * Inicializa el modo supervivencia con CardLayout para los botones.
     */
    private void iniciarJuegoSupervivencia() {
        // tiempo total ya definido en duracionJuego
        // lo dividimos en 2
        int tiempoPorRonda = duracionJuego / 2;

        JFrame ventanaJuego = new JFrame("Modo Supervivencia - Poob vs Zombies");
        ventanaJuego.setSize(800, 600);
        ventanaJuego.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GamePanel gamePanel = new GamePanel(GameMode.SUPERVIVENCIA);

        gamePanel.setDuracionJuego(tiempoPorRonda);

        List<Entidad> entidades = new ArrayList<>();
        Tablero tablero = new Tablero(8, 5, entidades);
        inicializarPodadoras(entidades, tablero, gamePanel);
        gamePanel.actualizarEntidades(entidades);

        // Crear el panel de botones con CardLayout
        cardLayout = new CardLayout();
        buttonPanel = new JPanel(cardLayout);
        buttonPanel.setOpaque(false);

        // Panel de Plantas
        JPanel panelPlantas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelPlantas.setOpaque(false);

        JButton botonPala = crearBotonConImagen("/resources/Shovel2.png");
        botonPala.addActionListener(e -> gamePanel.activarModoPala());
        panelPlantas.add(botonPala);

        JButton girasolButton = crearBotonConImagen("/resources/Girasolboton.png");
        girasolButton.addActionListener(e -> gamePanel.setPlantaSeleccionada("Girasol"));
        panelPlantas.add(girasolButton);

        JButton peashooterButton = crearBotonConImagen("/resources/botonPeashooter.png");
        peashooterButton.addActionListener(e -> gamePanel.setPlantaSeleccionada("Peashooter"));
        panelPlantas.add(peashooterButton);

        JButton wallnutButton = crearBotonConImagen("/resources/Wallnutmenu.png");
        wallnutButton.addActionListener(e -> gamePanel.setPlantaSeleccionada("WallNut"));
        panelPlantas.add(wallnutButton);

        JButton potatominemenuButton = crearBotonConImagen("/resources/Potatominemenu.png");
        potatominemenuButton.addActionListener(e -> gamePanel.setPlantaSeleccionada("PotatoMine"));
        panelPlantas.add(potatominemenuButton);

        JButton eciButton = crearBotonConImagen("/resources/ECIplantBoton.png");
        eciButton.addActionListener(e -> gamePanel.setPlantaSeleccionada("ECIPlant"));
        panelPlantas.add(eciButton);



        // Panel de Zombis
        JPanel panelZombis = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelZombis.setOpaque(false);

        JButton botonBasicZombie = crearBotonConImagen("/resources/BotonBasicZombie.png");
        botonBasicZombie.addActionListener(e -> gamePanel.setZombieSeleccionado("BasicZombie"));
        panelZombis.add(botonBasicZombie);

        JButton botonBucketHeadZombie = crearBotonConImagen("/resources/BotonBucketheadZombie.png");
        botonBucketHeadZombie.addActionListener(e -> gamePanel.setZombieSeleccionado("BucketHeadZombie"));
        panelZombis.add(botonBucketHeadZombie);

        JButton botonConeHeadZombie = crearBotonConImagen("/resources/BotonConeheadZombie.png");
        botonConeHeadZombie.addActionListener(e -> gamePanel.setZombieSeleccionado("ConeHeadZombie"));
        panelZombis.add(botonConeHeadZombie);

        JButton botonBrainstein = crearBotonConImagen("/resources/BrainsteinBoton.png");
        botonBrainstein.addActionListener(e -> gamePanel.setZombieSeleccionado("Brainstein"));
        panelZombis.add(botonBrainstein);

        // Añadir ambos paneles al CardLayout
        buttonPanel.add(panelPlantas, PLANTAS);
        buttonPanel.add(panelZombis, ZOMBIES);

        // Mostrar el panel inicial según el estado actual del turno
        if (gamePanel.isTurnoPlantas()) {
            cardLayout.show(buttonPanel, PLANTAS);
        } else {
            cardLayout.show(buttonPanel, ZOMBIES);
        }

        // Registrar este GUI como listener de cambios de turno en GamePanel
        gamePanel.addTurnChangeListener(this);

        // Establecer el buttonPanel como glass pane para superponer los botones sobre el juego
        ventanaJuego.getRootPane().setGlassPane(buttonPanel);
        buttonPanel.setVisible(true);

        ventanaJuego.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Detener cualquier generación automática de zombies o acciones en GamePanel
                // Asumiendo que tienes métodos para hacerlo en GamePanel
                // gamePanel.detenerAcciones();
                super.windowClosing(e);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        ventanaJuego.add(mainPanel);
        ventanaJuego.setVisible(true);

        // Iniciar la fase de preparación antes de la primera ronda
        gamePanel.iniciarFasePreparacion();
    }





    public static void iniciarJuegoMaquinaVsMaquina() {
        JFrame ventanaJuego = new JFrame("Máquina vs Máquina - Poob vs Zombies");
        ventanaJuego.setSize(800, 600);
        ventanaJuego.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Crear el panel de juego en modo Máquina vs Máquina
        GamePanel gamePanel = new GamePanel(GameMode.MAQUINA);

        // Configurar el tablero y las entidades
        List<Entidad> entidades = new ArrayList<>();
        Tablero tablero = new Tablero(5, 8, entidades); // 8 columnas, 5 filas
        inicializarPodadoras(entidades, tablero, gamePanel);
        gamePanel.actualizarEntidades(entidades);

        // Crear las máquinas de plantas y zombies
        PlantsIntelligent plantasInteligentes = new PlantsIntelligent(tablero, gamePanel, gamePanel);
        PlantsStrategic plantasEstrategicas = new PlantsStrategic(tablero, gamePanel);
        ZombiesStrategic zombiesEstrategicos = new ZombiesStrategic(entidades, gamePanel, gamePanel);

        // Variables para controlar turnos
        Random random = new Random();


        Thread estrategiasThread = new Thread(() -> {
            try {
                while (true) {
                    // Generar una fila aleatoria dentro de los límites del tablero
                    int filaAleatoria = random.nextInt(tablero.getFilas());
                    int columnaAleatoria = random.nextInt(tablero.getColumnas());


                    // Alternar entre estrategias
                    if (random.nextBoolean()) { // Elegir estrategia de forma aleatoria
                        if (!tablero.celdaOcupada(filaAleatoria, columnaAleatoria)) { // Verificar si la celda está libre
                            plantasInteligentes.ejecutarDefensaEnFila(filaAleatoria);
                        }
                    } else {
                        if (!tablero.celdaOcupada(filaAleatoria, columnaAleatoria)) {
                            plantasEstrategicas.ejecutarEstrategiaLimitada();
                        }
                    }

                    // Actualizar el panel de juego
                    gamePanel.actualizarEntidades(tablero.getEntidades());
                    gamePanel.repaint();

                    Thread.sleep(4000); // Intervalo de 5 segundos entre cada ejecución
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt(); // Salir del hilo si es interrumpido
            }
        });
        // Hilo para la generación de zombies, con un retraso inicial de 10 segundos
        Thread generacionZombies = new Thread(() -> {
            try {
                Thread.sleep(10000); // Esperar 10 segundos antes de comenzar la generación de zombies
                zombiesEstrategicos.iniciarGeneracionAutomatica(); // Iniciar la generación automática de zombies
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });

        // Iniciar ambos hilos
        estrategiasThread.start();
        generacionZombies.start();

        // Configurar el panel principal y añadirlo a la ventana
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        ventanaJuego.add(mainPanel);
        ventanaJuego.setVisible(true);

        // Añadir comportamiento para cerrar ventana
        ventanaJuego.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Detener todos los hilos y temporizadores
                estrategiasThread.interrupt();
                generacionZombies.interrupt();
                zombiesEstrategicos.detenerGeneracionAutomatica();
            }
        });
    }





    private static void inicializarPodadoras(List<Entidad> entidades, Tablero tablero, GamePanel gamePanel) {
        for (int i = 0; i < 5; i++) {
            Podadora podadora = new Podadora(0, 4 - i, gamePanel);
            entidades.add(podadora);
        }
    }

    private JPanel crearPanelDeBotonesOriginal(GamePanel gamePanel) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);

        JButton solboton = crearBotonConImagen("/resources/solmenu.png");
        buttonPanel.add(solboton);
        JButton botonPala = crearBotonConImagen("/resources/Shovel2.png");
        botonPala.addActionListener(e -> gamePanel.activarModoPala());
        buttonPanel.add(botonPala);

        JButton girasolButton = crearBotonConImagen("/resources/Girasolboton.png");
        girasolButton.addActionListener(e -> gamePanel.setPlantaSeleccionada("Girasol"));
        buttonPanel.add(girasolButton);

        JButton peashooterButton = crearBotonConImagen("/resources/botonPeashooter.png");
        peashooterButton.addActionListener(e -> gamePanel.setPlantaSeleccionada("Peashooter"));
        buttonPanel.add(peashooterButton);

        return buttonPanel;
    }

    private JPanel crearPanelDeBotones(GamePanel gamePanel, List<String> plantasSeleccionadas) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        JButton solboton = crearBotonConImagen("/resources/solmenu.png");
        buttonPanel.add(solboton);
        JButton botonPala = crearBotonConImagen("/resources/Shovel2.png");
        botonPala.addActionListener(e -> gamePanel.activarModoPala());
        buttonPanel.add(botonPala);

        for (String planta : plantasSeleccionadas) {
            String imagePath = getImagePathForPlanta(planta);
            JButton plantaButton = crearBotonConImagen(imagePath);
            plantaButton.addActionListener(e -> gamePanel.setPlantaSeleccionada(planta));
            buttonPanel.add(plantaButton);
            System.out.println("Botón creado para planta: " + planta);
        }

        return buttonPanel;
    }

    private JPanel crearPanelDeBotonesZombies(GamePanel gamePanel, List<String> ZombiesSeleccionados) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        JButton solboton = crearBotonConImagen("/resources/solmenu.png");
        buttonPanel.add(solboton);
        JButton botonPala = crearBotonConImagen("/resources/Shovel2.png");
        botonPala.addActionListener(e -> gamePanel.activarModoPala());
        buttonPanel.add(botonPala);

        for (String zombie : ZombiesSeleccionados) {
            String imagePath = getImagePathForZombie(zombie);
            JButton zombieBoton = crearBotonConImagen(imagePath);
            zombieBoton.addActionListener(e -> gamePanel.setZombieSeleccionado(zombie));
            buttonPanel.add(zombieBoton);
            System.out.println("Botón creado para zombie: " + zombie);
        }

        return buttonPanel;
    }



    private String getImagePathForPlanta(String planta) {
        switch (planta) {
            case "Peashooter":
                return "/resources/botonPeashooter.png";
            case "WallNut":
                return "/resources/Wallnutmenu.png";
            case "PotatoMine":
                return "/resources/Potatominemenu.png";
            case "Girasol":
                return "/resources/Girasolboton.png";
            case "ECIPlant":
                return "/resources/ECIplantBoton.png";
            default:
                return "";
        }
    }

    private String getImagePathForZombie(String zombie) {
        switch (zombie) {
            case "BasicZombie":
                return "/resources/BotonBasicZombie.png";
            case "BucketHeadZombie":
                return "/resources/BotonBucketheadZombie.png";
            case "ConeHeadZombie":
                return "/resources/BotonConeheadZombie.png";
            case "Brainstein":
                return "/resources/BrainsteinBoton.png";
            default:
                return "";
        }
    }

    private JButton crearBotonConImagen(String imagePath) {
        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
        Image image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);

        JButton button = new JButton(icon);
        button.setText(null);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 100));
        return button;
    }

    private JButton crearBotonPersonalizado(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(Color.GREEN);
        boton.setForeground(Color.BLUE);
        boton.setSize(150, 50);
        boton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(Color.YELLOW);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(Color.GREEN);
            }
        });

        return boton;
    }

    private JMenuBar crearMenuAudio() {
        JMenuBar menuAudio = new JMenuBar();
        JMenu menuOpcionesAudio = new JMenu("Opciones De Audio");
        JMenuItem itemMute = new JMenuItem("Mute");

        itemMute.addActionListener(e -> {
            if (clip != null) {
                if (isMuted) {
                    clip.start();
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    itemMute.setText("Mute");
                } else {
                    clip.stop();
                    itemMute.setText("Unmute");
                }
                isMuted = !isMuted;
            }
        });

        menuOpcionesAudio.add(itemMute);
        menuAudio.add(menuOpcionesAudio);
        return menuAudio;
    }

    private void reproducirMusica(String filepath) {
        try {
            URL audioURL = getClass().getResource(filepath);
            if (audioURL == null) {
                System.err.println("No se pudo encontrar el archivo de audio: " + filepath);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioURL);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PoobVsZombiesGUI();
    }
}
