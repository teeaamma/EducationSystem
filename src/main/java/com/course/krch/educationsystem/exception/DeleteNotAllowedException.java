package com.course.krch.educationsystem.exception;

public class DeleteNotAllowedException extends RuntimeException {
    public DeleteNotAllowedException(String message) {
        super(message);
    }
}
