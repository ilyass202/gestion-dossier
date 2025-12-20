package app.fichier.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import app.fichier.DTO.AdminDemande;
import app.fichier.DTO.AdminDetailsDemande;
import app.fichier.DTO.DemandeReponse;
import app.fichier.DTO.DemandeRequete;
import app.fichier.Entity.Commune;
import app.fichier.Entity.Demande;
import app.fichier.Entity.Document;
import app.fichier.Entity.Status;
import app.fichier.Entity.Utilisateur;
import app.fichier.repositry.CommuneRepo;
import app.fichier.repositry.DemandeRepo;
import app.fichier.repositry.DocumentRepo;
import app.fichier.repositry.UtilisateurRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import app.fichier.DTO.DocumentResponse;
import app.fichier.DTO.documentAdmin;
import app.fichier.DTO.updateDemande;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandeService {
     private final DocumentRepo documentRepo;
     private final DemandeRepo demandeRepo;
     private final DocumentImple documentService;
     private final DemandeSpecification specification;
     private final CommuneRepo communeRepo;
     private final UtilisateurRepo utilisateurRepo;

     @Transactional
     public DemandeReponse creerDemande(DemandeRequete requete, Authentication auth){
      if(!auth.isAuthenticated() && auth == null){ throw new RuntimeException("utilisateur doit etre authentifié");}
      Demande demande = new Demande();
      demande.setUtilisateur(utilisateurRepo.findByEmail(auth.getName()).get());
      demande.setId(genererIdDemande());
      demande.setCin(requete.cin());
      demande.setTypeAutorisation(requete.typeAutorisation());
      demande.setStatus(Status.EN_COURS);
      GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
      Point point = factory.createPoint(new Coordinate(requete.longitude(), requete.latitude()));
      Commune commune = communeRepo.calculateIntersection(point);
      demande.setCommune(commune);
      demande.setPointGemotrique(point);
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
        demande.getStatus().toString(),
        demande.getId(), 
        demande.getDateCreation(),
        documents,
        null
      );
     }

    

     private String genererIdDemande() {
       return "DEM" + UUID.randomUUID().toString().substring(0,8);
    }


    public DemandeReponse trackDemnande(String id, String cin, Utilisateur utilisateur){
      Demande demande = demandeRepo.findByIdAndCinAndUtilisateur(id, cin, utilisateur).get();
        .orElseThrow(() -> new EntityNotFoundException("Demande introuvable"));
        List<DocumentResponse> documents = demande.getDocuments()
        .stream()
        .map(document -> new DocumentResponse(document.getId(), document.getNomFichier()))
        .collect(Collectors.toList());
      return new DemandeReponse(
        demande.getStatus().toString(),
        demande.getId(),
        demande.getDateCreation(),
        documents,
        demande.getMotifRejet()
      );
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminDemande> getAllDemandes(){
      List<Demande> demandes = demandeRepo.findAll();
      
     return demandes.stream().map(demande -> new AdminDemande(
         demande.getId(), 
         demande.getStatus() != null ? demande.getStatus().toString() : null, 
         demande.getDateCreation(), 
         todocumentAdmin(demande.getDocuments()), 
         demande.getCin(), 
         demande.getTypeAutorisation(),
         demande.getMotifRejet()
     )).collect(Collectors.toList());
    }
    
    private List<documentAdmin> todocumentAdmin(List<Document> documents) {
      if (documents == null || documents.isEmpty()) {
        return new ArrayList<>();
      }
      return documents.stream()
          .map(document -> new documentAdmin(document.getId(), document.getNomFichier()))
          .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<AdminDemande> listerDemande(
      Pageable pageable,
      String statut,
      String commune,
      String type) {

  Specification<Demande> spec = null;

  if (statut != null) {
    spec = DemandeSpecification.byStatus(statut);
  }
  if (commune != null) {
    spec = (spec == null) ? DemandeSpecification.byCommune(commune) : spec.and(DemandeSpecification.byCommune(commune));
  }
  if (type != null) {
    spec = (spec == null) ? DemandeSpecification.byType(type) : spec.and(DemandeSpecification.byType(type));
  }

  return (spec == null ? demandeRepo.findAll(pageable) : demandeRepo.findAll(spec, pageable))
          .map(demande -> new AdminDemande(
              demande.getId(), 
              demande.getStatus() != null ? demande.getStatus().toString() : null, 
              demande.getDateCreation(), 
              todocumentAdmin(demande.getDocuments()), 
              demande.getCin(), 
              demande.getTypeAutorisation(),
              demande.getMotifRejet()
          ));
}

  public AdminDetailsDemande getDetails(String id){
     Demande demande = demandeRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("demande n'est pas trouvé"));
     return new AdminDetailsDemande(
         demande.getId(),
         demande.getCin(),
         demande.getDateCreation(),
         todocumentAdmin(demande.getDocuments()),
         demande.getStatus() != null ? demande.getStatus().toString() : null,
         demande.getTypeAutorisation(),
         demande.getMotifRejet(),
         demande.getCommune() != null ? demande.getCommune().getNomCommune() : null
     );
  }
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public AdminDetailsDemande updateDemande(String id, updateDemande updateRequest) {
    Demande demande = demandeRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("demande n'est pas trouvé")); 
    demande.setStatus(updateRequest.status());
    
    if(updateRequest.status() == Status.REJETE){
      if(updateRequest.motifRejet() == null || updateRequest.motifRejet().trim().isEmpty()){
        throw new IllegalArgumentException("le motif de rejet est obligatoire");
      }
      demande.setMotifRejet(updateRequest.motifRejet().trim());
    } else {
      demande.setMotifRejet(null);
    }
    
    demandeRepo.saveAndFlush(demande);
    
    return new AdminDetailsDemande(
        demande.getId(),
        demande.getCin(),
        demande.getDateCreation(),
        todocumentAdmin(demande.getDocuments()),
        demande.getStatus() != null ? demande.getStatus().toString() : null,
        demande.getTypeAutorisation(),
        demande.getMotifRejet(),
        demande.getCommune() != null ? demande.getCommune().getNomCommune() : null
    );
  }

}
