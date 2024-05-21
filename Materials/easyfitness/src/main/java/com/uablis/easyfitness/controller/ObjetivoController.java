package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Objetivo;
import com.uablis.easyfitness.repository.ObjetivoRepository;
import com.uablis.easyfitness.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/objetivos")
public class ObjetivoController {

  @Autowired
  private ObjetivoRepository objetivoRepository;

  // Obtener todos los objetivos
  @GetMapping
  public List<Objetivo> getAllObjetivos() {
    return objetivoRepository.findAll();
  }

  // Obtener un objetivo por ID
  @GetMapping("/{id}")
  public ResponseEntity<Objetivo> getObjetivoById(@PathVariable Integer id) {
    Optional<Objetivo> objetivo = objetivoRepository.findById(id);
    return objetivo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Crear un nuevo objetivo
  @PostMapping
  public Objetivo createObjetivo(@RequestBody Objetivo objetivo) {
    return objetivoRepository.save(objetivo);
  }

  // Actualizar un objetivo
  @PutMapping("/{id}")
  public ResponseEntity<Objetivo> updateObjetivo(@PathVariable Integer id, @RequestBody Objetivo objetivoDetails) {
    return objetivoRepository.findById(id)
        .map(existingObjetivo -> {
          existingObjetivo.setPesoObjetivo(objetivoDetails.getPesoObjetivo());
          existingObjetivo.setDescripcion(objetivoDetails.getDescripcion());
          return ResponseEntity.ok(objetivoRepository.save(existingObjetivo));
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Eliminar un objetivo
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteObjetivo(@PathVariable Integer id) {
    return objetivoRepository.findById(id)
        .map(objetivo -> {
          objetivoRepository.delete(objetivo);
          return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Métodos personalizados:

}
