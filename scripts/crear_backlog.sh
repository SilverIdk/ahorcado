#!/usr/bin/env bash
# Crea en GitHub las etiquetas, los milestones (Sprint 1 y Sprint 2) y un issue por historia de usuario.
# Requisitos: GitHub CLI (gh) con sesión iniciada (gh auth login) y estar dentro del repositorio.
# Uso: bash scripts/crear_backlog.sh
set -euo pipefail

echo "Creando etiquetas..."
gh label create historia --color 1D76DB --description "Historia de usuario" --force
gh label create Must     --color B60205 --description "Prioridad Must"   --force
gh label create Should   --color FBCA04 --description "Prioridad Should" --force
gh label create Could    --color 0E8A16 --description "Prioridad Could"  --force

echo "Creando milestones..."
gh api repos/{owner}/{repo}/milestones -f title="Sprint 1" -f description="MVP jugable sin persistencia" >/dev/null || true
gh api repos/{owner}/{repo}/milestones -f title="Sprint 2" -f description="Valor adicional y calidad" >/dev/null || true

crear() {  # id titulo prioridad puntos sprint criterio tareas
  local cuerpo
  cuerpo=$(cat <<EOF
## Historia
ID: $1

## Descripción
$2

## Criterios de aceptación
- [ ] $6

## Tareas técnicas
$7

## Pruebas
- [ ] Casos cubiertos en JUnit

**Prioridad:** $3 · **Puntos:** $4
EOF
)
  gh issue create --title "$1: $2" --body "$cuerpo" --label historia --label "$3" --milestone "$5"
}

crear "US-01" "Como jugador, quiero ver un menú principal para elegir acciones." "Must" 2 "Sprint 1" \
  "DADO el inicio, CUANDO se ejecuta la app, ENTONCES aparece un menú navegable con ≥5 opciones." \
  "- [ ] Router de menús
- [ ] Validación de entradas"

crear "US-02" "Como jugador, quiero iniciar una partida y ver la palabra como guiones." "Must" 3 "Sprint 1" \
  "DADA una nueva partida, CUANDO comienza, ENTONCES se muestran guiones por cada letra y cero aciertos iniciales." \
  "- [ ] Entidad Configuración (dificultad, intentos)
- [ ] Vista de consola del progreso"

crear "US-03" "Como jugador, quiero introducir letras válidas y ver el progreso." "Must" 5 "Sprint 1" \
  "DADA una partida en curso, CUANDO ingreso una letra A–Z no repetida, ENTONCES actualiza aciertos o resta intentos y muestra estado." \
  "- [ ] Dominio Partida (palabra oculta, letras usadas)
- [ ] Detección de repetidas e inválidas"

crear "US-04" "Como jugador, quiero finalizar la partida al adivinar la palabra o agotar intentos." "Must" 2 "Sprint 1" \
  "DADA una partida, CUANDO acierto todas las letras O llego a 0 intentos, ENTONCES el juego anuncia victoria o derrota." \
  "- [ ] Reglas de fin de juego
- [ ] Mensajes de victoria/derrota"

crear "US-06" "Como jugador, quiero elegir dificultad (Fácil, Media, Difícil)." "Must" 2 "Sprint 1" \
  "DADO el menú Dificultad, CUANDO selecciono una opción, ENTONCES se fijan los intentos (10/7/5)." \
  "- [ ] Enum Dificultad
- [ ] Menú de dificultad con validación de rango"

crear "US-05" "Como jugador, quiero ver un dibujo ASCII del ahorcado que avance con los fallos." "Should" 2 "Sprint 2" \
  "DADA una partida, CUANDO fallo, ENTONCES el dibujo avanza de acuerdo con intentos restantes." \
  "- [ ] Renderizador ASCII proporcional a los intentos"

crear "US-07" "Como jugador, quiero que la palabra se seleccione desde un diccionario por archivo." "Should" 3 "Sprint 2" \
  "DADA la existencia de un archivo diccionario, CUANDO inicio partida, ENTONCES la palabra se toma al azar (según semilla si aplica)." \
  "- [ ] Repositorio Diccionario (.txt) con validación de carga
- [ ] Selección aleatoria con java.util.Random"

crear "US-09" "Como jugador, quiero registrar y ver puntuaciones Top 10." "Should" 3 "Sprint 2" \
  "DADA una partida finalizada, CUANDO confirmo guardar, ENTONCES se guarda resultado y se muestra el ranking." \
  "- [ ] Repositorio Puntuaciones (CSV)
- [ ] Ordenamiento y Top 10"

crear "US-08" "Como usuario, quiero normalizar acentos para que 'Á' cuente como 'A'." "Could" 2 "Sprint 2" \
  "DADA la normalización activada, CUANDO ingreso una letra con o sin acento, ENTONCES el sistema considera equivalentes." \
  "- [ ] Normalizador (conserva la Ñ)
- [ ] Opción en Configuración"

crear "US-10" "Como usuario, quiero leer ayuda con reglas y ejemplos." "Could" 1 "Sprint 2" \
  "DADO el menú Ayuda, CUANDO lo selecciono, ENTONCES se muestran reglas y ejemplo." \
  "- [ ] Texto de ayuda"

crear "US-11" "Como estudiante, quiero fijar una semilla para reproducibilidad en pruebas." "Could" 1 "Sprint 2" \
  "DADA una semilla configurada, CUANDO inicio partida, ENTONCES la selección de palabra es determinista." \
  "- [ ] Inyección de semilla (Random con semilla)"

echo "Listo. Ahora crea el Project (Kanban) en GitHub y agrega estos issues."
