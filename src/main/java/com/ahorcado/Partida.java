package com.ahorcado;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Reglas de negocio de una partida de ahorcado (US-02, US-03, US-04, US-08).
 */
public final class Partida {

    private static final String LETRAS_VALIDAS = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZÁÉÍÓÚÜ";

    private final String palabra;
    private final int intentosMaximos;
    private final boolean normalizarAcentos;
    private final Set<Character> letrasUsadas = new LinkedHashSet<>();
    private int fallos;

    /**
     * Crea una partida.
     *
     * @param palabra           palabra a adivinar (solo letras)
     * @param intentosMaximos   número de fallos permitidos (mayor que 0)
     * @param normalizarAcentos si es verdadero, 'Á' y 'A' se consideran la misma letra
     */
    public Partida(String palabra, int intentosMaximos, boolean normalizarAcentos) {
        if (palabra == null || palabra.isBlank()) {
            throw new IllegalArgumentException("La palabra no puede estar vacía");
        }
        String mayusculas = palabra.trim().toUpperCase(Locale.ROOT);
        for (int i = 0; i < mayusculas.length(); i++) {
            if (!esLetraValida(mayusculas.charAt(i))) {
                throw new IllegalArgumentException("La palabra contiene caracteres no válidos: " + palabra);
            }
        }
        if (intentosMaximos <= 0) {
            throw new IllegalArgumentException("Los intentos deben ser mayores que 0");
        }
        this.palabra = mayusculas;
        this.intentosMaximos = intentosMaximos;
        this.normalizarAcentos = normalizarAcentos;
    }

    /** Crea una partida con los intentos de la dificultad indicada. */
    public Partida(String palabra, Dificultad dificultad, boolean normalizarAcentos) {
        this(palabra, dificultad.getIntentos(), normalizarAcentos);
    }

    /** Indica si un carácter (ya en mayúsculas) es una letra aceptada por el juego. */
    public static boolean esLetraValida(char c) {
        return LETRAS_VALIDAS.indexOf(c) >= 0;
    }

    /**
     * Procesa la entrada del jugador.
     *
     * @param entrada texto escrito por el jugador; debe ser exactamente una letra
     * @return el resultado del intento
     */
    public ResultadoIntento intentar(String entrada) {
        if (getEstado() != EstadoPartida.EN_CURSO) {
            return ResultadoIntento.PARTIDA_TERMINADA;
        }
        if (entrada == null) {
            return ResultadoIntento.INVALIDA;
        }
        String limpia = entrada.trim().toUpperCase(Locale.ROOT);
        if (limpia.length() != 1 || !esLetraValida(limpia.charAt(0))) {
            return ResultadoIntento.INVALIDA;
        }
        char clave = clave(limpia.charAt(0));
        if (!letrasUsadas.add(clave)) {
            return ResultadoIntento.REPETIDA;
        }
        if (contiene(clave)) {
            return ResultadoIntento.ACIERTO;
        }
        fallos++;
        return ResultadoIntento.FALLO;
    }

    private char clave(char letra) {
        return normalizarAcentos ? Normalizador.normalizar(letra) : letra;
    }

    private boolean contiene(char clave) {
        for (int i = 0; i < palabra.length(); i++) {
            if (clave(palabra.charAt(i)) == clave) {
                return true;
            }
        }
        return false;
    }

    /** Progreso de la palabra: letras adivinadas y guiones, separados por espacios (p. ej. "C _ S A"). */
    public String getProgreso() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < palabra.length(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            char letra = palabra.charAt(i);
            sb.append(letrasUsadas.contains(clave(letra)) ? letra : '_');
        }
        return sb.toString();
    }

    /** Número de letras distintas acertadas. */
    public int getAciertos() {
        int aciertos = 0;
        for (char letra : letrasUsadas) {
            if (contiene(letra)) {
                aciertos++;
            }
        }
        return aciertos;
    }

    /** Estado actual: en curso, ganada o perdida. */
    public EstadoPartida getEstado() {
        if (fallos >= intentosMaximos) {
            return EstadoPartida.PERDIDA;
        }
        for (int i = 0; i < palabra.length(); i++) {
            if (!letrasUsadas.contains(clave(palabra.charAt(i)))) {
                return EstadoPartida.EN_CURSO;
            }
        }
        return EstadoPartida.GANADA;
    }

    public String getPalabra() {
        return palabra;
    }

    public int getFallos() {
        return fallos;
    }

    public int getIntentosMaximos() {
        return intentosMaximos;
    }

    public int getIntentosRestantes() {
        return intentosMaximos - fallos;
    }

    /** Letras intentadas, en el orden en que se jugaron. */
    public Set<Character> getLetrasUsadas() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(letrasUsadas));
    }
}
