package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
  // Añadir métodos personalizados si necesitas hacer consultas específicas,

}
