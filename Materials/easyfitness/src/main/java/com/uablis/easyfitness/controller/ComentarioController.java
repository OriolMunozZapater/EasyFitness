package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Comentario;
import com.uablis.easyfitness.model.Ejercicio;
import com.uablis.easyfitness.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

  @Autowired
  private ComentarioRepository comentarioRepository;

  // Obtener todos los ejercicios
  @PostMapping("/crear")
  public Comentario createEjercicio(@RequestBody Comentario comentario) {
    return comentarioRepository.save(comentario);
  }
}