package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.RutinaEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicio, Integer> {
    // Puedes añadir métodos personalizados si es necesario
    List<RutinaEjercicio> findByRutinaId(Integer rutinaId);
}
