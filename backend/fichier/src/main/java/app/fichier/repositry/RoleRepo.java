package app.fichier.repositry;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.fichier.Entity.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long>{
    Optional<Role> findByName(String name);

}
