<!-- evaluacion:inicio -->
# Evaluación — salamin-noceti/proyecto-fisica--Tiro-Oblicuo

> Documento **generado automáticamente** por la cátedra de Programación II (RepoSense + análisis de calidad POO/SOLID). **No editar a mano**: se regenera en cada evaluación.
> Rama evaluada: `main`. Análisis **heurístico** (regex + métricas): es una criba para orientar la revisión, **no una nota**.
> Generado: 2026-11-02.

## 1. Veredicto

| Aspecto | Resultado |
|---|---|
| Índice de calidad POO/SOLID | **2/100** |
| Código (LOC) | 8,435 |
| Archivos de código | 81 |
| Clases (bien usadas) | 95 (9 OK) |
| Código dentro de clases (%POO) | 99% |
| Funciones fuera de clases | 1 |
| Duplicación lógica | 4.3% |
| Señales S/O/L/I/D | 31/5/0/0/42 |

> Este informe **describe** lo que el análisis midió con los criterios ya establecidos de la materia. No es una nota.

### Semáforo de aprobación

Criterios de `CRITERIOS-CALIDAD.md`. **Aprobar** se juega con el perfil `estandar`; **destacar**, con el `exigente` (meta de excelencia).

| Semáforo | Resultado |
|---|---|
| Aprobar (`estandar`) | **no cumple** — 3/4 |
| Destacar (`exigente`) | **no cumple** — 3/5 |

**Criterios para aprobar**

| Criterio | Requisito | Tu resultado | Cumple |
|---|---|---|---|
| Código dentro de clases (%POO) | ≥ 85% | 99% | sí |
| Duplicación lógica | ≤ 10% | 4.3% | sí |
| Funciones fuera de clases | ≤ 1 | 1 | sí |
| Señales POO/SOLID críticas | ninguna | Baja cohesión (LCOM), Acoplamiento alto (CBO), Switch sobre tipos (OCP) | no |

**Criterios para destacar**

| Criterio | Requisito | Tu resultado | Cumple |
|---|---|---|---|
| Código dentro de clases (%POO) | ≥ 95% | 99% | sí |
| Duplicación lógica | ≤ 5% | 4.3% | sí |
| Funciones fuera de clases | ≤ 1 | 1 | sí |
| Señales POO/SOLID críticas | ninguna | Clase demasiado grande (SRP), Baja cohesión (LCOM), Complejidad alta, Acoplamiento alto (CBO), Switch sobre tipos (OCP) | no |
| Índice de calidad POO/SOLID | ≥ 60 | 2/100 | no |

### Cómo se calcula el %POO (y por qué no sube solo)

`%POO = líneas de lógica dentro de clases ÷ líneas de lógica totales`.

Se cuentan las líneas de **lógica y campos**; no cuentan `package`/`import`/`using`/anotaciones ni comentarios, Javadoc o docstrings. Es una **proporción**: si agregás código nuevo dentro de una clase, el numerador y el denominador crecen juntos y el porcentaje casi no cambia.

Sube cuando el código que hoy está **fuera** de clases pasa a ser método de una clase (o desaparece): funciones sueltas, `main`, constantes de módulo, scripts y lógica de archivos sueltos. También importa que ese trabajo esté **integrado en `main`**: la evaluación siempre mide `main`, así que lo que sigue en otra rama no se refleja todavía.

## 2. Autoría (RepoSense)

| Autor | Commits | Código (LOC) | Tests (LOC) | Docs (LOC) | Días activos | % del código |
|---|---:|---:|---:|---:|---:|---:|
| avsalomon | 13 | 7,592 | 2,212 | 0 | 10 | 100% |
| Romano-22 | 6 | 0 | 19 | 2 | 5 | 0% |

> _El LOC de autoría lo calcula RepoSense sobre el historial y puede diferir del LOC de código del análisis de calidad (que mira solo el estado de `main`)._

> **Reparto del trabajo:** avsalomon concentra el 100% de las líneas de código. Un reparto muy desigual suele indicar que la integración no fue realmente en equipo; vale la pena reconstruir cómo se coordinó.

