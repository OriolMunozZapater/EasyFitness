package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Integer> {
  // Añadir consultas personalizadas si es necesario
}
