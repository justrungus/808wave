package com.wave808.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//clase para controlar todas los errores sin tener que ir haciendo try catch
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage();
        if (msg == null) return ResponseEntity.internalServerError().body("Unexpected error");

        if (msg.contains("not found"))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);

        if (msg.equals("Incorrect password"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);

        if (msg.contains("Only the creator") ||
            msg.contains("cannot follow yourself") ||
            msg.contains("already in use") ||
            msg.contains("empty") ||
            msg.contains("Unsupported"))
            return ResponseEntity.badRequest().body(msg);

        return ResponseEntity.internalServerError().body(msg);
    }
}