package com.ahorcado;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Interfaz de consola: menú principal y flujo de juego (US-01 a US-11).
 * Recibe la entrada y salida por constructor para poder probarla de forma automática.
 */
public final class AplicacionConsola {

    private final BufferedReader entrada;
    private final PrintStream salida;
    private final Configuracion configuracion;
    private final Diccionario diccionario;
    private final RepositorioPuntuaciones puntuaciones;
    private Random random;

    public AplicacionConsola(BufferedReader entrada, PrintStream salida, Configuracion configuracion,
                             Diccionario diccionario, RepositorioPuntuaciones puntuaciones) {
        this.entrada = entrada;
        this.salida = salida;
        this.configuracion = configuracion;
        this.diccionario = diccionario;
        this.puntuaciones = puntuaciones;
        this.random = configuracion.crearRandom();
    }

    /** Ciclo principal: muestra el menú hasta que el usuario elige Salir (o se acaba la entrada). */
    public void ejecutar() {
        salida.println("¡Bienvenido al AHORCADO!");
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            String opcion = leerLinea();
            if (opcion == null) {
                break;
            }
            continuar = procesarOpcion(opcion.trim());
        }
        salida.println("¡Hasta luego!");
    }

    private void mostrarMenu() {
        salida.println();
        salida.println("===== MENÚ PRINCIPAL =====");
        salida.println("1. Nueva partida");
        salida.println("2. Dificultad (actual: " + configuracion.getDificultad().getNombre() + ")");
        salida.println("3. Puntuaciones (Top 10)");
        salida.println("4. Ayuda");
        salida.println("5. Configuración");
        salida.println("6. Salir");
        salida.print("Elige una opción: ");
    }

    /** Ejecuta la opción elegida; devuelve {@code false} si hay que salir. */
    boolean procesarOpcion(String opcion) {
        switch (opcion) {
            case "1":
                nuevaPartida();
                return true;
            case "2":
                menuDificultad();
                return true;
            case "3":
                mostrarPuntuaciones();
                return true;
            case "4":
                salida.println(Ayuda.texto());
                return true;
            case "5":
                menuConfiguracion();
                return true;
            case "6":
                return false;
            default:
                salida.println("Opción no válida. Escribe un número del 1 al 6.");
                return true;
        }
    }

    // ---------------------------------------------------------------- Partida

    /** Juega una partida completa (US-02, US-03, US-04, US-05, US-07). */
    Partida nuevaPartida() {
        Dificultad dificultad = configuracion.getDificultad();
        String palabra = diccionario.seleccionar(dificultad, random);
        Partida partida = new Partida(palabra, dificultad, configuracion.isNormalizarAcentos());
        salida.println();
        salida.println("Nueva partida - dificultad " + dificultad.getNombre() + " ("
                + dificultad.getIntentos() + " intentos). La palabra tiene " + palabra.length() + " letras.");

        boolean abandono = false;
        while (partida.getEstado() == EstadoPartida.EN_CURSO) {
            mostrarEstado(partida);
            salida.print("Letra: ");
            String linea = leerLinea();
            if (linea == null || "!".equals(linea.trim())) {
                abandono = true;
                break;
            }
            if ("?".equals(linea.trim())) {
                salida.println("Letras usadas: " + letrasUsadas(partida));
                continue;
            }
            informarResultado(partida.intentar(linea));
        }

        mostrarEstado(partida);
        if (partida.getEstado() == EstadoPartida.GANADA) {
            salida.println("¡GANASTE! La palabra era " + partida.getPalabra()
                    + ". Fallos: " + partida.getFallos() + ".");
        } else {
            salida.println((abandono ? "Partida abandonada." : "¡PERDISTE!")
                    + " La palabra era " + partida.getPalabra() + ".");
        }
        if (!abandono) {
            ofrecerGuardar(partida, dificultad);
        }
        return partida;
    }

    private void mostrarEstado(Partida partida) {
        salida.println();
        salida.println(RenderizadorAscii.dibujar(partida.getFallos(), partida.getIntentosMaximos()));
        salida.println("Palabra:  " + partida.getProgreso());
        salida.println("Aciertos: " + partida.getAciertos()
                + "   Intentos restantes: " + partida.getIntentosRestantes()
                + "   Usadas: " + letrasUsadas(partida));
    }

    private static String letrasUsadas(Partida partida) {
        if (partida.getLetrasUsadas().isEmpty()) {
            return "(ninguna)";
        }
        return partida.getLetrasUsadas().stream().map(String::valueOf).collect(Collectors.joining(" "));
    }

    private void informarResultado(ResultadoIntento resultado) {
        switch (resultado) {
            case ACIERTO:
                salida.println("¡Acierto!");
                break;
            case FALLO:
                salida.println("Fallo. Esa letra no está.");
                break;
            case REPETIDA:
                salida.println("Ya habías intentado esa letra (no se resta intento).");
                break;
            case INVALIDA:
                salida.println("Entrada no válida: escribe una sola letra (A-Z).");
                break;
            default:
                break;
        }
    }

    private void ofrecerGuardar(Partida partida, Dificultad dificultad) {
        salida.print("¿Guardar puntuación? (s/n): ");
        String respuesta = leerLinea();
        if (respuesta == null || !respuesta.trim().equalsIgnoreCase("s")) {
            return;
        }
        salida.print("Tu nombre: ");
        String nombre = leerLinea();
        if (nombre == null || nombre.isBlank()) {
            salida.println("Nombre vacío: no se guardó la puntuación.");
            return;
        }
        Puntuacion p = new Puntuacion(nombre, partida.getPalabra(), dificultad, partida.getFallos(),
                partida.getEstado() == EstadoPartida.GANADA);
        try {
            puntuaciones.guardar(p);
            salida.println("Puntuación guardada: " + p.puntos() + " puntos.");
            mostrarPuntuaciones();
        } catch (IOException e) {
            salida.println("No se pudo guardar la puntuación: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- Menús

    private void menuDificultad() {
        salida.println();
        salida.println("=== DIFICULTAD ===");
        Dificultad[] opciones = Dificultad.values();
        for (int i = 0; i < opciones.length; i++) {
            salida.println((i + 1) + ". " + opciones[i].getNombre() + " (" + opciones[i].getIntentos() + " intentos)");
        }
        salida.print("Elige: ");
        String linea = leerLinea();
        int indice = parsearEntero(linea) - 1;
        if (indice >= 0 && indice < opciones.length) {
            configuracion.setDificultad(opciones[indice]);
            salida.println("Dificultad: " + opciones[indice].getNombre() + ".");
        } else {
            salida.println("Opción no válida; se mantiene " + configuracion.getDificultad().getNombre() + ".");
        }
    }

    private void mostrarPuntuaciones() {
        salida.println();
        salida.println("=== TOP " + RepositorioPuntuaciones.TOP + " ===");
        try {
            List<Puntuacion> top = puntuaciones.top(RepositorioPuntuaciones.TOP);
            if (top.isEmpty()) {
                salida.println("Aún no hay puntuaciones.");
                return;
            }
            salida.println(String.format("%-3s %-15s %-12s %-8s %-8s %-6s %s",
                    "#", "Nombre", "Palabra", "Dific.", "Fallos", "Ganó", "Puntos"));
            int posicion = 1;
            for (Puntuacion p : top) {
                salida.println(String.format("%-3d %-15s %-12s %-8s %-8d %-6s %d",
                        posicion++, p.nombre(), p.palabra(), p.dificultad().getNombre(), p.intentosUsados(),
                        p.gano() ? "Sí" : "No", p.puntos()));
            }
        } catch (IOException e) {
            salida.println("No se pudieron leer las puntuaciones: " + e.getMessage());
        }
    }

    private void menuConfiguracion() {
        salida.println();
        salida.println("=== CONFIGURACIÓN ===");
        salida.println("1. Normalizar acentos (á = a): " + (configuracion.isNormalizarAcentos() ? "SÍ" : "NO"));
        salida.println("2. Semilla: " + (configuracion.getSemilla() == null ? "aleatoria" : configuracion.getSemilla()));
        salida.println("3. Volver");
        salida.print("Elige: ");
        String linea = leerLinea();
        String opcion = linea == null ? "3" : linea.trim();
        if ("1".equals(opcion)) {
            configuracion.setNormalizarAcentos(!configuracion.isNormalizarAcentos());
            salida.println("Normalizar acentos: " + (configuracion.isNormalizarAcentos() ? "SÍ" : "NO"));
        } else if ("2".equals(opcion)) {
            salida.print("Semilla (número entero, vacío = aleatoria): ");
            String texto = leerLinea();
            if (texto == null || texto.isBlank()) {
                configuracion.setSemilla(null);
                salida.println("Semilla: aleatoria.");
            } else {
                try {
                    configuracion.setSemilla(Long.parseLong(texto.trim()));
                    salida.println("Semilla fijada en " + configuracion.getSemilla() + ".");
                } catch (NumberFormatException e) {
                    salida.println("Semilla no válida; no se cambió.");
                }
            }
            random = configuracion.crearRandom();
        }
    }

    // ---------------------------------------------------------------- Utilidades

    private String leerLinea() {
        try {
            return entrada.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static int parsearEntero(String texto) {
        if (texto == null) {
            return -1;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
