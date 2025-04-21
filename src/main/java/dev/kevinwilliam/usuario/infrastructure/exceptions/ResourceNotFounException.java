package dev.kevinwilliam.usuario.infrastructure.exceptions;

public class ResourceNotFounException extends RuntimeException{
    public ResourceNotFounException(String message) {
        super(message);
    }

    public ResourceNotFounException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
