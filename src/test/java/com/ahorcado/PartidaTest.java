package com.ahorcado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Reglas de negocio: US-02, US-03, US-04 y US-08. */
class PartidaTest {

    // ---------- US-02: inicio de partida

    @Test
    @DisplayName("US-02: al iniciar se muestra un guion por letra y cero aciertos")
    void inicioMuestraGuionesYCeroAciertos() {
        Partida p = new Partida("gato", 7, true);
        assertEquals("_ _ _ _", p.getProgreso());
        assertEquals(0, p.getAciertos());
        assertEquals(7, p.getIntentosRestantes());
        assertEquals(EstadoPartida.EN_CURSO, p.getEstado());
        assertEquals("GATO", p.getPalabra());
    }

    @Test
    void constructorConDificultadUsaSusIntentos() {
        assertEquals(10, new Partida("sol", Dificultad.FACIL, true).getIntentosMaximos());
        assertEquals(7, new Partida("sol", Dificultad.MEDIA, true).getIntentosMaximos());
        assertEquals(5, new Partida("sol", Dificultad.DIFICIL, true).getIntentosMaximos());
    }

    @Test
    void palabraVaciaONulaEsInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new Partida("", 5, true));
        assertThrows(IllegalArgumentException.class, () -> new Partida("   ", 5, true));
        assertThrows(IllegalArgumentException.class, () -> new Partida(null, 5, true));
    }

    @Test
    void palabraConSimbolosEsInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new Partida("ga7o", 5, true));
        assertThrows(IllegalArgumentException.class, () -> new Partida("ga to", 5, true));
    }

    @Test
    void intentosDebenSerPositivos() {
        assertThrows(IllegalArgumentException.class, () -> new Partida("gato", 0, true));
        assertThrows(IllegalArgumentException.class, () -> new Partida("gato", -1, true));
    }

    // ---------- US-03: introducir letras

    @Test
    @DisplayName("US-03: una letra correcta se revela en todas sus posiciones")
    void aciertoRevelaTodasLasPosiciones() {
        Partida p = new Partida("casa", 7, true);
        assertEquals(ResultadoIntento.ACIERTO, p.intentar("a"));
        assertEquals("_ A _ A", p.getProgreso());
        assertEquals(1, p.getAciertos());
        assertEquals(7, p.getIntentosRestantes());
    }

    @Test
    @DisplayName("US-03: una letra incorrecta resta un intento")
    void falloRestaUnIntento() {
        Partida p = new Partida("casa", 7, true);
        assertEquals(ResultadoIntento.FALLO, p.intentar("z"));
        assertEquals(6, p.getIntentosRestantes());
        assertEquals(1, p.getFallos());
        assertEquals("_ _ _ _", p.getProgreso());
    }

    @Test
    @DisplayName("US-03: letras repetidas no restan intentos")
    void letraRepetidaNoRestaIntento() {
        Partida p = new Partida("casa", 7, true);
        p.intentar("z");
        assertEquals(ResultadoIntento.REPETIDA, p.intentar("z"));
        assertEquals(ResultadoIntento.REPETIDA, p.intentar("Z"));
        assertEquals(6, p.getIntentosRestantes());
        p.intentar("c");
        assertEquals(ResultadoIntento.REPETIDA, p.intentar("C"));
    }

    @Test
    @DisplayName("US-03: entradas no válidas no restan intentos")
    void entradasInvalidas() {
        Partida p = new Partida("casa", 7, true);
        assertEquals(ResultadoIntento.INVALIDA, p.intentar("1"));
        assertEquals(ResultadoIntento.INVALIDA, p.intentar(""));
        assertEquals(ResultadoIntento.INVALIDA, p.intentar("ab"));
        assertEquals(ResultadoIntento.INVALIDA, p.intentar("#"));
        assertEquals(ResultadoIntento.INVALIDA, p.intentar(null));
        assertEquals(7, p.getIntentosRestantes());
        assertTrue(p.getLetrasUsadas().isEmpty());
    }

    @Test
    void ignoraEspaciosAlrededorYMinusculas() {
        Partida p = new Partida("casa", 7, true);
        assertEquals(ResultadoIntento.ACIERTO, p.intentar("  c "));
        assertEquals("C _ _ _", p.getProgreso());
    }

    @Test
    void letrasUsadasEnOrden() {
        Partida p = new Partida("casa", 7, true);
        p.intentar("x");
        p.intentar("a");
        p.intentar("c");
        assertEquals("[X, A, C]", p.getLetrasUsadas().toString());
    }

    @Test
    void aceptaEnie() {
        Partida p = new Partida("niño", 7, true);
        assertEquals(ResultadoIntento.ACIERTO, p.intentar("ñ"));
        assertEquals("_ _ Ñ _", p.getProgreso());
        assertEquals(ResultadoIntento.FALLO, new Partida("nino", 7, true).intentar("ñ"));
    }

    // ---------- US-04: fin de partida

    @Test
    @DisplayName("US-04: adivinar todas las letras gana la partida")
    void ganaAlAdivinarTodas() {
        Partida p = new Partida("sol", 5, true);
        p.intentar("s");
        p.intentar("o");
        assertEquals(EstadoPartida.EN_CURSO, p.getEstado());
        p.intentar("l");
        assertEquals(EstadoPartida.GANADA, p.getEstado());
        assertEquals("S O L", p.getProgreso());
    }

    @Test
    @DisplayName("US-04: agotar los intentos pierde la partida")
    void pierdeAlAgotarIntentos() {
        Partida p = new Partida("sol", 3, true);
        p.intentar("a");
        p.intentar("b");
        assertEquals(EstadoPartida.EN_CURSO, p.getEstado());
        p.intentar("c");
        assertEquals(EstadoPartida.PERDIDA, p.getEstado());
        assertEquals(0, p.getIntentosRestantes());
    }

    @Test
    void noSeAceptanLetrasDespuesDeTerminar() {
        Partida p = new Partida("sol", 1, true);
        p.intentar("x");
        assertEquals(ResultadoIntento.PARTIDA_TERMINADA, p.intentar("s"));
        assertEquals(1, p.getFallos());
    }

    // ---------- US-08: normalización de acentos

    @Test
    @DisplayName("US-08: con normalización, 'a' revela la 'Á' y viceversa")
    void normalizacionActivaHaceEquivalentes() {
        Partida p = new Partida("árbol", 7, true);
        assertEquals(ResultadoIntento.ACIERTO, p.intentar("a"));
        assertEquals("Á _ _ _ _", p.getProgreso());

        Partida q = new Partida("casa", 7, true);
        assertEquals(ResultadoIntento.ACIERTO, q.intentar("á"));
        assertEquals("_ A _ A", q.getProgreso());
        assertEquals(ResultadoIntento.REPETIDA, q.intentar("a"));
    }

    @Test
    @DisplayName("US-08: sin normalización, 'a' y 'á' son letras distintas")
    void normalizacionDesactivadaLasDistingue() {
        Partida p = new Partida("árbol", 7, false);
        assertEquals(ResultadoIntento.FALLO, p.intentar("a"));
        assertEquals(ResultadoIntento.ACIERTO, p.intentar("á"));
        assertEquals("Á _ _ _ _", p.getProgreso());
        assertFalse(p.getLetrasUsadas().isEmpty());
    }
}
