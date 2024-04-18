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
          existingUsuario.setCorreo(usuarioDetails.getCorreo());
          existingUsuario.setPassword(usuarioDetails.getPassword());
          existingUsuario.setSexo(usuarioDetails.getSexo());
          existingUsuario.setPeso_actual(usuarioDetails.getPeso_actual());
          existingUsuario.setAltura(usuarioDetails.getAltura());
          // El campo foto se manejaría de manera especial si se actualiza a través de un formulario multipart/form-data
          // existingUsuario.setFoto(usuarioDetails.getFoto());
          existingUsuario.setDescripcion(usuarioDetails.getDescripcion());
          existingUsuario.setRedes_sociales(usuarioDetails.getRedes_sociales());
          existingUsuario.setTiempo_entrenamiento(usuarioDetails.getTiempo_entrenamiento());

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
