package app.fichier.Entity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="document")
@Data
public class Document {
    @Id
    private String id;
    private String nomFichier;
    private String typeFichier;
    @ManyToOne(fetch = FetchType.LAZY)
    private Demande demande;

}
