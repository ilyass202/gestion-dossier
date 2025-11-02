package app.fichier.Controller;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import app.fichier.DTO.DemandeRequete;
import app.fichier.Entity.Document;
import app.fichier.DTO.DemandeReponse;

import lombok.RequiredArgsConstructor;
import app.fichier.Service.DemandeService;
import app.fichier.Service.DocumentService;
import app.fichier.repositry.DocumentRepo;

@RestController
@RequestMapping("/demande")
@RequiredArgsConstructor
public class DemandeController {
    private final DemandeService demandeservice;
    private final DocumentRepo documentRepo;
    private final DocumentService documentService;

    @PostMapping(value="/envoyerDemande" , consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@RequestParam String typeAutorisation,
    @RequestParam String cinDemandeur,      // ← du mobile
    @RequestParam double latitude,
    @RequestParam double longitude,
    @RequestParam(value = "files", required = false) List<MultipartFile> files){
        DemandeRequete requete = new DemandeRequete(cinDemandeur, typeAutorisation, longitude, latitude, files);
        DemandeReponse reponse = demandeservice.creerDemande(requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }
    
    @GetMapping("/track")
    public ResponseEntity<?> track(
        @RequestParam String idDemande,
        @RequestParam String cinDemandeur
    ){
        try{
            DemandeReponse reponse = demandeservice.trackDemnande(idDemande, cinDemandeur);
            return ResponseEntity.ok(reponse);
        }catch(jakarta.persistence.EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Demande introuvable");
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/telecharger/{documentId}")
    public ResponseEntity<?> telecharger(
        @RequestParam String cin,
        @RequestParam String demandeId,
        @PathVariable String documentId
    ){
        Optional<Document> documentOpt = documentRepo.findById(documentId);
        if(documentOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document introuvable");
        }
        Document document = documentOpt.get();
        if(!document.getDemande().getCin().equals(cin) || !document.getDemande().getId().equals(demandeId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
        }
        Resource resource = documentService.getDocument(documentId);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
    
}
