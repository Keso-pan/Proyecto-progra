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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

/**
 * Primera ventana funcional de la GUI (punto de partida).
 * Mas adelante se puede dividir en VentanaInicio, VentanaBatalla e
 * VentanaInventario separadas; por ahora todo vive en una sola ventana
 * para tener algo jugable rapido.
 */
public class VentanaJuego extends JFrame {

    private Juego juego;
    private Batalla batallaActual;

    private JLabel lblHeroeInfo;
    private JProgressBar barraVidaHeroe;
    private JLabel lblEnemigoInfo;
    private JProgressBar barraVidaEnemigo;
    private JTextArea areaLog;
    private JButton btnAtacar;
    private JButton btnInventario;
    private JButton btnGuardar;
    private JButton btnCargar;

    public VentanaJuego() {
        super("Aventura RPG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 480);
        setLocationRelativeTo(null);

        crearHeroeInicial();
        inicializarComponentes();
        nuevaBatalla();
    }

    private void crearHeroeInicial() {
        String[] opciones = {"Guerrero", "Arquero"};
        int seleccion = JOptionPane.showOptionDialog(this, "Elige tu clase de heroe:",
                "Creacion de personaje", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);

        String nombre = JOptionPane.showInputDialog(this, "Nombre del heroe:", "Heroe");
        if (nombre == null || nombre.isBlank()) {
            nombre = "Heroe";
        }

        Heroe heroe = (seleccion == 1) ? new Arquero(nombre) : new Guerrero(nombre);
        this.juego = new Juego(heroe);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new GridLayout(2, 2, 10, 5));
        lblHeroeInfo = new JLabel();
        barraVidaHeroe = new JProgressBar();
        lblEnemigoInfo = new JLabel();
        barraVidaEnemigo = new JProgressBar();
        panelSuperior.add(lblHeroeInfo);
        panelSuperior.add(lblEnemigoInfo);
        panelSuperior.add(barraVidaHeroe);
        panelSuperior.add(barraVidaEnemigo);
        add(panelSuperior, BorderLayout.NORTH);

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        add(new JScrollPane(areaLog), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        btnAtacar = new JButton("Atacar");
        btnInventario = new JButton("Inventario");
        btnGuardar = new JButton("Guardar");
        btnCargar = new JButton("Cargar");
        panelBotones.add(btnAtacar);
        panelBotones.add(btnInventario);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCargar);
        add(panelBotones, BorderLayout.SOUTH);

        btnAtacar.addActionListener(this::alAtacar);
        btnInventario.addActionListener(this::alAbrirInventario);
        btnGuardar.addActionListener(this::alGuardar);
        btnCargar.addActionListener(this::alCargar);
    }

    private void nuevaBatalla() {
        Enemigo enemigo = juego.generarEnemigo();
        batallaActual = new Batalla(juego.getHeroe(), enemigo);
        batallaActual.iniciar();
        areaLog.append("\n--- Nuevo enemigo: " + enemigo.getNombre() +
                " (nivel " + enemigo.getNivel() + ") ---\n");
        actualizarVista();
    }

    private void alAtacar(ActionEvent e) {
        if (batallaActual.estaTerminada()) {
            return;
        }

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
                JOptionPane.showMessageDialog(this, juego.getHeroe().getNombre() + " gano la batalla!");
                nuevaBatalla();
            } else {
                JOptionPane.showMessageDialog(this, "Has sido derrotado. Fin del juego.");
                btnAtacar.setEnabled(false);
            }
        }
    }

    private void alAbrirInventario(ActionEvent e) {
        Inventario inventario = juego.getHeroe().getInventario();

        if (inventario.getItems().isEmpty()) {
            int r = JOptionPane.showConfirmDialog(this,
                    "Inventario vacio. Quieres obtener un item aleatorio?",
                    "Inventario", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                try {
                    juego.getHeroe().recogerItem(ItemCatalogo.obtenerItemAleatorio());
                    areaLog.append("Obtuviste un nuevo item.\n");
                } catch (InventarioLlenoException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
            return;
        }

        String[] nombres = inventario.getItems().stream().map(Item::toString).toArray(String[]::new);
        String elegido = (String) JOptionPane.showInputDialog(this, "Elige un item para usar:",
                "Inventario", JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);

        if (elegido != null) {
            int indice = Arrays.asList(nombres).indexOf(elegido);
            try {
                juego.getHeroe().usarItem(indice);
                areaLog.append("Usaste: " + elegido + "\n");
                actualizarVista();
            } catch (ItemInvalidoException ex) {
                JOptionPane.showMessageDialog(this, "Error al usar el item: " + ex.getMessage());
            }
        }
    }

    private void alGuardar(ActionEvent e) {
        try {
            juego.guardar();
            JOptionPane.showMessageDialog(this, "Partida guardada correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }

    private void alCargar(ActionEvent e) {
        try {
            juego.cargar();
            JOptionPane.showMessageDialog(this, "Partida cargada correctamente.");
            nuevaBatalla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage());
        }
    }

    private void actualizarVista() {
        Heroe heroe = juego.getHeroe();
        Enemigo enemigo = batallaActual.getEnemigoPrincipal();

        lblHeroeInfo.setText(heroe.getNombre() + " (Nv." + heroe.getNivel() + ") HP: " +
                heroe.getVida() + "/" + heroe.getVidaMax());
        barraVidaHeroe.setMaximum(heroe.getVidaMax());
        barraVidaHeroe.setValue(Math.max(0, heroe.getVida()));

        lblEnemigoInfo.setText(enemigo.getNombre() + " (Nv." + enemigo.getNivel() + ") HP: " +
                enemigo.getVida() + "/" + enemigo.getVidaMax());
        barraVidaEnemigo.setMaximum(enemigo.getVidaMax());
        barraVidaEnemigo.setValue(Math.max(0, enemigo.getVida()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaJuego().setVisible(true));
    }
}
