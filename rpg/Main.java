package com.rpg;

import com.rpg.combate.Batalla;
import com.rpg.excepciones.InventarioLlenoException;
import com.rpg.items.ItemCatalogo;
import com.rpg.juego.Juego;
import com.rpg.model.Enemigo;
import com.rpg.model.Guerrero;
import com.rpg.model.Heroe;

/** Prueba de consola: valida que toda la logica de combate funcione antes de construir la GUI. */
public class Main {

    public static void main(String[] args) {
        Heroe heroe = new Guerrero("Aldric");
        Juego juego = new Juego(heroe);

        try {
            heroe.recogerItem(ItemCatalogo.obtenerItemAleatorio());
        } catch (InventarioLlenoException e) {
            System.out.println("No se pudo agregar item: " + e.getMessage());
        }

        for (int ronda = 1; ronda <= 5 && heroe.estaVivo(); ronda++) {
            Enemigo enemigo = juego.generarEnemigo();
            System.out.println("\n=== Ronda " + ronda + ": " + enemigo.getNombre() +
                    " (nivel " + enemigo.getNivel() + ", HP " + enemigo.getVidaMax() + ") ===");

            Batalla batalla = new Batalla(heroe, enemigo);
            batalla.iniciar();

            while (!batalla.estaTerminada()) {
                batalla.ejecutarTurnoCompleto();
            }

            for (String evento : batalla.getRegistroEventos()) {
                System.out.println(evento);
            }

            if (!heroe.estaVivo()) {
                System.out.println("\n" + heroe.getNombre() + " ha sido derrotado. Fin de la partida.");
            } else {
                System.out.println("\n" + heroe.getNombre() + " gano! Nivel actual: " + heroe.getNivel() +
                        " | Vida: " + heroe.getVida() + "/" + heroe.getVidaMax() +
                        " | XP: " + heroe.getExperiencia() + "/" + heroe.getExperienciaParaSubir());
            }
        }
    }
}
