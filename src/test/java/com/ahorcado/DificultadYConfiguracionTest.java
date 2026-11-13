package com.ahorcado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** US-06 (dificultad), US-08 (normalizador) y US-11 (semilla). */
class DificultadYConfiguracionTest {

    @Test
    @DisplayName("US-06: Fácil 10, Media 7, Difícil 5 intentos")
    void intentosPorDificultad() {
        assertEquals(10, Dificultad.FACIL.getIntentos());
        assertEquals(7, Dificultad.MEDIA.getIntentos());
        assertEquals(5, Dificultad.DIFICIL.getIntentos());
        assertEquals("Difícil", Dificultad.DIFICIL.getNombre());
    }

    @Test
    void dificultadDesdeTexto() {
        assertEquals(Dificultad.FACIL, Dificultad.desdeTexto("facil"));
        assertEquals(Dificultad.FACIL, Dificultad.desdeTexto(" Fácil "));
        assertEquals(Dificultad.DIFICIL, Dificultad.desdeTexto("DIFÍCIL"));
        assertEquals(Dificultad.MEDIA, Dificultad.desdeTexto("media"));
        assertThrows(IllegalArgumentException.class, () -> Dificultad.desdeTexto("extrema"));
        assertThrows(IllegalArgumentException.class, () -> Dificultad.desdeTexto(null));
    }

    @Test
    void configuracionPorDefecto() {
        Configuracion c = new Configuracion();
        assertEquals(Dificultad.MEDIA, c.getDificultad());
        assertTrue(c.isNormalizarAcentos());
        assertNull(c.getSemilla());
    }

    @Test
    void configuracionCambiaValores() {
        Configuracion c = new Configuracion();
        c.setDificultad(Dificultad.DIFICIL);
        c.setNormalizarAcentos(false);
        c.setSemilla(42L);
        assertEquals(Dificultad.DIFICIL, c.getDificultad());
        assertFalse(c.isNormalizarAcentos());
        assertEquals(Long.valueOf(42L), c.getSemilla());
        assertThrows(IllegalArgumentException.class, () -> c.setDificultad(null));
    }

    @Test
    @DisplayName("US-11: con la misma semilla, los números aleatorios son los mismos")
    void semillaEsDeterminista() {
        Configuracion c = new Configuracion();
        c.setSemilla(123L);
        Random a = c.crearRandom();
        Random b = c.crearRandom();
        for (int i = 0; i < 20; i++) {
            assertEquals(a.nextInt(1000), b.nextInt(1000));
        }
    }

    @Test
    @DisplayName("US-08: el normalizador quita acentos pero conserva la Ñ")
    void normalizador() {
        assertEquals('A', Normalizador.normalizar('Á'));
        assertEquals('u', Normalizador.normalizar('ü'));
        assertEquals('Ñ', Normalizador.normalizar('Ñ'));
        assertEquals('B', Normalizador.normalizar('B'));
        assertEquals("CANCION", Normalizador.normalizar("CANCIÓN"));
        assertEquals("murcielago", Normalizador.normalizar("murciélago"));
    }
}
