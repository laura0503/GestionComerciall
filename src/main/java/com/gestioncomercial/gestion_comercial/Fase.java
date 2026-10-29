package com.gestioncomercial.gestion_comercial;

// fases del embudo de ventas: solo se admiten estos valores
public enum Fase {
    PROSPECCION,   // se ha detectado una posible venta
    CUALIFICACION, // hay interés real y presupuesto
    PROPUESTA,     // se ha enviado presupuesto
    NEGOCIACION,   // ajustando precio y condiciones
    GANADA,        // venta cerrada
    PERDIDA        // venta descartada
}
