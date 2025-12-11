package app.fichier.Entity;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name="demande")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Demande extends BaseAuditing{
    @Id
    private String id;
    @Column(columnDefinition = "TEXT")
    private String motifRejet;
    private String cin;
    private String typeAutorisation;
    @ManyToOne(fetch = FetchType.EAGER
    )
    @JoinColumn(name="utilisateur")
    private Utilisateur utilisateur;
    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL)
    private List<Document> documents = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="demande_commune")
    private Commune commune;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "GEOMETRY(POINT, 4326)")
    private Point pointGemotrique;

    public void addDocument(Document document){
        this.documents.add(document);
        document.setDemande(this);
    }
    public void removeDocument(Document document){
        this.documents.remove(document);
        document.setDemande(null);
    }


}
