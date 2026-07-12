package com.rpg.items;

import com.rpg.model.Heroe;
import java.io.Serializable;

/**
 * En lugar de crear una clase por cada objeto (Pocion, Espada, Escudo...)
 * todos los objetos son instancias de Item, diferenciados por su TipoItem.
 * Esto respeta el principio Abierto/Cerrado (SOLID): para agregar un item
 * nuevo no se modifica codigo, solo se agrega una instancia al catalogo.
 */
public class Item implements Serializable {

    private String nombre;
    private String descripcion;
    private TipoItem tipo;
    private int valor;

    public Item(String nombre, String descripcion, TipoItem tipo, int valor) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.valor = valor;
    }

    /** Aplica el efecto del item sobre el heroe que lo usa. */
    public void aplicar(Heroe heroe) {
        switch (tipo) {
            case CURAR:
                heroe.curar(valor);
                break;
            case ATAQUE:
                heroe.setDanioBase(heroe.getDanioBase() + valor);
                break;
            case VIDA_MAXIMA:
                heroe.setVidaMax(heroe.getVidaMax() + valor);
                heroe.curar(valor);
                break;
        }
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public TipoItem getTipo() { return tipo; }
    public int getValor() { return valor; }

    @Override
    public String toString() {
        return nombre + " [" + tipo + " +" + valor + "] - " + descripcion;
    }
}
