package app.fichier.Service;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.fichier.DTO.StatsResponse;
import app.fichier.Entity.Status;
import app.fichier.repositry.CommuneRepo;
import app.fichier.repositry.DemandeRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final DemandeRepo demandeRepo;
    private final CommuneRepo communeRepo;

    public StatsResponse getStats() {
        Map<String, Long> parCommune = communeRepo.countDemandesByCommune()
            .stream()
            .filter(arr -> arr[0] != null) // Filtrer les clés null
            .collect(Collectors.toMap(
                arr -> (String) arr[0],
                arr -> arr[1] == null ? 0L : ((Number) arr[1]).longValue()
            ));

        Map<String, Long> parType = demandeRepo.countByTypeAutorisation()
            .stream()
            .filter(arr -> arr[0] != null) // Filtrer les clés null
            .collect(Collectors.toMap(
                arr -> (String) arr[0],
                arr -> arr[1] == null ? 0L : ((Number) arr[1]).longValue()
            ));

        return new StatsResponse(
            demandeRepo.count(),
            demandeRepo.countByStatus(Status.AVIS_FAVORABLE),
            demandeRepo.countByStatus(Status.EN_COURS),
            demandeRepo.countByStatus(Status.ACCEPTEE),
            demandeRepo.countByStatus(Status.REJETE),
            parCommune,
            parType
        );
    }
}