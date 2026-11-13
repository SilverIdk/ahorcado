package com.ahorcado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pruebas de integración: flujo completo por consola con diccionario de ejemplo
 * (una palabra por dificultad, así la partida es predecible).
 */
class AplicacionConsolaTest {

    @TempDir
    Path carpeta;

    private final Diccionario diccionario = Diccionario.desdeLineas(List.of(
            "FACIL;sol", "MEDIA;gato", "DIFICIL;árbol"));

    private final Configuracion configuracion = new Configuracion();

    private String ejecutar(String... lineas) {
        return ejecutar(new RepositorioPuntuaciones(carpeta.resolve("puntos.csv")), lineas);
    }

    private String ejecutar(RepositorioPuntuaciones repo, String... lineas) {
        String texto = String.join("\n", lineas) + "\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream salida = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        new AplicacionConsola(new BufferedReader(new StringReader(texto)), salida, configuracion,
                diccionario, repo).ejecutar();
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("US-01: el menú principal muestra al menos 5 opciones y permite salir")
    void menuPrincipal() {
        String out = ejecutar("6");
        assertTrue(out.contains("1. Nueva partida"));
        assertTrue(out.contains("2. Dificultad"));
        assertTrue(out.contains("3. Puntuaciones"));
        assertTrue(out.contains("4. Ayuda"));
        assertTrue(out.contains("5. Configuración"));
        assertTrue(out.contains("6. Salir"));
        assertTrue(out.contains("¡Hasta luego!"));
    }

    @Test
    void opcionInvalidaYFinDeEntrada() {
        String out = ejecutar("9");
        assertTrue(out.contains("Opción no válida"));
        assertTrue(out.contains("¡Hasta luego!"));
    }

    @Test
    @DisplayName("Integración: partida ganada y puntuación guardada en el Top 10")
    void partidaGanadaYGuardada() throws IOException {
        RepositorioPuntuaciones repo = new RepositorioPuntuaciones(carpeta.resolve("puntos.csv"));
        String out = ejecutar(repo, "1", "g", "x", "g", "1", "?", "a", "t", "o", "s", "Ana", "6");
        assertTrue(out.contains("_ _ _ _"));
        assertTrue(out.contains("Fallo"));
        assertTrue(out.contains("Ya habías intentado"));
        assertTrue(out.contains("Entrada no válida"));
        assertTrue(out.contains("Letras usadas: G X"));
        assertTrue(out.contains("¡GANASTE!"));
        assertTrue(out.contains("Puntuación guardada"));
        assertEquals(1, repo.cargar().size());
        assertEquals("Ana", repo.cargar().get(0).nombre());
    }

    @Test
    @DisplayName("Integración: dificultad Difícil, derrota tras 5 fallos")
    void partidaPerdidaEnDificil() {
        String out = ejecutar("2", "3", "1", "q", "w", "e", "y", "u", "n", "6");
        assertEquals(Dificultad.DIFICIL, configuracion.getDificultad());
        assertTrue(out.contains("5 intentos"));
        assertTrue(out.contains("¡PERDISTE!"));
        assertTrue(out.contains("ÁRBOL"));
    }

    @Test
    void abandonarPartida() {
        String out = ejecutar("1", "!", "6");
        assertTrue(out.contains("Partida abandonada"));
        assertFalse(out.contains("¿Guardar puntuación?"));
    }

    @Test
    void dificultadInvalidaNoCambia() {
        ejecutar("2", "7", "6");
        assertEquals(Dificultad.MEDIA, configuracion.getDificultad());
    }

    @Test
    void puntuacionesVaciasYAyuda() {
        String out = ejecutar("3", "4", "6");
        assertTrue(out.contains("Aún no hay puntuaciones"));
        assertTrue(out.contains("REGLAS"));
        assertTrue(out.contains("EJEMPLO"));
    }

    @Test
    @DisplayName("US-08/US-11: la configuración cambia normalización y semilla")
    void configuracion() {
        String out = ejecutar("5", "1", "5", "2", "42", "5", "2", "abc", "5", "2", "", "5", "3", "6");
        assertFalse(configuracion.isNormalizarAcentos());
        assertTrue(out.contains("Semilla fijada en 42"));
        assertTrue(out.contains("Semilla no válida"));
        assertTrue(out.contains("Semilla: aleatoria."));
        assertEquals(null, configuracion.getSemilla());
    }

    @Test
    void noGuardarYNombreVacio() throws IOException {
        RepositorioPuntuaciones repo = new RepositorioPuntuaciones(carpeta.resolve("puntos.csv"));
        ejecutar(repo, "1", "g", "a", "t", "o", "n", "1", "g", "a", "t", "o", "s", "", "6");
        assertTrue(repo.cargar().isEmpty());
    }
}
