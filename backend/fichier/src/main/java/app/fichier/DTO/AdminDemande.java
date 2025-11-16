package app.fichier.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record AdminDemande(String id, String status, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime temps, List<documentAdmin> documents, String cin, String typeAutorization, String motif){
}
