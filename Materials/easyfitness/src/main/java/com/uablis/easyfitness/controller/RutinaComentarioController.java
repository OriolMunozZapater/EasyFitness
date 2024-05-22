package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.RutinaComentario;
import com.uablis.easyfitness.repository.RutinaComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rutina_comentarios")
public class RutinaComentarioController {

    @Autowired
    private RutinaComentarioRepository rutinaComentarioRepository;

    @GetMapping
    public List<RutinaComentario> getAllComentarios() {
        return rutinaComentarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutinaComentario> getComentarioById(@PathVariable Long id) {
        Optional<RutinaComentario> comentario = rutinaComentarioRepository.findById(id);
        if (comentario.isPresent()) {
            return ResponseEntity.ok(comentario.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/rutina/{idRutina}")
    public ResponseEntity<List<RutinaComentario>> getComentariosByIdRutina(@PathVariable Long idRutina) {
        List<RutinaComentario> comentarios = rutinaComentarioRepository.findByIdRutina(idRutina);
        if (!comentarios.isEmpty()) {
            return ResponseEntity.ok(comentarios);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public RutinaComentario createComentario(@RequestBody RutinaComentario comentario) {
        return rutinaComentarioRepository.save(comentario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutinaComentario> updateComentario(@PathVariable Long id, @RequestBody RutinaComentario comentarioDetails) {
        Optional<RutinaComentario> comentario = rutinaComentarioRepository.findById(id);
        if (comentario.isPresent()) {
            RutinaComentario existingComentario = comentario.get();
            existingComentario.setIdRutina(comentarioDetails.getIdRutina());
            existingComentario.setIdUsuario(comentarioDetails.getIdUsuario());
            existingComentario.setComentario(comentarioDetails.getComentario());
            RutinaComentario updatedComentario = rutinaComentarioRepository.save(existingComentario);
            return ResponseEntity.ok(updatedComentario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComentario(@PathVariable Long id) {
        if (rutinaComentarioRepository.existsById(id)) {
            rutinaComentarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}