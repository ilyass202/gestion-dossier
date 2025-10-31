package app.fichier.Service;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


public interface DocumentService {
    String uploadDocument(MultipartFile file);
    Resource getDocument(String demandeId);
}
