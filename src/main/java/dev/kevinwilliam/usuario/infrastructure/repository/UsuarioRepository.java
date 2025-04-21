package dev.kevinwilliam.usuario.infrastructure.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository  extends JpaRepository <UsuarioRepository, Long> {

     boolean existsByEmail(String email);

     Optional<UsuarioRepository> findByEmail(String email);

     @Transactional
     void deleteByEmail(String email);

}
