package com.uablis.easyfitness.controller;


import com.uablis.easyfitness.model.Usuario;
import com.uablis.easyfitness.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

  @Autowired
  private UsuarioRepository usuarioRepository;

  // Obtener todos los usuarios
  @GetMapping
  public List<Usuario> getAllUsuarios() {
    return usuarioRepository.findAll();
  }

  // Obtener un usuario por ID
  @GetMapping("/{id}")
  public ResponseEntity<Usuario> getUsuarioById(@PathVariable Integer id) {
    Optional<Usuario> usuario = usuarioRepository.findById(id);
    return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Crear un nuevo usuario
  @PostMapping
  public Usuario createUsuario(@RequestBody Usuario usuario) {
    return usuarioRepository.save(usuario);
  }

  // Actualizar un usuario
  @PutMapping("/{id}")
  public ResponseEntity<Usuario> updateUsuario(@PathVariable Integer id, @RequestBody Usuario usuarioDetails) {
    return usuarioRepository.findById(id)
        .map(existingUsuario -> {
          existingUsuario.setNombre(usuarioDetails.getNombre());
          existingUsuario.setApellido(usuarioDetails.getApellido());
          // Setear el resto de propiedades que desees actualizar
          return ResponseEntity.ok(usuarioRepository.save(existingUsuario));
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Eliminar un usuario
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id) {
    return usuarioRepository.findById(id)
        .map(usuario -> {
          usuarioRepository.delete(usuario);
          return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Añadir métodos para manejar otras peticiones como
  // PUT, PATCH y DELETE si son necesarios
}
