package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Integer> {
  List<Rutina> findAllByUserID(Integer userID);
  List<Rutina> findByUserIDAndNombre(Integer userID, String nombre);
  @Query("SELECT r FROM Rutina r WHERE r.userID = :userId ORDER BY r.rutinaID DESC")
  Optional<Rutina> findTopByUserIdOrderByRutinaIdDesc(Integer userId);
}

