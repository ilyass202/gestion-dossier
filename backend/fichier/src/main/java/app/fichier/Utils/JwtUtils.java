package app.fichier.Utils;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

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
    private final static String JWT_SECRET = System.getenv("JWT_SECRET"); 
    
    public String generateToken(Authentication auth, long exp){
           SecretKey key = createSecretKey(JWT_SECRET);
           Date date = new Date();
           Date expiration = new Date(date.getTime() + exp);
           return Jwts.builder()
                      .setSubject(auth.getName())
                      .issuedAt(expiration)
                      .claim("roles", auth.getAuthorities())
                      .signWith(key, SignatureAlgorithm.HS256)
                      .compact();
    }

    private SecretKey createSecretKey(String jwtSecret) {
       byte[] key = Decoders.BASE64.decode(jwtSecret);
       return Keys.hmacShaKeyFor(key);
    }
    public boolean validateToken(String jwt){
        boolean isValid = false;

        try {
            Jwts.parser()
                .setSigningKey(createSecretKey(jwt))
                .build()
                .parseClaimsJws(jwt);
            isValid = true;
            return isValid;
            
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
        return isValid;
    }
    public Claims getClaimsFromToken(String jwt){
        SecretKey key = createSecretKey(jwt);
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
