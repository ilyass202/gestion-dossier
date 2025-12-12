package app.fichier.Service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import app.fichier.Entity.Demande;

@Component
public class DemandeSpecification {
    public static Specification<Demande> byStatus(String Status){
        return (root, query, cb) -> cb.equal(root.get("status"), Status);

    }
    public static Specification<Demande> byType(String type){
       return (root, query, cb) -> cb.equal(root.get("typeAutorisation"), type);
    }
    public static Specification<Demande> byCommune(String commune){
        return (root, query, cb) -> cb.equal(root.get("commune").get("nomCommune"),commune);
    }

}
