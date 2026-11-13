package com.ahorcado;

import java.util.Locale;

/** Niveles de dificultad y su número de intentos (US-06). */
public enum Dificultad {
    FACIL("Fácil", 10, 1),
    MEDIA("Media", 7, 2),
    DIFICIL("Difícil", 5, 3);

    private final String nombre;
    private final int intentos;
    private final int multiplicador;

    Dificultad(String nombre, int intentos, int multiplicador) {
        this.nombre = nombre;
        this.intentos = intentos;
        this.multiplicador = multiplicador;
    }

    public String getNombre() {
        return nombre;
    }

    public int getIntentos() {
        return intentos;
    }

    public int getMultiplicador() {
        return multiplicador;
    }

    /**
     * Convierte un texto (FACIL, fácil, media, Difícil...) en una dificultad.
     *
     * @throws IllegalArgumentException si el texto no corresponde a ninguna dificultad
     */
    public static Dificultad desdeTexto(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("Dificultad vacía");
        }
        String limpio = Normalizador.normalizar(texto.trim()).toUpperCase(Locale.ROOT);
        for (Dificultad d : values()) {
            if (d.name().equals(limpio)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Dificultad desconocida: " + texto);
    }
}
