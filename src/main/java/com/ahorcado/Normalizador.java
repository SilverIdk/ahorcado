package com.ahorcado;

/**
 * Normalización de acentos (US-08): Á→A, É→E, Í→I, Ó→O, Ú→U, Ü→U.
 * La Ñ se conserva porque en español es una letra distinta.
 */
public final class Normalizador {

    private static final String CON_ACENTO = "ÁÀÄÂÉÈËÊÍÌÏÎÓÒÖÔÚÙÜÛáàäâéèëêíìïîóòöôúùüû";
    private static final String SIN_ACENTO = "AAAAEEEEIIIIOOOOUUUUaaaaeeeeiiiioooouuuu";

    private Normalizador() {
    }

    /** Devuelve la letra sin acento (o la misma letra si no tiene). */
    public static char normalizar(char letra) {
        int indice = CON_ACENTO.indexOf(letra);
        return indice >= 0 ? SIN_ACENTO.charAt(indice) : letra;
    }

    /** Devuelve el texto sin acentos. */
    public static String normalizar(String texto) {
        StringBuilder sb = new StringBuilder(texto.length());
        for (int i = 0; i < texto.length(); i++) {
            sb.append(normalizar(texto.charAt(i)));
        }
        return sb.toString();
    }
}
