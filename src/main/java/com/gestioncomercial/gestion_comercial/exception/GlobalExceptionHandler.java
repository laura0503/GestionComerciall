package com.gestioncomercial.gestion_comercial.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// cada módulo tiene sus excepciones en su domain/exception
import com.gestioncomercial.gestion_comercial.actividad.domain.exception.ActividadNoEncontradaException;
import com.gestioncomercial.gestion_comercial.actividad.domain.exception.ActividadYaCompletadaException;
import com.gestioncomercial.gestion_comercial.contacto.domain.exception.ContactoNoEncontradoException;
import com.gestioncomercial.gestion_comercial.empresa.domain.exception.EmpresaNoEncontradaException;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.exception.OportunidadNoEncontradaException;
import com.gestioncomercial.gestion_comercial.usuario.domain.exception.CredencialesInvalidasException;

// captura los errores de todos los controladores y decide el código HTTP
@RestControllerAdvice // --> Hace que la clase "vigile" todos los @RestController. Cuando cualquiera
                      // lanza un error que no captura, Spring mira aquí antes de responder 500
public class GlobalExceptionHandler {

    // no existe → 404
    @ExceptionHandler(EmpresaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> noEncontrada(EmpresaNoEncontradaException e) {
        return construir(HttpStatus.NOT_FOUND, e.getMessage());
        // "Si se lanza un error de tipo X, ejecuta este método." Es como un catch para
        // toda la aplicación
    }

    // datos incorrectos → 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> datosInvalidos(IllegalArgumentException e) {
        return construir(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // crea la respuesta para no repetir el mismo código en cada método
    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensaje) {
        ErrorResponse error = new ErrorResponse(status.value(), mensaje, LocalDateTime.now());
        return ResponseEntity.status(status).body(error);
    }

    // contacto no existe → 404
    @ExceptionHandler(ContactoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> contactoNoEncontrado(ContactoNoEncontradoException e) {
        return construir(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // la operación choca con datos relacionados → 409
    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorResponse> conflicto(ConflictoException e) {
        return construir(HttpStatus.CONFLICT, e.getMessage());
    }

    // red de seguridad: la BD rechaza la operación por una clave foránea → 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException e) {
        return construir(HttpStatus.CONFLICT, "La operación no se puede realizar porque hay datos relacionados");
    }

    // oportunidad no existe → 404
    @ExceptionHandler(OportunidadNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> oportunidadNoEncontrada(OportunidadNoEncontradaException e) {
        return construir(HttpStatus.NOT_FOUND, e.getMessage());
    }
    /*
     * HttpStatus.NOT_FOUND Una constante de Spring que representa el 404. Es más
     * legible que escribir 404
     * status.value() Saca el número: NOT_FOUND.value() → 404
     * ResponseEntity Una respuesta HTTP completa: código + cuerpo (+ cabeceras)
     * .status(status) Pone el código HTTP
     * .body(error)
     * Pone el JSON
     */

    // actividad no existe → 404
    @ExceptionHandler(ActividadNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> actividadNoEncontrada(ActividadNoEncontradaException e) {
        return construir(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // completar una actividad ya completada → 409
    @ExceptionHandler(ActividadYaCompletadaException.class)
    public ResponseEntity<ErrorResponse> actividadYaCompletada(ActividadYaCompletadaException e) {
        return construir(HttpStatus.CONFLICT, e.getMessage());
    }

    // login incorrecto → 401
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> credencialesInvalidas(CredencialesInvalidasException e) {
        return construir(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    // el JSON del body no se puede leer: mal escrito, o un valor que no encaja
    // (por ejemplo "fase": "INVENTADA" o "importe": "mucho") → 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException e) {
        return construir(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es válido: revisa el formato del JSON y los valores enviados");
    }

    // un valor de la URL no es del tipo esperado
    // (por ejemplo /api/empresas/abc o ?fase=INVENTADA) → 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return construir(HttpStatus.BAD_REQUEST,
                "El parámetro '" + e.getName() + "' tiene un valor no válido: '" + e.getValue() + "'");
    }
}
