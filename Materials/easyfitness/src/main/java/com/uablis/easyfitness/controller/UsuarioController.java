package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Usuario;
import com.uablis.easyfitness.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
          if (usuarioDetails.getNombre() != null) {
            existingUsuario.setNombre(usuarioDetails.getNombre());
          }
          if (usuarioDetails.getApellido() != null) {
            existingUsuario.setApellido(usuarioDetails.getApellido());
          }
          if (usuarioDetails.getCorreo() != null) {
            existingUsuario.setCorreo(usuarioDetails.getCorreo());
          }
          if (usuarioDetails.getPassword() != null) {
            existingUsuario.setPassword(usuarioDetails.getPassword());
          }
          if (usuarioDetails.getSexo() != null) {
            existingUsuario.setSexo(usuarioDetails.getSexo());
          }
          if (usuarioDetails.getPeso_actual() != null) {
            existingUsuario.setPeso_actual(usuarioDetails.getPeso_actual());
          }
          if (usuarioDetails.getAltura() != null) {
            existingUsuario.setAltura(usuarioDetails.getAltura());
          }
          if (usuarioDetails.getFechaNacimiento() != null) {
            existingUsuario.setFechaNacimiento(usuarioDetails.getFechaNacimiento());
          }
          if (usuarioDetails.getDescripcion() != null) {
            existingUsuario.setDescripcion(usuarioDetails.getDescripcion());
          }
          if (usuarioDetails.getRedes_sociales() != null) {
            existingUsuario.setRedes_sociales(usuarioDetails.getRedes_sociales());
          }
          if (usuarioDetails.getTiempo_entrenamiento() != null) {
            existingUsuario.setTiempo_entrenamiento(usuarioDetails.getTiempo_entrenamiento());
          }
          existingUsuario.setFirstLogin(usuarioDetails.getFirstLogin());

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

  @GetMapping("/firstlogin")
  public ResponseEntity<?> getIsFirstLoginByEmail(@RequestParam String email) {
    return usuarioRepository.findByCorreo(email)
        .map(usuario -> ResponseEntity.ok(Collections.singletonMap("isFirstLogin", usuario.getFirstLogin())))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Encuentra el userID por correo electrónico
  @GetMapping("/findUserIdByEmail")
  public ResponseEntity<?> findUserIdByEmail(@RequestParam String email) {
    return usuarioRepository.findByCorreo(email)
        .map(usuario -> ResponseEntity.ok(Collections.singletonMap("userID", usuario.getUserID())))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

}
