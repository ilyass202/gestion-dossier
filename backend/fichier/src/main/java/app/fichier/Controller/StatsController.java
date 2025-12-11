package app.fichier.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.fichier.DTO.StatsResponse;
import app.fichier.Service.StatsService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/stats")
public class StatsController {
    @Autowired
    private StatsService service;
    @GetMapping("/getStats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatsResponse> getStats(){
        var stats = service.getStats();
        return ResponseEntity.ok(stats);
    }
}
