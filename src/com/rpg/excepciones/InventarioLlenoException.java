package com.rpg.excepciones;

/** Se lanza cuando se intenta agregar un item y el inventario ya alcanzo su maximo. */
public class InventarioLlenoException extends Exception {
    public InventarioLlenoException(String mensaje) {
        super(mensaje);
    }
}
