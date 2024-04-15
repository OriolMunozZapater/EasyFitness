package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.RutinaCompartida;
import com.uablis.easyfitness.model.RutinaCompartidaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RutinaCompartidaRepository extends JpaRepository<RutinaCompartida, RutinaCompartidaId> {
    // Añadir consultas personalizadas si es necesario
}