# Ahorcado (Scrum + XP)

![Java CI](https://github.com/SilverIdk/ahorcado/actions/workflows/ci.yml/badge.svg)

Juego de consola **Ahorcado** en Java 17, desarrollado como práctica de **Scrum** y **eXtreme Programming**
(TDD, refactorización, GitHub Flow y CI/CD con GitHub Actions).


## Requisitos

- Java 17 o superior
- Maven 3.8 o superior

## Compilar, probar y ejecutar

```bash
mvn verify                              # compila, ejecuta las pruebas JUnit 5 y verifica cobertura >= 70 %
mvn checkstyle:check                    # análisis de estilo
mvn com.github.spotbugs:spotbugs-maven-plugin:4.8.6.0:check   # análisis estático de bugs

java -jar target/ahorcado-1.0.0.jar                    # jugar con el diccionario incluido
java -jar target/ahorcado-1.0.0.jar mis_palabras.txt   # jugar con un diccionario propio
```

El reporte de cobertura queda en `target/site/jacoco/index.html`.

## Cómo se juega

1. En el menú principal elige **1. Nueva partida**.
2. La palabra aparece como guiones: `_ _ _ _`.
3. Escribe una letra y presiona Enter. Si está en la palabra, se revela; si no, pierdes un intento y el dibujo avanza.
4. Ganas si descubres la palabra; pierdes al quedarte sin intentos.

| Comando en partida | Acción |
|---|---|
| `<letra>` | Intentar una letra (A‑Z, Ñ, con o sin acento) |
| `?` | Ver letras usadas |
| `!` | Abandonar la partida |

### Ejemplo de partida

```
Nueva partida - dificultad Media (7 intentos). La palabra tiene 4 letras.
Palabra:  _ _ _ _     Intentos restantes: 7
Letra: a   -> ¡Acierto!     Palabra: _ A _ _
Letra: e   -> Fallo.        Intentos restantes: 6
Letra: g   -> ¡Acierto!     Palabra: G A _ _
Letra: t   -> ¡Acierto!     Palabra: G A T _
Letra: o   -> ¡GANASTE! La palabra era GATO.
¿Guardar puntuación? (s/n): s
Tu nombre: Ana
```

### Menú principal

| Opción | Descripción | Historia |
|---|---|---|
| 1. Nueva partida | Juega con una palabra del diccionario según la dificultad | US‑02, 03, 04, 05, 07 |
| 2. Dificultad | Fácil (10 intentos), Media (7), Difícil (5) | US‑06 |
| 3. Puntuaciones | Top 10 guardado en `puntuaciones.csv` | US‑09 |
| 4. Ayuda | Reglas, comandos y ejemplo | US‑10 |
| 5. Configuración | Normalizar acentos (á = a) y semilla reproducible | US‑08, US‑11 |
| 6. Salir | | US‑01 |

### Puntuación

Si ganas: `(intentos restantes + 1) × 10 × multiplicador` (Fácil ×1, Media ×2, Difícil ×3). Si pierdes: 0.

## Diccionario

Archivo de texto UTF‑8 con una palabra por línea en formato `DIFICULTAD;palabra`
(ver `src/main/resources/palabras.txt`). Las líneas vacías o que empiezan con `#` se ignoran.
Si una línea es inválida, el juego indica el número de línea del error.

```
FACIL;casa
MEDIA;ventana
DIFICIL;murciélago
```

## Estructura

```
src/main/java/com/ahorcado/
  Main.java                 punto de entrada
  AplicacionConsola.java    menús y flujo de juego (consola)
  Partida.java              reglas: letras, intentos, victoria/derrota
  Dificultad.java           Fácil/Media/Difícil
  Configuracion.java        dificultad, normalización, semilla
  Normalizador.java         quitar acentos (conserva la Ñ)
  Diccionario.java          carga y selección de palabras
  RenderizadorAscii.java    dibujo del ahorcado
  Puntuacion.java           registro de una partida
  RepositorioPuntuaciones.java  CSV y Top 10
  Ayuda.java                texto de ayuda
src/test/java/com/ahorcado/  pruebas JUnit 5 (unitarias e integración)
.github/workflows/ci.yml     pipeline de CI
```

## Flujo de trabajo (Git)

- `main` estable; una rama `feature/US-XX-descripcion` por historia.
- Commits convencionales: `feat:`, `fix:`, `test:`, `refactor:`, `chore:`, `docs:`.
- Pull Request con al menos 1 revisión y CI en verde antes de hacer merge.
