package com.rpg.juego;

import com.rpg.model.Enemigo;
import com.rpg.model.Esqueleto;
import com.rpg.model.Goblin;
import com.rpg.model.Heroe;
import com.rpg.model.Mago;
import com.rpg.model.ReyGoblin;
import com.rpg.model.ReyMago;
import com.rpg.persistencia.GestorArchivos;
import java.util.Random;

/** Controla el flujo general del juego: generacion de enemigos y persistencia. */
public class Juego {

    private Heroe heroe;
    private int nivelActual;
    private int rondaActual;

    private final Random random = new Random();

    public static final int MAX_RONDAS = 40;

    public Juego(Heroe heroe) {
        this.heroe = heroe;
        this.nivelActual = 1;
        this.rondaActual = 1;
    }

    /**
     * Controla qué enemigo sale dependiendo de la ronda.
     * MODIFICADO: los enemigos escalan su nivel usando rondaActual para que
     * sean más dificiles en cada ronda, y los jefes reaparecen en rondas
     * especificas a lo largo de las 40 rondas de la aventura:
     * 5 y 15 y 20 y 35 -> Rey Goblin, 25 y 30 y 40 -> Rey Mago (jefe final).
     */
    public Enemigo generarSiguienteEnemigo() {
        // MODIFICADO: Sumamos rondaActual directamente al nivel del enemigo para aumentar la dificultad progresivamente
        int nivelEnemigo = heroe.getNivel() + rondaActual + random.nextInt(2);

        switch (rondaActual) {
            case 5:
            case 15:
            case 20:
            case 35:
                return new ReyGoblin(nivelEnemigo + 2);
            case 25:
            case 30:
            case MAX_RONDAS:
                return new ReyMago(nivelEnemigo + 4);
            default:
                int tipo = random.nextInt(3);
                switch (tipo) {
                    case 0: return new Goblin(nivelEnemigo);
                    case 1: return new Esqueleto(nivelEnemigo);
                    default: return new Mago(nivelEnemigo);
                }
        }
    }

    public void guardar() throws Exception {
        GestorArchivos.guardarHeroe(heroe);
    }

    public void cargar() throws Exception {
        Heroe cargado = GestorArchivos.cargarHeroe();
        if (cargado != null) {
            this.heroe = cargado;
        }
    }

    public Heroe getHeroe() { return heroe; }
    public int getNivelActual() { return nivelActual; }
    public void avanzarNivel() { nivelActual++; }
    public int getRondaActual() { return rondaActual; }
    public void avanzarRonda() { rondaActual++; }
}