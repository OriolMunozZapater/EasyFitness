package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Rutina;
import com.uablis.easyfitness.repository.RutinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rutinas")
public class RutinaController {

    @Autowired
    private RutinaRepository rutinaRepository;

  // Obtener todas las rutinas
    @GetMapping
    public List<Rutina> getAllRutinas() {
        return rutinaRepository.findAll();
    }

    // Obtener una rutina por ID
    @GetMapping("/{id}")
    public ResponseEntity<Rutina> getRutinaById(@PathVariable Integer id) {
        Optional<Rutina> rutina = rutinaRepository.findById(id);
        return rutina.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear una nueva rutina
    @PostMapping
    public Rutina createRutina(@RequestBody Rutina rutina) {
        return rutinaRepository.save(rutina);
    }

    // Actualizar una rutina
    @PutMapping("/{id}")
    public ResponseEntity<Rutina> updateRutina(@PathVariable Integer id, @RequestBody Rutina rutinaDetails) {
        return rutinaRepository.findById(id)
                .map(existingRutina -> {
                    existingRutina.setNombre(rutinaDetails.getNombre());
                    existingRutina.setDescripcion(rutinaDetails.getDescripcion());
                    existingRutina.setUserID(rutinaDetails.getUserID());
                    existingRutina.setPublico(rutinaDetails.isPublico());
                    return ResponseEntity.ok(rutinaRepository.save(existingRutina));
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar una rutina
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRutina(@PathVariable Integer id) {
      Optional<Rutina> rutinaOpt = rutinaRepository.findById(id);
      if (rutinaOpt.isPresent()) {
        Rutina rutina = rutinaOpt.get();
        rutinaRepository.delete(rutina);
        return ResponseEntity.ok().build();
      } else {
        return ResponseEntity.notFound().build();
      }
    }


    // Métodos personalizados:

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Rutina>> getRutinasByUserId(@PathVariable Integer userId) {
      List<Rutina> rutinas = rutinaRepository.findAllByUserID(userId);
      if (rutinas.isEmpty()) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(rutinas);
    }

}
