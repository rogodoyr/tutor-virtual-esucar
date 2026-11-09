# Tutor Virtual ESUCAR

Aplicacion movil Android (Kotlin + Jetpack Compose + Material 3) desarrollada para la
asignatura **DSY1105 - Desarrollo de Aplicaciones Moviles (Duoc UC)**. El proyecto
resuelve el caso **"Tutor Virtual para la asignatura de Trabajo de Titulo"** de la
Escuela de Suboficiales de Carabineros de Chile (ESUCAR).

## Estudiantes

- Rodrigo Godoy
- Axel Brogi

**Asignatura:** DSY1105 - Desarrollo de Aplicaciones Moviles_002V (Duoc UC)
**Profesor:** Juan Guillermo Azocar Malverde

## Problema que resuelve

Los estudiantes de Trabajo de Titulo I, II y III carecen de una herramienta movil que
les permita consultar la guia metodologica, resolver dudas de formato y citacion, y
llevar el control de sus entregas fuera del aula. La app funciona como **tutor virtual**
que guia paso a paso el proceso y reduce las consultas repetitivas a los profesores guia.

## Funcionalidades implementadas

| Funcionalidad | Implementacion |
|---|---|
| Acceso institucional (login) | Login y registro validados con perfiles (estudiante / profesor guia), contrasenas cifradas (SHA-256 + salt) y sesion persistida |
| Diseno visual Material 3 | Tema claro/oscuro con paleta institucional, NavigationBar, Cards, TopAppBar |
| Navegacion funcional | Navigation Compose con 7 pantallas y transiciones animadas |
| Formularios validados | Formulario de entregable y formulario de perfil, con iconos, mensajes de error por campo y retroalimentacion animada |
| Validaciones desacopladas | Logica centralizada en `domain/validation` (Validadores, ValidadorEntregable, ValidadorPerfil), probada con JUnit |
| Gestion de estado | MVVM: ViewModels + StateFlow + repositorios |
| Almacenamiento local | Room (entregables) + DataStore (perfil y ajustes), funcionamiento sin conexion |
| Recursos nativos | 1) Camara para la foto de perfil (FileProvider + TakePicture) 2) Notificaciones locales programadas con AlarmManager para recordar fechas limite (permiso POST_NOTIFICATIONS en tiempo de ejecucion) |
| Animaciones | Transiciones entre pantallas, AnimatedVisibility en listas y errores, barra de progreso animada, colores animados por estado, escala al presionar tarjetas, contenido expandible en FAQ |

## Estructura del proyecto (arquitectura MVVM)

```
app/src/main/java/cl/esucar/tutor/
├── TutorApp.kt                  Application + ServiceLocator
├── MainActivity.kt              Actividad unica (tema + navegacion)
├── domain/
│   ├── model/                   Modelos de dominio (Entregable, Etapa, Faq, Perfil)
│   └── validation/              Validaciones centralizadas (sin dependencias de UI)
├── data/
│   ├── local/                   Room: entidad, DAO y base de datos
│   ├── datastore/               DataStore: perfil y preferencias
│   └── repository/              Repositorios (Entregable, Guia, FAQ)
├── notifications/               Notificaciones locales (canal, scheduler, receiver)
├── navigation/                  Rutas y NavHost con transiciones
└── ui/
    ├── theme/                   Tema Material 3
    ├── components/              Componentes reutilizables (campos validados, chips, progreso)
    ├── home/                    Inicio con progreso y accesos rapidos
    ├── guia/                    Guia metodologica y detalle de etapas
    ├── faq/                     Buscador de preguntas frecuentes
    ├── entregables/             Lista y formulario validado de entregables
    └── perfil/                  Perfil, camara y ajustes
```

## Acceso

La app es institucional (ESUCAR): primero se debe iniciar sesion o registrarse
con el perfil correspondiente (estudiante de trabajo de titulo o profesor guia).
Para pruebas existe la cuenta de demostracion `demo@esucar.cl` / `demo1234`
(boton "Ingresar con cuenta de demostracion"). La sesion persiste en el
dispositivo y se cierra desde Perfil > "Cerrar sesion".

## Como ejecutar

1. Abrir el proyecto en **Android Studio** (File > Open > carpeta del repositorio).
2. Esperar la sincronizacion de Gradle (descarga automatica de dependencias).
3. Seleccionar un emulador (API 24 o superior) o dispositivo fisico con depuracion USB.
4. Ejecutar el boton **Run** (Shift + F10).

Requisitos: Android Studio con JDK 17+, Android SDK 37, Gradle 9.6 (wrapper incluido).

## Persistencia local

- **Room** (`tutor_esucar.db`): tabla `entregables` con titulo, etapa, descripcion,
  fecha limite, estado (Por iniciar / En desarrollo / Enviado / Aprobado) y recordatorio;
  tabla `usuarios` con nombre, correo unico, hash de contrasena (SHA-256 + salt) y rol.
- **DataStore** (`perfil_ajustes`): nombre, correo, carrera, semestre, ruta de la foto,
  modo oscuro y preferencia de notificaciones.

## Recursos nativos

1. **Camara**: el icono de la foto de perfil abre la camara del dispositivo mediante
   `ActivityResultContracts.TakePicture` y un `FileProvider`. La imagen se guarda en el
   almacenamiento interno y se persiste su ruta en DataStore.
2. **Notificaciones locales**: al registrar un entregable con recordatorio, se programa
   una alerta con `AlarmManager` para un dia antes de la fecha limite. El permiso
   `POST_NOTIFICATIONS` se solicita en tiempo de ejecucion desde Ajustes.

## Pruebas

`app/src/test/java/cl/esucar/tutor/domain/ValidadoresTest.kt` valida la logica de
validacion desacoplada (campos requeridos, email, semestre, fechas y formularios).
Ejecutar con: `./gradlew test`
