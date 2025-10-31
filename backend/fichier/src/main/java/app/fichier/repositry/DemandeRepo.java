package app.fichier.repositry;

import org.springframework.data.jpa.repository.JpaRepository;

import app.fichier.Entity.Demande;
public interface DemandeRepo extends JpaRepository<Demande, String>{
       
}
