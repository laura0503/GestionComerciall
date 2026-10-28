package com.gestioncomercial.gestion_comercial.exception;

import java.time.LocalDateTime;

// forma del JSON de error que recibe el cliente
public record ErrorResponse(int status, String mensaje, LocalDateTime fecha) {
    // si ponemos record , vamosa recotrtar hacer elineas de private, de
    // costrucutortes , de getter , con una solo la linea hacemos todo
}
