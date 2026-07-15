package com.rpg.model;

import com.rpg.combate.EfectoEstado;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase abstracta que representa a cualquier ente que participa en combate
 * (Heroe o Enemigo). Aplica ENCAPSULAMIENTO: todos los atributos son
 * privados y solo se acceden/modifican mediante metodos controlados
 * (por ejemplo, la vida nunca puede superar vidaMax ni bajar de 0).
 */
public abstract class Personaje implements Serializable {

    private String nombre;
    private int vida;
    private int vidaMax;
    private int danioBase;
    private int nivel;
    private List<EfectoEstado> efectosActivos;

    public Personaje(String nombre, int vidaMax, int danioBase, int nivel) {
        this.nombre = nombre;
        this.vidaMax = vidaMax;
        this.vida = vidaMax;
        this.danioBase = danioBase;
        this.nivel = nivel;
        this.efectosActivos = new ArrayList<>();
    }

    /**
     * Cada subclase decide COMO ataca (Sobreescritura / Polimorfismo).
     * Devuelve el danio efectivo infligido, util para el log de batalla.
     */
    public abstract int atacar(Personaje objetivo);

    /** Reduce la vida sin permitir que quede fuera del rango [0, vidaMax]. */
    public void recibirDanio(int cantidad) {
        if (cantidad < 0) cantidad = 0;
        this.vida = Math.max(0, this.vida - cantidad);
    }

    public void curar(int cantidad) {
        this.vida = Math.min(vidaMax, this.vida + cantidad);
    }

    public boolean estaVivo() {
        return this.vida > 0;
    }

    /** Procesa todos los efectos activos (aplica sangrado, reduce duraciones). */
    public void aplicarEfectos() {
        List<EfectoEstado> terminados = new ArrayList<>();
        for (EfectoEstado efecto : efectosActivos) {
            efecto.aplicar(this);
            if (efecto.haTerminado()) {
                terminados.add(efecto);
            }
        }
        efectosActivos.removeAll(terminados);
    }

    /** Un personaje congelado no puede actuar en su turno. */
    public boolean puedeActuar() {
        for (EfectoEstado efecto : efectosActivos) {
            if (efecto.isImpideAccion()) return false;
        }
        return true;
    }

    public void agregarEfecto(EfectoEstado efecto) {
        efectosActivos.add(efecto);
    }

    /** Busca un efecto activo de un tipo dado (por ejemplo, para saber si ya hay sangrado y sumarle un stack). */
    public EfectoEstado buscarEfecto(EfectoEstado.Tipo tipo) {
        for (EfectoEstado efecto : efectosActivos) {
            if (efecto.getTipo() == tipo) return efecto;
        }
        return null;
    }

    public List<EfectoEstado> getEfectosActivos() {
        return efectosActivos;
    }

    // ----- Getters / Setters (unico acceso permitido a los atributos) -----

    public String getNombre() { return nombre; }

    public int getVida() { return vida; }

    public void setVida(int vida) {
        this.vida = Math.max(0, Math.min(vida, vidaMax));
    }

    public int getVidaMax() { return vidaMax; }

    public void setVidaMax(int vidaMax) {
        this.vidaMax = Math.max(1, vidaMax);
    }

    public int getDanioBase() { return danioBase; }

    public void setDanioBase(int danioBase) {
        this.danioBase = Math.max(0, danioBase);
    }

    public int getNivel() { return nivel; }

    public void setNivel(int nivel) { this.nivel = nivel; }
}
