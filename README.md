# Brío · alfa 0.1

Asistente de gimnasio con personalidad: fanfarrón, motivador y optimista.
App nativa de Android en **Kotlin + Jetpack Compose** (sin HTML).

## Qué incluye el alfa

- **Animación de arranque**: Brío entra trotando, resopla y aparece el nombre.
- **Pantalla Hoy**: Brío te habla (tócalo para otra frase), racha en semanas, entrenos, volumen de 7 días y días seguidos. Si llevas 6 o más días seguidos, te sugiere descansar.
- **Entreno**: añadir ejercicios (27 en 7 grupos), series con kg y repeticiones, marcar series hechas, temporizador de descanso con vibración, ±15 s y saltar.
- **Progresión automática por reglas** (sin IA, instantánea): si la última vez completaste todas las series, sube 2,5 kg (barra) o 1 kg (resto); si no, repite.
- **Récords**: detecta marcas nuevas al momento (Brío lo celebra y el móvil vibra) y muestra 1RM estimado.
- **Calculadora de discos**: qué discos poner en cada lado con barra de 20, 15 o 10 kg.
- **Historial** desplegable con borrado.
- **Tres personalidades**: Amable, Normal y Sin piedad (+18, con confirmación de edad).
- **Modo lesión**: Brío se vuelve amable automáticamente.
- Datos guardados solo en el móvil (`brio.json`).

**Regla de oro de las frases**: Brío se mete con el esfuerzo y las excusas, nunca con el cuerpo, el peso, la cara o la comida.

## Cómo compilar (igual que Órbita, con GitHub)

1. Crea un repositorio nuevo en GitHub (por ejemplo `brio`).
2. Sube **todo el contenido** de esta carpeta (incluida la carpeta oculta `.github`).
3. Ve a la pestaña **Actions**: se lanza solo "Compilar Brío". Tarda unos 5–8 minutos.
4. Al terminar, en el resumen de la ejecución descarga el artefacto **Brio-apk** e instala el APK.
5. Para publicar una versión con el APK adjunto: crea una etiqueta `v0.1` (Releases → Draft a new release → tag `v0.1`).

El proyecto lleva una llave de firma fija (`app/brio-debug.keystore`), así cada APK nuevo se instala encima del anterior sin perder datos. Antes de publicar en Google Play habrá que crear una llave de verdad y no subirla al repositorio.

## Estructura

```
app/src/main/java/com/brio/gym/
├── MainActivity.kt          navegación y pestañas
├── brio/
│   ├── Frases.kt            todas las frases por situación y tono
│   └── Entrenador.kt        racha, progresión, récords, discos
├── data/
│   ├── Modelos.kt           Entreno, Serie, Ajustes…
│   ├── Ejercicios.kt        biblioteca de ejercicios
│   └── Almacen.kt           guardado en JSON
└── ui/
    ├── BrioViewModel.kt     estado de la app y temporizador
    ├── components/BrioAvatar.kt   Brío animado + bocadillo
    └── screens/             Inicio (arranque), Casa, Entreno, Historial, Ajustes
```

Para añadir frases nuevas basta con editar `Frases.kt`.

## Pendiente para la beta

- Fotos de progreso con comparativa antes/después (guardadas solo en el móvil).
- Medidas corporales que apunta el usuario (cintura, brazo…).
- Biblioteca de ejercicios con dibujos de la técnica.
- Arte definitivo de Brío con expresiones (contento, serio, eufórico) y logo final.
- Plantillas de rutina y plan semanal.
- Recordatorios.
