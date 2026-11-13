package com.ahorcado;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** US-10: ayuda con reglas, comandos y ejemplo. */
class AyudaTest {

    @Test
    @DisplayName("US-10: la ayuda incluye reglas, comandos y un ejemplo")
    void contenidoDeLaAyuda() {
        String texto = Ayuda.texto();
        assertTrue(texto.contains("REGLAS"));
        assertTrue(texto.contains("COMANDOS"));
        assertTrue(texto.contains("EJEMPLO"));
        assertTrue(texto.contains("Fácil 10, Media 7, Difícil 5"));
    }
}
