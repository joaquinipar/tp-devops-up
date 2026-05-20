package com.devops.alumnos.controller;

import com.devops.alumnos.entity.Alumno;
import com.devops.alumnos.exception.AlumnoNotFoundException;
import com.devops.alumnos.service.AlumnoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController {

    private final AlumnoService alumnoService;

    // Inyección de dependencias por constructor
    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping
    public ResponseEntity<List<Alumno>> listarAlumnos() {
        return ResponseEntity.ok(alumnoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alumno> obtenerAlumno(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(alumnoService.findById(id));
        } catch (AlumnoNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Alumno> crearAlumno(@RequestBody Alumno alumno) {
        Alumno creado = alumnoService.save(alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alumno> actualizarAlumno(@PathVariable Long id, @RequestBody Alumno alumno) {
        try {
            return ResponseEntity.ok(alumnoService.update(id, alumno));
        } catch (AlumnoNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAlumno(@PathVariable Long id) {
        try {
            alumnoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (AlumnoNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
