# Guía de Refactorización de Código Fuente - Examen RDA-2 (EcoTrip 2026)

Este documento detalla los cambios exactos que se deben realizar en el código fuente de la aplicación **EcoTrip 2026** para cumplir con los requerimientos técnicos de cada uno de los 5 casos de examen.

---

## Caso 1: Navegación Tipada y Contrato de Datos (`ticketCode`)

### 1. Modificar la Ruta (`@Serializable`)
En el archivo donde defines las rutas de navegación tipada (`NavRoutes.kt` o similar), actualiza el objeto o data class que representa la Pantalla 2 (`ResumenScreen`) para incluir el nuevo parámetro no anulable `ticketCode`:

```kotlin
@Serializable
data class ResumenScreen(
    val nombreViajero: String,
    val destino: String,
    val ticketCode: String // Nuevo parámetro obligatorio no anulable
)
```

### 2. Resolver el Contrato de Navegación (`NavHost` y Pantalla 1)
En tu componente de navegación (`NavHost`), al construir el destino de la Pantalla 2, asegúrate de recibir y mapear el argumento. Asimismo, en la Pantalla 1, al momento de invocar el evento de navegación, inyecta el código por defecto solicitado:

```kotlin
// Dentro del NavHost
composable<ResumenScreen> { backStackEntry ->
    val args = backStackEntry.toRoute<ResumenScreen>()
    ResumenScreenContent(
        nombre = args.nombreViajero,
        destino = args.destino,
        ticketCode = args.ticketCode // Pasando el argumento recibido
    )
}

// En la Pantalla 1 (al presionar el botón de planificar ruta)
navController.navigate(
    ResumenScreen(
        nombreViajero = nombreInput,
        destino = destinoInput,
        ticketCode = "ECO-2026-XYZ" // Inyección del código obligatorio por defecto
    )
)
```

### 3. Actualizar la UI Destino (`ResumenScreen`)
Modifica la Pantalla 2 para recibir el parámetro `ticketCode` y renderizarlo dentro de la tarjeta central (`Card`) informativa:

```kotlin
@Composable
fun ResumenScreenContent(nombre: String, destino: String, ticketCode: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Viajero: $nombre")
            Text(text = "Destino: $destino")
            // Línea requerida por la regulación de turismo sostenible
            Text(text = "Código de Reserva: $ticketCode")
        }
    }
}
```

---

## Caso 2: Semántica Cromática y Material You (`tertiaryContainer`)

### Ubicación y Modificación en la UI (`ResumenScreen`)
Abre el archivo composable de la Pantalla 2, localiza la `Card` principal y refactoriza su configuración de colores usando estrictamente los tokens semánticos de Material Theme:

```kotlin
Card(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    )
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Resumen de su viaje ecológico",
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
        // Resto de elementos de la tarjeta
    }
}
```

---

## Caso 3: Validación Reactiva y Flujo UDF (`enabled` en Button)

### 1. Establecer la Condición Lógica en el ViewModel o Pantalla
Calcula la variable booleana de validación en tiempo real basándote en la longitud del nombre (4 o más caracteres) y que el destino no esté vacío:

```kotlin
// Ejemplo usando State Hoisting / Estado reactivo
val isFormValid = nombreViajero.length >= 4 && destinoViaje.isNotBlank()
```

### 2. Vincular el Estado al Componente `Button`
Asigna la variable booleana directamente al parámetro `enabled` del componente `Button`:

```kotlin
Button(
    onClick = { /* Acción de navegación */ },
    enabled = isFormValid, // Restricción reactiva en tiempo real
    modifier = Modifier.fillMaxWidth()
) {
    Text("Planificar Ruta")
}
```

---

## Caso 4: Ciclo de Vida del ViewModel y Limpieza de Memoria (`onCleared`)

### Sobrescribir el Gancho de Limpieza en el ViewModel
En tu clase personalizada que extiende de `ViewModel` (`EcoTripViewModel.kt`), aplica polimorfismo sobrescribiendo el método `onCleared()` y registrando el evento en el Logcat:

```kotlin
class EcoTripViewModel : ViewModel() {

    // Lógica de negocio del viaje...

    override fun onCleared() {
        super.onCleared()
        // Registro de auditoría para el ciclo de vida del ViewModel
        android.util.Log.d("EcoTrip_Lifecycle", "Memoria del viaje liberada.")
    }
}
```

---

## Case 5: Manejo Seguro de Nulos y Conversión Robusta (`toIntOrNull() ?: 1`)

### Interceptar y Sanitizar la Entrada Numérica
Localiza el bloque de código donde se procesa la duración en días (proveniente de un `String` introducido por el usuario) y aplica el parseo seguro junto al operador Elvis:

```kotlin
// Transformación segura de String a Int con valor de rescate por defecto
val diasDuracion: Int = inputDiasString.toIntOrNull() ?: 1
```
