package com.rpg.model;

import com.rpg.combate.Batalla;
import com.rpg.combate.TipoAtaque;

/** El esqueleto es debil al hielo: aumenta la probabilidad de ser congelado. */
public class Esqueleto extends Enemigo {

    public Esqueleto(int nivel) {
        super("Esqueleto", 35 + nivel * 5, 9 + nivel, nivel, 25 + nivel * 5);
    }

    @Override
    public boolean esDebilA(TipoAtaque tipo) {
        return tipo == TipoAtaque.HIELO;
    }

    @Override
    public void habilidadEspecial(Batalla batalla) {
        // No tiene habilidad especial propia.
    }
}
