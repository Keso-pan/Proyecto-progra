package com.rpg.combate;

import com.rpg.model.Personaje;
import java.io.Serializable;

/**
 * Representa un efecto de estado temporal (Sangrado, Congelado) que se
 * aplica sobre un Personaje durante un numero determinado de turnos.
 */
public class EfectoEstado implements Serializable {

    public enum Tipo { SANGRADO, CONGELADO }

    /** El sangrado se puede acumular hasta 3 veces (10% + 10% + 10% = 30% del danio del heroe). */
    public static final int MAX_STACKS_SANGRADO = 3;

    private Tipo tipo;
    private int duracionTurnos;
    private int danioPorTurno;
    private final int danioPorStack;
    private int stacks;

    public EfectoEstado(Tipo tipo, int duracionTurnos, int danioPorTurno) {
        this.tipo = tipo;
        this.duracionTurnos = duracionTurnos;
        this.danioPorTurno = danioPorTurno;
        this.danioPorStack = danioPorTurno;
        this.stacks = 1;
    }

    /** Aplica el efecto sobre el personaje afectado y reduce su duracion. */
    public void aplicar(Personaje objetivo) {
        if (tipo == Tipo.SANGRADO) {
            objetivo.recibirDanio(danioPorTurno);
        }
        duracionTurnos--;
    }

    /**
     * Suma un stack de sangrado (10% adicional del danio del heroe, hasta el
     * maximo de 3) y refresca la duracion del efecto. Se usa cuando el
     * Guerrero da un golpe critico o el Arquero activa el disparo triple.
     */
    public void agregarStack(int duracionNueva) {
        if (tipo != Tipo.SANGRADO) return;
        if (stacks < MAX_STACKS_SANGRADO) {
            stacks++;
            danioPorTurno = danioPorStack * stacks;
        }
        this.duracionTurnos = duracionNueva;
    }

    public boolean isImpideAccion() {
        return tipo == Tipo.CONGELADO && duracionTurnos > 0;
    }

    public boolean haTerminado() {
        return duracionTurnos <= 0;
    }

    public Tipo getTipo() { return tipo; }
    public int getStacks() { return stacks; }
    public int getDanioPorTurno() { return danioPorTurno; }
}
