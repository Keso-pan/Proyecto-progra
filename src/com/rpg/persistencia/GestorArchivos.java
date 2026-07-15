package com.rpg.persistencia;

import com.rpg.model.Heroe;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Maneja la persistencia del progreso del jugador mediante serializacion
 * de objetos a ficheros locales (carpeta "guardado/").
 */
public class GestorArchivos {

    private static final String CARPETA = "guardado";
    private static final String ARCHIVO_HEROE = CARPETA + File.separator + "heroe.dat";

    public static void guardarHeroe(Heroe heroe) throws IOException {
        File carpeta = new File(CARPETA);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_HEROE))) {
            oos.writeObject(heroe);
        }
    }

    /** @return el Heroe guardado, o null si no existe ningun guardado previo. */
    public static Heroe cargarHeroe() throws IOException, ClassNotFoundException {
        File archivo = new File(ARCHIVO_HEROE);
        if (!archivo.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Heroe) ois.readObject();
        }
    }

    public static boolean existeGuardado() {
        return new File(ARCHIVO_HEROE).exists();
    }
}
