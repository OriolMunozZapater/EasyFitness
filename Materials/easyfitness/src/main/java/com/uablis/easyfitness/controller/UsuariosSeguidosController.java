package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Usuario;
import com.uablis.easyfitness.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

  // Método para dejar de seguir a un usuario
  @DeleteMapping("/{usuarioId}/dejarSeguir/{seguidoId}")
  public ResponseEntity<String> dejarSeguirUsuario(@PathVariable Integer usuarioId, @PathVariable Integer seguidoId) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
    Optional<Usuario> seguidoOpt = usuarioRepository.findById(seguidoId);
    if (usuarioOpt.isPresent() && seguidoOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();
      usuario.getSeguidos().remove(seguidoOpt.get());
      usuarioRepository.save(usuario);
      return ResponseEntity.ok("Has dejado de seguir al usuario con éxito");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
  }

  // Método para obtener todos los usuarios que un usuario específico sigue
  @GetMapping("/{usuarioId}/seguidos")
  public ResponseEntity<List<Map<String, Object>>> obtenerUsuariosSeguidos(@PathVariable Integer usuarioId) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
    if (usuarioOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();
      List<Map<String, Object>> seguidosList = new ArrayList<>();
      for (Usuario seguido : usuario.getSeguidos()) {
        Map<String, Object> seguidoMap = new HashMap<>();
        seguidoMap.put("userID", seguido.getUserID());
        seguidoMap.put("nombre", seguido.getNombre());
        seguidoMap.put("apellido", seguido.getApellido());
        seguidoMap.put("correo", seguido.getCorreo());
        seguidoMap.put("sexo", seguido.getSexo());
        seguidoMap.put("peso_actual", seguido.getPeso_actual());
        seguidoMap.put("altura", seguido.getAltura());
        seguidoMap.put("descripcion", seguido.getDescripcion());
        seguidoMap.put("redes_sociales", seguido.getRedes_sociales());
        seguidoMap.put("foto", seguido.getFoto());
        seguidosList.add(seguidoMap);
      }
      return ResponseEntity.ok(seguidosList);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
  }

  // Método para obtener todos los seguidores de un usuario específico
  @GetMapping("/{usuarioId}/seguidores")
  public ResponseEntity<Set<Usuario>> obtenerSeguidores(@PathVariable Integer usuarioId) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
    if (usuarioOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();
      usuario.getSeguidores().forEach(seguidor -> seguidor.setSeguidos(null)); // Evitar recursión infinita
      return ResponseEntity.ok(usuario.getSeguidores());
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
  }
}

