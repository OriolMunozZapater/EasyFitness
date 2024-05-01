<<<<<<< HEAD
package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Registro;
import com.uablis.easyfitness.repository.RegistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/registros")
public class RegistroController {

  @Autowired
  private RegistroRepository registroRepository;

  // Obtener todos los registros
  @GetMapping
  public List<Registro> getAllRegistros() {
    return registroRepository.findAll();
  }

  // Obtener un registro por ID
  @GetMapping("/{id}")
  public ResponseEntity<Registro> getRegistroById(@PathVariable Integer id) {
    Optional<Registro> registro = registroRepository.findById(id);
    return registro.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Crear un nuevo registro
  @PostMapping
  public Registro createRegistro(@RequestBody Registro registro) {
    return registroRepository.save(registro);
  }

  // Actualizar un registro
  @PutMapping("/{id}")
  public ResponseEntity<Registro> updateRegistro(@PathVariable Integer id, @RequestBody Registro registroDetails) {
    return registroRepository.findById(id)
        .map(existingRegistro -> {
          existingRegistro.setNombreRutina(registroDetails.getNombreRutina());
          existingRegistro.setTiempoTardado(registroDetails.getTiempoTardado());
          existingRegistro.setUserID(registroDetails.getUserID());
          return ResponseEntity.ok(registroRepository.save(existingRegistro));
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Eliminar un registro
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRegistro(@PathVariable Integer id) {
    return registroRepository.findById(id)
        .map(registro -> {
          registroRepository.delete(registro);
          return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<Registro>> getRegistrosByUserId(@PathVariable Integer userId) {
    List<Registro> registros = registroRepository.findAllByUserID(userId);
    if (registros.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(registros);
    }
  }
}
=======
package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Registro;
import com.uablis.easyfitness.repository.RegistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/registros")
public class RegistroController {

  @Autowired
  private RegistroRepository registroRepository;

  // Obtener todos los registros
  @GetMapping
  public List<Registro> getAllRegistros() {
    return registroRepository.findAll();
  }

  // Obtener un registro por ID
  @GetMapping("/{id}")
  public ResponseEntity<Registro> getRegistroById(@PathVariable Integer id) {
    Optional<Registro> registro = registroRepository.findById(id);
    return registro.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Crear un nuevo registro
  @PostMapping
  public Registro createRegistro(@RequestBody Registro registro) {
    return registroRepository.save(registro);
  }

  // Actualizar un registro
  @PutMapping("/{id}")
  public ResponseEntity<Registro> updateRegistro(@PathVariable Integer id, @RequestBody Registro registroDetails) {
    return registroRepository.findById(id)
        .map(existingRegistro -> {
          existingRegistro.setNombreRutina(registroDetails.getNombreRutina());
          existingRegistro.setTiempoTardado(registroDetails.getTiempoTardado());
          existingRegistro.setUserID(registroDetails.getUserID());
          return ResponseEntity.ok(registroRepository.save(existingRegistro));
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Eliminar un registro
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRegistro(@PathVariable Integer id) {
    return registroRepository.findById(id)
        .map(registro -> {
          registroRepository.delete(registro);
          return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<Registro>> getRegistrosByUserId(@PathVariable Integer userId) {
    List<Registro> registros = registroRepository.findAllByUserID(userId);
    if (registros.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(registros);
    }
  }
}
>>>>>>> a897230213c6b8d9bf5816a808364420c2e13928
