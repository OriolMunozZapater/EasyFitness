package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Usuario;
import com.uablis.easyfitness.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosSeguidosController {

  @Autowired
  private UsuarioRepository usuarioRepository;

  // Método para seguir a un usuario
  @PostMapping("/{usuarioId}/seguir/{seguidoId}")
  public ResponseEntity<String> seguirUsuario(@PathVariable Integer usuarioId, @PathVariable Integer seguidoId) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
    Optional<Usuario> seguidoOpt = usuarioRepository.findById(seguidoId);
    if (usuarioOpt.isPresent() && seguidoOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();
      usuario.getSeguidos().add(seguidoOpt.get());
      usuarioRepository.save(usuario);
      return ResponseEntity.ok("Usuario seguido con éxito");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
  }

}

