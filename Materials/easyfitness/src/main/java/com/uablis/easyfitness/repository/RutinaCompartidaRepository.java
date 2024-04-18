package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.RutinaCompartida;
import com.uablis.easyfitness.model.RutinaCompartidaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutinaCompartidaRepository extends JpaRepository<RutinaCompartida, RutinaCompartidaId> {

  @Query("SELECT rc FROM RutinaCompartida rc WHERE rc.id.userID = :userID")
  List<RutinaCompartida> findByUserId(@Param("userID") Integer userID);
}

