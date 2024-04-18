package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.RutinaCompartida;
import com.uablis.easyfitness.model.RutinaCompartidaId;
import com.uablis.easyfitness.repository.RutinaCompartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rutinaCompartida")
public class RutinaCompartidaController {

    @Autowired
    private RutinaCompartidaRepository rutinaCompartidaRepository;

    // Obtener todas las rutinas compartidas
    @GetMapping
    public List<RutinaCompartida> getAllRutinasCompartidas() {
        return rutinaCompartidaRepository.findAll();
    }

    // Obtener una rutina compartida por IDs
    @GetMapping("/{rutinaID}/{userID}")
    public ResponseEntity<RutinaCompartida> getRutinaCompartidaById(@PathVariable Integer rutinaID, @PathVariable Integer userID) {
        Optional<RutinaCompartidaId> id = Optional.ofNullable(new RutinaCompartidaId(rutinaID, userID));
        Optional<RutinaCompartida> rutinaCompartida = id.flatMap(rutinaCompartidaRepository::findById);
        return rutinaCompartida.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Crear una nueva rutina compartida
    @PostMapping
    public RutinaCompartida createRutinaCompartida(@RequestBody RutinaCompartida rutinaCompartida) {
        return rutinaCompartidaRepository.save(rutinaCompartida);
    }

    // Actualizar una rutina compartida
    @PutMapping("/{rutinaID}/{userID}")
    public ResponseEntity<RutinaCompartida> updateRutinaCompartida(@PathVariable Integer rutinaID, @PathVariable Integer userID, @RequestBody RutinaCompartida rutinaCompartidaDetails) {
        Optional<RutinaCompartidaId> id = Optional.ofNullable(new RutinaCompartidaId(rutinaID, userID));
        Optional<RutinaCompartida> existingRutinaCompartida = id.flatMap(rutinaCompartidaRepository::findById);
        return existingRutinaCompartida.map(rutina -> {
            rutina.setId(rutinaCompartidaDetails.getId());
            return ResponseEntity.ok(rutinaCompartidaRepository.save(rutina));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar una rutina compartida
    @DeleteMapping("/{rutinaID}/{userID}")
    public ResponseEntity<Void> deleteRutinaCompartida(@PathVariable Integer rutinaID, @PathVariable Integer userID) {
        Optional<RutinaCompartidaId> id = Optional.ofNullable(new RutinaCompartidaId(rutinaID, userID));
        Optional<RutinaCompartida> rutinaCompartida = id.flatMap(rutinaCompartidaRepository::findById);
        if (rutinaCompartida.isPresent()) {
            rutinaCompartidaRepository.delete(rutinaCompartida.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<RutinaCompartida>> getRutinasCompartidasByUserId(@PathVariable Integer userId) {
        List<RutinaCompartida> rutinasCompartidas = rutinaCompartidaRepository.findByUserId(userId);
        if(rutinasCompartidas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rutinasCompartidas);
    }
}
