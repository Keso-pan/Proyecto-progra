package com.rpg.model;

import com.rpg.combate.Batalla;
import com.rpg.combate.TipoAtaque;

/** El goblin no tiene resistencia critica: recibe 50% de danio extra de golpes criticos. */
public class Goblin extends Enemigo {

    private static final double MULTIPLICADOR_DEBILIDAD = 1.5;

    public Goblin(int nivel) {
        super("Goblin", 40 + nivel * 5, 8 + nivel, nivel, 20 + nivel * 5);
    }

    @Override
    public boolean esDebilA(TipoAtaque tipo) {
        return tipo == TipoAtaque.CRITICO;
    }

    @Override
    public void recibirDanio(int cantidad, TipoAtaque tipo) {
        if (tipo == TipoAtaque.CRITICO) {
            cantidad = (int) Math.round(cantidad * MULTIPLICADOR_DEBILIDAD);
        }
        recibirDanio(cantidad);
    }

    @Override
    public void habilidadEspecial(Batalla batalla) {
        // El goblin comun no tiene habilidad especial.
    }
}
