package ru.yandex.practicum.filmorate.exception;

public class FailedRegistrationException extends RuntimeException {
    public FailedRegistrationException(String message) {
        super(message);
    }
}
