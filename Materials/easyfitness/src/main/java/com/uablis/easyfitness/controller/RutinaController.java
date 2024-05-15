package com.uablis.easyfitness.controller;

import com.uablis.easyfitness.model.Ejercicio;
import com.uablis.easyfitness.model.Rutina;
import com.uablis.easyfitness.model.RutinaEjercicio;
import com.uablis.easyfitness.model.Serie;
import com.uablis.easyfitness.repository.EjercicioRepository;
import com.uablis.easyfitness.repository.RutinaEjercicioRepository;
import com.uablis.easyfitness.repository.RutinaRepository;
import com.uablis.easyfitness.repository.SerieRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rutinas")
public class RutinaController {

    @Autowired
    private RutinaRepository rutinaRepository;
    @Autowired
    private RutinaEjercicioRepository rutinaEjercicioRepository;
    @Autowired
    private EjercicioRepository ejercicioRepository;
    @Autowired
    private SerieRepository serieRepository;
    private static final Logger log = LoggerFactory.getLogger(RutinaController.class);

    @PostMapping("/copiar/{rutinaId}")
    @Transactional
    public ResponseEntity<Rutina> copiarRutina(@PathVariable Integer rutinaId, @RequestBody Map<String, Object> body) {
      try {
        Integer userId = (Integer) body.get("userID");
        if (userId == null) {
          log.error("UserID no proporcionado");
          return ResponseEntity.badRequest().build();
        }
        Rutina original = rutinaRepository.findById(rutinaId)
            .orElseThrow(() -> new RuntimeException("Rutina no encontrada con ID: " + rutinaId));

        Rutina nuevaRutina = new Rutina();
        nuevaRutina.setNombre(original.getNombre());
        nuevaRutina.setDescripcion(original.getDescripcion());
        nuevaRutina.setUserID(userId);
        nuevaRutina.setPublico(false);
        Rutina savedRutina = rutinaRepository.save(nuevaRutina);

        copiarEjerciciosYRelaciones(original.getRutinaID(), savedRutina.getRutinaID(), userId);  // Include userId here
        return ResponseEntity.ok(savedRutina);
      } catch (DataAccessException e) {
        log.error("Error de acceso a datos: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      } catch (Exception e) {
        log.error("Error general: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    }

    private void copiarEjerciciosYRelaciones(Integer originalRutinaId, Integer nuevaRutinaId, Integer userId) {
      List<RutinaEjercicio> rutinaEjercicios = rutinaEjercicioRepository.findByRutinaID(originalRutinaId);
      for (RutinaEjercicio re : rutinaEjercicios) {
        Ejercicio originalEjercicio = ejercicioRepository.findById(re.getEjercicioId())
            .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado con ID: " + re.getEjercicioId()));

        Ejercicio nuevoEjercicio = new Ejercicio();
        nuevoEjercicio.setNombre(originalEjercicio.getNombre());
        nuevoEjercicio.setDescripcion(originalEjercicio.getDescripcion());
        nuevoEjercicio.setTipo(originalEjercicio.getTipo());
        nuevoEjercicio.setValoracion(originalEjercicio.getValoracion());
        nuevoEjercicio.setGrupoMuscular(originalEjercicio.getGrupoMuscular());
        nuevoEjercicio.setVideo(originalEjercicio.getVideo());
        nuevoEjercicio.setUserID(userId);
        nuevoEjercicio.setRutinaID(nuevaRutinaId);
        Ejercicio savedEjercicio = ejercicioRepository.save(nuevoEjercicio);

        RutinaEjercicio nuevoRE = new RutinaEjercicio();
        nuevoRE.setRutinaId(nuevaRutinaId);
        nuevoRE.setEjercicioId(savedEjercicio.getEjercicioID());
        nuevoRE.setOrden(re.getOrden());
        rutinaEjercicioRepository.save(nuevoRE);

        List<Serie> series = serieRepository.findByEjercicioID(originalEjercicio.getEjercicioID());
        for (Serie serie : series) {
          Serie nuevaSerie = new Serie();
          nuevaSerie.setEjercicioID(savedEjercicio.getEjercicioID());
          nuevaSerie.setNRepeticiones(serie.getNRepeticiones());
          nuevaSerie.setPeso(serie.getPeso());
          nuevaSerie.setComentarioSerie(serie.getComentarioSerie());
          nuevaSerie.setTipo(serie.getTipo());
          serieRepository.save(nuevaSerie);
        }
      }
    }



  // Obtener todas las rutinas
    @GetMapping
    public List<Rutina> getAllRutinas() {
        return rutinaRepository.findAll();
    }

    // Obtener una rutina por ID
    @GetMapping("/{id}")
    public ResponseEntity<Rutina> getRutinaById(@PathVariable Integer id) {
        Optional<Rutina> rutina = rutinaRepository.findById(id);
        return rutina.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear una nueva rutina
    @PostMapping
    public ResponseEntity<Object> createRutina(@RequestBody Rutina rutina) {
      log.info("Received Rutina: " + rutina.toString());
      if (rutina.getUserID() == null) {
        log.error("UserID is null");
        return ResponseEntity.badRequest().body(null);
      }
      try {
        Rutina savedRutina = rutinaRepository.save(rutina);
        return ResponseEntity.ok(savedRutina);
      } catch (Exception e) {
        log.error("Error saving Rutina: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
      }
    }

    // Actualizar una rutina
    @PutMapping("/{id}")
    public ResponseEntity<Rutina> updateRutina(@PathVariable Integer id, @RequestBody Rutina rutinaDetails) {
        return rutinaRepository.findById(id)
                .map(existingRutina -> {
                    existingRutina.setNombre(rutinaDetails.getNombre());
                    existingRutina.setDescripcion(rutinaDetails.getDescripcion());
                    existingRutina.setUserID(rutinaDetails.getUserID());
                    existingRutina.setPublico(rutinaDetails.isPublico());
                    return ResponseEntity.ok(rutinaRepository.save(existingRutina));
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar una rutina
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRutina(@PathVariable Integer id) {
      Optional<Rutina> rutinaOpt = rutinaRepository.findById(id);
      if (rutinaOpt.isPresent()) {
        Rutina rutina = rutinaOpt.get();
        rutinaRepository.delete(rutina);
        return ResponseEntity.ok().build();
      } else {
        return ResponseEntity.notFound().build();
      }
    }


    // Métodos personalizados:

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Rutina>> getRutinasByUserId(@PathVariable Integer userId) {
      List<Rutina> rutinas = rutinaRepository.findAllByUserID(userId);
      if (rutinas.isEmpty()) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(rutinas);
    }

  @GetMapping("/last/{userId}")
  public ResponseEntity<Integer> getRutinaMasGrandeByUserId(@PathVariable Integer userId) {
    Optional<Rutina> rutinaMasGrande = rutinaRepository.findTopByUserIDOrderByRutinaIDDesc(userId);

    if (rutinaMasGrande.isPresent()) {
      return ResponseEntity.ok(rutinaMasGrande.get().getRutinaID());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/buscar/userID/{userID}/nombre/{nombre}")
  public ResponseEntity<List<Rutina>> getRutinaByUserIdAndNombre(@RequestParam Integer userID, @RequestParam String nombre) {
    List<Rutina> rutinas = rutinaRepository.findByUserIDAndNombre(userID, nombre);
    if (rutinas.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(rutinas);
  }

  @PutMapping("/actualizarNombre/{id}/{nombre}")
  public ResponseEntity<Rutina> updateRutinaName(@PathVariable String nombre, @PathVariable Integer id) {
    return rutinaRepository.findById(id)
        .map(existingRutina -> {
          existingRutina.setNombre(nombre);
          return ResponseEntity.ok(rutinaRepository.save(existingRutina));
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
