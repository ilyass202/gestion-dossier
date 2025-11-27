package app.fichier.repositry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import app.fichier.Entity.Demande;
import app.fichier.Entity.Status;

public interface DemandeRepo extends JpaRepository<Demande, String>, JpaSpecificationExecutor<Demande>{
    Optional<Demande> findByIdAndCin(String id, String cin);

    @Query("SELECT d.typeAutorisation, COUNT(d) FROM Demande d GROUP BY d.typeAutorisation")
    List<Object[]> countByTypeAutorisation();
    long countByStatut(Status status);
}
