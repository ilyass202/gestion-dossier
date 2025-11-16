package app.fichier.DTO;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
public record DemandeReponse(
    String status, 
    String idDemande, 
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime date, 
    List<DocumentResponse> documents, 
    String motifRejet
) {

}
