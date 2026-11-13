package com.ahorcado;

/** Dibujo ASCII progresivo del ahorcado según los fallos (US-05). */
public final class RenderizadorAscii {

    private static final String[] ETAPAS = {
        "  +---+\n  |   |\n      |\n      |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n      |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n  |   |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n /|   |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n /|\\  |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n /|\\  |\n /    |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n /|\\  |\n / \\  |\n      |\n=========",
    };

    private RenderizadorAscii() {
    }

    /** Número de etapas del dibujo (de 0 = horca vacía a 6 = ahorcado completo). */
    public static int totalEtapas() {
        return ETAPAS.length;
    }

    /**
     * Calcula la etapa del dibujo de forma proporcional, para que con cualquier
     * dificultad el dibujo quede completo justo al agotar los intentos.
     */
    public static int etapa(int fallos, int intentosMaximos) {
        if (intentosMaximos <= 0 || fallos <= 0) {
            return 0;
        }
        int ultima = ETAPAS.length - 1;
        int f = Math.min(fallos, intentosMaximos);
        return (f * ultima + intentosMaximos - 1) / intentosMaximos;
    }

    /** Devuelve el dibujo correspondiente a los fallos actuales. */
    public static String dibujar(int fallos, int intentosMaximos) {
        return ETAPAS[etapa(fallos, intentosMaximos)];
    }
}
