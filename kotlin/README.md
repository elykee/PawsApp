# 🐾 PawsApp — Android Studio (Kotlin)

Implementación nativa para Android.

---

## Qué hace la app
* **Gestión de mascotas:** creación y edición de perfiles con datos básicos, raza, fecha de nacimiento, género y peso.
* **Cuidados y organización:** seguimiento de rutinas, notas y registro de cuidados.
* **Comunidad y actualidad:** acceso a eventos, consejos y novedades de la comunidad.
* **Feedback interactivo:** validación de formularios, controles táctiles y respuestas visuales/sonoras en acciones clave.

---

## Aspectos técnicos destacados
* **Persistencia local:** almacenamiento de datos de sesión y preferencias del usuario mediante `SharedPreferences` y un helper estructurado en Kotlin.
* **Navegación y estado:** paso de datos y banderas de control entre `Activity` y `Fragment` (`HomeFragment`, `PetsFragment`, `PetProfileCreationFragment`).
* **Controles y UX:** validación de campos obligatorios, componentes de Material Design (`RadioGroup`, `AutoCompleteTextView`, `DatePickerDialog`), gesto táctil horizontal (`OnTouchListener`) para peso y gestión limpia de `MediaPlayer` liberando recursos en `onDestroy()`.

---

## Cómo probarlo
* **Instalación rápida (APK):** descargue directamente el archivo [`PawsApp-demo.apk`](./PawsApp-demo.apk) de esta misma carpeta e instálelo en un dispositivo o emulador Android.
* **Desde el código:** abra el proyecto en **Android Studio**, sincronice Gradle y ejecútelo en un emulador o móvil.

---

## Próximos pasos
* Evolucionar la persistencia hacia **Room** y **Firebase**, y estructurar aún más el estado con **ViewModel**.