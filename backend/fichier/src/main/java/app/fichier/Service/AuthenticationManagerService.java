package app.fichier.Service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import app.fichier.DTO.LoginRequete;
import app.fichier.Utils.JwtUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationManagerService {

    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    public String generateTokenAfterAutentication(LoginRequete requete){
        Authentication authentication = authenticationManager.authenticate(new 
        UsernamePasswordAuthenticationToken(requete.email(), requete.password()) 
        );
        if(authentication.isAuthenticated()){
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtUtils.generateToken(authentication, 3600000L);
        }
        return "";
    }

}
