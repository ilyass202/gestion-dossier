package app.fichier.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.fichier.DTO.StatsResponse;
import app.fichier.Service.StatsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;
    @GetMapping("/getStats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatsResponse> getStats(){
        var stats = service.getStats();
        return ResponseEntity.ok(stats);
    }
}
