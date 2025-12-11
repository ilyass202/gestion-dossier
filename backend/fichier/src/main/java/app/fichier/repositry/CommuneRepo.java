package app.fichier.repositry;

import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.fichier.Entity.Commune;

@Repository
public interface CommuneRepo extends JpaRepository<Commune, Long>{
    @Query(value = """
        SELECT c.nom_commune, COUNT(d.id_demande)
        FROM communes c
        LEFT JOIN demandes d ON d.commune_id = c.id
        GROUP BY c.nom_commune
        ORDER BY COUNT(d.id_demande) DESC
        """, nativeQuery = true)
    List<Object[]> countDemandesByCommune();
    
    @Query(value = """
            select c.* From communes c  where ST_Intersects(c.geom, :point) order by ST_Area(ST_Intersect(c.geom, :point)) desc limit 1
            """, nativeQuery = true)
    Commune calculateIntersection(@Param("point") Point point);

}
