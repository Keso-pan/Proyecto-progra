package com.rpg.gui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class ReproductorAudio {
    private Clip clipMusica;

    // Reproduce la musica de fondo en bucle
    public void reproducirEnBucle(String archivoWav) {
        try {
            URL url = getClass().getResource("/" + archivoWav);
            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                clipMusica = AudioSystem.getClip();
                clipMusica.open(audioIn);
                clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
                clipMusica.start();
            }
        } catch (Exception e) {
            System.err.println("Error música: " + e.getMessage());
        }
    }

    public void detener() {
        if (clipMusica != null && clipMusica.isRunning()) {
            clipMusica.stop();
            clipMusica.close();
        }
    }


    public void reproducirEfecto(String archivoWav) {
        try {
            URL url = getClass().getResource("/" + archivoWav);
            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clipEfecto = AudioSystem.getClip();
                clipEfecto.open(audioIn);
                clipEfecto.start();
            }
        } catch (Exception e) {
            System.err.println("Error efecto: " + archivoWav);
        }
    }
}