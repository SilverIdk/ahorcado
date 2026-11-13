package com.ahorcado;

/**
 * Resultado de una partida para la tabla de puntuaciones (US-09).
 *
 * @param nombre         nombre del jugador
 * @param palabra        palabra jugada
 * @param dificultad     dificultad de la partida
 * @param intentosUsados fallos cometidos
 * @param gano           si el jugador adivinó la palabra
 */
public record Puntuacion(String nombre, String palabra, Dificultad dificultad, int intentosUsados, boolean gano) {

    /** Valida y limpia los datos (el ';' se reserva como separador del CSV). */
    public Puntuacion {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (dificultad == null) {
            throw new IllegalArgumentException("La dificultad no puede ser nula");
        }
        if (intentosUsados < 0) {
            throw new IllegalArgumentException("Los intentos usados no pueden ser negativos");
        }
        nombre = nombre.replace(";", " ").strip();
    }

    /**
     * Puntos de la partida: 0 si perdió; si ganó, (intentos restantes + 1) × 10 × multiplicador
     * de dificultad (Fácil ×1, Media ×2, Difícil ×3).
     */
    public int puntos() {
        if (!gano) {
            return 0;
        }
        int restantes = Math.max(0, dificultad.getIntentos() - intentosUsados);
        return (restantes + 1) * 10 * dificultad.getMultiplicador();
    }

    /** Representación en una línea CSV separada por ';'. */
    public String aCsv() {
        return String.join(";", nombre, palabra, dificultad.name(), String.valueOf(intentosUsados),
                String.valueOf(gano));
    }

    /**
     * Lee una línea CSV.
     *
     * @throws IllegalArgumentException si la línea tiene un formato incorrecto
     */
    public static Puntuacion desdeCsv(String linea) {
        String[] p = linea.split(";");
        if (p.length != 5) {
            throw new IllegalArgumentException("Línea de puntuación inválida: " + linea);
        }
        try {
            return new Puntuacion(p[0], p[1], Dificultad.valueOf(p[2]), Integer.parseInt(p[3]),
                    Boolean.parseBoolean(p[4]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Línea de puntuación inválida: " + linea, e);
        }
    }
}
