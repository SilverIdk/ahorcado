package com.ahorcado;

/** Resultado de intentar una letra (US-03). */
public enum ResultadoIntento {
    /** La letra está en la palabra. */
    ACIERTO,
    /** La letra no está en la palabra: se resta un intento. */
    FALLO,
    /** La letra ya se había intentado: no se resta intento. */
    REPETIDA,
    /** La entrada no es una sola letra válida: no se resta intento. */
    INVALIDA,
    /** La partida ya terminó. */
    PARTIDA_TERMINADA
}
