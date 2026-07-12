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
    private final Random random = new Random();

    public Juego(Heroe heroe) {
        this.heroe = heroe;
        this.nivelActual = 1;
    }

    /** Genera un enemigo normal (Goblin, Esqueleto o Mago) con nivel = heroe.nivel + 1, 2 o 3. */
    public Enemigo generarEnemigo() {
        int nivelEnemigo = heroe.getNivel() + 1 + random.nextInt(3);
        int tipo = random.nextInt(3);
        switch (tipo) {
            case 0: return new Goblin(nivelEnemigo);
            case 1: return new Esqueleto(nivelEnemigo);
            default: return new Mago(nivelEnemigo);
        }
    }

    /** Genera un jefe (Rey Goblin o Rey Mago), por ejemplo cada N rondas. */
    public Enemigo generarJefe() {
        int nivelJefe = heroe.getNivel() + 2;
        return random.nextBoolean() ? new ReyGoblin(nivelJefe) : new ReyMago(nivelJefe);
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
}
