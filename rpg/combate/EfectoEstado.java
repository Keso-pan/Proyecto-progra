package com.rpg.combate;

import com.rpg.model.Personaje;
import java.io.Serializable;

/**
 * Representa un efecto de estado temporal (Sangrado, Congelado) que se
 * aplica sobre un Personaje durante un numero determinado de turnos.
 */
public class EfectoEstado implements Serializable {

    public enum Tipo { SANGRADO, CONGELADO }

    private Tipo tipo;
    private int duracionTurnos;
    private int danioPorTurno;

    public EfectoEstado(Tipo tipo, int duracionTurnos, int danioPorTurno) {
        this.tipo = tipo;
        this.duracionTurnos = duracionTurnos;
        this.danioPorTurno = danioPorTurno;
    }

    /** Aplica el efecto sobre el personaje afectado y reduce su duracion. */
    public void aplicar(Personaje objetivo) {
        if (tipo == Tipo.SANGRADO) {
            objetivo.recibirDanio(danioPorTurno);
        }
        duracionTurnos--;
    }

    public boolean isImpideAccion() {
        return tipo == Tipo.CONGELADO && duracionTurnos > 0;
    }

    public boolean haTerminado() {
        return duracionTurnos <= 0;
    }

    public Tipo getTipo() { return tipo; }
}
