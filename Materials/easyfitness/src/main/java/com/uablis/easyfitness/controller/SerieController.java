package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Serie;
import com.uablis.easyfitness.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/serie")
public class SerieController {

  @Autowired
  private SerieRepository serieRepository;

  // Obtener las series
  @GetMapping
  public List<Serie> getAllSeries() {
    return serieRepository.findAll();
  }

  // Obtener una serie por ID
  @GetMapping("/{id}")
  public ResponseEntity<Serie> getSerieById(@PathVariable Integer id) {
    Optional<Serie> serie = serieRepository.findById(id);
    return serie.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Crear una nueva serie
  @PostMapping
  public Serie createSerie(@RequestBody Serie serie) {
    return serieRepository.save(serie);
  }

  // Actualizar una serie
  @PutMapping("/{id}")
  public ResponseEntity<Serie> updateSerie(@PathVariable Integer id, @RequestBody Serie serieDetails) {
    return serieRepository.findById(id)
        .map(existingSerie -> {
          existingSerie.setEjercicioID(serieDetails.getEjercicioID());
          existingSerie.setNRepeticiones(serieDetails.getNRepeticiones());
          existingSerie.setPeso(serieDetails.getPeso());
          existingSerie.setComentarioSerie(serieDetails.getComentarioSerie());
          existingSerie.setTipo(serieDetails.getTipo());
          return ResponseEntity.ok(serieRepository.save(existingSerie));
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Eliminar una serie
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSerie(@PathVariable Integer id) {
    return serieRepository.findById(id)
        .map(serie -> {
          serieRepository.delete(serie);
          return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
