package app.fichier.Controller;

import app.fichier.DTO.AdminDemande;
import app.fichier.Service.DemandeService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/admin/")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final DemandeService service;
    @GetMapping("/demandes")
    public Page<AdminDemande> getMethodName(@RequestParam(defaultValue ="0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String status,
    @RequestParam(required = false) String type,
    @RequestParam(required = false) String nomCommune
    ) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));
        return service.listerDemande(pageRequest, status, nomCommune, type);
        
    }
    

}
