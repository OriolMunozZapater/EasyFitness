package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Objetivo;
import com.uablis.easyfitness.model.Usuario;
import com.uablis.easyfitness.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uablis.easyfitness.repository.ObjetivoRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Base64;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private ObjetivoRepository objetivoRepository;

  // Obtener todos los usuarios
  @GetMapping("/all_users")
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
          if (usuarioDetails.getGimnasio() != null) {
            existingUsuario.setGimnasio(usuarioDetails.getGimnasio());
          }
          if (usuarioDetails.getRedes_sociales() != null) {
            existingUsuario.setRedes_sociales(usuarioDetails.getRedes_sociales());
          }
          if (usuarioDetails.getTiempo_entrenamiento() != null) {
            existingUsuario.setTiempo_entrenamiento(usuarioDetails.getTiempo_entrenamiento());
          }

          // Manejo de la foto
          if (usuarioDetails.getFotoUrl() != null) {
            existingUsuario.setFotoUrl(usuarioDetails.getFotoUrl());
          }

          existingUsuario.setFirstLogin(false); // Directly set to false
          Usuario updatedUser = usuarioRepository.save(existingUsuario);
          return ResponseEntity.ok(updatedUser);
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

  private void updateUsuarioDetails(Usuario user, Map<String, Object> updateData) {
    Optional.ofNullable(updateData.get("nombre")).ifPresent(value -> user.setNombre(value.toString()));
    Optional.ofNullable(updateData.get("apellido")).ifPresent(value -> user.setApellido(value.toString()));
    Optional.ofNullable(updateData.get("correo")).ifPresent(value -> user.setCorreo(value.toString()));
    Optional.ofNullable(updateData.get("password")).ifPresent(value -> user.setPassword(value.toString()));
    Optional.ofNullable(updateData.get("sexo")).ifPresent(value -> user.setSexo(value.toString()));

    Optional.ofNullable(updateData.get("peso_actual")).ifPresent(value -> {
      if (value instanceof String) {
        user.setPeso_actual(Double.parseDouble((String) value));
      } else if (value instanceof Number) {
        user.setPeso_actual(((Number) value).doubleValue());
      }
    });

    Optional.ofNullable(updateData.get("altura")).ifPresent(value -> {
      if (value instanceof String) {
        user.setAltura(Integer.parseInt((String) value));
      } else if (value instanceof Number) {
        user.setAltura(((Number) value).intValue());
      }
    });

    Optional.ofNullable(updateData.get("fecha_nacimiento")).ifPresent(value -> user.setFechaNacimiento(parseDate(value.toString())));
    Optional.ofNullable(updateData.get("descripcion")).ifPresent(value -> user.setDescripcion(value.toString()));
    Optional.ofNullable(updateData.get("redes_sociales")).ifPresent(value -> user.setRedes_sociales(value.toString()));
    Optional.ofNullable(updateData.get("tiempo_entrenamiento")).ifPresent(value -> user.setTiempo_entrenamiento(value.toString()));
    Optional.ofNullable(updateData.get("gimnasio")).ifPresent(value -> user.setGimnasio(value.toString()));
    user.setFirstLogin(false);
  }


  private Objetivo createAndSaveObjective(Map<String, Object> updateData) {
    Objetivo objetivo = new Objetivo();
    Optional.ofNullable(updateData.get("peso_objetivo")).ifPresent(value -> {
      BigDecimal bd = new BigDecimal((String) value);
      objetivo.setPesoObjetivo(bd.doubleValue());
    });
    Optional.ofNullable(updateData.get("descripcion_objetivo")).ifPresent(value -> objetivo.setDescripcion((String) value));
    objetivoRepository.save(objetivo);
    return objetivo;
  }

  private Date parseDate(String dateString) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      return sdf.parse(dateString);
    } catch (ParseException e) {
      throw new RuntimeException("Error parsing date", e);
    }
  }

  // Métodos personalizados:
  @GetMapping("/{id}/objetivo")
  public ResponseEntity<?> getUsuarioObjetivo(@PathVariable Integer id) {
    return usuarioRepository.findById(id)
        .map(usuario -> {
          if (usuario.getObjetivo() != null) {
            return ResponseEntity.ok(Collections.singletonMap("pesoObjetivo", usuario.getObjetivo().getPesoObjetivo()));
          } else {
            return ResponseEntity.notFound().build();
          }
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/{userId}/objetivo/detalles")
  public ResponseEntity<Map<String, Object>> getObjetivoDetalles(@PathVariable Integer userId) {
    return (ResponseEntity<Map<String, Object>>) usuarioRepository.findById(userId)
        .map(usuario -> {
          Objetivo objetivo = usuario.getObjetivo();
          if (objetivo != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("peso_objetivo", objetivo.getPesoObjetivo());
            response.put("descripcion", objetivo.getDescripcion());
            return ResponseEntity.ok(response);
          } else {
            return ResponseEntity.notFound().build();
          }
        })
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}/updateWithObjective")
  public ResponseEntity<?> updateUsuarioWithObjective(@PathVariable Integer id, @RequestBody Map<String, Object> updateData) {
    return usuarioRepository.findById(id).map(user -> {
      updateUsuarioDetails(user, updateData);
      if (user.getObjetivo() != null) {
        if (updateData.containsKey("peso_objetivo")) {
          // Asegúrate de convertir correctamente aquí
          double pesoObjetivo = Double.parseDouble(updateData.get("peso_objetivo").toString());
          user.getObjetivo().setPesoObjetivo(pesoObjetivo);
        }
        if (updateData.containsKey("descripcion_objetivo")) {
          String descripcionObjetivo = updateData.get("descripcion_objetivo").toString();
          user.getObjetivo().setDescripcion(descripcionObjetivo);
        }
        objetivoRepository.save(user.getObjetivo());
      } else {
        // Crear y guardar un nuevo objetivo si no existe uno
        Objetivo newObjective = createAndSaveObjective(updateData);
        user.setObjetivo(newObjective);
      }
      usuarioRepository.save(user);
      return ResponseEntity.ok(user);
    }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // New endpoint to get followed users' details
  @GetMapping("/{userId}/seguidos/detalles")
  public ResponseEntity<List<Map<String, Object>>> getFollowedUsersDetails(@PathVariable Integer userId) {
    return usuarioRepository.findById(userId).map(usuario -> {
      Set<Usuario> followedUsers = usuario.getSeguidos();
      List<Map<String, Object>> followedUsersDetails = new ArrayList<>();

      for (Usuario followedUser : followedUsers) {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("nombre", followedUser.getNombre());
        userDetails.put("sexo", followedUser.getSexo());
        userDetails.put("peso_actual", followedUser.getPeso_actual());
        userDetails.put("foto", followedUser.getFotoUrl());
        userDetails.put("altura", followedUser.getAltura());
        userDetails.put("descripcion", followedUser.getDescripcion());
        userDetails.put("redes_sociales", followedUser.getRedes_sociales());
        followedUsersDetails.add(userDetails);
      }

      return ResponseEntity.ok(followedUsersDetails);
    }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/buscar/{id}")
  public ResponseEntity<Usuario> getUsuarioByID(@PathVariable Integer id) {
    Usuario usuario = usuarioRepository.findByUserID(id);
    if (usuario != null) {
      return ResponseEntity.ok(usuario);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

}
