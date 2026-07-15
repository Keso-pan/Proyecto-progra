package com.rpg.model;

import com.rpg.combate.Batalla;
import com.rpg.combate.TipoAtaque;

/**
 * Enemigo hereda de Personaje. Cada subclase concreta (Goblin, Esqueleto,
 * Mago, ReyGoblin, ReyMago) sobreescribe habilidadEspecial() y, si tiene
 * una debilidad, esDebilA() / recibirDanio(cantidad, tipo).
 */
public abstract class Enemigo extends Personaje {

    private int recompensaXP;

    public Enemigo(String nombre, int vidaMax, int danioBase, int nivel, int recompensaXP) {
        super(nombre, vidaMax, danioBase, nivel);
        this.recompensaXP = recompensaXP;
    }

    /**
     * Comportamiento especial propio de cada enemigo (invocar refuerzos,
     * curarse, o no hacer nada). Recibe la Batalla como contexto para
     * poder interactuar con ella (por ejemplo, invocar refuerzos).
     */
    public abstract void habilidadEspecial(Batalla batalla);

    /** Por defecto ningun enemigo tiene debilidades. */
    public boolean esDebilA(TipoAtaque tipo) {
        return false;
    }

    /**
     * Sobrecarga de recibirDanio que tiene en cuenta el tipo de ataque
     * (por ejemplo, el Goblin recibe mas danio de ataques criticos).
     * Por defecto delega en el metodo simple heredado de Personaje.
     */
    public void recibirDanio(int cantidad, TipoAtaque tipo) {
        recibirDanio(cantidad);
    }

    @Override
    public int atacar(Personaje objetivo) {
        int danio = getDanioBase();
        objetivo.recibirDanio(danio);
        return danio;
    }

    public int getRecompensaXP() { return recompensaXP; }
}
