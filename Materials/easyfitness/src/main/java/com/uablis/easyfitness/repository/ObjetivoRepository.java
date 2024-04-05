package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Objetivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjetivoRepository extends JpaRepository<Objetivo, Integer> {
  // Añadir consultas personalizadas si es necesario
}
