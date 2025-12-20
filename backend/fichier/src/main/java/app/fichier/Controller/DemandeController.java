package app.fichier.Controller;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import app.fichier.DTO.DemandeRequete;
import app.fichier.Entity.Document;
import app.fichier.Entity.Utilisateur;
import app.fichier.DTO.DemandeReponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import app.fichier.Service.DemandeService;
import app.fichier.Service.DocumentService;
import app.fichier.repositry.DocumentRepo;
import app.fichier.repositry.UtilisateurRepo;

@RestController
@Slf4j
@RequestMapping("/demande")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class DemandeController {
    private final DemandeService demandeservice;
    private final DocumentRepo documentRepo;
    private final DocumentService documentService;
    private final UtilisateurRepo userRepo;
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value="/envoyerDemande" , consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@RequestParam String typeAutorisation,
    @RequestParam String cinDemandeur,      // ← du mobile
    @RequestParam double latitude,
    @RequestParam double longitude,
    @RequestParam(value = "files", required = false) List<MultipartFile> files, Authentication auth){
        DemandeRequete requete = new DemandeRequete(cinDemandeur, typeAutorisation, longitude, latitude, files);
        DemandeReponse reponse = demandeservice.creerDemande(requete, auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/track")
    public ResponseEntity<?> track(
        @RequestParam String idDemande,
        @RequestParam String cinDemandeur, Authentication auth
    )
    
    {   
        try{
            Utilisateur utilisateur = userRepo.findByEmail(auth.getName()).get();
            DemandeReponse reponse = demandeservice.trackDemnande(idDemande, cinDemandeur, utilisateur);
            return ResponseEntity.ok(reponse);
        }catch(jakarta.persistence.EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Demande introuvable");
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/telecharger/{documentId}")
    public ResponseEntity<?> telecharger(
        @RequestParam String cin,
        @RequestParam String demandeId,
        @PathVariable String documentId, Authentication auth
    ){
    Utilisateur user = userRepo.findByEmail(auth.getName())
    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    Document document = documentRepo.findByIdAndDemandeUtilisateur(documentId, user)
    .orElseThrow(() -> new RuntimeException("Document introuvable ou accès refusé"));
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
