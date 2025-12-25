package app.fichier.Service;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import app.fichier.DTO.LoginRequete;
import app.fichier.DTO.RegisterRequete;
import app.fichier.Entity.Utilisateur;
import app.fichier.Utils.JwtUtils;
import app.fichier.repositry.RoleRepo;
import app.fichier.repositry.UtilisateurRepo;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationManagerService {

    private final RoleRepo roleRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UtilisateurRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    public String generateTokenAfterAutentication(LoginRequete requete){
        Authentication authentication = authenticationManager.authenticate(new 
        UsernamePasswordAuthenticationToken(requete.email(), requete.password()) 
        );
        if(authentication.isAuthenticated()){
            SecurityContextHolder.getContext().setAuthentication(authentication);
             jwtUtils.generateToken(authentication, 3600000L);
        }
        return "";
    }
    public String createUser(RegisterRequete requete){
        if(userRepo.findByEmail(requete.email()).isPresent()){
            return "email est deja présent";
        }
        else{
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setEmaireturnl(requete.email());
            utilisateur.setPassword(passwordEncoder.encode(requete.password()));
            utilisateur.setRoles(List.of(roleRepo.findByRole("ROLE_USER").get()));
            userRepo.save(utilisateur);
            return "utilisateur enregistre";
        }
    }

}
