# Gestión Comercial — CRM

Backend de un **CRM (Customer Relationship Management)** desarrollado con **Spring Boot** y **arquitectura hexagonal**, orientado a la gestión de la cartera comercial de una empresa: clientes y potenciales clientes, sus personas de contacto, las oportunidades de venta abiertas con cada uno y las actividades comerciales (llamadas, reuniones, correos y tareas).

La versión actual implementa cuatro módulos relacionados entre sí: **Empresas**, **Contactos**, **Oportunidades de venta** y **Actividades**.

> **Estado:** en desarrollo activo. Consulta la [hoja de ruta](#hoja-de-ruta) para ver las funcionalidades previstas.

---

## Índice

- [Stack tecnológico](#stack-tecnológico)
- [Puesta en marcha](#puesta-en-marcha)
- [Arquitectura hexagonal](#arquitectura-hexagonal)
- [Modelo de datos](#modelo-de-datos)
- [API REST](#api-rest)
- [Reglas de validación y errores](#reglas-de-validación-y-errores)
- [Pruebas](#pruebas)
- [Hoja de ruta](#hoja-de-ruta)
- [Limitaciones conocidas](#limitaciones-conocidas)

---

## Stack tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 25 | Lenguaje de desarrollo |
| Spring Boot | 4.1.2 | Framework principal (solo en la capa de infraestructura) |
| Spring Web MVC | — | Exposición de la API REST |
| Spring Data JPA | — | Persistencia y consultas (adaptadores de salida) |
| H2 Database | — | Base de datos embebida en archivo para desarrollo |
| springdoc-openapi | 3.1.1 | Documentación interactiva de la API (Swagger UI) |
| JUnit 5 + Mockito | — | Tests unitarios |
| MockMvc | — | Tests de integración de la API |
| Lombok | — | Reducción de código repetitivo |
| Maven | — | Gestión de dependencias y construcción |

---

## Puesta en marcha

### Requisitos

- JDK 25
- No es necesario instalar Maven: el proyecto incluye Maven Wrapper (`mvnw`).

### Ejecución

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows (PowerShell)
.\mvnw.cmd spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

### Documentación interactiva (Swagger)

Con la aplicación arrancada, la documentación de todos los endpoints está en:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **Especificación OpenAPI (JSON):** `http://localhost:8080/v3/api-docs`

Desde Swagger UI se pueden probar las peticiones directamente desde el navegador.

### Base de datos

H2 guarda los datos en el archivo `data/gestiondb.mv.db`, de modo que **se conservan entre reinicios**. El esquema se crea y actualiza automáticamente al arrancar (`spring.jpa.hibernate.ddl-auto=update`).

Para empezar con la base de datos vacía, detén la aplicación y elimina la carpeta `data/` (está excluida del control de versiones). Los tests usan una base de datos en memoria independiente, por lo que nunca modifican estos datos.

**Consola H2:**

| Parámetro | Valor |
|---|---|
| URL | `http://localhost:8080/h2-console` |
| JDBC URL | `jdbc:h2:file:./data/gestiondb` |
| Usuario | `sa` |
| Contraseña | *(vacía)* |

---

## Arquitectura hexagonal

El proyecto sigue una **arquitectura hexagonal (puertos y adaptadores)**. La lógica de negocio vive en un **núcleo de Java puro**, sin dependencias de Spring ni de JPA, y se comunica con el exterior a través de **puertos** (interfaces) que implementan los **adaptadores**.

```
                  ┌───────────────────── infraestructura ─────────────────────┐
                  │                                                           │
 Cliente HTTP ──► │  Controller + DTOs           Adaptadores JPA ──► Base de  │
   (JSON)         │  (adaptador de entrada)      (adaptadores de      datos   │
                  │        │                      salida)                     │
                  │        ▼                         ▲                        │
                  │  ┌──────────────────── núcleo ───┼──────────────┐         │
                  │  │  puertos in ──► Service ──► puertos out      │         │
                  │  │  (casos de uso)    │                         │         │
                  │  │                    ▼                         │         │
                  │  │               Modelo de dominio              │         │
                  │  │           (datos + reglas de negocio)        │         │
                  │  └──────────────────────────────────────────────┘         │
                  └───────────────────────────────────────────────────────────┘
```

**Todas las dependencias apuntan hacia dentro:** la infraestructura conoce el núcleo, pero el núcleo no conoce la infraestructura. Así, cambiar la base de datos o el framework web solo afecta a los adaptadores.

### Estructura de paquetes

Cada módulo es independiente y tiene la misma estructura:

```
com.gestioncomercial.gestion_comercial
├── empresa/
│   ├── domain/
│   │   ├── model/          Empresa (modelo con sus reglas de negocio)
│   │   └── exception/      Excepciones de negocio
│   ├── application/
│   │   ├── port/in/        Casos de uso (lo que se le puede pedir al módulo)
│   │   ├── port/out/       Lo que el módulo necesita del exterior
│   │   └── service/        Implementación de los casos de uso (sin Spring)
│   └── infrastructure/
│       ├── web/            Controller REST + DTOs (adaptador de entrada)
│       ├── persistence/    Entidad JPA, repositorio, mapper y adaptadores de salida
│       └── config/         Registro del servicio como bean de Spring
├── contacto/               (misma estructura)
├── oportunidad/            (misma estructura)
├── actividad/              (misma estructura)
├── exception/              Gestión común de errores (GlobalExceptionHandler)
└── config/                 Configuración común (OpenAPI)
```

### Responsabilidades

| Capa | Pieza | Responsabilidad |
|---|---|---|
| **Dominio** | `Empresa`, `Contacto`, `Oportunidad`, `Actividad` | Datos y reglas de negocio. Se crean con métodos de fábrica (`nueva()`) que validan, y solo cambian a través de métodos que aplican las reglas (`actualizar()`, `cambiarFase()`, `completar()`) |
| **Aplicación** | Puertos `in` (`CrearEmpresaUseCase`, `CambiarFaseUseCase`…) | Contratos de entrada: una interfaz por acción de negocio |
| | Puertos `out` (`EmpresaRepositoryPort`, `DatosRelacionadosPort`…) | Contratos de salida: lo que el núcleo necesita, sin saber cómo se obtiene |
| | `*Service` | Coordina el dominio y los puertos. No lleva anotaciones de Spring |
| **Infraestructura** | `*Controller` + DTOs | Traduce HTTP/JSON ⇄ casos de uso |
| | `*Entity`, `*JpaRepository`, `*Mapper` | Representación en tablas y conversión dominio ⇄ entidad |
| | `*JpaAdapter` y demás adaptadores | Implementan los puertos de salida con Spring Data JPA |
| | `*Config` | Crea cada servicio con sus adaptadores (`@Bean`) |

### Decisiones de diseño

- **Relaciones por identificador en el dominio.** El modelo de dominio guarda solo el `id` de las entidades relacionadas (`empresaId`, `contactoId`). Las relaciones `@ManyToOne` existen únicamente en las entidades JPA, de modo que la base de datos conserva sus claves foráneas sin acoplar los módulos entre sí.
- **Módulos desacoplados.** Cuando un módulo necesita información de otro, lo pide a través de un puerto propio y mínimo (por ejemplo, `EmpresaDeContactoPort.buscarNombre()` o `ContactoResumen` en oportunidades), nunca usando directamente el dominio del otro módulo.
- **La API nunca expone el dominio ni las entidades JPA.** Los controladores reciben un `*Request` y devuelven un `*Response`.

---

## Modelo de datos

```
               ┌──────────── * ACTIVIDAD
               │
EMPRESAS 1 ────┼──────────── * CONTACTO
               │                   1
               │                   │ (opcional)
               └──── * OPORTUNIDAD *
```

- Una **empresa** tiene muchos **contactos**, **oportunidades** y **actividades**.
- Cada **oportunidad** pertenece a una empresa y puede tener un **contacto principal**, que debe ser de esa misma empresa.

En todos los módulos, el `id` lo asigna la base de datos: si el cliente lo incluye en el cuerpo de un `POST` o `PUT`, se ignora.

### Empresa

| Campo | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `id` | `Long` | Autogenerado | Identificador único |
| `nombre` | `String` | Sí | Razón social o nombre comercial |
| `sector` | `String` | Sí | Sector de actividad |
| `provincia` | `String` | Sí | Provincia de la sede |
| `estado` | `String` | Sí | Situación comercial (p. ej. `Cliente`, `Potencial`, `Inactiva`) |

```json
{ "id": 1, "nombre": "Acme S.L.", "sector": "Tecnología", "provincia": "Madrid", "estado": "Cliente" }
```

### Contacto

| Campo | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `id` | `Long` | Autogenerado | Identificador único |
| `nombre` | `String` | Sí | Nombre de pila |
| `apellidos` | `String` | No | Apellidos |
| `cargo` | `String` | No | Puesto en la empresa |
| `email` | `String` | No | Debe contener `@` si se indica |
| `telefono` | `String` | No | Teléfono de contacto |
| `empresaId` | `Long` | Sí | Empresa a la que pertenece |

La respuesta incluye además `empresaNombre`:

```json
{
  "id": 1, "nombre": "Ana", "apellidos": "López García", "cargo": "Directora de compras",
  "email": "ana@acme.com", "telefono": "600 123 456", "empresaId": 1, "empresaNombre": "Acme S.L."
}
```

### Oportunidad

| Campo | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `id` | `Long` | Autogenerado | Identificador único |
| `titulo` | `String` | Sí | Descripción breve de la venta |
| `importe` | `BigDecimal` | Sí | Importe estimado (no negativo, 2 decimales) |
| `fase` | `Fase` | No | Fase del embudo. Por defecto, `PROSPECCION` |
| `fechaCierrePrevista` | `LocalDate` | No | Formato `AAAA-MM-DD` |
| `empresaId` | `Long` | Sí | Empresa a la que se vende |
| `contactoId` | `Long` | No | Contacto principal (de la misma empresa) |

La respuesta incluye además `empresaNombre` y `contactoNombre`:

```json
{
  "id": 1, "titulo": "Renovación de 50 licencias", "importe": 12000.50, "fase": "PROPUESTA",
  "fechaCierrePrevista": "2026-12-15", "empresaId": 1, "empresaNombre": "Acme S.L.",
  "contactoId": 1, "contactoNombre": "Ana"
}
```

#### Embudo de ventas

| Fase | Significado |
|---|---|
| `PROSPECCION` | Se ha detectado una posible venta |
| `CUALIFICACION` | Hay interés real y presupuesto |
| `PROPUESTA` | Se ha enviado presupuesto |
| `NEGOCIACION` | Ajuste de precio y condiciones |
| `GANADA` | Venta cerrada |
| `PERDIDA` | Venta descartada |

### Actividad

| Campo | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `id` | `Long` | Autogenerado | Identificador único |
| `tipo` | `TipoActividad` | Sí | `LLAMADA`, `REUNION`, `EMAIL` o `TAREA` |
| `asunto` | `String` | Sí | Descripción breve |
| `descripcion` | `String` | No | Detalle de la actividad |
| `fecha` | `LocalDateTime` | Sí | Formato `AAAA-MM-DDTHH:MM:SS` |
| `completada` | `boolean` | Automático | Siempre empieza en `false`; cambia con `PATCH …/completar` |
| `empresaId` | `Long` | Sí | Empresa con la que se realiza |

```json
{
  "id": 1, "tipo": "LLAMADA", "asunto": "Llamar para cerrar el presupuesto", "descripcion": null,
  "fecha": "2026-11-20T10:00:00", "completada": false, "empresaId": 1
}
```

Una actividad está **vencida** si no está completada y su fecha ya ha pasado.

---

## API REST

La documentación completa e interactiva está disponible en [Swagger UI](#documentación-interactiva-swagger).

### Empresas

| Método | Endpoint | Descripción | Respuesta |
|---|---|---|---|
| `GET` | `/api/empresas` | Listar todas las empresas | `200` |
| `GET` | `/api/empresas/{id}` | Obtener una empresa | `200` |
| `GET` | `/api/empresas/buscar` | Búsqueda con filtros combinables (`nombre`, `sector`, `provincia`, `estado`) | `200` |
| `GET` | `/api/empresas/provincia/{provincia}` | Filtrar por provincia | `200` |
| `GET` | `/api/empresas/sector/{sector}` | Filtrar por sector | `200` |
| `POST` | `/api/empresas` | Registrar una empresa | `201` |
| `POST` | `/api/empresas/lote` | Registrar varias empresas en una única operación | `201` |
| `PUT` | `/api/empresas/{id}` | Actualizar una empresa | `200` |
| `DELETE` | `/api/empresas/{id}` | Eliminar una empresa (sin contactos, oportunidades ni actividades) | `204` |

### Contactos

| Método | Endpoint | Descripción | Respuesta |
|---|---|---|---|
| `GET` | `/api/contactos` | Listar todos los contactos | `200` |
| `GET` | `/api/contactos/{id}` | Obtener un contacto | `200` |
| `GET` | `/api/empresas/{empresaId}/contactos` | Contactos de una empresa | `200` |
| `POST` | `/api/contactos` | Registrar un contacto | `201` |
| `PUT` | `/api/contactos/{id}` | Actualizar un contacto (puede cambiar de empresa) | `200` |
| `DELETE` | `/api/contactos/{id}` | Eliminar un contacto (sin oportunidades) | `204` |

### Oportunidades

| Método | Endpoint | Descripción | Respuesta |
|---|---|---|---|
| `GET` | `/api/oportunidades` | Listar todas; admite `?fase=PROPUESTA` | `200` |
| `GET` | `/api/oportunidades/{id}` | Obtener una oportunidad | `200` |
| `GET` | `/api/empresas/{empresaId}/oportunidades` | Oportunidades de una empresa | `200` |
| `POST` | `/api/oportunidades` | Registrar una oportunidad | `201` |
| `PUT` | `/api/oportunidades/{id}` | Actualizar una oportunidad | `200` |
| `PATCH` | `/api/oportunidades/{id}/fase` | Mover la oportunidad a otra fase | `200` |
| `DELETE` | `/api/oportunidades/{id}` | Eliminar una oportunidad | `204` |

### Actividades

| Método | Endpoint | Descripción | Respuesta |
|---|---|---|---|
| `GET` | `/api/actividades/{id}` | Obtener una actividad | `200` |
| `GET` | `/api/actividades/vencidas` | Actividades pendientes cuya fecha ya ha pasado | `200` |
| `GET` | `/api/empresas/{empresaId}/actividades` | Actividades de una empresa | `200` |
| `POST` | `/api/actividades` | Registrar una actividad | `201` |
| `PATCH` | `/api/actividades/{id}/completar` | Marcar la actividad como completada | `200` |
| `DELETE` | `/api/actividades/{id}` | Eliminar una actividad | `204` |

### Ejemplos de uso

**Registrar empresas en lote**

```bash
curl -X POST http://localhost:8080/api/empresas/lote \
  -H "Content-Type: application/json" \
  -d '[
        {"nombre":"Acme S.L.","sector":"Tecnología","provincia":"Madrid","estado":"Cliente"},
        {"nombre":"Bar Pepe","sector":"Hostelería","provincia":"Sevilla","estado":"Potencial"}
      ]'
```

> Todos los registros del lote se validan antes de persistirse: si alguno no es válido, no se guarda ninguno.

**Búsqueda de empresas con filtros combinables**

Todos los parámetros son opcionales y se combinan entre sí (condición `AND`). El filtro `nombre` busca coincidencias parciales sin distinguir mayúsculas.

```bash
curl "http://localhost:8080/api/empresas/buscar?provincia=Madrid&sector=Tecnología"
```

**Registrar un contacto**

```bash
curl -X POST http://localhost:8080/api/contactos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana","apellidos":"López García","cargo":"Directora de compras","email":"ana@acme.com","empresaId":1}'
```

**Registrar una oportunidad y avanzarla en el embudo**

```bash
curl -X POST http://localhost:8080/api/oportunidades \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Renovación de 50 licencias","importe":12000.50,"fechaCierrePrevista":"2026-12-15","empresaId":1,"contactoId":1}'

curl -X PATCH http://localhost:8080/api/oportunidades/1/fase \
  -H "Content-Type: application/json" \
  -d '{"fase":"NEGOCIACION"}'
```

**Registrar y completar una actividad**

```bash
curl -X POST http://localhost:8080/api/actividades \
  -H "Content-Type: application/json" \
  -d '{"tipo":"LLAMADA","asunto":"Llamar para cerrar el presupuesto","fecha":"2026-11-20T10:00:00","empresaId":1}'

curl -X PATCH http://localhost:8080/api/actividades/1/completar
```

**Consultas relacionadas**

```bash
curl http://localhost:8080/api/empresas/1/contactos
curl http://localhost:8080/api/empresas/1/oportunidades
curl http://localhost:8080/api/empresas/1/actividades
curl "http://localhost:8080/api/oportunidades?fase=PROPUESTA"
curl http://localhost:8080/api/actividades/vencidas
```

---

## Reglas de validación y errores

Las reglas de negocio se aplican en el **modelo de dominio** y en los **servicios de aplicación**. Las excepciones se traducen a códigos HTTP en `GlobalExceptionHandler` (`@RestControllerAdvice`).

### Empresas

| Condición | Código | Mensaje |
|---|---|---|
| `nombre`, `sector`, `provincia` o `estado` nulos o vacíos | `400` | `El <campo> de la empresa es obligatorio` |
| Lote nulo o vacío | `400` | `La lista de empresas no puede estar vacía` |
| Identificador inexistente | `404` | `Empresa no encontrada con id {id}` |
| Borrar una empresa con contactos | `409` | `No se puede borrar la empresa {id} porque tiene contactos` |
| Borrar una empresa con oportunidades | `409` | `No se puede borrar la empresa {id} porque tiene oportunidades` |
| Borrar una empresa con actividades | `409` | `No se puede borrar la empresa {id} porque tiene actividades` |

### Contactos

| Condición | Código | Mensaje |
|---|---|---|
| `nombre` nulo o vacío | `400` | `El nombre del contacto es obligatorio` |
| `empresaId` nulo | `400` | `La empresa del contacto es obligatoria` |
| `email` sin `@` | `400` | `El email del contacto no es válido` |
| Contacto inexistente | `404` | `Contacto no encontrado con id {id}` |
| Empresa inexistente | `404` | `Empresa no encontrada con id {id}` |
| Borrar un contacto con oportunidades | `409` | `No se puede borrar el contacto {id} porque tiene oportunidades` |

### Oportunidades

| Condición | Código | Mensaje |
|---|---|---|
| `titulo` nulo o vacío | `400` | `El título de la oportunidad es obligatorio` |
| `importe` nulo | `400` | `El importe de la oportunidad es obligatorio` |
| `importe` negativo | `400` | `El importe no puede ser negativo` |
| `empresaId` nulo | `400` | `La empresa de la oportunidad es obligatoria` |
| Contacto de otra empresa | `400` | `El contacto {id} no pertenece a la empresa {id}` |
| `fase` nula en el `PATCH` | `400` | `La fase es obligatoria` |
| Oportunidad, empresa o contacto inexistentes | `404` | `… no encontrad… con id {id}` |

### Actividades

| Condición | Código | Mensaje |
|---|---|---|
| `tipo` nulo | `400` | `El tipo de la actividad es obligatorio` |
| `asunto` nulo o vacío | `400` | `El asunto de la actividad es obligatorio` |
| `fecha` nula | `400` | `La fecha de la actividad es obligatoria` |
| `empresaId` nulo | `400` | `La empresa de la actividad es obligatoria` |
| Empresa inexistente | `400` | `La empresa {id} no existe` |
| Actividad inexistente | `404` | `Actividad no encontrada con id {id}` |
| Completar una actividad ya completada | `409` | `La actividad {id} ya está completada` |

### Formato de error

Todas las respuestas de error gestionadas comparten el mismo formato:

```json
{
  "status": 409,
  "mensaje": "No se puede borrar la empresa 1 porque tiene contactos",
  "fecha": "2026-10-29T10:15:30"
}
```

Como red de seguridad, cualquier operación que la base de datos rechace por integridad referencial también se devuelve como `409`.

---

## Pruebas

El proyecto cuenta con **37 tests automatizados**:

| Tipo | Clases | Qué comprueban |
|---|---|---|
| **Unitarios** (JUnit 5 + Mockito) | `EmpresaServiceTest`, `ContactoServiceTest`, `OportunidadServiceTest` | Reglas de negocio de cada servicio, aislado de la base de datos: los **puertos de salida** se sustituyen por mocks |
| **Integración** (MockMvc) | `EmpresaControllerIntegrationTest`, `ContactoControllerIntegrationTest`, `OportunidadControllerIntegrationTest` | La cadena completa con peticiones HTTP reales: controller → caso de uso → servicio → adaptador JPA → base de datos |
| **Arranque** | `GestionComercialApplicationTests` | Que la aplicación arranca y Spring conecta todos los puertos con sus adaptadores |

Los tests de integración usan una base de datos H2 **en memoria** y deshacen sus cambios al terminar cada test (`@Transactional`).

```bash
# Linux / macOS
./mvnw test

# Windows (PowerShell)
.\mvnw.cmd test
```

Además, los endpoints se han verificado manualmente con **Postman** y **Swagger UI**.

---

## Hoja de ruta

| Fase | Funcionalidad | Estado |
|---|---|---|
| 1 | Gestión de empresas (CRUD, filtros, alta en lote) | Completado |
| 1 | Filtros dinámicos combinables con JPA Specifications | Completado |
| 1 | DTOs de entrada y salida | Completado |
| 1 | Gestión centralizada de errores con respuestas 400 / 404 / 409 | Completado |
| 2 | Gestión de **contactos** asociados a cada empresa | Completado |
| 2 | Gestión de **oportunidades de venta** y embudo comercial | Completado |
| 2 | Registro de **actividades** (llamadas, reuniones, correos, tareas) | Completado |
| 2 | Documentación interactiva de la API (OpenAPI / Swagger) | Completado |
| 2 | Tests unitarios (JUnit + Mockito) y de integración (MockMvc) | Completado |
| 2 | Migración a **arquitectura hexagonal** | Completado |
| 3 | Base de datos **PostgreSQL** con **Docker Compose** | Pendiente |
| 3 | **Usuarios, autenticación y roles** (Spring Security + JWT) | Pendiente |
| 3 | Integración continua con **GitHub Actions** | Pendiente |
| 3 | Paginación y ordenación de resultados | Pendiente |
| 4 | Panel de métricas comerciales e informes | Pendiente |

---

## Limitaciones conocidas

- H2 es una base de datos embebida pensada para desarrollo; está prevista la migración a PostgreSQL.
- Los errores de formato en el JSON de entrada (por ejemplo, una `fase` que no existe) los devuelve Spring con su formato por defecto, distinto del de `ErrorResponse`.
- Las actividades guardan la empresa solo por su identificador (sin clave foránea en la base de datos); la existencia de la empresa se comprueba en la aplicación al registrar la actividad y al intentar borrar la empresa.
