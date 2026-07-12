package com.rpg.model;

import com.rpg.combate.Batalla;

/** El mago no tiene debilidades: siempre recibe el danio base. */
public class Mago extends Enemigo {

    public Mago(int nivel) {
        super("Mago", 30 + nivel * 5, 11 + nivel, nivel, 30 + nivel * 5);
    }

    @Override
    public void habilidadEspecial(Batalla batalla) {
        // Sin debilidades y sin habilidad especial: danio siempre base.
    }
}
