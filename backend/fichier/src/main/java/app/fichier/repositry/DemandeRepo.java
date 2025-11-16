package app.fichier.repositry;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import app.fichier.Entity.Demande;

public interface DemandeRepo extends JpaRepository<Demande, String>, JpaSpecificationExecutor<Demande>{
    Optional<Demande> findByIdAndCin(String id, String cin);
}
