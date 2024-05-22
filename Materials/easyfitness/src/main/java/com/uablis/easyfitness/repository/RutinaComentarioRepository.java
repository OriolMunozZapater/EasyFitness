package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.RutinaComentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutinaComentarioRepository extends JpaRepository<RutinaComentario, Long> {
    List<RutinaComentario> findByIdRutina(Long idRutina);
}