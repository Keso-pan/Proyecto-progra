package com.rpg.model;

import com.rpg.combate.Batalla;
import com.rpg.combate.EfectoEstado;
import com.rpg.combate.TipoAtaque;
import java.util.Random;

/**
 * Arquero: nivel 5 desbloquea probabilidad de sangrado, nivel 10 desbloquea
 * flecha de hielo (probabilidad de congelar 1 turno, doblada contra
 * enemigos debiles al hielo), nivel 15 desbloquea probabilidad de disparo
 * triple (x3 danio base).
 */
public class Arquero extends Heroe {

    private static final int NIVEL_SANGRADO = 5;
    private static final int NIVEL_HIELO = 10;
    private static final int NIVEL_TRIPLE = 15;

    private static final double PROB_SANGRADO = 0.25;
    private static final double PROB_CONGELAR = 0.20;
    private static final double PROB_TRIPLE = 0.15;

    private final Random random = new Random();

    public Arquero(String nombre) {
        super(nombre, 85, 14, 1);
    }

    @Override
    public int atacar(Personaje objetivo) {
        int danio = getDanioBase();
        TipoAtaque tipo = TipoAtaque.FISICO;

        // Triple disparo: sobrecarga conceptual del ataque normal (mismo
        // metodo, distinto resultado segun probabilidad / nivel).
        if (getNivel() >= NIVEL_TRIPLE && random.nextDouble() < PROB_TRIPLE) {
            danio = danio * 3;
        }

        // Flecha de hielo: probabilidad de congelar, duplicada si el
        // enemigo es debil al hielo (Esqueleto).
        if (getNivel() >= NIVEL_HIELO) {
            double probCongelar = PROB_CONGELAR;
            if (objetivo instanceof Enemigo && ((Enemigo) objetivo).esDebilA(TipoAtaque.HIELO)) {
                probCongelar *= 2;
            }
            if (random.nextDouble() < probCongelar) {
                tipo = TipoAtaque.HIELO;
                objetivo.agregarEfecto(new EfectoEstado(EfectoEstado.Tipo.CONGELADO, 1, 0));
            }
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
        // El arquero no tiene bono pasivo fijo; todas sus mejoras son
        // probabilidades de habilidad que ya se evaluan en atacar().
    }

    @Override
    public void habilidadEspecial(Batalla batalla, Personaje objetivo) {
        atacar(objetivo);
    }
}