## 3. Fallas detectadas

Reúne lo que el análisis marcó y lo que todavía falta para cada semáforo. Cuando la señal se puede asociar a archivos, se indican para orientar la revisión. No es una nota.

| Falla | Tu resultado | Requisito | Archivos |
|---|---|---|---|
| Señales POO/SOLID críticas | Baja cohesión (LCOM), Acoplamiento alto (CBO), Switch sobre tipos (OCP) | Aprobar: ninguna<br>Destacar: ninguna | `app/src/main/kotlin/org/example/model/ReporteAutocorreccion.kt`, `app/src/main/kotlin/org/example/service/EntradaUsuarioService.kt`, `app/src/main/kotlin/org/example/service/EvaluadorRespuestas.kt`, `app/src/main/kotlin/org/example/service/MenuConsoleRunner.kt`, `app/src/main/kotlin/org/example/service/ValidadorEntradaRespuesta.kt`, `app/src/main/kotlin/org/example/state/AppState.kt` (+38 más) |
| Índice de calidad POO/SOLID | 2/100 | Destacar: ≥ 60 | — |
| Métodos/funciones largos o con muchos parámetros | **35** método(s)/función(es) largos o con muchos parámetros | Sin señales | `app/src/main/kotlin/org/example/service/CalculadorTiroOblicuo.kt`, `app/src/main/kotlin/org/example/service/SimulacionConsoleRunner.kt`, `app/src/main/kotlin/org/example/ui/PantallaEjercicios.kt`, `app/src/main/kotlin/org/example/ui/PantallaPractica.kt`, `app/src/main/kotlin/org/example/ui/autocorrecion/PanelResumenCalificacion.kt`, `app/src/main/kotlin/org/example/ui/autocorrecion/PanelSolucionPasoAPaso.kt` (+4 más) |
| Dependencia de concretos y falta de abstracciones (DIP) | **42** dependencia(s) de concretos sin abstracciones | Sin señales | `app/src/main/kotlin/org/example/model/ReporteAutocorreccion.kt`, `app/src/main/kotlin/org/example/service/EvaluadorRespuestas.kt`, `app/src/main/kotlin/org/example/service/MenuConsoleRunner.kt`, `app/src/main/kotlin/org/example/ui/BarraNavegacion.kt`, `app/src/main/kotlin/org/example/ui/PantallaEjercicios.kt`, `app/src/main/kotlin/org/example/ui/PantallaPractica.kt` (+34 más) |
| Clase sin métodos (solo datos) | **22** clase(s) sin métodos (solo datos) | Sin señales | `app/src/main/kotlin/org/example/model/ReporteAutocorreccion.kt`, `app/src/main/kotlin/org/example/model/RespuestaEstudiante.kt`, `app/src/main/kotlin/org/example/navigation/SeccionNavegacion.kt`, `app/src/main/kotlin/org/example/service/EvaluadorRespuestas.kt`, `app/src/main/kotlin/org/example/ui/practica/componentes/ControladorAdvertenciaFormato.kt`, `app/src/test/kotlin/org/example/GeneradorEjerciciosBancoAdapterTest.kt` (+15 más) |
| Campos públicos (encapsulación) | **58** clase(s) con campos públicos | Sin señales | `app/src/main/kotlin/org/example/model/ReporteAutocorreccion.kt`, `app/src/main/kotlin/org/example/model/RespuestaEstudiante.kt`, `app/src/main/kotlin/org/example/service/EvaluadorRespuestas.kt`, `app/src/main/kotlin/org/example/service/MenuConsoleRunner.kt`, `app/src/main/kotlin/org/example/service/ValidadorEntradaRespuesta.kt`, `app/src/main/kotlin/org/example/state/AppState.kt` (+46 más) |

## 4. Aspectos evaluados

Cada aspecto indica **qué mide**, **por qué importa** y **tu resultado**. Cuando el análisis reportó una señal, se agregan preguntas para **investigar la causa** (no hay recetas: entender el porqué es parte de la evaluación).

### 1. Índice de calidad POO/SOLID (indice)

