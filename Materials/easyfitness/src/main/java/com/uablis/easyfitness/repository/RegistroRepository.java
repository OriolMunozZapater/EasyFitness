package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Integer> {
  List<Registro> findAllByUserID(Integer userID);
  // Añadir consultas personalizadas si es necesario
}
