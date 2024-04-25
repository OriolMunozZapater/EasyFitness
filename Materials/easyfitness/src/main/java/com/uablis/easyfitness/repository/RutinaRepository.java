package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Integer> {
  List<Rutina> findAllByUserID(Integer userID);
}

