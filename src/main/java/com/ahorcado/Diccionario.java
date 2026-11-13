package com.ahorcado;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Diccionario de palabras por dificultad (US-07).
 *
 * <p>Formato del archivo (UTF-8), una palabra por línea:
 * <pre>
 * # comentario
 * FACIL;casa
 * MEDIA;ventana
 * DIFICIL;murciélago
 * </pre>
 */
public final class Diccionario {

    /** Recurso embebido en el jar con el diccionario de ejemplo. */
    public static final String RECURSO_POR_DEFECTO = "/palabras.txt";

    private final Map<Dificultad, List<String>> palabras;

    private Diccionario(Map<Dificultad, List<String>> palabras) {
        this.palabras = palabras;
    }

    /**
     * Construye el diccionario validando cada línea.
     *
     * @throws IllegalArgumentException si una línea tiene formato incorrecto o falta una dificultad
     */
    public static Diccionario desdeLineas(List<String> lineas) {
        Map<Dificultad, List<String>> mapa = new EnumMap<>(Dificultad.class);
        for (Dificultad d : Dificultad.values()) {
            mapa.put(d, new ArrayList<>());
        }
        int numero = 0;
        for (String linea : lineas) {
            numero++;
            String texto = linea.strip();
            if (texto.isEmpty() || texto.startsWith("#")) {
                continue;
            }
            String[] partes = texto.split(";");
            if (partes.length != 2) {
                throw new IllegalArgumentException(
                        "Línea " + numero + ": se esperaba DIFICULTAD;palabra pero se encontró '" + texto + "'");
            }
            Dificultad dificultad;
            try {
                dificultad = Dificultad.desdeTexto(partes[0]);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Línea " + numero + ": " + e.getMessage(), e);
            }
            String palabra = partes[1].strip().toUpperCase(Locale.ROOT);
            if (palabra.isEmpty() || !palabra.chars().allMatch(c -> Partida.esLetraValida((char) c))) {
                throw new IllegalArgumentException("Línea " + numero + ": palabra no válida '" + partes[1] + "'");
            }
            mapa.get(dificultad).add(palabra);
        }
        for (Map.Entry<Dificultad, List<String>> entrada : mapa.entrySet()) {
            if (entrada.getValue().isEmpty()) {
                throw new IllegalArgumentException(
                        "El diccionario no tiene palabras para la dificultad " + entrada.getKey().getNombre());
            }
        }
        return new Diccionario(mapa);
    }

    /** Carga el diccionario desde un archivo de texto UTF-8. */
    public static Diccionario desdeArchivo(Path archivo) throws IOException {
        return desdeLineas(Files.readAllLines(archivo, StandardCharsets.UTF_8));
    }

    /** Carga el diccionario de ejemplo incluido en el proyecto. */
    public static Diccionario porDefecto() {
        try (InputStream in = Diccionario.class.getResourceAsStream(RECURSO_POR_DEFECTO)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró el recurso " + RECURSO_POR_DEFECTO);
            }
            try (BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return desdeLineas(lector.lines().collect(Collectors.toList()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el diccionario por defecto", e);
        }
    }

    /** Palabras disponibles para una dificultad. */
    public List<String> getPalabras(Dificultad dificultad) {
        return List.copyOf(palabras.get(dificultad));
    }

    /** Selecciona una palabra al azar para la dificultad indicada. */
    public String seleccionar(Dificultad dificultad, Random random) {
        List<String> lista = palabras.get(dificultad);
        return lista.get(random.nextInt(lista.size()));
    }
}
