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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import app.fichier.DTO.AdminDemande;
import app.fichier.DTO.DemandeReponse;
import app.fichier.DTO.DemandeRequete;
import app.fichier.Entity.Demande;
import app.fichier.Entity.Document;
import app.fichier.Entity.Status;
import app.fichier.repositry.DemandeRepo;
import app.fichier.repositry.DocumentRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import app.fichier.DTO.DocumentResponse;
import app.fichier.DTO.documentAdmin;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandeService {
     private final DocumentRepo documentRepo;
     private final DemandeRepo demandeRepo;
     private final DocumentImple documentService;
     private final DemandeSpecification specification;

     @Transactional
     public DemandeReponse creerDemande(DemandeRequete requete){
        Demande demande = new Demande();
      demande.setId(genererIdDemande());
      demande.setCin(requete.cin());
      demande.setTypeAutorisation(requete.typeAutorisation());
      demande.setStatus(Status.EN_COURS);
      GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
      Point point = factory.createPoint(new Coordinate(requete.longitude(), requete.latitude()));
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
        demande.getStatus().toString(),demande.getId(), demande.getDateCreation(), documents
      );
     }

     private String genererIdDemande() {
       return "DEM" + UUID.randomUUID().toString().substring(0,8);
    }


    public DemandeReponse trackDemnande(String id, String cin){
      Demande demande = demandeRepo.findByIdAndCin(id, cin)
        .orElseThrow(() -> new EntityNotFoundException("Demande introuvable"));
        List<DocumentResponse> documents = demande.getDocuments()
        .stream()
        .map(document -> new DocumentResponse(document.getId(), document.getNomFichier()))
        .collect(Collectors.toList());
      return new DemandeReponse(
        demande.getStatus().toString(),
        demande.getId(),
        demande.getDateCreation(),
        documents
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
         demande.getTypeAutorisation()
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
              demande.getTypeAutorisation()
          ));
}
}
