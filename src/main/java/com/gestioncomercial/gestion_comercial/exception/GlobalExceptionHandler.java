package com.gestioncomercial.gestion_comercial.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
