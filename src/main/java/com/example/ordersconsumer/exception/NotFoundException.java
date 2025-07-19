package com.example.ordersconsumer.exception;

public class NotFoundException extends RuntimeException {

    // Constructor que recibe solo el mensaje de error
    public NotFoundException(String message) {
        super(message);  // Llamada al constructor de la clase base (RuntimeException)
    }

    // Constructor que recibe el mensaje y la causa del error
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
