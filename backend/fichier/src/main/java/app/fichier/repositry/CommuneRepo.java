package app.fichier.repositry;

import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.fichier.Entity.Commune;

@Repository
public interface CommuneRepo extends JpaRepository<Commune, String>{
    @Query(value = """
        SELECT c.nom_commun, COUNT(d.id)
        FROM communes c
        LEFT JOIN demande d ON d.demande_commune = c.code_commu
        GROUP BY c.nom_commun
        ORDER BY COUNT(d.id) DESC
        """, nativeQuery = true)
    List<Object[]> countDemandesByCommune();
    

     @Query(value = """
             select c.* From communes c  where ST_Intersects(c.geom, :point) order by ST_Area(ST_Intersection(c.geom, :point)) desc limit 1
             """, nativeQuery = true)
     Commune calculateIntersection(@Param("point") Point point);
    

}
