package app.fichier.Utils;
import java.util.Date;
import java.util.List;

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
    @Value("${jwt.secret:#{systemEnvironment['JWT_SECRET']}}")
    private String jwtSecret; 
    
    public String generateToken(Authentication auth, long exp){
           SecretKey key = createSecretKey(getJwtSecret());
           Date date = new Date();
           Date expiration = new Date(date.getTime() + exp);
           return Jwts.builder()
                      .setSubject(auth.getName())
                      .issuedAt(expiration)
                      .claim("roles", auth.getAuthorities())
                      .signWith(key, SignatureAlgorithm.HS256)
                      .compact();
    }
    
    private String getJwtSecret() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET n'est pas défini. Définissez-le dans application.properties ou comme variable d'environnement.");
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
            return true;
            
        } catch (UnsupportedJwtException e) {
            log.error("Token non supporté :{}", e.getMessage());
        }
        catch (MalformedJwtException e){
            log.error("Token est malformé :{}", e.getMessage());

        }
        catch (IllegalArgumentException e){
            log.error("validation JWT :{}", e.getMessage());
        }
        catch(ExpiredJwtException e){
            log.error("Token est expiré : {}", e.getMessage());
        }
        catch (Exception e) {
            log.error("Erreur inattendue lors de la validation du token : {}", e.getMessage());
        }
        return false;
    }
    public Claims getClaimsFromToken(String jwt){
        SecretKey key = createSecretKey(getJwtSecret());
        return Jwts.parser()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(jwt)
                   .getBody();
        
    }
    public List getAuthorities(String jwt){
        return getClaimsFromToken(jwt).get("roles", List.class);
    }
    public String getSubject(String jwt){
        return getClaimsFromToken(jwt).getSubject();
    }
    public Date getExpirationDate(String jwt){
        return getClaimsFromToken(jwt).getExpiration();
    }
}
