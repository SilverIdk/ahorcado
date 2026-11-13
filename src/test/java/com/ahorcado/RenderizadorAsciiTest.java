package com.ahorcado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** US-05: dibujo ASCII progresivo. */
class RenderizadorAsciiTest {

    @Test
    void sinFallosLaHorcaEstaVacia() {
        String dibujo = RenderizadorAscii.dibujar(0, 7);
        assertFalse(dibujo.contains("O"));
        assertEquals(0, RenderizadorAscii.etapa(0, 7));
    }

    @Test
    @DisplayName("US-05: al agotar los intentos el dibujo está completo en todas las dificultades")
    void completoAlAgotarIntentos() {
        int ultima = RenderizadorAscii.totalEtapas() - 1;
        for (Dificultad d : Dificultad.values()) {
            assertEquals(ultima, RenderizadorAscii.etapa(d.getIntentos(), d.getIntentos()));
        }
        assertTrue(RenderizadorAscii.dibujar(5, 5).contains("/ \\"));
    }

    @Test
    @DisplayName("US-05: el dibujo avanza (nunca retrocede) con cada fallo")
    void avanzaConCadaFallo() {
        for (Dificultad d : Dificultad.values()) {
            int anterior = 0;
            for (int fallos = 1; fallos <= d.getIntentos(); fallos++) {
                int actual = RenderizadorAscii.etapa(fallos, d.getIntentos());
                assertTrue(actual >= anterior);
                assertTrue(actual > 0);
                anterior = actual;
            }
        }
    }

    @Test
    void primerFalloCambiaElDibujo() {
        assertNotEquals(RenderizadorAscii.dibujar(0, 10), RenderizadorAscii.dibujar(1, 10));
    }

    @Test
    void valoresFueraDeRango() {
        assertEquals(0, RenderizadorAscii.etapa(-3, 5));
        assertEquals(0, RenderizadorAscii.etapa(2, 0));
        assertEquals(RenderizadorAscii.totalEtapas() - 1, RenderizadorAscii.etapa(99, 5));
    }
}