- **Qué mide:** Un resumen numérico (0-100) que combina la riqueza de clases bien usadas con la penalización por señales POO/SOLID y duplicación, y luego aplica topes duros.
- **Por qué importa:** Ordena y prioriza la revisión: un índice bajo indica varias señales que conviene mirar juntas.
- **Tu resultado:** índice **2/100**

### 2. Duplicación lógica (DRY) (duplicacion) — sin señales

- **Qué mide:** Porcentaje de bloques de LÓGICA repetidos. Quedan fuera imports/includes, anotaciones, declaraciones de campos y comentarios/Javadoc/docstrings: esos bloques son boilerplate, no lógica.
- **Por qué importa:** Cada copia de lógica se mantiene por separado: un arreglo hay que repetirlo y los defectos se multiplican.
- **Tu resultado:** **4.3%** de duplicación lógica

### 3. Adopción de POO (% de código en clases) (poo) — sin señales

- **Qué mide:** Proporción de las **líneas de lógica** que viven dentro de una clase. Quedan fuera `package`/`import`/`using`/anotaciones y comentarios/Javadoc/docstrings: no son código que pueda estar dentro de una clase y, si contaran, el porcentaje quedaría topado por el boilerplate de cada archivo.
- **Por qué importa:** El diseño orientado a objetos es el eje de la materia: el comportamiento debería vivir como método de una clase.
- **Tu resultado:** **98.6%** del código dentro de clases
- **Cómo leerlo:** Es una **proporción**, no un acumulado: agregar código dentro de una clase no la sube por sí solo (numerador y denominador crecen juntos). Sube cuando el código que está **fuera** de clases (funciones sueltas, `main`, constantes de módulo, scripts) pasa a ser método de una clase o deja de existir. La medición se hace sobre `main`: el trabajo que todavía vive en otra rama no se refleja hasta que se integra.

### 4. Funciones fuera de clases (sueltas) — sin señales

- **Qué mide:** Funciones de nivel de módulo que no pertenecen a ninguna clase (en esta materia el comportamiento debe ser método de una clase).
- **Por qué importa:** Son la forma más directa de código procedural en un proyecto que debería ser orientado a objetos.
- **Tu resultado:** **1** función(es) fuera de clases

### 5. Clase demasiado grande (SRP) (god) — con señales

- **Qué mide:** Clases con demasiadas líneas o métodos de LÓGICA. No cuentan los getters/setters triviales ni la cantidad de campos por sí sola.
- **Por qué importa:** Una clase que hace de todo cambia por muchos motivos y cuesta entenderla y probarla.
- **Tu resultado:** **6** clase(s) demasiado grande(s)
- **Para investigar la causa:**
    - ¿La clase creció por acumulación de responsabilidades?
    - ¿Qué partes de la clase se usan siempre juntas y cuáles no?
    - ¿Se podría describir su responsabilidad en una sola frase?

### 6. Baja cohesión (LCOM) (cohesion) — con señales

- **Qué mide:** Métodos y campos de una clase que casi no se relacionan entre sí.
- **Por qué importa:** Una unidad que cambia por motivos distintos y comparte poca información suele esconder más de una responsabilidad.
- **Tu resultado:** **4** clase(s) con baja cohesión
- **Para investigar la causa:**
    - ¿Qué métodos de la clase no comparten estado con el resto?
    - ¿Cambian por motivos distintos según la tarea?
    - ¿Se pensó su responsabilidad antes de escribirla, o se fue agregando?

### 7. Complejidad alta (complejidad) — con señales

- **Qué mide:** Complejidad ciclomática: cantidad de caminos posibles dentro de un método o función (condicionales, bucles, casos).
- **Por qué importa:** Muchos caminos = difícil de seguir y de probar; suelen esconderse casos sin cubrir.
- **Tu resultado:** **1** método(s)/función(es) con complejidad alta
- **Para investigar la causa:**
    - ¿Se agregaron condiciones a medida que aparecían casos nuevos?
    - ¿Se probaron todos los caminos o solo el caso principal?
    - ¿Hay reglas de negocio mezcladas con detalles técnicos?

