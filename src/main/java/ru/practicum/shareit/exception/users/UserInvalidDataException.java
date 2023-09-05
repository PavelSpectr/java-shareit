package ru.practicum.shareit.exception.users;

public class UserInvalidDataException extends RuntimeException {
    public UserInvalidDataException(String message) {
        super(message);
    }
}
