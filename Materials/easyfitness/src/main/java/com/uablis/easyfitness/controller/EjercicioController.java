package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Ejercicio;
import com.uablis.easyfitness.model.Serie;
import com.uablis.easyfitness.repository.EjercicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ejercicios")
public class EjercicioController {

    @Autowired
    private EjercicioRepository ejercicioRepository;

    // Obtener todos los ejercicios
    @GetMapping
    public List<Ejercicio> getAllEjercicios() {
        return ejercicioRepository.findAll();
    }

    // Obtener un ejercicio por ID
    @GetMapping("/{id}")
    public ResponseEntity<Ejercicio> getEjercicioById(@PathVariable Integer id) {
        Optional<Ejercicio> ejercicio = ejercicioRepository.findById(id);
        return ejercicio.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo ejercicio
    @PostMapping
    public Ejercicio createEjercicio(@RequestBody Ejercicio ejercicio) {
        return ejercicioRepository.save(ejercicio);
    }

    // Actualizar un ejercicio
    @PutMapping("/{id}")
    public ResponseEntity<Ejercicio> updateObjetivo(@PathVariable Integer id, @RequestBody Ejercicio ejercicioDetails) {
        return ejercicioRepository.findById(id)
                .map(existingEjercicio -> {
                    existingEjercicio.setNombre(ejercicioDetails.getNombre());
                    existingEjercicio.setDescripcion(ejercicioDetails.getDescripcion());
                    existingEjercicio.setTipo(ejercicioDetails.getTipo());
                    existingEjercicio.setValoracion(ejercicioDetails.getValoracion());
                    existingEjercicio.setGrupoMuscular(ejercicioDetails.getGrupoMuscular());
                    existingEjercicio.setRutinaID(ejercicioDetails.getRutinaID());
                    existingEjercicio.setVideo(ejercicioDetails.getVideo());


                    return ResponseEntity.ok(ejercicioRepository.save(existingEjercicio));
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar un ejercicio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEjercicio(@PathVariable Integer id) {
        return ejercicioRepository.findById(id)
                .map(ejercicio -> {
                    ejercicioRepository.delete(ejercicio);
                    return ResponseEntity.ok().<Void>build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Métodos personalizados:
    @GetMapping("/getEjercicioID")
    public ResponseEntity<?> getEjercicioByNomAndRutinaId(@RequestParam String nom, @RequestParam Integer rutinaID) {
        Optional<Ejercicio> ejercicio = ejercicioRepository.findByNombreAndRutinaID(nom, rutinaID);
        return ejercicio.map(e -> ResponseEntity.ok(Collections.singletonMap("ejercicioID", e.getEjercicioID())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/rutina/{rutinaID}")
    public ResponseEntity<List<Ejercicio>> getEjerciciosByRutinaId(@PathVariable Integer rutinaID) {
        List<Ejercicio> ejercicios = ejercicioRepository.findByRutinaID(rutinaID);
        if (ejercicios.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(ejercicios);
        }
    }

}
