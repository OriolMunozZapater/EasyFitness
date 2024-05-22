package com.uablis.easyfitness.repository;

import org.springframework.stereotype.Repository;
import com.uablis.easyfitness.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer>  {
}
