package app.fichier.Entity;

import java.util.List;

import org.locationtech.jts.geom.MultiPolygon;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="communes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Commune {
    @Id
    @Column(name="code_commu")
    private String codeCommune;
    @Column(name = "geom", columnDefinition = "GEOMETRY(POLYGON,4326)")
    private MultiPolygon polygone;
    @Column(name="nom_commun")
    private String nomCommune;

    @OneToMany(mappedBy = "commune", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Demande> demandes;

    public void addDemande(Demande demande){
        this.demandes.add(demande);
        demande.setCommune(this);
    }
    public void removeDemande(Demande demande){
        this.demandes.remove(demande);
        demande.setCommune(null);
    }

}
