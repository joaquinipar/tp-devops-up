package com.devops.alumnos.service;

import com.devops.alumnos.entity.Alumno;
import com.devops.alumnos.exception.AlumnoNotFoundException;
import com.devops.alumnos.repository.AlumnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlumnoServiceTest {

    @Mock
    private AlumnoRepository alumnoRepository;

    @InjectMocks
    private AlumnoService alumnoService;

    private Alumno alumno;

    @BeforeEach
    void setUp() {
        alumno = new Alumno("Juan", "Perez", "juan.perez@email.com", "MAT-001");
        alumno.setId(1L);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll: debe retornar la lista de todos los alumnos")
    void findAll_debeRetornarListaDeAlumnos() {
        when(alumnoRepository.findAll()).thenReturn(List.of(alumno));

        List<Alumno> resultado = alumnoService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Juan");
        verify(alumnoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll: debe retornar lista vacía si no hay alumnos")
    void findAll_debeRetornarListaVacia() {
        when(alumnoRepository.findAll()).thenReturn(List.of());

        List<Alumno> resultado = alumnoService.findAll();

        assertThat(resultado).isEmpty();
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findById: debe retornar el alumno cuando existe")
    void findById_debeRetornarAlumnoCuandoExiste() {
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));

        Alumno resultado = alumnoService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEmail()).isEqualTo("juan.perez@email.com");
    }

    @Test
    @DisplayName("findById: debe lanzar AlumnoNotFoundException cuando no existe")
    void findById_debeLanzarExcepcionCuandoNoExiste() {
        when(alumnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alumnoService.findById(99L))
                .isInstanceOf(AlumnoNotFoundException.class)
                .hasMessageContaining("99");
    }

    // -------------------------------------------------------------------------
    // save (POST)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("save: debe crear y retornar el alumno guardado")
    void save_debeCrearYRetornarAlumno() {
        Alumno nuevo = new Alumno("Maria", "Lopez", "maria.lopez@email.com", "MAT-002");
        Alumno guardado = new Alumno("Maria", "Lopez", "maria.lopez@email.com", "MAT-002");
        guardado.setId(2L);

        when(alumnoRepository.save(any(Alumno.class))).thenReturn(guardado);

        Alumno resultado = alumnoService.save(nuevo);

        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getNombre()).isEqualTo("Maria");
        assertThat(resultado.getMatricula()).isEqualTo("MAT-002");
        verify(alumnoRepository, times(1)).save(nuevo);
    }

    // -------------------------------------------------------------------------
    // deleteById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteById: debe eliminar el alumno cuando existe")
    void deleteById_debeEliminarCuandoExiste() {
        when(alumnoRepository.existsById(1L)).thenReturn(true);

        alumnoService.deleteById(1L);

        verify(alumnoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById: debe lanzar AlumnoNotFoundException cuando no existe")
    void deleteById_debeLanzarExcepcionCuandoNoExiste() {
        when(alumnoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> alumnoService.deleteById(99L))
                .isInstanceOf(AlumnoNotFoundException.class)
                .hasMessageContaining("99");

        verify(alumnoRepository, times(0)).deleteById(any());
    }
}
