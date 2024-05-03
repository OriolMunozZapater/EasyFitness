package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Integer> {
  // Añadir consultas personalizadas si es necesario
  List<Ejercicio> findByRutinaID(int rutinaID);
  Ejercicio findByEjercicioID(int rutinaID);
}
