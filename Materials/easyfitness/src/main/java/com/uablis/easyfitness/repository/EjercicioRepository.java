package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Integer> {
  // Añadir consultas personalizadas si es necesario
  Ejercicio findByEjercicioID(int rutinaID);
  List<Ejercicio> findByUserID(int userID);
  List<Ejercicio> findByUserIDAndGrupoMuscular(Integer userID, String grupoMuscular);

}