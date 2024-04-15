package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.RutinaEjercicio;
import com.uablis.easyfitness.repository.RutinaEjercicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rutina_ejercicios")
public class RutinaEjercicioController {

    @Autowired
    private RutinaEjercicioRepository rutinaEjercicioRepository;

    // Obtener todos los registros de rutina_ejercicio
    @GetMapping
    public List<RutinaEjercicio> getAllRutinaEjercicios() {
        return rutinaEjercicioRepository.findAll();
    }

    // Obtener un registro de rutina_ejercicio por su ID
    @GetMapping("/{id}")
    public ResponseEntity<RutinaEjercicio> getRutinaEjercicioById(@PathVariable Integer id) {
        Optional<RutinaEjercicio> rutinaEjercicio = rutinaEjercicioRepository.findById(id);
        return rutinaEjercicio.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo registro de rutina_ejercicio
    @PostMapping
    public RutinaEjercicio createRutinaEjercicio(@RequestBody RutinaEjercicio rutinaEjercicio) {
        return rutinaEjercicioRepository.save(rutinaEjercicio);
    }

    // Actualizar un registro de rutina_ejercicio
    @PutMapping("/{id}")
    public ResponseEntity<RutinaEjercicio> updateRutinaEjercicio(@PathVariable Integer id, @RequestBody RutinaEjercicio rutinaEjercicioDetails) {
        return rutinaEjercicioRepository.findById(id)
                .map(existingRutinaEjercicio -> {
                    existingRutinaEjercicio.setRutinaId(rutinaEjercicioDetails.getRutinaId());
                    existingRutinaEjercicio.setEjercicioId(rutinaEjercicioDetails.getEjercicioId());
                    existingRutinaEjercicio.setOrden(rutinaEjercicioDetails.getOrden());
                    return ResponseEntity.ok(rutinaEjercicioRepository.save(existingRutinaEjercicio));
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar un registro de rutina_ejercicio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRutinaEjercicio(@PathVariable Integer id) {
        return rutinaEjercicioRepository.findById(id)
                .map(rutinaEjercicio -> {
                    rutinaEjercicioRepository.delete(rutinaEjercicio);
                    return ResponseEntity.ok().<Void>build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Métodos personalizados:
}

