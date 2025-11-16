package app.fichier.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record AdminDetailsDemande(String id, String cin, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime date ,List<documentAdmin> documents, String status, String typeAuthorization, String motif, String nomCommune) {

}
