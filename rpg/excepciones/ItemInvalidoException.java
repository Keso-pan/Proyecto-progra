package com.rpg.excepciones;

/** Se lanza al intentar usar o eliminar un item con un indice invalido o inexistente. */
public class ItemInvalidoException extends Exception {
    public ItemInvalidoException(String mensaje) {
        super(mensaje);
    }
}
