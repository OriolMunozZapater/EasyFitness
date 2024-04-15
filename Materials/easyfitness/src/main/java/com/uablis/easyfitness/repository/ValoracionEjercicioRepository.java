package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.ValoracionEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValoracionEjercicioRepository extends JpaRepository<ValoracionEjercicio, Integer> {
}