### 8. Métodos/funciones largos o con muchos parámetros (tamano) — con señales

- **Qué mide:** Tamaño de los métodos/funciones y cantidad de parámetros.
- **Por qué importa:** Una función que hace varias cosas cuesta leerla, nombrarla y reutilizarla.
- **Tu resultado:** **35** método(s)/función(es) largos o con muchos parámetros
- **Para investigar la causa:**
    - ¿La función creció por acumulación de pasos?
    - ¿Agrupa operaciones que podrían describirse por separado?
    - ¿Los parámetros representan una misma idea (un objeto) o cosas sueltas?

### 9. Acoplamiento alto (CBO) (acoplamiento) — con señales

- **Qué mide:** Cuánto depende una clase de otros tipos del proyecto.
- **Por qué importa:** Piezas muy enlazadas hacen que un cambio se propague y que sea difícil reemplazar partes.
- **Tu resultado:** **43** clase(s) muy acoplada(s)
- **Para investigar la causa:**
    - ¿La clase conoce detalles internos de muchas otras?
    - ¿Se puede cambiar una parte sin tocar las demás?
    - ¿Los colaboradores llegan como parámetros o se crean por dentro?

### 10. Herencia profunda (DIT) (herencia) — sin señales

- **Qué mide:** Profundidad de la cadena de herencia de una clase.
- **Por qué importa:** Jerarquías muy profundas vuelven difícil saber de dónde viene cada comportamiento.
- **Tu resultado:** **0** clase(s) con herencia profunda

### 11. Switch sobre tipos (OCP) (ocp) — con señales

- **Qué mide:** Chequeos repetidos de tipo (`instanceof`/`is`/`typeof`) que equivalen a un switch sobre el tipo.
- **Por qué importa:** Cada tipo nuevo obliga a tocar varios lugares, en vez de extender con una implementación nueva.
- **Tu resultado:** **5** clase(s) con switch sobre tipos
- **Para investigar la causa:**
    - ¿Qué operación se resuelve preguntando el tipo?
    - ¿Qué se toca cada vez que aparece un tipo o caso nuevo?
    - ¿El comportamiento podría vivir en la propia jerarquía de tipos?

### 12. Interfaz con demasiados métodos (ISP) (isp) — sin señales

- **Qué mide:** Interfaces o clases abstractas con muchos métodos declarados.
- **Por qué importa:** Obliga a implementar operaciones que no se usan y acopla a los clientes a métodos que no necesitan.
- **Tu resultado:** **0** interfaz(es) con demasiados métodos

### 13. Override vacío o 'no implementado' (LSP) (lsp) — sin señales

- **Qué mide:** Métodos sobrescritos que quedan vacíos o lanzan 'no implementado'.
- **Por qué importa:** Suele indicar que la jerarquía no representa bien el problema y rompe la sustitución entre clases.
- **Tu resultado:** **0** override(s) vacío(s) o 'no implementado'

### 14. Dependencia de concretos y falta de abstracciones (DIP) (dip) — con señales

- **Qué mide:** Instanciación directa de clases concretas y ausencia de interfaces o clases abstractas pese al acoplamiento.
- **Por qué importa:** Depender de implementaciones concretas dificulta cambiar o probar una parte sin arrastrar las demás.
- **Tu resultado:** **42** dependencia(s) de concretos sin abstracciones
- **Para investigar la causa:**
    - ¿La clase crea por dentro a sus colaboradores en vez de recibirlos?
    - ¿Hay puntos de variación que convendría expresar como abstracción?
    - ¿Cómo se probaría esa clase cambiando una implementación?

### 15. Clase sin métodos (solo datos) (data) — con señales

- **Qué mide:** Clases que solo acumulan campos y no tienen comportamiento.
- **Por qué importa:** Separar datos de comportamiento reparte por el proyecto la lógica que debería estar cerca de esos datos.
- **Tu resultado:** **22** clase(s) sin métodos (solo datos)
- **Para investigar la causa:**
    - ¿Dónde vive el comportamiento que opera sobre esos datos?
    - ¿La clase representa un concepto del dominio o un contenedor técnico?
    - ¿Se evita la lógica de negocio dentro de la clase por comodidad?

