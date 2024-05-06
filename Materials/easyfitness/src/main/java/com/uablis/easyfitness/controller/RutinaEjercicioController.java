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
    public ResponseEntity<RutinaEjercicio> createRutinaEjercicio(@RequestBody RutinaEjercicio rutinaEjercicio) {
      RutinaEjercicio savedRutinaEjercicio = rutinaEjercicioRepository.save(rutinaEjercicio);
      return ResponseEntity.ok(savedRutinaEjercicio);
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
    @DeleteMapping("/delete/{ejercicioID}/{rutinaID}")
    public ResponseEntity<Void> deleteRutinaEjercicio(@PathVariable Integer ejercicioID, @PathVariable Integer rutinaID) {
      // Buscar el registro de rutina_ejercicio por ejercicioID y rutinaID
      Optional<RutinaEjercicio> rutinaEjercicioOptional = rutinaEjercicioRepository.findByEjercicioIDAndRutinaID(ejercicioID, rutinaID);

      // Verificar si se encontró el registro
      if (rutinaEjercicioOptional.isPresent()) {
        // Eliminar el registro encontrado
        rutinaEjercicioRepository.delete(rutinaEjercicioOptional.get());
        return ResponseEntity.ok().build(); // Se eliminó correctamente
      } else {
        return ResponseEntity.notFound().build(); // No se encontró el registro
      }
    }

    // Métodos personalizados:

  @GetMapping("/rutina/{rutinaID}")
  public ResponseEntity<?> findExercisesByRutinaID(@PathVariable Integer rutinaID) {
    List<RutinaEjercicio> rutinaEjercicios = rutinaEjercicioRepository.findByRutinaID(rutinaID);
    if (rutinaEjercicios.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(rutinaEjercicios);
  }


}

