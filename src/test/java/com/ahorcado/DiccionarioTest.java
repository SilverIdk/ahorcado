package com.ahorcado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** US-07: diccionario por archivo y US-11: selección reproducible. */
class DiccionarioTest {

    @TempDir
    Path carpeta;

    private static final List<String> LINEAS = List.of(
            "# comentario",
            "",
            "FACIL;sol",
            "FACIL;gato",
            "MEDIA;ventana",
            "DIFICIL;murciélago");

    @Test
    void cargaLineasPorDificultad() {
        Diccionario d = Diccionario.desdeLineas(LINEAS);
        assertEquals(List.of("SOL", "GATO"), d.getPalabras(Dificultad.FACIL));
        assertEquals(List.of("VENTANA"), d.getPalabras(Dificultad.MEDIA));
        assertEquals(List.of("MURCIÉLAGO"), d.getPalabras(Dificultad.DIFICIL));
    }

    @Test
    @DisplayName("US-07: carga el diccionario desde un archivo .txt")
    void cargaDesdeArchivo() throws IOException {
        Path archivo = carpeta.resolve("palabras.txt");
        Files.write(archivo, LINEAS, StandardCharsets.UTF_8);
        Diccionario d = Diccionario.desdeArchivo(archivo);
        assertEquals(2, d.getPalabras(Dificultad.FACIL).size());
    }

    @Test
    void elDiccionarioPorDefectoTienePalabrasParaCadaDificultad() {
        Diccionario d = Diccionario.porDefecto();
        for (Dificultad dif : Dificultad.values()) {
            assertFalse(d.getPalabras(dif).isEmpty());
        }
    }

    @Test
    @DisplayName("US-07: la palabra seleccionada pertenece a la dificultad pedida")
    void seleccionaDeLaDificultadCorrecta() {
        Diccionario d = Diccionario.desdeLineas(LINEAS);
        Random r = new Random();
        for (int i = 0; i < 30; i++) {
            assertTrue(d.getPalabras(Dificultad.FACIL).contains(d.seleccionar(Dificultad.FACIL, r)));
        }
    }

    @Test
    @DisplayName("US-11: con la misma semilla se selecciona la misma secuencia de palabras")
    void semillaHaceSeleccionDeterminista() {
        Diccionario d = Diccionario.porDefecto();
        Random a = new Random(7);
        Random b = new Random(7);
        for (int i = 0; i < 10; i++) {
            assertEquals(d.seleccionar(Dificultad.MEDIA, a), d.seleccionar(Dificultad.MEDIA, b));
        }
    }

    @Test
    void lineaSinSeparadorEsError() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Diccionario.desdeLineas(List.of("FACIL;sol", "MEDIA casa")));
        assertTrue(e.getMessage().contains("Línea 2"));
    }

    @Test
    void dificultadDesconocidaEsError() {
        assertThrows(IllegalArgumentException.class,
                () -> Diccionario.desdeLineas(List.of("EXTREMA;sol")));
    }

    @Test
    void palabraConNumerosEsError() {
        assertThrows(IllegalArgumentException.class,
                () -> Diccionario.desdeLineas(List.of("FACIL;s0l", "MEDIA;casa", "DIFICIL;perro")));
    }

    @Test
    void faltaUnaDificultadEsError() {
        assertThrows(IllegalArgumentException.class,
                () -> Diccionario.desdeLineas(List.of("FACIL;sol", "MEDIA;casa")));
    }

    @Test
    void laListaDevueltaNoSePuedeModificar() {
        Diccionario d = Diccionario.desdeLineas(LINEAS);
        assertThrows(UnsupportedOperationException.class, () -> d.getPalabras(Dificultad.FACIL).add("X"));
    }
}
