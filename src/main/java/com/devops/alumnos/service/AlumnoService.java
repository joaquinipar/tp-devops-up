package com.devops.alumnos.service;

import com.devops.alumnos.entity.Alumno;
import com.devops.alumnos.exception.AlumnoNotFoundException;
import com.devops.alumnos.repository.AlumnoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;

    // Inyección de dependencias por constructor
    public AlumnoService(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }

    public List<Alumno> findAll() {
        return alumnoRepository.findAll();
    }

    public Alumno findById(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new AlumnoNotFoundException(id));
    }

    public Alumno save(Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

    public Alumno update(Long id, Alumno datos) {
        Alumno existente = findById(id);
        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setEmail(datos.getEmail());
        existente.setMatricula(datos.getMatricula());
        return alumnoRepository.save(existente);
    }

    public void deleteById(Long id) {
        if (!alumnoRepository.existsById(id)) {
            throw new AlumnoNotFoundException(id);
        }
        alumnoRepository.deleteById(id);
    }
}
