package app.fichier.Controller;

import app.fichier.DTO.AdminDemande;
import app.fichier.DTO.AdminDetailsDemande;
import app.fichier.DTO.updateDemande;
import app.fichier.Service.DemandeService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final DemandeService service;
    @GetMapping("/demandes")
    public ResponseEntity<Page<AdminDemande>> getMethodName(@RequestParam(defaultValue ="0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String status,
    @RequestParam(required = false) String type,
    @RequestParam(required = false) String nomCommune
    ) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));
        Page<AdminDemande> result = service.listerDemande(pageRequest, status, nomCommune, type);
        return ResponseEntity.ok().body(result);
        
    }
    @GetMapping("/details/{id}")
    public ResponseEntity<AdminDetailsDemande> MethodName(@PathVariable String id) {
        AdminDetailsDemande admin = service.getDetails(id);
        return ResponseEntity.ok().body(admin);
    }
    @PatchMapping("/demande/{id}/status")
    public ResponseEntity<AdminDetailsDemande> updateDetails(@PathVariable String id,  @RequestBody updateDemande demande){
        AdminDetailsDemande updateDemande = service.updateDemande(id, demande);
        return ResponseEntity.ok().body(updateDemande);
    }
    
    

}
