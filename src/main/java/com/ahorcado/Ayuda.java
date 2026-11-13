package com.ahorcado;

/** Texto de ayuda: reglas, comandos y ejemplo de partida (US-10). */
public final class Ayuda {

    private Ayuda() {
    }

    /** Devuelve el texto completo de la ayuda. */
    public static String texto() {
        return String.join(System.lineSeparator(),
                "=== AYUDA ===",
                "REGLAS",
                "  - El juego elige una palabra secreta y la muestra como guiones: _ _ _ _",
                "  - En cada turno escribes UNA letra (A-Z, Ñ; con o sin acento) y presionas Enter.",
                "  - Si la letra está en la palabra, se revela en todas sus posiciones.",
                "  - Si no está, pierdes un intento y el dibujo del ahorcado avanza.",
                "  - Ganas si descubres la palabra; pierdes si te quedas sin intentos.",
                "  - Las letras repetidas o inválidas NO restan intentos.",
                "  - Intentos por dificultad: Fácil 10, Media 7, Difícil 5.",
                "",
                "COMANDOS DURANTE LA PARTIDA",
                "  <letra>   intenta una letra",
                "  ?         muestra las letras ya usadas",
                "  !         abandona la partida (cuenta como derrota)",
                "",
                "EJEMPLO (palabra GATO, dificultad Media)",
                "  _ _ _ _   intentos: 7   > A  -> ¡Acierto!   _ A _ _",
                "  _ A _ _   intentos: 7   > E  -> Fallo       intentos: 6",
                "  _ A _ _   intentos: 6   > G  -> ¡Acierto!   G A _ _",
                "  G A _ _   intentos: 6   > T  -> ¡Acierto!   G A T _",
                "  G A T _   intentos: 6   > O  -> ¡Ganaste!   G A T O");
    }
}
