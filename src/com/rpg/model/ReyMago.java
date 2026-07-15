package com.rpg.model;

import com.rpg.combate.Batalla;
import java.util.Random;

/** El Rey Mago tiene 20% de probabilidad de curarse 60% de su vida maxima al terminar su turno. */
public class ReyMago extends Enemigo {

    private static final double PROB_CURACION = 0.15;
    private static final double PORCENTAJE_CURACION = 0.60;

    private final Random random = new Random();

    public ReyMago(int nivel) {
        super("Rey Mago", 110 + nivel * 12, 20 + nivel, nivel, 100 + nivel * 15);
    }

    @Override
    public void habilidadEspecial(Batalla batalla) {
        if (random.nextDouble() < PROB_CURACION) {
            int curacion = (int) Math.round(getVidaMax() * PORCENTAJE_CURACION);
            curar(curacion);
            batalla.registrarEvento(getNombre() + " se curo " + curacion + " puntos de vida!");
        }
    }
}
