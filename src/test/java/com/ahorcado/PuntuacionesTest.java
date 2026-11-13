package com.ahorcado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** US-09: registrar puntuaciones y mostrar Top 10. */
class PuntuacionesTest {

    @TempDir
    Path carpeta;

    @Test
    void puntosSegunResultadoYDificultad() {
        assertEquals(0, new Puntuacion("Ana", "SOL", Dificultad.FACIL, 10, false).puntos());
        // Fácil, 2 fallos -> 8 restantes -> (8+1)*10*1
        assertEquals(90, new Puntuacion("Ana", "SOL", Dificultad.FACIL, 2, true).puntos());
        // Difícil, 0 fallos -> 5 restantes -> (5+1)*10*3
        assertEquals(180, new Puntuacion("Ana", "SOL", Dificultad.DIFICIL, 0, true).puntos());
    }

    @Test
    void csvIdaYVuelta() {
        Puntuacion p = new Puntuacion("Luis", "GATO", Dificultad.MEDIA, 3, true);
        assertEquals("Luis;GATO;MEDIA;3;true", p.aCsv());
        assertEquals(p, Puntuacion.desdeCsv(p.aCsv()));
    }

    @Test
    void elNombreNoPuedeRomperElCsv() {
        Puntuacion p = new Puntuacion("a;b", "GATO", Dificultad.MEDIA, 1, true);
        assertEquals("a b", p.nombre());
    }

    @Test
    void datosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Puntuacion(" ", "X", Dificultad.FACIL, 0, true));
        assertThrows(IllegalArgumentException.class, () -> new Puntuacion("A", "X", null, 0, true));
        assertThrows(IllegalArgumentException.class, () -> new Puntuacion("A", "X", Dificultad.FACIL, -1, true));
        assertThrows(IllegalArgumentException.class, () -> Puntuacion.desdeCsv("solo;tres;campos"));
        assertThrows(IllegalArgumentException.class, () -> Puntuacion.desdeCsv("A;X;FACIL;dos;true"));
    }

    @Test
    @DisplayName("US-09: guardar y volver a leer del archivo")
    void guardaYCarga() throws IOException {
        RepositorioPuntuaciones repo = new RepositorioPuntuaciones(carpeta.resolve("datos/puntos.csv"));
        assertTrue(repo.cargar().isEmpty());
        repo.guardar(new Puntuacion("Ana", "SOL", Dificultad.FACIL, 1, true));
        repo.guardar(new Puntuacion("Beto", "CASA", Dificultad.MEDIA, 7, false));
        List<Puntuacion> todas = repo.cargar();
        assertEquals(2, todas.size());
        assertEquals("Beto", todas.get(1).nombre());
    }

    @Test
    @DisplayName("US-09: el Top 10 ordena por puntos y se limita a 10")
    void topDiezOrdenado() throws IOException {
        RepositorioPuntuaciones repo = new RepositorioPuntuaciones(carpeta.resolve("puntos.csv"));
        for (int i = 0; i < 12; i++) {
            repo.guardar(new Puntuacion("J" + i, "SOL", Dificultad.FACIL, i % 10, true));
        }
        repo.guardar(new Puntuacion("Experto", "XILÓFONO", Dificultad.DIFICIL, 0, true));
        List<Puntuacion> top = repo.top(RepositorioPuntuaciones.TOP);
        assertEquals(10, top.size());
        assertEquals("Experto", top.get(0).nombre());
        for (int i = 1; i < top.size(); i++) {
            assertTrue(top.get(i - 1).puntos() >= top.get(i).puntos());
        }
    }

    @Test
    void ignoraLineasDanadas() throws IOException {
        Path archivo = carpeta.resolve("puntos.csv");
        Files.write(archivo, List.of("Ana;SOL;FACIL;1;true", "basura", "", "Beto;SOL;NINGUNA;1;true"),
                StandardCharsets.UTF_8);
        RepositorioPuntuaciones repo = new RepositorioPuntuaciones(archivo);
        assertEquals(1, repo.cargar().size());
        assertEquals(2, repo.getLineasIgnoradas());
    }
}
