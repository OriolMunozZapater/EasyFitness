package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Ejercicio;
import com.uablis.easyfitness.model.RutinaEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicio, Integer> {
    List<RutinaEjercicio> findByRutinaID(Integer rutinaID);
    Optional<RutinaEjercicio> findByEjercicioIDAndRutinaID(Integer ejercicioID, Integer rutinaID);
}

