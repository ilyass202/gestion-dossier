package app.fichier.repositry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.fichier.Entity.Document;

@Repository
public interface DocumentRepo extends JpaRepository<Document, String>{
    List<Document> findByDemande_Id(String demandeId);
}
