# Plan de integración de ramas

Este documento detalla un flujo paso a paso para integrar las ramas restantes en `main` sin perder las funcionalidades clave que ya aporta la rama `backend-database`.

## 1. Línea base actual (`main`)
- **Arquitectura**: Aplicación Android con Jetpack Compose, Hilt y Firebase. La navegación central se gestiona en `AppNavHost.kt`, que define rutas dinámicas para tutor, estudiante y docente.【F:app/src/main/java/com/example/alphakids/navigation/AppNavHost.kt†L1-L357】【F:app/src/main/java/com/example/alphakids/navigation/AppNavHost.kt†L357-L714】
- **Dominio y datos**: Los casos de uso (`domain/usecases`) y repositorios (`domain/repository`) orquestan el acceso a Firebase mediante implementaciones en `data/firebase/repository`. Destacan los flujos de asignaciones y estudiantes para mantener la pantalla de juegos y asignaciones.【F:app/src/main/java/com/example/alphakids/domain/usecases/AssignmentUseCase.kt†L1-L32】【F:app/src/main/java/com/example/alphakids/data/firebase/repository/AssignmentRepositoryImpl.kt†L1-L123】
- **Juego + cámara**: El flujo tutor → palabras asignadas → puzzle está activo. `WordPuzzleScreen` invoca `onTakePhotoClick`, que debe llevar a `CameraOCRScreen` para cerrar el ciclo de reconocimiento.【F:app/src/main/java/com/example/alphakids/ui/screens/tutor/games/WordPuzzleScreen.kt†L1-L79】【F:app/src/main/java/com/example/alphakids/ui/screens/tutor/games/CameraOCRScreen.kt†L1-L199】

## 2. Preparación
1. **Sincroniza ramas** en tu equipo con red: `git fetch --all --prune` y valida nombres con `git branch -r`.
2. **Documenta cada rama**: crea una tabla con propósito, pantallas afectadas y dependencias externas. Esto facilitará priorizar integraciones conflictivas (por ejemplo, cambios simultáneos en `AppNavHost.kt` o `app/build.gradle.kts`).
3. **Crea una rama de integración** desde `main`, por ejemplo `git switch -c integracion/multi-rama`.

## 3. Orden sugerido de integración
1. **Ramas de infraestructura** (p. ej. ajustes de Gradle o configuración de Firebase) → aseguran que dependencias y módulos comunes estén listos antes de tocar UI.
2. **Ramas de datos/backend** → consolidan repositorios y casos de uso; revisa conflictos con `AssignmentRepositoryImpl.kt` y `WordViewModel.kt`, que ya contienen lógica sensible a Firestore.【F:app/src/main/java/com/example/alphakids/ui/word/WordViewModel.kt†L1-L204】
3. **Ramas de navegación/UI mayor** → cualquier rama que modifique `Routes.kt` o `AppNavHost.kt` debe integrarse de una en una, probando navegación de tutor y docente tras cada merge.【F:app/src/main/java/com/example/alphakids/navigation/Routes.kt†L1-L86】
4. **Ramas específicas de juegos/cámara** → finaliza con ramas que toquen `ui/screens/tutor/games` para evitar sobreescribir el wiring actual del flujo puzzle → cámara.【F:app/src/main/java/com/example/alphakids/ui/screens/tutor/games/AssignedWordsScreen.kt†L1-L137】

## 4. Ciclo por rama
Para cada rama a integrar:
1. `git merge origen/<rama>` dentro de `integracion/multi-rama`.
2. **Resolver conflictos** priorizando conservar:
   - Hooks de navegación (`AppNavHost.kt`, `Routes.kt`).
   - Casos de uso y repositorios que alimentan Firebase.
   - Dependencias duplicadas en `app/build.gradle.kts` (ejemplo: unifica versiones de Coil en 2.7.0 para evitar choques con la rama de cámara).【F:app/build.gradle.kts†L1-L116】
3. **Revisar wiring del flujo tutor**:
   - `AssignedWordsScreen` debe seguir usando `AssignedWordsViewModel.loadAssignedWords` para poblar la lista.【F:app/src/main/java/com/example/alphakids/ui/screens/tutor/games/AssignedWordsViewModel.kt†L1-L91】
   - `WordPuzzleScreen` extrae `palabraTexto` y `palabraImagen` del `WordPuzzleViewModel` y pasa el identificador + la palabra codificada a `Routes.cameraOCRRoute` al invocar `onTakePhotoClick`.
   - `CameraOCRScreen` y `CameraOCRViewModel` deben recibir `assignmentId` + palabra objetivo sin perder la lógica de TTS y guardado de historial.【F:app/src/main/java/com/example/alphakids/ui/screens/tutor/games/CameraOCRViewModel.kt†L1-L114】
4. **Actualizar módulos Hilt** si la rama introduce nuevos repositorios (ver `data/di/RepositoryModule.kt`).
5. **Ejecutar pruebas manuales focalizadas**:
   - Inicio de sesión tutor y docente.
   - Flujo tutor → perfiles → estudiante → palabras asignadas → puzzle → cámara.
   - Flujo docente → listado de palabras → creación/edición/eliminación.
6. **Anotar regresiones** en un archivo temporal (`docs/integration-notes.md`) para no perder contexto entre merges.

## 5. Validaciones finales
1. Ejecuta `./gradlew clean assembleDebug` para validar compilación completa.
2. Verifica permisos de cámara en `AndroidManifest.xml` y pruebas en dispositivo/emulador real para confirmar que la cámara abre y reconoce palabras.
3. Después de integrar todas las ramas, realiza `git switch main` seguido de `git merge integracion/multi-rama`.
4. Actualiza documentación del proyecto (README o notas internas) con el flujo final y dependencias críticas.

## 6. Próximos pasos
- Si aparece una rama dedicada al OCR, revisa que no reinstale dependencias ya presentes ni elimine el almacenamiento de historial (`WordHistoryStorage.kt`).
- Considera agregar pruebas unitarias para los viewmodels críticos (`AssignedWordsViewModel`, `WordPuzzleViewModel`) una vez que el flujo completo esté estable.

> **Tip:** Mantén commits pequeños y documentados durante la integración. Si un merge introduce demasiados conflictos, evalúa `git cherry-pick` de commits específicos para evitar arrastrar cambios obsoletos.
