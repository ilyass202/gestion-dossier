package app.fichier.repositry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import app.fichier.Entity.Demande;
import app.fichier.Entity.Status;
import app.fichier.Entity.Utilisateur;

public interface DemandeRepo extends JpaRepository<Demande, String>, JpaSpecificationExecutor<Demande>{
    Optional<Demande> findByIdAndCinAndUtilisateur(String id, String cin, Utilisateur utilisateur);

    @Query("SELECT d.typeAutorisation, COUNT(d) FROM Demande d GROUP BY d.typeAutorisation")
    List<Object[]> countByTypeAutorisation();
    long countByStatus(Status status);
}
