package com.ahorcado;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Persistencia de puntuaciones en un archivo CSV y cálculo del Top 10 (US-09). */
public final class RepositorioPuntuaciones {

    /** Tamaño del ranking. */
    public static final int TOP = 10;

    private final Path archivo;
    private int lineasIgnoradas;

    public RepositorioPuntuaciones(Path archivo) {
        this.archivo = archivo;
    }

    /** Agrega una puntuación al final del archivo (lo crea si no existe). */
    public void guardar(Puntuacion puntuacion) throws IOException {
        Path padre = archivo.toAbsolutePath().getParent();
        if (padre != null) {
            Files.createDirectories(padre);
        }
        Files.writeString(archivo, puntuacion.aCsv() + System.lineSeparator(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /** Lee todas las puntuaciones; ignora líneas dañadas. */
    public List<Puntuacion> cargar() throws IOException {
        List<Puntuacion> resultado = new ArrayList<>();
        int ignoradas = 0;
        lineasIgnoradas = 0;
        if (!Files.exists(archivo)) {
            return resultado;
        }
        for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
            if (linea.isBlank()) {
                continue;
            }
            try {
                resultado.add(Puntuacion.desdeCsv(linea.strip()));
            } catch (IllegalArgumentException e) {
                // línea dañada: se cuenta y se ignora para no perder el resto del ranking
                ignoradas++;
            }
        }
        lineasIgnoradas = ignoradas;
        return resultado;
    }

    /** Número de líneas dañadas que se ignoraron en la última carga. */
    public int getLineasIgnoradas() {
        return lineasIgnoradas;
    }

    /** Las mejores {@code n} puntuaciones: más puntos primero; a empate, menos intentos usados. */
    public List<Puntuacion> top(int n) throws IOException {
        return cargar().stream()
                .sorted(Comparator.comparingInt(Puntuacion::puntos).reversed()
                        .thenComparingInt(Puntuacion::intentosUsados)
                        .thenComparing(Puntuacion::nombre))
                .limit(n)
                .collect(Collectors.toList());
    }
}
