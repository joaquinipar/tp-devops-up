package com.devops.alumnos.exception;

public class AlumnoNotFoundException extends RuntimeException {

    public AlumnoNotFoundException(Long id) {
        super("Alumno no encontrado con id: " + id);
    }
}
