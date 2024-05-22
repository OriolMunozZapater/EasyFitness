package com.uablis.easyfitness.repository;

import com.uablis.easyfitness.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
  // Añadir métodos personalizados si necesitas hacer consultas específicas,
  Optional<Usuario> findByCorreo(String email);
  Usuario findByUserID(Integer userID);
}