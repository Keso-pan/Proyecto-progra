package com.rpg.model;

import com.rpg.combate.Batalla;
import java.util.Random;

/**
 * El Rey Goblin no tiene debilidades, actua dos veces antes que el heroe
 * la primera vez, y se enfurece una unica vez al caer por debajo del 20%
 * de su vida maxima: gana entre 2% y 8% de danio base adicional.
 */
public class ReyGoblin extends Enemigo {

    private static final double UMBRAL_ENFURECIDO = 0.20;
    private static final double BONO_MINIMO = 0.20;
    private static final double BONO_MAXIMO = 0.40;

    private boolean primerTurno;
    private boolean enfurecido;
    private final Random random = new Random();

    public ReyGoblin(int nivel) {
        super("Rey Goblin", 100 + nivel * 10, 10 + nivel, nivel, 100 + nivel * 15);
        this.primerTurno = true;
        this.enfurecido = false;
    }

    /** Devuelve true solo la primera vez que se consulta (su primer turno). */
    public boolean actuaDosVeces() {
        if (primerTurno) {
            primerTurno = false;
            return true;
        }
        return false;
    }

    @Override
    public void habilidadEspecial(Batalla batalla) {
        if (!enfurecido && getVida() < getVidaMax() * UMBRAL_ENFURECIDO) {
            enfurecido = true;
            double porcentaje = BONO_MINIMO + random.nextDouble() * (BONO_MAXIMO - BONO_MINIMO);
            int bono = Math.max(1, (int) Math.round(getDanioBase() * porcentaje));
            setDanioBase(getDanioBase() + bono);
            batalla.registrarEvento(getNombre() + " se enfurece al quedar por debajo del 20% de vida "
                    + "y gana " + bono + " de danio adicional!");
        }
    }
}
