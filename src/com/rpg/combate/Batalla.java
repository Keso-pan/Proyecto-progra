package com.rpg.combate;

import com.rpg.model.Enemigo;
import com.rpg.model.Heroe;
import com.rpg.model.ReyGoblin;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Controla toda la logica de un combate 1 vs 1 (con posibles refuerzos
 * invocados). Aqui ocurre: criticos, congelamiento, sangrado, triple
 * disparo, invocaciones, curaciones, muerte y reparto de experiencia.
 */
public class Batalla {

    private Heroe heroe;
    private Enemigo enemigoPrincipal;
    private Queue<Enemigo> refuerzos;
    private List<String> registroEventos;
    private boolean terminada;

    public Batalla(Heroe heroe, Enemigo enemigo) {
        this.heroe = heroe;
        this.enemigoPrincipal = enemigo;
        this.refuerzos = new LinkedList<>();
        this.registroEventos = new ArrayList<>();
        this.terminada = false;
    }

    public void iniciar() {
        registrarEvento("Comienza la batalla: " + heroe.getNombre() +
                " (Nv." + heroe.getNivel() + ") vs " + enemigoPrincipal.getNombre() +
                " (Nv." + enemigoPrincipal.getNivel() + ")");
    }

    public void registrarEvento(String evento) {
        registroEventos.add(evento);
    }

    /** Usado por ReyGoblin.habilidadEspecial() para sumar refuerzos a la cola de ataque. */
    public void invocarRefuerzos(List<Enemigo> nuevos) {
        refuerzos.addAll(nuevos);
        registrarEvento(enemigoPrincipal.getNombre() + " invoco refuerzos!");
    }

    public void turnoHeroe() {
        if (terminada) return;
        if (!heroe.puedeActuar()) {
            registrarEvento(heroe.getNombre() + " esta congelado y no puede actuar!");
        } else {
            int danio = heroe.atacar(enemigoPrincipal);
            registrarEvento(heroe.getNombre() + " ataca a " + enemigoPrincipal.getNombre() +
                    " por " + danio + " de danio. (HP restante: " + enemigoPrincipal.getVida() + ")");

            // Muestra si se activo alguna habilidad (critico, sangrado,
            // disparo triple, flecha de hielo) durante ese ataque.
            String detalle = heroe.getUltimoDetalleAtaque();
            if (detalle != null && !detalle.isEmpty()) {
                registrarEvento(detalle);
            }
        }
        heroe.aplicarEfectos();
    }

    public void turnoEnemigo() {
        if (terminada || !enemigoPrincipal.estaVivo()) return;

        int vecesActuar = 1;
        if (enemigoPrincipal instanceof ReyGoblin && ((ReyGoblin) enemigoPrincipal).actuaDosVeces()) {
            vecesActuar = 2;
        }

        for (int i = 0; i < vecesActuar && enemigoPrincipal.estaVivo() && heroe.estaVivo(); i++) {
            if (!enemigoPrincipal.puedeActuar()) {
                registrarEvento(enemigoPrincipal.getNombre() + " esta congelado y no puede actuar!");
                continue;
            }
            int danio = enemigoPrincipal.atacar(heroe);
            registrarEvento(enemigoPrincipal.getNombre() + " ataca a " + heroe.getNombre() +
                    " por " + danio + " de danio. (HP restante: " + heroe.getVida() + ")");
        }

        if (enemigoPrincipal.estaVivo()) {
            enemigoPrincipal.habilidadEspecial(this);
        }

        while (!refuerzos.isEmpty() && heroe.estaVivo()) {
            Enemigo refuerzo = refuerzos.poll();
            int danio = refuerzo.atacar(heroe);
            registrarEvento(refuerzo.getNombre() + " (refuerzo) ataca por " + danio +
                    " de danio. (HP restante: " + heroe.getVida() + ")");
        }

        enemigoPrincipal.aplicarEfectos();
    }

    /** @return "HEROE", "ENEMIGO" o null si la batalla continua. */
    public String verificarGanador() {
        if (!heroe.estaVivo()) {
            terminada = true;
            return "ENEMIGO";
        }
        if (!enemigoPrincipal.estaVivo()) {
            terminada = true;
            heroe.ganarXP(enemigoPrincipal.getRecompensaXP());
            registrarEvento(heroe.getNombre() + " gano " + enemigoPrincipal.getRecompensaXP() + " puntos de experiencia.");
            return "HEROE";
        }
        return null;
    }

    /** Ejecuta un turno completo (heroe + enemigo) y evalua si la batalla termino. */
    public void ejecutarTurnoCompleto() {
        if (terminada) return;
        turnoHeroe();
        if (verificarGanador() != null) return;
        turnoEnemigo();
        verificarGanador();
    }

    public boolean estaTerminada() { return terminada; }
    public Heroe getHeroe() { return heroe; }
    public Enemigo getEnemigoPrincipal() { return enemigoPrincipal; }
    public List<String> getRegistroEventos() { return registroEventos; }
}
