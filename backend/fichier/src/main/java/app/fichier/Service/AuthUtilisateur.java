package app.fichier.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import app.fichier.Entity.Role;
import app.fichier.Entity.Utilisateur;
import app.fichier.repositry.RoleRepo;
import app.fichier.repositry.UtilisateurRepo;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthUtilisateur implements UserDetailsService{
    @Autowired
    private UtilisateurRepo utilisateurRepo;
    @Autowired
    private RoleRepo roleRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Utilisateur utilisateur = utilisateurRepo.findByEmail(username).orElseThrow(()-> new EntityNotFoundException("utilisateur n'est pas trouvé"));
       return new User(utilisateur.getEmail(), utilisateur.getPassword(), getAutorities(utilisateur.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAutorities(List<Role> roles) 
    {
        return roles.stream()
            .map(role -> {
                String roleName = role.getRole();
                // S'assurer que le rôle a le préfixe ROLE_ pour hasRole()
                // Si le rôle dans la BD est "ADMIN", il devient "ROLE_ADMIN"
                // Si le rôle dans la BD est déjà "ROLE_ADMIN", il reste "ROLE_ADMIN"
                if (!roleName.startsWith("ROLE_")) {
                    roleName = "ROLE_" + roleName;
                }
                return new SimpleGrantedAuthority(roleName);
            })
            .collect(Collectors.toList());
    }

}
