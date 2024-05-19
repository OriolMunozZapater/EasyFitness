package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Integer> {
  // Añadir consultas personalizadas si es necesario
  List<Serie> findByEjercicioID(Integer ejercicioID);
}
