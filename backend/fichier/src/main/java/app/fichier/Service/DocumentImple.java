package app.fichier.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import app.fichier.Exception.DocumentExceptionStorage;
import app.fichier.config.DirDocument;

@Service
public class DocumentImple implements DocumentService{

    private final Path documentPath;
    public DocumentImple(DirDocument dirDocument){
        this.documentPath = Paths.get(dirDocument.getDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.documentPath);
        } catch (Exception e) {
            throw new DocumentExceptionStorage("Erreur lors de la création du répertoire de stockage des documents", e);
        }
    }
    @Override
    public String uploadDocument(MultipartFile document) {
        if(document.isEmpty()){
            throw new DocumentExceptionStorage("Le fichier est vide" + document.getOriginalFilename());
        }
        String documentNom = document.getOriginalFilename();
        var documentDir = UUID.randomUUID() + documentNom;
        if(documentDir.contains("..")){
            throw new DocumentExceptionStorage("Le nom du fichier contient des caractères invalides" + documentNom);
        }
        try{
             Path documentLocation = this.documentPath.resolve(documentDir);
             Files.copy(document.getInputStream(), documentLocation, StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException e){
            throw new DocumentExceptionStorage("Erreur lors de la sauvegarde du fichier" + documentNom, e);
        }
        return documentDir;

    }

    @Override
    public Resource getDocument(String demandeId) {
        try {
       var documentLocation = this.documentPath.resolve(demandeId).normalize();
       var resource = new UrlResource(documentLocation.toUri());
       if(resource.isReadable() && resource.exists()){
        return resource;
       }
       else{
        throw new DocumentExceptionStorage("document pas trouvé" + documentLocation);
       }
}   catch(MalformedURLException e){
    throw new DocumentExceptionStorage("document pas trouvé" + e.getMessage());
}

}
}