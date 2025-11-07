package app.fichier.interceptors;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import app.fichier.Service.AuthUtilisateur;
import app.fichier.Utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

@Component
public class inter extends OncePerRequestFilter{

    private final JwtUtils jwtUtils;
    private final AuthUtilisateur authUtilisateur;
    public inter(JwtUtils jwtUtils, AuthUtilisateur authUtilisateur){
        this.jwtUtils = jwtUtils;
        this.authUtilisateur = authUtilisateur;
    } 
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = getTokenFromrequest(request);
        if(StringUtils.hasText(token) && jwtUtils.validateToken(token)){
           String nom = jwtUtils.getSubject(token);
           UserDetails details = authUtilisateur.loadUserByUsername(nom);
           var auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
           auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
           SecurityContextHolder.getContext().setAuthentication(auth);
        }
}
    private String getTokenFromrequest(HttpServletRequest request) {
       String bearer = request.getHeader("Authorization");
       if(StringUtils.hasText(bearer) && bearer.startsWith("Bearer")){
        return bearer.substring(7, bearer.length());
       }
       return null;

    }
}
