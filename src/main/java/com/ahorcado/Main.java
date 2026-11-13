package com.ahorcado;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Punto de entrada.
 *
 * <pre>
 * java -jar ahorcado.jar                         # diccionario incluido
 * java -jar ahorcado.jar mi_diccionario.txt      # diccionario propio (US-07)
 * </pre>
 */
public final class Main {

    private Main() {
    }

    /** Arranca el juego. */
    public static void main(String[] args) throws IOException {
        Diccionario diccionario = args.length > 0
                ? Diccionario.desdeArchivo(Path.of(args[0]))
                : Diccionario.porDefecto();
        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintStream salida = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        new AplicacionConsola(entrada, salida, new Configuracion(), diccionario,
                new RepositorioPuntuaciones(Path.of("puntuaciones.csv"))).ejecutar();
    }
}
