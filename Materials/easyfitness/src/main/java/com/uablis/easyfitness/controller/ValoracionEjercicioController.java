package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.ValoracionEjercicio;
import com.uablis.easyfitness.repository.ValoracionEjercicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/valoracion_ejercicio")
public class ValoracionEjercicioController {

  @Autowired
  private ValoracionEjercicioRepository valoracionEjercicioRepository;

  // Obtener las valoraciones
  @GetMapping
  public List<ValoracionEjercicio> getAllValoracionEjercicios() {
    return valoracionEjercicioRepository.findAll();
  }

  // Obtener una valoracion por ID
  @GetMapping("/{id}")
  public ResponseEntity<ValoracionEjercicio> getValoracionEjercicioById(@PathVariable Integer id) {
    Optional<ValoracionEjercicio> valoracionEjercicio = valoracionEjercicioRepository.findById(id);
    return valoracionEjercicio.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Crear una nueva valoracion
  @PostMapping
  public ValoracionEjercicio createValoracionEjercicio(@RequestBody ValoracionEjercicio valoracionEjercicio) {
    return valoracionEjercicioRepository.save(valoracionEjercicio);
  }

  // Actualizar una serie
  @PutMapping("/{id}")
  public ResponseEntity<ValoracionEjercicio> updateValoracionEjercicio(@PathVariable Integer id, @RequestBody ValoracionEjercicio valoracionEjercicioDetails) {
    return valoracionEjercicioRepository.findById(id)
        .map(existingValoracionEjercicio -> {
          existingValoracionEjercicio.setUserID(valoracionEjercicioDetails.getUserID());
          existingValoracionEjercicio.setEjercicioID(valoracionEjercicioDetails.getEjercicioID());
          existingValoracionEjercicio.setValoracion(valoracionEjercicioDetails.getValoracion());
          existingValoracionEjercicio.setComentario(valoracionEjercicioDetails.getComentario());
          return ResponseEntity.ok(valoracionEjercicioRepository.save(existingValoracionEjercicio));
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Eliminar una valoracion
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteValoracionEjercicio(@PathVariable Integer id) {
    return valoracionEjercicioRepository.findById(id)
        .map(valoracionEjercicio -> {
          valoracionEjercicioRepository.delete(valoracionEjercicio);
          return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
