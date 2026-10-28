# Gestión Comercial — CRM

Backend de un **CRM (Customer Relationship Management)** desarrollado con **Spring Boot**, orientado a la gestión de la cartera comercial de una empresa: clientes, potenciales clientes y, en próximas versiones, contactos, oportunidades de venta y actividades comerciales.

La versión actual implementa el módulo de **Empresas**, que constituye la base sobre la que se construirán el resto de módulos del sistema.

> **Estado:** en desarrollo activo. Consulta la [hoja de ruta](#hoja-de-ruta) para ver las funcionalidades previstas.

---

## Índice

- [Stack tecnológico](#stack-tecnológico)
- [Puesta en marcha](#puesta-en-marcha)
- [Arquitectura](#arquitectura)
- [Modelo de datos](#modelo-de-datos)
- [API REST](#api-rest)
- [Reglas de validación](#reglas-de-validación)
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
| H2 Database | — | Base de datos en memoria para desarrollo |
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

### Consola de base de datos (H2)

| Parámetro | Valor |
|---|---|
| URL | `http://localhost:8080/h2-console` |
| JDBC URL | `jdbc:h2:mem:gestiondb` |
| Usuario | `sa` |
| Contraseña | *(vacía)* |

> La base de datos H2 se ejecuta en memoria: los datos se eliminan al detener la aplicación. Está prevista la migración a una base de datos persistente (ver [hoja de ruta](#hoja-de-ruta)).

---

## Arquitectura

El proyecto sigue una **arquitectura en capas**, separando la exposición HTTP, la lógica de negocio y el acceso a datos:

```
Cliente HTTP
     │
     ▼
Controller   → Expone los endpoints REST y gestiona las respuestas HTTP
     │
     ▼
Service      → Lógica de negocio, validaciones y control transaccional
     │
     ▼
Repository   → Acceso a datos mediante Spring Data JPA
     │
     ▼
Base de datos
```

### Componentes del módulo Empresas

| Componente | Capa | Responsabilidad |
|---|---|---|
| [GestionComercialApplication](src/main/java/com/gestioncomercial/gestion_comercial/GestionComercialApplication.java) | Arranque | Punto de entrada de la aplicación |
| [Empresas](src/main/java/com/gestioncomercial/gestion_comercial/Empresas.java) | Dominio | Entidad JPA mapeada a la tabla `EMPRESAS` |
| [EmpresaController](src/main/java/com/gestioncomercial/gestion_comercial/EmpresaController.java) | Presentación | Endpoints REST bajo `/api/empresas` |
| [EmpresaService](src/main/java/com/gestioncomercial/gestion_comercial/EmpresaService.java) | Negocio | Validaciones y orquestación de operaciones |
| [EmpresaRepository](src/main/java/com/gestioncomercial/gestion_comercial/EmpresaRepository.java) | Persistencia | `JpaRepository` + `JpaSpecificationExecutor` |
| [EmpresaSpecifications](src/main/java/com/gestioncomercial/gestion_comercial/EmpresaSpecifications.java) | Persistencia | Filtros dinámicos combinables (JPA Specifications) |
| [EmpresaRequest](src/main/java/com/gestioncomercial/gestion_comercial/dto/EmpresaRequest.java) | DTO | Datos de entrada que envía el cliente (sin `id`) |
| [EmpresaResponse](src/main/java/com/gestioncomercial/gestion_comercial/dto/EmpresaResponse.java) | DTO | Datos de salida que devuelve la API |
| [GlobalExceptionHandler](src/main/java/com/gestioncomercial/gestion_comercial/exception/GlobalExceptionHandler.java) | Errores | Traduce las excepciones a respuestas `400` / `404` (`@RestControllerAdvice`) |
| [EmpresaNoEncontradaException](src/main/java/com/gestioncomercial/gestion_comercial/exception/EmpresaNoEncontradaException.java) | Errores | Excepción para empresas inexistentes |
| [ErrorResponse](src/main/java/com/gestioncomercial/gestion_comercial/exception/ErrorResponse.java) | Errores | Formato JSON común de las respuestas de error |

La API nunca expone la entidad JPA: el servicio recibe un `EmpresaRequest`, opera con la entidad `Empresas` y devuelve un `EmpresaResponse`.

---

## Modelo de datos

### Empresa

| Campo | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `id` | `Long` | Autogenerado | Identificador único |
| `nombre` | `String` | Sí | Razón social o nombre comercial |
| `sector` | `String` | Sí | Sector de actividad |
| `provincia` | `String` | Sí | Provincia de la sede |
| `estado` | `String` | Sí | Situación comercial (p. ej. `Activa`, `Potencial`, `Inactiva`) |

```json
{
  "id": 1,
  "nombre": "Acme S.L.",
  "sector": "Tecnología",
  "provincia": "Madrid",
  "estado": "Activa"
}
```

> El `id` lo asigna la base de datos. Si el cliente lo incluye en el cuerpo de un `POST` o `PUT`, se ignora.

---

## API REST

**Ruta base:** `/api/empresas`

| Método | Endpoint | Descripción | Respuesta |
|---|---|---|---|
| `GET` | `/api/empresas` | Listar todas las empresas | `200 OK` |
| `GET` | `/api/empresas/{id}` | Obtener una empresa por su identificador | `200 OK` |
| `GET` | `/api/empresas/buscar` | Búsqueda con filtros combinables (`nombre`, `sector`, `provincia`, `estado`) | `200 OK` |
| `GET` | `/api/empresas/provincia/{provincia}` | Filtrar empresas por provincia | `200 OK` |
| `GET` | `/api/empresas/sector/{sector}` | Filtrar empresas por sector | `200 OK` |
| `POST` | `/api/empresas` | Registrar una empresa | `201 Created` |
| `POST` | `/api/empresas/lote` | Registrar varias empresas en una única operación | `201 Created` |
| `PUT` | `/api/empresas/{id}` | Actualizar una empresa existente | `200 OK` |
| `DELETE` | `/api/empresas/{id}` | Eliminar una empresa | `204 No Content` |

### Ejemplos de uso

**Registrar una empresa**

```bash
curl -X POST http://localhost:8080/api/empresas \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Acme S.L.","sector":"Tecnología","provincia":"Madrid","estado":"Activa"}'
```

**Registro en lote**

```bash
curl -X POST http://localhost:8080/api/empresas/lote \
  -H "Content-Type: application/json" \
  -d '[
        {"nombre":"Acme S.L.","sector":"Tecnología","provincia":"Madrid","estado":"Activa"},
        {"nombre":"Bar Pepe","sector":"Hostelería","provincia":"Sevilla","estado":"Potencial"}
      ]'
```

> La operación en lote es **transaccional**: todos los registros se validan antes de persistirse y, si alguno no es válido, no se guarda ninguno.

**Consultas**

```bash
curl http://localhost:8080/api/empresas
curl http://localhost:8080/api/empresas/1
curl http://localhost:8080/api/empresas/provincia/Madrid
curl http://localhost:8080/api/empresas/sector/Tecnología
```

**Búsqueda con filtros combinables**

Todos los parámetros son opcionales y se combinan entre sí (condición `AND`). El filtro `nombre` busca coincidencias parciales sin distinguir mayúsculas.

```bash
curl "http://localhost:8080/api/empresas/buscar?provincia=Madrid&sector=Tecnología"
curl "http://localhost:8080/api/empresas/buscar?nombre=acme&estado=Activa"
```

**Actualización**

```bash
curl -X PUT http://localhost:8080/api/empresas/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Acme S.L.","sector":"Tecnología","provincia":"Barcelona","estado":"Inactiva"}'
```

**Eliminación**

```bash
curl -X DELETE http://localhost:8080/api/empresas/1
```

---

## Reglas de validación

Las validaciones se aplican en la capa de servicio (`EmpresaService`):

| Condición | Código | Mensaje |
|---|---|---|
| `nombre` nulo o vacío | `400` | `El nombre de la empresa es obligatorio` |
| `sector` nulo o vacío | `400` | `El sector de la empresa es obligatorio` |
| `provincia` nula o vacía | `400` | `La provincia de la empresa es obligatoria` |
| `estado` nulo o vacío | `400` | `El estado de la empresa es obligatorio` |
| Lote nulo o vacío | `400` | `La lista de empresas no puede estar vacía` |
| Identificador inexistente | `404` | `Empresa no encontrada con id {id}` |

Todas las respuestas de error comparten el mismo formato:

```json
{
  "status": 404,
  "mensaje": "Empresa no encontrada con id 99",
  "fecha": "2026-10-28T10:15:30"
}
```

---

## Pruebas

Todos los endpoints de la API han sido verificados mediante **pruebas funcionales con Postman**, cubriendo:

- Operaciones CRUD completas sobre empresas.
- Filtros por provincia y sector, y búsqueda con filtros combinables.
- Registro en lote, incluido el comportamiento transaccional ante datos no válidos.
- Respuestas ante datos obligatorios ausentes e identificadores inexistentes.

La incorporación de **tests automatizados** (JUnit + MockMvc) forma parte de la hoja de ruta.

---

## Hoja de ruta

El objetivo del proyecto es evolucionar hacia un CRM completo. Funcionalidades planificadas:

| Fase | Funcionalidad | Estado |
|---|---|---|
| 1 | Gestión de empresas (CRUD, filtros, alta en lote) | Completado |
| 1 | Filtros dinámicos combinables con JPA Specifications | Completado |
| 1 | DTOs de entrada y salida | Completado |
| 1 | Gestión centralizada de errores (`@RestControllerAdvice`) con respuestas 400 / 404 | Completado |
| 1 | Tests automatizados de integración | Pendiente |
| 2 | Gestión de **contactos** asociados a cada empresa | Pendiente |
| 2 | Gestión de **oportunidades de venta** y embudo comercial | Pendiente |
| 2 | Registro de **actividades** (llamadas, reuniones, correos, tareas) | Pendiente |
| 3 | **Usuarios, autenticación y roles** (Spring Security + JWT) | Pendiente |
| 3 | Paginación y ordenación de resultados | Pendiente |
| 3 | Documentación interactiva de la API (OpenAPI / Swagger) | Pendiente |
| 3 | Base de datos persistente (PostgreSQL / MySQL) | Pendiente |
| 4 | Panel de métricas comerciales e informes | Pendiente |

---

## Limitaciones conocidas

- La base de datos en memoria no conserva los datos entre reinicios.
