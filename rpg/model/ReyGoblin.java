package com.rpg.model;

import com.rpg.combate.Batalla;
import java.util.ArrayList;
import java.util.List;

/**
 * El Rey Goblin no tiene debilidades, actua dos veces antes que el heroe
 * la primera vez, y puede invocar goblins que atacan despues de su turno.
 */
public class ReyGoblin extends Enemigo {

    private boolean primerTurno;

    public ReyGoblin(int nivel) {
        super("Rey Goblin", 120 + nivel * 10, 15 + nivel, nivel, 100 + nivel * 10);
        this.primerTurno = true;
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
        List<Enemigo> invocados = new ArrayList<>();
        invocados.add(new Goblin(getNivel()));
        batalla.invocarRefuerzos(invocados);
    }
}
