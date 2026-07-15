package com.rpg.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Catalogo de los 10 items disponibles en el juego. */
public class ItemCatalogo {

    private static final List<Item> CATALOGO = new ArrayList<>();
    private static final Random RANDOM = new Random();

    static {
        CATALOGO.add(new Item("Pocion pequena", "Restaura 20 puntos de vida", TipoItem.CURAR, 20));
        CATALOGO.add(new Item("Pocion mediana", "Restaura 40 puntos de vida", TipoItem.CURAR, 40));
        CATALOGO.add(new Item("Pocion grande", "Restaura 70 puntos de vida", TipoItem.CURAR, 70));
        CATALOGO.add(new Item("Elixir mayor", "Restaura 100 puntos de vida", TipoItem.CURAR, 100));
        CATALOGO.add(new Item("Daga afilada", "Aumenta el danio base en 4", TipoItem.ATAQUE, 4));
        CATALOGO.add(new Item("Espada de acero", "Aumenta el danio base en 8", TipoItem.ATAQUE, 8));
        CATALOGO.add(new Item("Amuleto de poder", "Aumenta el danio base en 12", TipoItem.ATAQUE, 12));
        CATALOGO.add(new Item("Escudo de hierro", "Aumenta la vida maxima en 25", TipoItem.VIDA_MAXIMA, 25));
        CATALOGO.add(new Item("Armadura pesada", "Aumenta la vida maxima en 40", TipoItem.VIDA_MAXIMA, 40));
        CATALOGO.add(new Item("Corazon de dragon", "Aumenta la vida maxima en 60", TipoItem.VIDA_MAXIMA, 60));
    }

    public static List<Item> getCatalogoCompleto() {
        return CATALOGO;
    }

    /** Devuelve una copia nueva de un item aleatorio del catalogo. */
    public static Item obtenerItemAleatorio() {
        Item original = CATALOGO.get(RANDOM.nextInt(CATALOGO.size()));
        return new Item(original.getNombre(), original.getDescripcion(), original.getTipo(), original.getValor());
    }
}
