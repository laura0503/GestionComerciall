# Gestión Comercial

API REST para gestionar **empresas** (clientes o potenciales clientes): crearlas, consultarlas, filtrarlas, actualizarlas y borrarlas.

Proyecto de aprendizaje con **Spring Boot**.

---

## Tecnologías

| Tecnología | Versión | Para qué se usa |
|---|---|---|
| Java | 25 | Lenguaje del proyecto |
| Spring Boot | 4.1.2 | Framework base |
| Spring Web MVC | — | Crear los endpoints REST (`@RestController`) |
| Spring Data JPA | — | Acceder a la base de datos sin escribir SQL |
| H2 | — | Base de datos en memoria (se borra al parar la app) |
| H2 Console | — | Ver la base de datos desde el navegador |
| Lombok | — | Reducir código repetitivo (getters, setters...) |
| DevTools | — | Reinicio automático al cambiar código |
| Maven | — | Compilar y gestionar dependencias |

---

## Cómo arrancarlo

```bash
./mvnw spring-boot:run
```

En Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

La API queda en `http://localhost:8080`.

### Consola de la base de datos (H2)

| Campo | Valor |
|---|---|
| URL en el navegador | `http://localhost:8080/h2-console` |
| JDBC URL | `jdbc:h2:mem:gestiondb` |
| Usuario | `sa` |
| Contraseña | *(vacía)* |

> Como la base de datos está **en memoria**, los datos se pierden cada vez que se reinicia la aplicación.

---

## Arquitectura (capas)

Cada petición pasa por estas capas, de arriba a abajo:

```
Cliente (Postman, navegador...)
        │  HTTP
        ▼
EmpresaController   → recibe la petición y devuelve la respuesta
        │
        ▼
EmpresaService      → reglas de negocio y validaciones
        │
        ▼
EmpresaRepository   → habla con la base de datos
        │
        ▼
Base de datos H2    → tabla EMPRESAS
```

| Archivo | Capa | Qué hace |
|---|---|---|
| [GestionComercialApplication.java](src/main/java/com/gestioncomercial/gestion_comercial/GestionComercialApplication.java) | Arranque | Punto de entrada de la aplicación |
| [Empresas.java](src/main/java/com/gestioncomercial/gestion_comercial/Empresas.java) | Entidad | Representa la tabla `EMPRESAS` (`@Entity`) |
| [EmpresaController.java](src/main/java/com/gestioncomercial/gestion_comercial/EmpresaController.java) | Controlador | Define los endpoints en `/api/empresas` |
| [EmpresaService.java](src/main/java/com/gestioncomercial/gestion_comercial/EmpresaService.java) | Servicio | Valida datos y coordina las operaciones |
| [EmpresaRepository.java](src/main/java/com/gestioncomercial/gestion_comercial/EmpresaRepository.java) | Repositorio | Acceso a datos (`JpaRepository` + `JpaSpecificationExecutor`) |
| [EmpresaSpecifications.java](src/main/java/com/gestioncomercial/gestion_comercial/EmpresaSpecifications.java) | Repositorio | 🚧 En construcción: filtros dinámicos |
| [dto/EmpresaResquest.java](src/main/java/com/gestioncomercial/gestion_comercial/dto/EmpresaResquest.java) | DTO | 🚧 En construcción: datos de entrada |
| [dto/EmpresaResponse.java](src/main/java/com/gestioncomercial/gestion_comercial/dto/EmpresaResponse.java) | DTO | 🚧 En construcción: datos de salida |

---

## Modelo de datos: `Empresas`

| Campo | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `id` | `Long` | Se genera solo | Clave primaria autoincremental (`IDENTITY`) |
| `nombre` | `String` | ✅ | Nombre de la empresa |
| `sector` | `String` | ✅ | Sector (ej. Tecnología, Hostelería) |
| `provincia` | `String` | ✅ | Provincia donde está |
| `estado` | `String` | ✅ | Estado comercial (ej. Activa, Potencial) |

Ejemplo en JSON:

```json
{
  "id": 1,
  "nombre": "Acme S.L.",
  "sector": "Tecnología",
  "provincia": "Madrid",
  "estado": "Activa"
}
```

---

## Endpoints

Ruta base: `/api/empresas`

