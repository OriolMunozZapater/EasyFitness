package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Ejercicio;
import com.uablis.easyfitness.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Integer> {
    List<Ejercicio> findByRutinaID(Integer rutinaID);
    Optional<Ejercicio> findByNombreAndRutinaID(String nom, Integer rutinaID);
}
