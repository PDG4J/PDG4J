package ru.hse.pdg4j.api.check.exception;

public class CheckException extends RuntimeException {
    public CheckException() {
    }

    public CheckException(String message) {
        super(message);
    }
}
