package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Ejercicio;
import com.uablis.easyfitness.repository.EjercicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ejercicios")
public class EjercicioController {

  @Autowired
  private EjercicioRepository ejercicioRepository;

  // Obtener todos los ejercicios
  @GetMapping
  public List<Ejercicio> getAllEjercicios() {
    return ejercicioRepository.findAll();
  }

  // Obtener un ejercicio por ID
  @GetMapping("/{id}")
  public ResponseEntity<Ejercicio> getEjercicioById(@PathVariable Integer id) {
    Optional<Ejercicio> ejercicio = ejercicioRepository.findById(id);
    return ejercicio.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Crear un nuevo ejercicio
  @PostMapping("/crear")
  public Ejercicio createEjercicio(@RequestBody Ejercicio ejercicio) {
    return ejercicioRepository.save(ejercicio);
  }
  // Actualizar un ejercicio
  @PutMapping("/{id}")
  public ResponseEntity<Ejercicio> updateObjetivo(@PathVariable Integer id, @RequestBody Ejercicio ejercicioDetails) {
    return ejercicioRepository.findById(id)
        .map(existingEjercicio -> {
          existingEjercicio.setNombre(ejercicioDetails.getNombre());
          existingEjercicio.setDescripcion(ejercicioDetails.getDescripcion());
          existingEjercicio.setTipo(ejercicioDetails.getTipo());
          existingEjercicio.setValoracion(ejercicioDetails.getValoracion());
          existingEjercicio.setGrupoMuscular(ejercicioDetails.getGrupoMuscular());
          existingEjercicio.setUserID(ejercicioDetails.getUserID());
          existingEjercicio.setVideo(ejercicioDetails.getVideo());


          return ResponseEntity.ok(ejercicioRepository.save(existingEjercicio));
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Eliminar un ejercicio
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEjercicio(@PathVariable Integer id) {
    return ejercicioRepository.findById(id)
        .map(ejercicio -> {
          ejercicioRepository.delete(ejercicio);
          return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Métodos personalizados:

  @GetMapping("/name/{ejercicioID}")
  public ResponseEntity<?> findExercisesByExerciseID(@PathVariable String ejercicioID) {
    // Convertir los IDs de String a un array de Integer
    String[] idStrings = ejercicioID.split(",");
    Integer[] ids = new Integer[idStrings.length];
    for (int i = 0; i < idStrings.length; i++) {
      ids[i] = Integer.parseInt(idStrings[i].trim());
    }

    List<Ejercicio> ejercicios = new ArrayList<>();

    for (int id : ids) {
      Ejercicio ejercicio = ejercicioRepository.findByEjercicioID(id);
      if (ejercicio != null) {
        ejercicios.add(ejercicio);
      }
    }

    if (ejercicios.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(ejercicios);
  }

  @GetMapping("/user/{userID}")
  public ResponseEntity<?> findExercisesByUserID(@PathVariable Integer userID) {
    List<Ejercicio> ejercicios = ejercicioRepository.findByUserID(userID);
    if (ejercicios.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(ejercicios);
  }

  @GetMapping("/user/{userID}/muscle/{selectedMuscle}")
  public ResponseEntity<?> findExercisesByUserIDAndMuscles(@PathVariable Integer userID, @PathVariable String selectedMuscle) {
    List<Ejercicio> ejercicios = ejercicioRepository.findByUserIDAndGrupoMuscular(userID, selectedMuscle);
    if (ejercicios.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(ejercicios);
  }

  @GetMapping("/getEjercicioID")
  public ResponseEntity<?> getEjercicioByNomAndRutinaId(@RequestParam String nom, @RequestParam Integer rutinaID) {
    Optional<Ejercicio> ejercicio = ejercicioRepository.findByNombreAndRutinaID(nom, rutinaID);
    return ejercicio.map(e -> ResponseEntity.ok(Collections.singletonMap("ejercicioID", e.getEjercicioID())))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
  @GetMapping("/rutina/{rutinaID}")
  public ResponseEntity<List<Ejercicio>> getEjerciciosByRutinaId(@PathVariable Integer rutinaID) {
    List<Ejercicio> ejercicios = ejercicioRepository.findByRutinaID(rutinaID);
    if (ejercicios.isEmpty()) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.ok(ejercicios);
    }
  }
  // Añadir en EjercicioController

  @GetMapping("/sugerencias/{userID}")
  public ResponseEntity<List<Ejercicio>> getSugerencias(@PathVariable Integer userID) {
    List<Ejercicio> ejerciciosUsuario = ejercicioRepository.findByUserID(userID);

    // Contar los grupos musculares trabajados
    Map<String, Integer> muscleGroupCount = new HashMap<>();
    for (Ejercicio ejercicio : ejerciciosUsuario) {
      String grupoMuscular = ejercicio.getGrupoMuscular();
      muscleGroupCount.put(grupoMuscular, muscleGroupCount.getOrDefault(grupoMuscular, 0) + 1);
    }

    // Obtener todos los grupos musculares posibles
    List<String> allMuscleGroups = Arrays.asList("Pecho", "Espalda", "Piernas", "Todo el cuerpo");

    // Filtrar los grupos musculares menos trabajados
    List<String> leastWorkedMuscleGroups = new ArrayList<>();
    for (String muscleGroup : allMuscleGroups) {
      if (!muscleGroupCount.containsKey(muscleGroup)) {
        leastWorkedMuscleGroups.add(muscleGroup);
      }
    }

    // Si todos los grupos musculares han sido trabajados, seleccionar los menos trabajados
    if (leastWorkedMuscleGroups.isEmpty()) {
      List<Map.Entry<String, Integer>> sortedMuscleGroups = new ArrayList<>(muscleGroupCount.entrySet());
      sortedMuscleGroups.sort(Map.Entry.comparingByValue());
      for (Map.Entry<String, Integer> entry : sortedMuscleGroups) {
        leastWorkedMuscleGroups.add(entry.getKey());
        if (leastWorkedMuscleGroups.size() >= 3) break; // Limitar a los 3 menos trabajados
      }
    }

    // Obtener los ejercicios sugeridos para los grupos musculares menos trabajados
    List<Ejercicio> sugerencias = new ArrayList<>();
    for (String muscleGroup : leastWorkedMuscleGroups) {
      List<Ejercicio> ejerciciosSugeridos = ejercicioRepository.findByGrupoMuscular(muscleGroup);
      for (Ejercicio ejercicio : ejerciciosSugeridos) {
        if (!ejerciciosUsuario.contains(ejercicio)) {
          sugerencias.add(ejercicio);
        }
        if (sugerencias.size() >= 5) break; // Limitar a 5 sugerencias
      }
      if (sugerencias.size() >= 5) break; // Limitar a 5 sugerencias
    }

    return ResponseEntity.ok(sugerencias);
  }

  @GetMapping("/sugerencias/{userID}")
  public ResponseEntity<List<Ejercicio>> getSugerencias(@PathVariable Integer userID) {
    List<Ejercicio> ejerciciosUsuario = ejercicioRepository.findByUserID(userID);

    // Contar los grupos musculares trabajados
    Map<String, Integer> muscleGroupCount = new HashMap<>();
    for (Ejercicio ejercicio : ejerciciosUsuario) {
      String grupoMuscular = ejercicio.getGrupoMuscular();
      muscleGroupCount.put(grupoMuscular, muscleGroupCount.getOrDefault(grupoMuscular, 0) + 1);
    }

    // Obtener todos los grupos musculares posibles
    List<String> allMuscleGroups = Arrays.asList("Pecho", "Espalda", "Piernas", "Todo el cuerpo");

    // Filtrar los grupos musculares menos trabajados
    List<String> leastWorkedMuscleGroups = new ArrayList<>();
    for (String muscleGroup : allMuscleGroups) {
      if (!muscleGroupCount.containsKey(muscleGroup)) {
        leastWorkedMuscleGroups.add(muscleGroup);
      }
    }

    // Si todos los grupos musculares han sido trabajados, seleccionar los menos trabajados
    if (leastWorkedMuscleGroups.isEmpty()) {
      List<Map.Entry<String, Integer>> sortedMuscleGroups = new ArrayList<>(muscleGroupCount.entrySet());
      sortedMuscleGroups.sort(Map.Entry.comparingByValue());
      for (Map.Entry<String, Integer> entry : sortedMuscleGroups) {
        leastWorkedMuscleGroups.add(entry.getKey());
        if (leastWorkedMuscleGroups.size() >= 3) break; // Limitar a los 3 menos trabajados
      }
    }

    // Obtener los ejercicios sugeridos para los grupos musculares menos trabajados
    List<Ejercicio> sugerencias = new ArrayList<>();
    for (String muscleGroup : leastWorkedMuscleGroups) {
      List<Ejercicio> ejerciciosSugeridos = ejercicioRepository.findByGrupoMuscular(muscleGroup);
      for (Ejercicio ejercicio : ejerciciosSugeridos) {
        if (!ejerciciosUsuario.contains(ejercicio)) {
          sugerencias.add(ejercicio);
        }
        if (sugerencias.size() >= 5) break; // Limitar a 5 sugerencias
      }
      if (sugerencias.size() >= 5) break; // Limitar a 5 sugerencias
    }

    return ResponseEntity.ok(sugerencias);
  }
}
