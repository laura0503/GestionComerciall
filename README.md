# Gestión Comercial — CRM

Backend de un **CRM (Customer Relationship Management)** desarrollado con **Spring Boot**, orientado a la gestión de la cartera comercial de una empresa: clientes y potenciales clientes, sus personas de contacto y las oportunidades de venta abiertas con cada uno.

La versión actual implementa tres módulos relacionados entre sí: **Empresas**, **Contactos** y **Oportunidades de venta**.

> **Estado:** en desarrollo activo. Consulta la [hoja de ruta](#hoja-de-ruta) para ver las funcionalidades previstas.

---

## Índice

- [Stack tecnológico](#stack-tecnológico)
- [Puesta en marcha](#puesta-en-marcha)
- [Arquitectura](#arquitectura)
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
| Spring Boot | 4.1.2 | Framework principal |
| Spring Web MVC | — | Exposición de la API REST |
| Spring Data JPA | — | Capa de persistencia y consultas |
| H2 Database | — | Base de datos embebida en archivo para desarrollo |
| Lombok | — | Reducción de código repetitivo |
| Spring DevTools | — | Recarga automática en desarrollo |
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

### Base de datos

H2 guarda los datos en el archivo `data/gestiondb.mv.db`, de modo que **se conservan entre reinicios**. El esquema se crea y actualiza automáticamente al arrancar (`spring.jpa.hibernate.ddl-auto=update`).

Para empezar con la base de datos vacía, detén la aplicación y elimina la carpeta `data/` (está excluida del control de versiones).

**Consola H2:**

| Parámetro | Valor |
|---|---|
| URL | `http://localhost:8080/h2-console` |
| JDBC URL | `jdbc:h2:file:./data/gestiondb` |
| Usuario | `sa` |
| Contraseña | *(vacía)* |

---

## Arquitectura

El proyecto sigue una **arquitectura en capas**, separando la exposición HTTP, la lógica de negocio y el acceso a datos:

```
Cliente HTTP
     │  JSON (DTOs)
     ▼
Controller   → Expone los endpoints REST y gestiona las respuestas HTTP
     │
     ▼
Service      → Validaciones, reglas de negocio y conversión DTO ⇄ entidad
     │
     ▼
Repository   → Acceso a datos mediante Spring Data JPA
     │  Entidades
     ▼
Base de datos
```

La API nunca expone las entidades JPA: cada servicio recibe un `*Request`, opera con la entidad y devuelve un `*Response`.

### Componentes

| Módulo | Entidad | Repositorio | Servicio | Controlador | DTOs |
|---|---|---|---|---|---|
| Empresas | [Empresas](src/main/java/com/gestioncomercial/gestion_comercial/Empresas.java) | [EmpresaRepository](src/main/java/com/gestioncomercial/gestion_comercial/EmpresaRepository.java) | [EmpresaService](src/main/java/com/gestioncomercial/gestion_comercial/EmpresaService.java) | [EmpresaController](src/main/java/com/gestioncomercial/gestion_comercial/EmpresaController.java) | [Request](src/main/java/com/gestioncomercial/gestion_comercial/dto/EmpresaRequest.java) / [Response](src/main/java/com/gestioncomercial/gestion_comercial/dto/EmpresaResponse.java) |
| Contactos | [Contacto](src/main/java/com/gestioncomercial/gestion_comercial/Contacto.java) | [ContactoRepository](src/main/java/com/gestioncomercial/gestion_comercial/ContactoRepository.java) | [ContactoService](src/main/java/com/gestioncomercial/gestion_comercial/ContactoService.java) | [ContactoController](src/main/java/com/gestioncomercial/gestion_comercial/ContactoController.java) | [Request](src/main/java/com/gestioncomercial/gestion_comercial/dto/ContactoRequest.java) / [Response](src/main/java/com/gestioncomercial/gestion_comercial/dto/ContactoResponse.java) |
| Oportunidades | [Oportunidad](src/main/java/com/gestioncomercial/gestion_comercial/Oportunidad.java) | [OportunidadRepository](src/main/java/com/gestioncomercial/gestion_comercial/OportunidadRepository.java) | [OportunidadService](src/main/java/com/gestioncomercial/gestion_comercial/OportunidadService.java) | [OportunidadController](src/main/java/com/gestioncomercial/gestion_comercial/OportunidadController.java) | [Request](src/main/java/com/gestioncomercial/gestion_comercial/dto/OportunidadRequest.java) / [Response](src/main/java/com/gestioncomercial/gestion_comercial/dto/OportunidadResponse.java) / [CambioFase](src/main/java/com/gestioncomercial/gestion_comercial/dto/CambioFaseRequest.java) |

Otros componentes:

| Componente | Responsabilidad |
|---|---|
| [EmpresaSpecifications](src/main/java/com/gestioncomercial/gestion_comercial/EmpresaSpecifications.java) | Filtros dinámicos combinables de empresas (JPA Specifications) |
| [Fase](src/main/java/com/gestioncomercial/gestion_comercial/Fase.java) | Enumeración con las fases del embudo de ventas |
| [GlobalExceptionHandler](src/main/java/com/gestioncomercial/gestion_comercial/exception/GlobalExceptionHandler.java) | Traduce las excepciones a respuestas `400` / `404` / `409` (`@RestControllerAdvice`) |
| [ErrorResponse](src/main/java/com/gestioncomercial/gestion_comercial/exception/ErrorResponse.java) | Formato JSON común de las respuestas de error |
| `*NoEncontrad*Exception` | Una excepción por módulo para recursos inexistentes (`404`) |
| [ConflictoException](src/main/java/com/gestioncomercial/gestion_comercial/exception/ConflictoException.java) | Operaciones bloqueadas por datos relacionados (`409`) |

---

## Modelo de datos

```
EMPRESAS 1 ──── * CONTACTO
    1                 1
    │                 │ (opcional)
    *                 *
    └──── OPORTUNIDAD ┘
```

- Una **empresa** tiene muchos **contactos** y muchas **oportunidades**.
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

---

## API REST

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
| `DELETE` | `/api/empresas/{id}` | Eliminar una empresa (sin contactos ni oportunidades) | `204` |

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

> La operación en lote es **transaccional**: todos los registros se validan antes de persistirse y, si alguno no es válido, no se guarda ninguno.

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

**Registrar una oportunidad**

```bash
curl -X POST http://localhost:8080/api/oportunidades \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Renovación de 50 licencias","importe":12000.50,"fechaCierrePrevista":"2026-12-15","empresaId":1,"contactoId":1}'
```

**Avanzar una oportunidad en el embudo**

```bash
curl -X PATCH http://localhost:8080/api/oportunidades/1/fase \
  -H "Content-Type: application/json" \
  -d '{"fase":"NEGOCIACION"}'
```

**Consultas relacionadas**

```bash
curl http://localhost:8080/api/empresas/1/contactos
curl http://localhost:8080/api/empresas/1/oportunidades
curl "http://localhost:8080/api/oportunidades?fase=PROPUESTA"
```

---

## Reglas de validación y errores

Las validaciones se aplican en la capa de servicio y los errores se traducen a códigos HTTP en `GlobalExceptionHandler`.

### Empresas

| Condición | Código | Mensaje |
|---|---|---|
| `nombre`, `sector`, `provincia` o `estado` nulos o vacíos | `400` | `El <campo> de la empresa es obligatorio` |
| Lote nulo o vacío | `400` | `La lista de empresas no puede estar vacía` |
| Identificador inexistente | `404` | `Empresa no encontrada con id {id}` |
| Borrar una empresa con contactos | `409` | `No se puede borrar la empresa {id} porque tiene contactos` |
| Borrar una empresa con oportunidades | `409` | `No se puede borrar la empresa {id} porque tiene oportunidades` |

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

Todos los endpoints de la API han sido verificados mediante **pruebas funcionales con Postman**, cubriendo:

- Operaciones CRUD completas sobre empresas, contactos y oportunidades.
- Filtros de empresas y búsqueda con filtros combinables.
- Registro en lote, incluido el comportamiento transaccional ante datos no válidos.
- Consultas anidadas (`/api/empresas/{id}/contactos` y `/oportunidades`) y filtro por fase.
- Cambio de fase mediante `PATCH`.
- Respuestas `400`, `404` y `409` ante datos no válidos, recursos inexistentes y borrados con datos relacionados.

La incorporación de **tests automatizados** (JUnit + MockMvc) forma parte de la hoja de ruta.

---

## Hoja de ruta

El objetivo del proyecto es evolucionar hacia un CRM completo. Funcionalidades planificadas:

| Fase | Funcionalidad | Estado |
|---|---|---|
| 1 | Gestión de empresas (CRUD, filtros, alta en lote) | Completado |
| 1 | Filtros dinámicos combinables con JPA Specifications | Completado |
| 1 | DTOs de entrada y salida | Completado |
| 1 | Gestión centralizada de errores (`@RestControllerAdvice`) con respuestas 400 / 404 / 409 | Completado |
| 1 | Tests automatizados de integración | Pendiente |
| 2 | Gestión de **contactos** asociados a cada empresa | Completado |
| 2 | Gestión de **oportunidades de venta** y embudo comercial | Completado |
| 2 | Registro de **actividades** (llamadas, reuniones, correos, tareas) | Pendiente |
| 3 | **Usuarios, autenticación y roles** (Spring Security + JWT) | Pendiente |
| 3 | Paginación y ordenación de resultados | Pendiente |
| 3 | Documentación interactiva de la API (OpenAPI / Swagger) | Pendiente |
| 3 | Base de datos de producción (PostgreSQL / MySQL) | Pendiente |
| 4 | Panel de métricas comerciales e informes | Pendiente |

---

## Limitaciones conocidas

- H2 es una base de datos embebida pensada para desarrollo; está prevista la migración a PostgreSQL o MySQL.
- Los errores de formato en el JSON de entrada (por ejemplo, una `fase` que no existe) los devuelve Spring con su formato por defecto, distinto del de `ErrorResponse`.