### 16. Campos públicos (encapsulación) (encapsulacion) — con señales

- **Qué mide:** Clases que exponen campos sin control de acceso.
- **Por qué importa:** Expone el estado interno y hace que cualquier cambio dependa de quién lo modifica desde afuera.
- **Tu resultado:** **58** clase(s) con campos públicos
- **Para investigar la causa:**
    - ¿Se accede al campo desde otras clases para evitar un método?
    - ¿Qué reglas deberían respetarse al modificar ese estado?
    - ¿Los campos públicos se usan más como configuración que como estado?

### 17. Clase solo con métodos estáticos (utilitaria) (static) — sin señales

- **Qué mide:** Clases donde todos los métodos son estáticos.
- **Por qué importa:** Es comportamiento procedural disfrazado de clase: no se instancia ni participa del modelo de objetos.
- **Tu resultado:** **0** clase(s) solo con métodos estáticos

## 5. Evolución por commit

Métricas calculadas sobre el estado de `main` en **cada commit** (14 commits con trabajo), del primero al último. La calidad usa el perfil `exigente`; el aporte de código lo mide RepoSense. El índice puede **bajar** al crecer el proyecto: se acumulan señales.

```mermaid
xychart-beta
    title "Índice POO/SOLID a lo largo de los commits"
    x-axis ["f5b4533", "0407e5e", "dd3780e", "d36b80c", "34979f5", "5b92c65", "2e89df6", "39d9b06", "0a667a3", "76a2cc8", "4b1ecf0", "27cb813", "5bef311", "e6fe907"]
    y-axis "Índice" 0 --> 100
    line [0, 0, 0, 50, 35, 18, 41, 34, 7, 5, 3, 3, 2, 2]
```

| # | Fecha | Commit | Autor | LOC | Índice | %POO | Duplic. | Sueltas |
|---|---|---|---|---:|---:|---:|---:|---:|
| 1 | 2026-08-14 | `f5b4533` | Romano-22 | 0 | 0 | 0% | 0.0% | 0 |
| 2 | 2026-08-14 | `0407e5e` | Romano-22 | 0 | 0 | 0% | 0.0% | 0 |
| 3 | 2026-08-28 | `dd3780e` | dqmdz-etec | 0 | 0 | 0% | 0.0% | 0 |
| 4 | 2026-08-28 | `d36b80c` | avsalomon | 65 | 50 | 15% | 0.0% | 0 |
| 5 | 2026-09-04 | `34979f5` | avsalomon | 133 | 35 | 54% | 0.0% | 1 |
| 6 | 2026-09-07 | `5b92c65` | Romano-22 | 215 | 18 | 62% | 0.0% | 1 |
| 7 | 2026-10-02 | `2e89df6` | Romano-22 | 277 | 41 | 77% | 0.0% | 1 |
| 8 | 2026-10-05 | `39d9b06` | avsalomon | 638 | 34 | 87% | 0.0% | 0 |
| 9 | 2026-10-09 | `0a667a3` | avsalomon | 1,443 | 7 | 93% | 12.0% | 1 |
| 10 | 2026-10-16 | `76a2cc8` | avsalomon | 3,076 | 5 | 96% | 5.5% | 1 |
| 11 | 2026-10-16 | `4b1ecf0` | avsalomon | 5,634 | 3 | 98% | 5.1% | 1 |
| 12 | 2026-10-19 | `27cb813` | avsalomon | 5,927 | 3 | 98% | 5.0% | 1 |
| 13 | 2026-10-26 | `5bef311` | avsalomon | 7,177 | 2 | 98% | 4.7% | 1 |
| 14 | 2026-10-30 | `e6fe907` | avsalomon | 8,435 | 2 | 99% | 4.3% | 1 |

---
_Informe generado por la cátedra. Los criterios y umbrales de medición corresponden al perfil `exigente` de `calidad.py`; el semáforo de aprobación, a `CRITERIOS-CALIDAD.md` (ver `README.md`). Ante dudas, revisá el archivo señalado._
<!-- evaluacion:fin -->
