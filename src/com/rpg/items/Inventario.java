package com.rpg.items;

import com.rpg.excepciones.InventarioLlenoException;
import com.rpg.excepciones.ItemInvalidoException;
import com.rpg.model.Heroe;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Inventario ♦──── Item  (COMPOSICION: el Item no tiene sentido fuera
 * del Inventario que lo contiene; si el Inventario se destruye, sus items
 * tambien dejan de existir como parte del sistema).
 */
public class Inventario implements Serializable {

    public static final int MAX_ITEMS = 5;

    private List<Item> items;

    public Inventario() {
        this.items = new ArrayList<>();
    }

    public boolean agregarItem(Item item) throws InventarioLlenoException {
        if (estaLleno()) {
            throw new InventarioLlenoException(
                    "El inventario ya tiene el maximo de " + MAX_ITEMS + " objetos.");
        }
        return items.add(item);
    }

    public Item eliminarItem(int indice) throws ItemInvalidoException {
        validarIndice(indice);
        return items.remove(indice);
    }

    /** Aplica el efecto del item sobre el heroe y lo consume del inventario. */
    public void usarItem(int indice, Heroe heroe) throws ItemInvalidoException {
        validarIndice(indice);
        Item item = items.get(indice);
        item.aplicar(heroe);
        items.remove(indice);
    }

    private void validarIndice(int indice) throws ItemInvalidoException {
        if (indice < 0 || indice >= items.size()) {
            throw new ItemInvalidoException("Indice de item invalido: " + indice);
        }
    }

    public boolean estaLleno() {
        return items.size() >= MAX_ITEMS;
    }

    public List<Item> getItems() {
        return items;
    }
}
