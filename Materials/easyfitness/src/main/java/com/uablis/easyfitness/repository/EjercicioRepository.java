package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Integer> {
  // Añadir consultas personalizadas si es necesario
  List<Ejercicio> findByRutinaID(Integer rutinaID);
  Ejercicio findByEjercicioID(int ejercicioID);
  List<Ejercicio> findByUserID(int userID);
  List<Ejercicio> findByUserIDAndGrupoMuscular(Integer userID, String grupoMuscular);
  Optional<Ejercicio> findByNombreAndRutinaID(String nom, Integer rutinaID);
  List<Ejercicio> findByGrupoMuscular(String grupoMuscular);
}