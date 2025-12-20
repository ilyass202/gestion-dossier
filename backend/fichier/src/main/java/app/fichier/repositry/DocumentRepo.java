package app.fichier.repositry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import app.fichier.Entity.Document;
import app.fichier.Entity.Utilisateur;

@Repository
public interface DocumentRepo extends JpaRepository<Document, String>{
    List<Document> findByDemandeId(String demandeId);
    @Query("SELECT d FROM Document d WHERE d.id = :documentId AND d.demande.utilisateur = :user")
Optional<Document> findByIdAndDemandeUtilisateur(
    @Param("documentId") String documentId,
    @Param("user") Utilisateur user);
}
