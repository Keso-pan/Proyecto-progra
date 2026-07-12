package com.rpg.model;

import com.rpg.combate.Batalla;
import com.rpg.combate.EfectoEstado;
import com.rpg.combate.TipoAtaque;
import java.util.Random;

/**
 * Guerrero: nivel 5 desbloquea probabilidad de critico, nivel 10 aumenta
 * vida (+20%) y danio base (+10%) de forma pasiva, nivel 15 desbloquea
 * probabilidad de sangrado.
 */
public class Guerrero extends Heroe {

    private static final int NIVEL_CRITICO = 5;
    private static final int NIVEL_BONO_PASIVO = 10;
    private static final int NIVEL_SANGRADO = 15;

    private static final double PROB_CRITICO = 0.30;
    private static final double MULTIPLICADOR_CRITICO = 2.0;
    private static final double PROB_SANGRADO = 0.25;

    private final Random random = new Random();

    public Guerrero(String nombre) {
        super(nombre, 100, 12, 1);
    }

    @Override
    public int atacar(Personaje objetivo) {
        int danio = getDanioBase();
        TipoAtaque tipo = TipoAtaque.FISICO;

        if (getNivel() >= NIVEL_CRITICO && random.nextDouble() < PROB_CRITICO) {
            danio = (int) Math.round(danio * MULTIPLICADOR_CRITICO);
            tipo = TipoAtaque.CRITICO;
        }

        aplicarDanioSegunTipo(objetivo, danio, tipo);

        if (getNivel() >= NIVEL_SANGRADO && random.nextDouble() < PROB_SANGRADO) {
            objetivo.agregarEfecto(new EfectoEstado(EfectoEstado.Tipo.SANGRADO, 2, 2));
        }

        return danio;
    }

    private void aplicarDanioSegunTipo(Personaje objetivo, int danio, TipoAtaque tipo) {
        if (objetivo instanceof Enemigo) {
            ((Enemigo) objetivo).recibirDanio(danio, tipo);
        } else {
            objetivo.recibirDanio(danio);
        }
    }

    @Override
    protected void aplicarBonoDeNivel() {
        if (getNivel() == NIVEL_BONO_PASIVO) {
            setVidaMax((int) Math.round(getVidaMax() * 1.20));
            setVida(getVidaMax());
            setDanioBase((int) Math.round(getDanioBase() * 1.10));
        }
    }

    @Override
    public void habilidadEspecial(Batalla batalla, Personaje objetivo) {
        // El guerrero no tiene una habilidad activable aparte: sus bonos
        // (critico, +vida/+danio, sangrado) son pasivos segun el nivel
        // y ya se aplican dentro de atacar().
        atacar(objetivo);
    }
}
