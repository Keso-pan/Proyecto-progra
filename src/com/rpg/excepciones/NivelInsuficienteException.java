package com.rpg.excepciones;

/** Se lanza cuando se intenta usar una habilidad que aun no fue desbloqueada por nivel. */
public class NivelInsuficienteException extends Exception {
    public NivelInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
