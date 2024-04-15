package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Integer> {
    // Puedes añadir métodos personalizados si es necesario
}
