package app.fichier.Utils;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtUtils {
    @Value("${JWT_SECRET}")
    private String jwtSecret; 
    
    public String generateToken(Authentication auth, long exp){
        try {
            SecretKey key = createSecretKey(getJwtSecret());
            Date now = new Date();
            long expirationTime = (exp > 0) ? exp : 24 * 60 * 60 * 1000L; 
            Date expiration = new Date(now.getTime() + expirationTime);
            List<String> roles = auth.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList());
            
            String token = Jwts.builder()
                          .setSubject(auth.getName())
                          .issuedAt(now)
                          .expiration(expiration)
                          .claim("roles", roles)
                          .signWith(key, SignatureAlgorithm.HS256)
                          .compact();
            
            log.debug("Token JWT généré pour l'utilisateur: {}, expiration: {}", auth.getName(), expiration);
            return token;
        } catch (Exception e) {
            log.error("Erreur lors de la génération du token JWT: {}", e.getMessage(), e);
            throw new IllegalStateException("Impossible de générer le token JWT", e);
        }
    }
    
    private String getJwtSecret() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET n'est pas défini. Définissez-le dans application.properties (JWT_SECRET ou jwt.secret) ou comme variable d'environnement.");
        }
        return jwtSecret;
    }

    private SecretKey createSecretKey(String jwtSecret) {
        try {
            byte[] key = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(key);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors du décodage de JWT_SECRET (doit être en Base64): {}", e.getMessage());
            throw new IllegalStateException("JWT_SECRET invalide. Il doit être encodé en Base64.", e);
        }
    }
    public boolean validateToken(String jwt){
        if (jwt == null || jwt.isEmpty()) {
            log.debug("Token JWT est null ou vide");
            return false;
        }

        try {
            Jwts.parser()
                .setSigningKey(createSecretKey(getJwtSecret()))
                .build()
                .parseClaimsJws(jwt);
            log.debug("Token JWT valide");
            return true;
            
        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expiré: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("Token JWT non supporté: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Token JWT malformé: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("Token JWT invalide (argument illégal): {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la validation du token JWT: {}", e.getMessage(), e);
            return false;
        }
    }
    public Claims getClaimsFromToken(String jwt) {
        if (jwt == null || jwt.isEmpty()) {
            throw new IllegalArgumentException("Le token JWT ne peut pas être null ou vide");
        }
        
        try {
            SecretKey key = createSecretKey(getJwtSecret());
            Claims claims = Jwts.parser()
                           .setSigningKey(key)
                           .build()
                           .parseClaimsJws(jwt)
                           .getBody();
            log.debug("Claims extraits du token JWT pour le sujet: {}", claims.getSubject());
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("Tentative d'extraction des claims d'un token expiré: {}", e.getMessage());
            throw new IllegalStateException("Le token JWT est expiré", e);
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT non supporté lors de l'extraction des claims: {}", e.getMessage());
            throw new IllegalStateException("Le token JWT n'est pas supporté", e);
        } catch (MalformedJwtException e) {
            log.error("Token JWT malformé lors de l'extraction des claims: {}", e.getMessage());
            throw new IllegalStateException("Le token JWT est malformé", e);
        } catch (IllegalArgumentException e) {
            log.error("Token JWT invalide lors de l'extraction des claims: {}", e.getMessage());
            throw new IllegalStateException("Le token JWT est invalide", e);
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'extraction des claims du token JWT: {}", e.getMessage(), e);
            throw new IllegalStateException("Erreur lors de l'extraction des claims du token JWT", e);
        }
    }
    public List getAuthorities(String jwt){
        try {
            Claims claims = getClaimsFromToken(jwt);
            return claims.get("roles", List.class);
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction des autorités du token JWT: {}", e.getMessage());
            throw e;
        }
    }
    
    public String getSubject(String jwt){
        try {
            Claims claims = getClaimsFromToken(jwt);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction du sujet du token JWT: {}", e.getMessage());
            throw e;
        }
    }
    

}
