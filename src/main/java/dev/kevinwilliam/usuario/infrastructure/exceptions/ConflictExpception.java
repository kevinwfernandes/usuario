package dev.kevinwilliam.usuario.infrastructure.exceptions;

public class ConflictExpception  extends RuntimeException{

    public ConflictExpception(String message) {
        super(message);
    }

    public ConflictExpception(String message, Throwable thwoble) {
        super(message);
    }


}
