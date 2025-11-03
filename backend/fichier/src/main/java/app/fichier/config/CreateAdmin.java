package app.fichier.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import app.fichier.Entity.Role;
import app.fichier.Entity.Utilisateur;
import app.fichier.repositry.RoleRepo;
import app.fichier.repositry.UtilisateurRepo;
import jakarta.transaction.Transactional;

@Component
public class CreateAdmin implements ApplicationListener<ContextRefreshedEvent>{
    
    private boolean setup = false;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UtilisateurRepo utilisateurRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Transactional
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (setup) return;
        Role adminRole = CreateRoleIfNotFound("ROLE_ADMIN");
         CreateAdminUserIfNotFound("admin1@example.com", "admin1", adminRole, "EHTP1");
         CreateAdminUserIfNotFound("admin2@example.com", "admin2", adminRole, "EHTP2");
       setup = true;
    }
    private Utilisateur CreateAdminUserIfNotFound(String string, String string2, Role adminRole, String string3) {
       Utilisateur utilisateur = new Utilisateur();
       utilisateur.setEmail(string);
       utilisateur.setPassword(passwordEncoder.encode(string2));
       utilisateur.setNom(string3);
       utilisateur.setRoles(Arrays.asList(adminRole));
       return utilisateurRepo.save(utilisateur);

    }
    @Transactional
    private Role CreateRoleIfNotFound(String string) {
       Role role = roleRepo.findByRole(string).orElse(null);
       if(role==null){
        Role roleCreated = new Role();
        roleCreated.setRole(string);
        return roleRepo.save(roleCreated);
       }
       return role;
    }

}
