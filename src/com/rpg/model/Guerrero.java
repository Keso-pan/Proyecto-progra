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
    private static final double PORCENTAJE_SANGRADO = 0.10;
    private static final int DURACION_SANGRADO = 2;

    private final Random random = new Random();

    public Guerrero(String nombre) {
        super(nombre, 100, 12, 1);
    }

    @Override
    public int atacar(Personaje objetivo) {
        int danio = getDanioBase();
        TipoAtaque tipo = TipoAtaque.FISICO;
        boolean fueCritico = false;
        StringBuilder detalle = new StringBuilder();

        if (getNivel() >= NIVEL_CRITICO && random.nextDouble() < PROB_CRITICO) {
            danio = (int) Math.round(danio * MULTIPLICADOR_CRITICO);
            tipo = TipoAtaque.CRITICO;
            fueCritico = true;
            detalle.append("¡Golpe critico! (x").append(MULTIPLICADOR_CRITICO).append(" danio) ");
        }

        aplicarDanioSegunTipo(objetivo, danio, tipo);

        if (getNivel() >= NIVEL_SANGRADO && random.nextDouble() < PROB_SANGRADO) {
            detalle.append(aplicarSangrado(objetivo, fueCritico));
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
     * Aplica sangrado al objetivo: 10% del danio base del Guerrero por stack.
     * Si ya tiene sangrado activo, solo se le suma un stack (hasta 3, o sea
     * hasta 30%) cuando el golpe que lo provoco fue critico; si no fue
     * critico, el efecto simplemente se mantiene como estaba.
     *
     * @return descripcion de lo ocurrido, para el registro de eventos de la batalla.
     */
    private String aplicarSangrado(Personaje objetivo, boolean fueCritico) {
        int danioStack = (int) Math.round(getDanioBase() * PORCENTAJE_SANGRADO);
        EfectoEstado sangradoExistente = objetivo.buscarEfecto(EfectoEstado.Tipo.SANGRADO);
        if (sangradoExistente != null) {
            if (fueCritico) {
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
