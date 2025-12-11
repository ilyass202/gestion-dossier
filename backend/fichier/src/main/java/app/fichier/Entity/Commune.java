package app.fichier.Entity;

import java.util.List;

import org.locationtech.jts.geom.Polygon;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "GEOMETRY(POLYGON,4326)")
    private Polygon polygone;

    private String codeCommune;
    private String nomCommune;
    private String province;

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
