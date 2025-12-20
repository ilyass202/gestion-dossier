package app.fichier.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.fichier.DTO.JwtReponse;
import app.fichier.DTO.LoginRequete;
import app.fichier.DTO.RegisterRequete;
import app.fichier.Service.AuthenticationManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {
    private final AuthenticationManagerService service;
    @PostMapping("/login")
    public ResponseEntity<JwtReponse> login(@RequestBody LoginRequete requete) {
        String token = service.generateTokenAfterAutentication(requete);
        var reponse = new JwtReponse(token);
       return ResponseEntity.ok().body(reponse);
    }
    @PostMapping("/register")
    public ResponseEntity<?> postMethodName(@RequestBody RegisterRequete entity) {
        String savedUtilisateur = service.createUser(entity);
        return ResponseEntity.ok(savedUtilisateur);
    }
    
}
