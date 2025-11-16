package app.fichier.DTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record DemandeRequete(String cin, String typeAutorisation, double longitude, double latitude, List<MultipartFile> fichiers) {}
