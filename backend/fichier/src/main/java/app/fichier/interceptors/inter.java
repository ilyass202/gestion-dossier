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
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

@Component
public class inter extends OncePerRequestFilter{

    private final JwtUtils jwtUtils;
    private final AuthUtilisateur authUtilisateur;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(inter.class);
    
    public inter(JwtUtils jwtUtils, AuthUtilisateur authUtilisateur){
        this.jwtUtils = jwtUtils;
        this.authUtilisateur = authUtilisateur;
    } 
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        // Ignorer les requêtes OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String token = getTokenFromrequest(request);
            
            if (StringUtils.hasText(token)) {
                
                if (jwtUtils.validateToken(token)) {
                    try {
                        String username = jwtUtils.getSubject(token);
                        UserDetails userDetails = authUtilisateur.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                            log.info("Rôles chargés pour {} : {}", username, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } catch (IllegalStateException e) {
                        log.warn("Impossible d'extraire les claims du token pour {}: {}", request.getRequestURI(), e.getMessage());
                        SecurityContextHolder.clearContext();
                    } catch (Exception e) {
                        log.error("Erreur lors du chargement de l'utilisateur pour {}: {}", request.getRequestURI(), e.getMessage(), e);
                        SecurityContextHolder.clearContext();
                    }
                } else {
                    log.warn("Token invalide pour la requête: {}", request.getRequestURI());
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.debug("Aucun token trouvé dans la requête: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            log.error("Erreur inattendue dans l'intercepteur JWT pour {}: {}", request.getRequestURI(), e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }
    private String getTokenFromrequest(HttpServletRequest request) {
       String bearer = request.getHeader("Authorization");
       if(StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")){
        return bearer.substring(7).trim();
       }
       return null;

    }
}
