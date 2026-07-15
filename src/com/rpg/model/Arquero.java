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
    private static final double PORCENTAJE_SANGRADO = 0.10;
    private static final int DURACION_SANGRADO = 2;

    private final Random random = new Random();

    public Arquero(String nombre) {
        super(nombre, 85, 14, 1);
    }

    @Override
    public int atacar(Personaje objetivo) {
        int danio = getDanioBase();
        TipoAtaque tipo = TipoAtaque.FISICO;
        boolean fueTriple = false;
        StringBuilder detalle = new StringBuilder();

        // Triple disparo: sobrecarga conceptual del ataque normal (mismo
        // metodo, distinto resultado segun probabilidad / nivel).
        if (getNivel() >= NIVEL_TRIPLE && random.nextDouble() < PROB_TRIPLE) {
            danio = danio * 3;
            fueTriple = true;
            detalle.append("¡Disparo triple! (x3 danio) ");
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
                detalle.append("Flecha de hielo: ").append(objetivo.getNombre()).append(" queda congelado. ");
            }
        }

        aplicarDanioSegunTipo(objetivo, danio, tipo);

        if (getNivel() >= NIVEL_SANGRADO && random.nextDouble() < PROB_SANGRADO) {
            detalle.append(aplicarSangrado(objetivo, fueTriple));
        }

        setUltimoDetalleAtaque(detalle.toString().trim());
        return danio;
    }

    private void aplicarDanioSegunTipo(Personaje objetivo, int danio, TipoAtaque tipo) {
        if (objetivo instanceof Enemigo) {
            ((Enemigo) objetivo).recibirDanio(danio, tipo);
        } else {
            objetivo.recibirDanio(danio);
        }
    }

    /**
     * Aplica sangrado al objetivo: 10% del danio base del Arquero por stack.
     * Si ya tiene sangrado activo, solo se le suma un stack (hasta 3, o sea
     * hasta 30%) cuando el disparo que lo provoco fue un disparo triple; si
     * no fue triple, el efecto simplemente se mantiene como estaba.
     *
     * @return descripcion de lo ocurrido, para el registro de eventos de la batalla.
     */
    private String aplicarSangrado(Personaje objetivo, boolean fueTriple) {
        int danioStack = (int) Math.round(getDanioBase() * PORCENTAJE_SANGRADO);
        EfectoEstado sangradoExistente = objetivo.buscarEfecto(EfectoEstado.Tipo.SANGRADO);
        if (sangradoExistente != null) {
            if (fueTriple) {
                sangradoExistente.agregarStack(DURACION_SANGRADO);
                return "Sangrado se acumula a x" + sangradoExistente.getStacks() +
                        " (" + (sangradoExistente.getStacks() * 10) + "% de danio por turno).";
            }
            return "";
        } else {
            objetivo.agregarEfecto(new EfectoEstado(EfectoEstado.Tipo.SANGRADO, DURACION_SANGRADO, danioStack));
            return "Sangrado aplicado (10% de danio por turno).";
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
