package com.ahorcado;

import java.util.Random;

/** Configuración del juego: dificultad, normalización de acentos y semilla (US-06, US-08, US-11). */
public final class Configuracion {

    private Dificultad dificultad = Dificultad.MEDIA;
    private boolean normalizarAcentos = true;
    private Long semilla;

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        if (dificultad == null) {
            throw new IllegalArgumentException("La dificultad no puede ser nula");
        }
        this.dificultad = dificultad;
    }

    public boolean isNormalizarAcentos() {
        return normalizarAcentos;
    }

    public void setNormalizarAcentos(boolean normalizarAcentos) {
        this.normalizarAcentos = normalizarAcentos;
    }

    /** Semilla para selección reproducible, o {@code null} si la selección es aleatoria. */
    public Long getSemilla() {
        return semilla;
    }

    public void setSemilla(Long semilla) {
        this.semilla = semilla;
    }

    /** Crea el generador de números aleatorios: determinista si hay semilla (US-11). */
    public Random crearRandom() {
        return semilla == null ? new Random() : new Random(semilla);
    }
}
