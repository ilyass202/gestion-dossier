package app.fichier.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import app.fichier.DTO.DemandeReponse;
import app.fichier.DTO.DemandeRequete;
import app.fichier.Entity.Demande;
import app.fichier.Entity.Document;
import app.fichier.Entity.Status;
import app.fichier.repositry.DemandeRepo;
import app.fichier.repositry.DocumentRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import app.fichier.DTO.DocumentResponse;

@Service
@RequiredArgsConstructor
public class DemandeService {
     private final DocumentRepo documentRepo;
     private final DemandeRepo demandeRepo;
     private final DocumentImple documentService;

     @Transactional
     public DemandeReponse creerDemande(DemandeRequete requete){
        Demande demande = new Demande();
      demande.setId(genererIdDemande());
      demande.setCin(requete.cin());
      demande.setTypeAutorisation(requete.typeAutorisation());
      demande.setStatus(Status.EN_COURS);
      demandeRepo.saveAndFlush(demande); 
      demande = demandeRepo.findById(demande.getId()).orElse(demande);
      List<DocumentResponse> documents = new ArrayList<>();
      List<MultipartFile> files = requete.fichiers();
      if(files != null && !files.isEmpty()){
        for(MultipartFile fichier : files){
            Document doc = new Document();
            String documentId = documentService.uploadDocument(fichier);
            doc.setId(documentId);
            doc.setNomFichier(fichier.getOriginalFilename());
            doc.setDemande(demande);
            documentRepo.save(doc);
            documents.add(
                new DocumentResponse(
                    documentId,
                    fichier.getOriginalFilename()
                )
            );
        }
      }
      return new DemandeReponse(
        demande.getStatus().toString(),demande.getId(), demande.getDateCreation(), documents
      );
     }

     private String genererIdDemande() {
       return "DEM" + UUID.randomUUID().toString().substring(0,8);
    }
}