| Método | Ruta | Qué hace | Respuesta OK |
|---|---|---|---|
| `GET` | `/api/empresas` | Lista todas las empresas | `200 OK` |
| `GET` | `/api/empresas/{id}` | Busca una empresa por id | `200 OK` |
| `GET` | `/api/empresas/provincia/{provincia}` | Filtra por provincia | `200 OK` |
| `GET` | `/api/empresas/sector/{sector}` | Filtra por sector | `200 OK` |
| `POST` | `/api/empresas` | Crea una empresa | `201 Created` |
| `POST` | `/api/empresas/lote` | Crea varias empresas a la vez | `201 Created` |
| `PUT` | `/api/empresas/{id}` | Actualiza una empresa | `200 OK` |
| `DELETE` | `/api/empresas/{id}` | Borra una empresa | `204 No Content` |

### Ejemplos

**Crear una empresa**

```bash
curl -X POST http://localhost:8080/api/empresas \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Acme S.L.","sector":"Tecnología","provincia":"Madrid","estado":"Activa"}'
```

**Crear varias de golpe (lote)**

```bash
curl -X POST http://localhost:8080/api/empresas/lote \
  -H "Content-Type: application/json" \
  -d '[
        {"nombre":"Acme S.L.","sector":"Tecnología","provincia":"Madrid","estado":"Activa"},
        {"nombre":"Bar Pepe","sector":"Hostelería","provincia":"Sevilla","estado":"Potencial"}
      ]'
```

> El lote es **transaccional** (`@Transactional`): primero se validan todas; si una falla, **no se guarda ninguna**.

**Listar / buscar**

```bash
curl http://localhost:8080/api/empresas
curl http://localhost:8080/api/empresas/1
curl http://localhost:8080/api/empresas/provincia/Madrid
curl http://localhost:8080/api/empresas/sector/Tecnología
```

**Actualizar**

```bash
curl -X PUT http://localhost:8080/api/empresas/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Acme S.L.","sector":"Tecnología","provincia":"Barcelona","estado":"Inactiva"}'
```

**Borrar**

```bash
curl -X DELETE http://localhost:8080/api/empresas/1
```

---

## Validaciones (en `EmpresaService`)

| Situación | Mensaje de error |
|---|---|
| `nombre` vacío o nulo | `El nombre de la empresa es obligatorio` |
| `sector` vacío o nulo | `El sector de la empresa es obligatorio` |
| `provincia` vacía o nula | `La provincia de la empresa es obligatoria` |
| `estado` vacío o nulo | `El estado de la empresa es obligatorio` |
| Lote vacío o nulo | `La lista de empresas no puede estar vacía` |
| El id no existe (buscar, actualizar, borrar) | `Empresa no encontrada con id {id}` |

> ⚠️ Ahora mismo estos errores se lanzan como `RuntimeException`, así que la API responde **500** en todos los casos. Lo ideal sería devolver **400** (datos incorrectos) y **404** (no encontrada).

---

## Códigos HTTP de referencia

| Código | Nombre | Significado |
|---|---|---|
| 200 | OK | Todo bien, aquí tienes los datos |
| 201 | Created | Todo bien, he creado algo nuevo |
| 204 | No Content | Todo bien, pero no te devuelvo nada |
| 400 | Bad Request | Me has enviado datos incorrectos |
| 404 | Not Found | Eso no existe |
| 500 | Internal Server Error | Ha fallado algo en el servidor |

---

## Estado del proyecto

### Hecho ✅

- Entidad `Empresas` con JPA y base de datos H2
- CRUD completo de empresas
- Filtros por provincia y por sector
- Alta de empresas en lote (transaccional)
- Validación de campos obligatorios

### En curso 🚧 (rama `feat/empresas`)

- `EmpresaSpecifications`: filtros dinámicos combinables (ej. provincia **y** sector a la vez). El repositorio ya extiende `JpaSpecificationExecutor`.
- DTOs `EmpresaResquest` / `EmpresaResponse`: separar lo que entra y sale de la API de la entidad.

### Pendiente 📝

- Manejo de errores con `@RestControllerAdvice` para devolver 400 / 404
- Tests de los endpoints
- Base de datos persistente (ej. PostgreSQL o MySQL)

---

## Historial

| Commit | Descripción |
|---|---|
| `f5c8553` | Inicio del proyecto |
| `3cfda8d` | Gestión de empresas |
| `d5ee826` | Creación de la API |
