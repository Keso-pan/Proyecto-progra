package com.rpg.gui;

import com.rpg.combate.Batalla;
import com.rpg.excepciones.InventarioLlenoException;
import com.rpg.excepciones.ItemInvalidoException;
import com.rpg.items.Inventario;
import com.rpg.items.Item;
import com.rpg.items.ItemCatalogo;
import com.rpg.juego.Juego;
import com.rpg.model.Arquero;
import com.rpg.model.Enemigo;
import com.rpg.model.Guerrero;
import com.rpg.model.Heroe;
import com.rpg.model.Esqueleto;
import com.rpg.model.Mago;
import com.rpg.model.ReyGoblin;
import com.rpg.model.ReyMago;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class VentanaJuego extends JFrame {

    private Juego juego;
    private Batalla batallaActual;

    private CardLayout cardLayout;
    private JPanel panelContenedor;
    private JPanel panelBatalla;

    private EtiquetaBordeada lblHeroeInfo;
    private EtiquetaBordeada lblEnemigoInfo;

    private JProgressBar barraVidaHeroe;
    private JProgressBar barraVidaEnemigo;
    private JLabel lblImagenHeroe;
    private JLabel lblImagenEnemigo;
    private JTextArea areaLog;
    private JButton btnAtacar;
    private JButton btnInventario;
    private JButton btnGuardar;
    private JButton btnCargar;

    private ReproductorAudio reproductorMusica = new ReproductorAudio();

    // NUEVO: Variable para guardar el fondo actual de la batalla
    private Image imagenFondoBatalla;

    // Clase interna para crear textos con bordes
    class EtiquetaBordeada extends JLabel {
        private Color colorBorde;

        public EtiquetaBordeada(String texto, int alineacion, Color colorBorde) {
            super(texto, alineacion);
            this.colorBorde = colorBorde;
        }

        @Override
        public void paintComponent(Graphics g) {
            int grosor = 3;
            Color colorOriginal = getForeground();

            setForeground(colorBorde);
            for (int x = -grosor; x <= grosor; x++) {
                for (int y = -grosor; y <= grosor; y++) {
                    if (x != 0 || y != 0) {
                        g.translate(x, y);
                        super.paintComponent(g);
                        g.translate(-x, -y);
                    }
                }
            }
            setForeground(colorOriginal);
            super.paintComponent(g);
        }
    }

    class RetroScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.trackColor = new Color(20, 20, 20);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return crearBotonVacio();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return crearBotonVacio();
        }

        private JButton crearBotonVacio() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            return btn;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;

            g.setColor(new Color(70, 70, 70));
            g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
            g.setColor(Color.BLACK);
            g.drawRect(thumbBounds.x, thumbBounds.y, thumbBounds.width - 1, thumbBounds.height - 1);
        }
    }

    public VentanaJuego() {
        super("Aventura RPG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        setContentPane(panelContenedor);

        crearPantallaInicio();
        crearPantallaBatalla();

        cardLayout.show(panelContenedor, "MENU");
        reproductorMusica.reproducirEnBucle("musica_menu.wav");
    }

    private ImageIcon cargarImagen(String archivo) {
        try {
            java.net.URL imgURL = getClass().getResource("/" + archivo);
            if (imgURL != null) {
                ImageIcon iconoOriginal = new ImageIcon(imgURL);
                java.awt.Image img = iconoOriginal.getImage();
                java.awt.Image imgEscalada = img.getScaledInstance(220, 220, java.awt.Image.SCALE_DEFAULT);
                return new ImageIcon(imgEscalada);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar: " + archivo);
        }
        return new ImageIcon();
    }

    private Font cargarFuenteRetro(float size) {
        try {
            java.net.URL fontUrl = getClass().getResource("/pixel.ttf");
            if (fontUrl != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
                return font.deriveFont(size);
            }
        } catch (Exception e) {
            System.err.println("Usando fuente normal.");
        }
        return new Font("Monospaced", Font.BOLD, (int)size);
    }

    // =========================================================================
    // POPUPS MEJORADOS
    // =========================================================================

    private void mostrarMensaje(String mensaje) {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        // MODIFICADO: Ventana más ancha (550) y un poco más alta (240)
        dialog.setSize(550, 240);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout()) {
            Image img = cargarImagen("fondo_popup.jpg").getImage();
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 4));

        JTextPane textPane = new JTextPane();
        textPane.setOpaque(false);
        textPane.setEditable(false);
        textPane.setFocusable(false);
        // MODIFICADO: Fuente un poco más pequeña (14f en lugar de 16f) para evitar cortes
        textPane.setFont(cargarFuenteRetro(14f));
        textPane.setForeground(Color.WHITE);

        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        StyledDocument doc = textPane.getStyledDocument();
        textPane.setText(mensaje);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        JPanel textContainer = new JPanel(new GridBagLayout());
        textContainer.setOpaque(false);
        textContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        textContainer.add(textPane);

        JButton btnOk = new JButton("ACEPTAR");
        btnOk.setFont(cargarFuenteRetro(14f));
        btnOk.setBackground(new Color(178, 34, 34));
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.setPreferredSize(new Dimension(120, 35));
        btnOk.addActionListener(e -> dialog.dispose());

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panelBotones.add(btnOk);

        panel.add(textContainer, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private int mostrarInventario(Inventario inventario) {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        // MODIFICADO: Un poco más ancha para prevenir cortes
        dialog.setSize(550, 260);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            Image img = cargarImagen("fondo_popup.jpg").getImage();
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 4));

        EtiquetaBordeada lblTitulo = new EtiquetaBordeada("ELIGE UN ITEM:", SwingConstants.CENTER, Color.BLACK);
        lblTitulo.setForeground(new Color(255, 215, 0));
        lblTitulo.setFont(cargarFuenteRetro(20f));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        String[] nombres = inventario.getItems().stream().map(Item::toString).toArray(String[]::new);
        JComboBox<String> combo = new JComboBox<>(nombres);
        combo.setFont(cargarFuenteRetro(14f));
        combo.setBackground(new Color(40, 40, 40));
        combo.setForeground(Color.WHITE);
        combo.setPreferredSize(new Dimension(480, 45));

        JPanel panelCentro = new JPanel();
        panelCentro.setOpaque(false);
        panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panelCentro.add(combo);

        JButton btnUsar = new JButton("USAR");
        btnUsar.setFont(cargarFuenteRetro(14f));
        btnUsar.setBackground(new Color(50, 150, 50));
        btnUsar.setForeground(Color.WHITE);
        btnUsar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        btnUsar.setPreferredSize(new Dimension(120, 35));
        btnUsar.setFocusPainted(false);
        btnUsar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCancelar = new JButton("CANCELAR");
        btnCancelar.setFont(cargarFuenteRetro(14f));
        btnCancelar.setBackground(new Color(178, 34, 34));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final int[] eleccion = {-1};
        btnUsar.addActionListener(e -> {
            eleccion[0] = combo.getSelectedIndex();
            dialog.dispose();
        });
        btnCancelar.addActionListener(e -> dialog.dispose());

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panelBotones.add(btnUsar);
        panelBotones.add(Box.createRigidArea(new Dimension(20, 0)));
        panelBotones.add(btnCancelar);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(panelCentro, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);

        return eleccion[0];
    }

    // =========================================================================

    private void crearPantallaInicio() {
        final ImageIcon fondoPantalla = cargarImagen("fondo_menu.jpg");

        JPanel panelMenu = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondoPantalla != null && fondoPantalla.getImage() != null) {
                    g.drawImage(fondoPantalla.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panelMenu.setBackground(new Color(20, 20, 20));
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100));

        Font fuenteRetroTitulo = cargarFuenteRetro(75f);
        Font fuenteRetroNormal = cargarFuenteRetro(18f);
        Font fuenteInput = cargarFuenteRetro(16f);

        EtiquetaBordeada titulo = new EtiquetaBordeada("AVENTURA RPG", SwingConstants.CENTER, Color.BLACK);
        titulo.setFont(fuenteRetroTitulo);
        titulo.setForeground(new Color(255, 215, 0));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        EtiquetaBordeada lblNombre = new EtiquetaBordeada("Nombre de tu héroe:", SwingConstants.CENTER, Color.BLACK);
        lblNombre.setFont(fuenteRetroNormal);
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtNombre = new JTextField(15);
        txtNombre.setMaximumSize(new Dimension(250, 40));
        txtNombre.setFont(fuenteInput);
        txtNombre.setBackground(new Color(20, 20, 20, 220));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setCaretColor(Color.WHITE);
        txtNombre.setHorizontalAlignment(JTextField.CENTER);
        txtNombre.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        EtiquetaBordeada lblClase = new EtiquetaBordeada("Selecciona tu clase:", SwingConstants.CENTER, Color.BLACK);
        lblClase.setFont(fuenteRetroNormal);
        lblClase.setForeground(Color.WHITE);
        lblClase.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] clases = {"Guerrero", "Arquero"};
        JComboBox<String> comboClase = new JComboBox<>(clases);
        comboClase.setMaximumSize(new Dimension(250, 40));
        comboClase.setFont(fuenteInput);
        comboClase.setBackground(new Color(20, 20, 20, 220));
        comboClase.setForeground(Color.WHITE);
        comboClase.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JButton btnJugar = new JButton("INICIAR AVENTURA");
        btnJugar.setFont(fuenteRetroNormal);
        btnJugar.setBackground(new Color(178, 34, 34));
        btnJugar.setForeground(Color.WHITE);
        btnJugar.setFocusPainted(false);
        btnJugar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnJugar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnJugar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        btnJugar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) nombre = "Héroe";

            Heroe heroe = (comboClase.getSelectedIndex() == 1) ? new Arquero(nombre) : new Guerrero(nombre);
            this.juego = new Juego(heroe);

            reproductorMusica.detener();
            reproductorMusica.reproducirEnBucle("musica_batalla.wav");

            nuevaBatalla();
            cardLayout.show(panelContenedor, "BATALLA");
        });

        panelMenu.add(Box.createVerticalGlue());
        panelMenu.add(titulo);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 100)));
        panelMenu.add(lblNombre);
        panelMenu.add(txtNombre);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 20)));
        panelMenu.add(lblClase);
        panelMenu.add(comboClase);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 40)));
        panelMenu.add(btnJugar);
        panelMenu.add(Box.createVerticalGlue());

        panelContenedor.add(panelMenu, "MENU");
    }

    private void crearPantallaBatalla() {
        // MODIFICADO: El fondo ahora es dinámico, por lo que lo leemos de la variable imagenFondoBatalla
        panelBatalla = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagenFondoBatalla != null) {
                    g.drawImage(imagenFondoBatalla, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panelBatalla.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Font fuenteRetroNormal = cargarFuenteRetro(16f);
        Font fuenteRetroLog = cargarFuenteRetro(14f);
        Font fuenteRetroChica = cargarFuenteRetro(12f);

        JPanel panelSuperiorBatalla = new JPanel(new GridLayout(1, 2, 20, 0));
        panelSuperiorBatalla.setOpaque(false);

        JPanel panelHeroe = new JPanel(new BorderLayout(5, 5));
        panelHeroe.setOpaque(false);
        lblHeroeInfo = new EtiquetaBordeada("Heroe", SwingConstants.CENTER, Color.BLACK);
        lblHeroeInfo.setFont(fuenteRetroNormal);
        lblHeroeInfo.setForeground(Color.WHITE);
        lblImagenHeroe = new JLabel("", SwingConstants.CENTER);

        barraVidaHeroe = new JProgressBar();
        barraVidaHeroe.setStringPainted(true);
        barraVidaHeroe.setFont(fuenteRetroChica);
        barraVidaHeroe.setForeground(new Color(50, 205, 50));
        barraVidaHeroe.setBackground(new Color(40, 40, 40));
        barraVidaHeroe.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        panelHeroe.add(lblHeroeInfo, BorderLayout.NORTH);
        panelHeroe.add(lblImagenHeroe, BorderLayout.CENTER);
        panelHeroe.add(barraVidaHeroe, BorderLayout.SOUTH);

        JPanel panelEnemigo = new JPanel(new BorderLayout(5, 5));
        panelEnemigo.setOpaque(false);
        lblEnemigoInfo = new EtiquetaBordeada("Enemigo", SwingConstants.CENTER, Color.BLACK);
        lblEnemigoInfo.setFont(fuenteRetroNormal);
        lblEnemigoInfo.setForeground(Color.WHITE);
        lblImagenEnemigo = new JLabel("", SwingConstants.CENTER);

        barraVidaEnemigo = new JProgressBar();
        barraVidaEnemigo.setStringPainted(true);
        barraVidaEnemigo.setFont(fuenteRetroChica);
        barraVidaEnemigo.setForeground(new Color(220, 20, 60));
        barraVidaEnemigo.setBackground(new Color(40, 40, 40));
        barraVidaEnemigo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        panelEnemigo.add(lblEnemigoInfo, BorderLayout.NORTH);
        panelEnemigo.add(lblImagenEnemigo, BorderLayout.CENTER);
        panelEnemigo.add(barraVidaEnemigo, BorderLayout.SOUTH);

        panelSuperiorBatalla.add(panelHeroe);
        panelSuperiorBatalla.add(panelEnemigo);
        panelBatalla.add(panelSuperiorBatalla, BorderLayout.NORTH);

        areaLog = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        areaLog.setOpaque(false);
        areaLog.setEditable(false);
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        areaLog.setFont(fuenteRetroLog);
        areaLog.setForeground(Color.WHITE);
        areaLog.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setOpaque(false);
        scrollLog.getViewport().setOpaque(false);
        scrollLog.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        scrollLog.getVerticalScrollBar().setUI(new RetroScrollBarUI());
        scrollLog.getVerticalScrollBar().setPreferredSize(new Dimension(15, 0));

        panelBatalla.add(scrollLog, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);

        btnAtacar = new JButton("ATACAR");
        btnInventario = new JButton("INVENTARIO");
        btnGuardar = new JButton("GUARDAR");
        btnCargar = new JButton("CARGAR");

        JButton[] botones = {btnAtacar, btnInventario, btnGuardar, btnCargar};
        for(JButton btn : botones) {
            btn.setFont(fuenteRetroNormal);
            btn.setBackground(new Color(70, 70, 70));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(160, 40));
            panelBotones.add(btn);
        }

        btnAtacar.setBackground(new Color(178, 34, 34));

        panelBatalla.add(panelBotones, BorderLayout.SOUTH);

        btnAtacar.addActionListener(this::alAtacar);
        btnInventario.addActionListener(this::alAbrirInventario);
        btnGuardar.addActionListener(this::alGuardar);
        btnCargar.addActionListener(this::alCargar);

        panelContenedor.add(panelBatalla, "BATALLA");
    }

    private void nuevaBatalla() {
        Enemigo enemigo = juego.generarSiguienteEnemigo();
        batallaActual = new Batalla(juego.getHeroe(), enemigo);
        batallaActual.iniciar();

        areaLog.append("\n--- RONDA " + juego.getRondaActual() + " --- Nuevo enemigo: " + enemigo.getNombre() +
                " (nivel " + enemigo.getNivel() + ") ---\n");

        btnAtacar.setEnabled(true);
        actualizarVista();
    }

    private void alAtacar(ActionEvent e) {
        if (batallaActual.estaTerminada()) return;

        reproductorMusica.reproducirEfecto("ataque.wav");

        int nivelAntes = juego.getHeroe().getNivel();
        int eventosPrevios = batallaActual.getRegistroEventos().size();
        batallaActual.ejecutarTurnoCompleto();

        List<String> eventos = batallaActual.getRegistroEventos();
        for (int i = eventosPrevios; i < eventos.size(); i++) {
            areaLog.append(eventos.get(i) + "\n");
        }
        areaLog.setCaretPosition(areaLog.getDocument().getLength());

        actualizarVista();

        if (batallaActual.estaTerminada()) {
            if (juego.getHeroe().estaVivo()) {
                reproductorMusica.reproducirEfecto("ganar.wav");

                // El nivel (y sus bonos de vida/danio) ya se actualizo dentro de
                // ejecutarTurnoCompleto() -> verificarGanador() -> heroe.ganarXP().
                // Aqui solo comparamos nivelAntes/despues para el mensaje, sin
                // volver a tocar el nivel del heroe.
                int nivelDespues = juego.getHeroe().getNivel();

                String mensajeNivel = juego.getHeroe().getNombre() + " ganó la batalla de la Ronda " + juego.getRondaActual() + "!";

                if (nivelDespues > nivelAntes) {
                    mensajeNivel += "\n¡Subió al Nivel " + nivelDespues + "!";
                }

                if (nivelDespues >= 3 && nivelAntes < 3) {
                    if (juego.getHeroe() instanceof Guerrero) {
                        mensajeNivel += "\n\n¡Has aprendido: GOLPE DEMOLEDOR!";
                    } else {
                        mensajeNivel += "\n\n¡Has aprendido: FLECHA PERFORANTE!";
                    }
                } else if (nivelDespues >= 5 && nivelAntes < 5) {
                    mensajeNivel += "\n\n¡Has aprendido una habilidad pasiva de regeneración!";
                }

                mostrarMensaje(mensajeNivel);

                if (juego.getRondaActual() == 2 || Math.random() < 0.60) {
                    Item loot = ItemCatalogo.obtenerItemAleatorio();
                    try {
                        juego.getHeroe().recogerItem(loot);
                        reproductorMusica.reproducirEfecto("loot.wav");
                        mostrarMensaje("¡El enemigo dejó caer algo al morir!\nObtuviste: " + loot.getNombre());
                    } catch (InventarioLlenoException ex) {
                        mostrarMensaje("¡El enemigo soltó [" + loot.getNombre() + "] pero tu inventario está lleno!");
                    }
                }

                if (juego.getRondaActual() == Juego.MAX_RONDAS) {
                    reproductorMusica.detener();
                    mostrarMensaje("¡FELICIDADES!\nHas derrotado al Rey Mago y completado tu aventura RPG.\nJuego terminado con éxito.");

                    cardLayout.show(panelContenedor, "MENU");
                    reproductorMusica.reproducirEnBucle("musica_menu.wav");
                } else {
                    juego.avanzarRonda();
                    nuevaBatalla();
                }
            } else {
                reproductorMusica.detener();
                reproductorMusica.reproducirEfecto("perder.wav");
                mostrarMensaje("Has sido derrotado.\n\nFin del juego.");

                cardLayout.show(panelContenedor, "MENU");
                reproductorMusica.reproducirEnBucle("musica_menu.wav");
            }
        }
    }

    private void alAbrirInventario(ActionEvent e) {
        reproductorMusica.reproducirEfecto("inventario.wav");

        Inventario inventario = juego.getHeroe().getInventario();
        if (inventario.getItems().isEmpty()) {
            mostrarMensaje("Tu inventario está vacío.");
            return;
        }

        int indiceElegido = mostrarInventario(inventario);

        if (indiceElegido != -1) {
            try {
                Item itemUsado = inventario.getItems().get(indiceElegido);
                juego.getHeroe().usarItem(indiceElegido);
                areaLog.append("Usaste: " + itemUsado.toString() + "\n");
                actualizarVista();
            } catch (ItemInvalidoException ex) {
                mostrarMensaje("Error: " + ex.getMessage());
            }
        }
    }

    private void alGuardar(ActionEvent e) {
        reproductorMusica.reproducirEfecto("guardar.wav");

        try {
            juego.guardar();
            mostrarMensaje("Partida guardada correctamente en la Ronda " + juego.getRondaActual() + ".");
        } catch (Exception ex) {
            mostrarMensaje("Error al guardar: " + ex.getMessage());
        }
    }

    private void alCargar(ActionEvent e) {
        reproductorMusica.reproducirEfecto("cargar.wav");

        try {
            juego.cargar();
            mostrarMensaje("Partida cargada correctamente.");

            if (!juego.getHeroe().estaVivo()) {
                juego.getHeroe().curar(juego.getHeroe().getVidaMax());
            }

            reproductorMusica.detener();
            reproductorMusica.reproducirEnBucle("musica_batalla.wav");

            cardLayout.show(panelContenedor, "BATALLA");
            nuevaBatalla();
        } catch (Exception ex) {
            mostrarMensaje("Error al cargar: " + ex.getMessage());
        }
    }

    private void actualizarVista() {
        Heroe heroe = juego.getHeroe();
        Enemigo enemigo = batallaActual.getEnemigoPrincipal();

        lblHeroeInfo.setText(heroe.getNombre() + " (Nv." + heroe.getNivel() + ")");
        barraVidaHeroe.setMaximum(heroe.getVidaMax());
        barraVidaHeroe.setValue(Math.max(0, heroe.getVida()));
        barraVidaHeroe.setString(Math.max(0, heroe.getVida()) + " / " + heroe.getVidaMax() + " HP");

        lblEnemigoInfo.setText(enemigo.getNombre() + " (Nv." + enemigo.getNivel() + ")");
        barraVidaEnemigo.setMaximum(enemigo.getVidaMax());
        barraVidaEnemigo.setValue(Math.max(0, enemigo.getVida()));
        barraVidaEnemigo.setString(Math.max(0, enemigo.getVida()) + " / " + enemigo.getVidaMax() + " HP");

        String rutaHeroe;
        if (heroe instanceof Arquero) {
            rutaHeroe = (heroe.getNivel() >= 10) ? "arquero_avanzado.png" : "arquero.png";
        } else {
            rutaHeroe = (heroe.getNivel() >= 10) ? "guerrero_avanzado.png" : "guerrero.png";
        }
        lblImagenHeroe.setIcon(cargarImagen(rutaHeroe));

        String rutaEnemigo = "goblin.png";
        // MODIFICADO: Aquí verificamos qué enemigo es y le asignamos su imagen y SU FONDO RESPECTIVO
        if (enemigo instanceof Esqueleto) {
            rutaEnemigo = "esqueleto.png";
            imagenFondoBatalla = cargarImagen("fondo_esqueleto.jpg").getImage();
        }
        else if (enemigo instanceof Mago || enemigo instanceof ReyMago) {
            if (enemigo instanceof Mago) rutaEnemigo = "mago.png";
            else rutaEnemigo = "rey_mago.png";

            imagenFondoBatalla = cargarImagen("fondo_mago.jpg").getImage();
        }
        else { // Goblins y Rey Goblin (El fondo por defecto será el de césped)
            if (enemigo instanceof ReyGoblin) rutaEnemigo = "rey_goblin.png";
            else rutaEnemigo = "goblin.png";

            imagenFondoBatalla = cargarImagen("fondo_goblin.jpg").getImage();
        }

        lblImagenEnemigo.setIcon(cargarImagen(rutaEnemigo));

        if (panelBatalla != null) {
            panelBatalla.repaint(); // Refresca el panel para asegurar que el nuevo fondo se dibuje de inmediato
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaJuego().setVisible(true));
    }
}