package app.fichier.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import app.fichier.DTO.DemandeRequete;
import app.fichier.DTO.DemandeReponse;

import lombok.RequiredArgsConstructor;
import app.fichier.Service.DemandeService;

@RestController
@RequestMapping("/demande")
@RequiredArgsConstructor
public class DemandeController {
    private final DemandeService demandeservice;

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
    
}
