package com.rpg.model;

import com.rpg.combate.Batalla;
import com.rpg.excepciones.InventarioLlenoException;
import com.rpg.excepciones.ItemInvalidoException;
import com.rpg.items.Inventario;
import com.rpg.items.Item;

/**
 * Heroe hereda de Personaje (HERENCIA). Agrega experiencia e inventario,
 * y obliga a sus subclases (Guerrero, Arquero) a definir su propia
 * habilidad especial y su propio bono al subir de nivel (POLIMORFISMO).
 */
public abstract class Heroe extends Personaje {

    private int experiencia;
    private int experienciaParaSubir;
    private Inventario inventario;

    public Heroe(String nombre, int vidaMax, int danioBase, int nivel) {
        super(nombre, vidaMax, danioBase, nivel);
        this.experiencia = 0;
        this.experienciaParaSubir = 100;
        this.inventario = new Inventario();
    }

    public void ganarXP(int xp) {
        experiencia += xp;
        while (experiencia >= experienciaParaSubir) {
            experiencia -= experienciaParaSubir;
            subirNivel();
            experienciaParaSubir = (int) Math.round(experienciaParaSubir * 1.3);
        }
    }

    public void subirNivel() {
        setNivel(getNivel() + 1);
        setVidaMax(getVidaMax() + 10);
        setVida(getVidaMax());
        setDanioBase(getDanioBase() + 2);
        aplicarBonoDeNivel();
    }

    /** Cada subclase de Heroe decide que bono pasivo aplica al subir de nivel. */
    protected abstract void aplicarBonoDeNivel();

    /** Cada subclase decide en que consiste su habilidad especial al atacar. */
    public abstract void habilidadEspecial(Batalla batalla, Personaje objetivo);

    public void usarItem(int indice) throws ItemInvalidoException {
        inventario.usarItem(indice, this);
    }

    public boolean recogerItem(Item item) throws InventarioLlenoException {
        return inventario.agregarItem(item);
    }

    public Inventario getInventario() { return inventario; }
    public int getExperiencia() { return experiencia; }
    public int getExperienciaParaSubir() { return experienciaParaSubir; }
}
