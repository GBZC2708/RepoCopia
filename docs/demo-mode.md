# Alphakids - Modo Demo

Este repositorio incluye una version demo completamente funcional del flujo de tutor y docente. El modo demo se encuentra habilitado mediante `BuildConfig.DEMO_MODE = true`, lo que reemplaza las integraciones con Firebase por repositorios en memoria con datos semilla.

## Como ejecutar la demo

1. Abre el proyecto en Android Studio Iguana o superior.
2. Sincroniza los gradle scripts (`Sync Project`).
3. Conecta un dispositivo fisico o inicia un emulador API 24+.
4. Ejecuta la app con el boton **Run** sobre la variante `debug` (el modo demo solo esta activo en esta variante).

## Credenciales disponibles

| Rol     | Correo             | Contrasena |
|---------|--------------------|------------|
| Docente | `docente@demo.com` | `123456`   |
| Tutor   | `tutor@demo.com`   | `123456`   |

El registro esta habilitado y anade nuevos perfiles al almacen de demo para pruebas adicionales.

## Datos precargados

- 1 docente demo y 1 tutor demo.
- 3 estudiantes con saldo inicial de 0 monedas.
- 8 palabras con imagenes mock (6 visibles para asignaciones y diccionario).
- 5 palabras en el diccionario del docente.
- 3-4 asignaciones pendientes por estudiante.
- El modulo de logros refleja las palabras completadas y las monedas ganadas.

## Consideraciones

- El modo demo elimina las dependencias en tiempo real de Firebase, pero mantiene la arquitectura de casos de uso y viewmodels.
- Las operaciones de monedas utilizan transacciones en memoria para simular `FieldValue.increment`.
- Para volver al comportamiento real, desactiva la bandera `DEMO_MODE` en `app/build.gradle.kts`.
