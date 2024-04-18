package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.RutinaEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicio, Integer> {
    // Puedes añadir métodos personalizados si es necesario
}
