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
            .collect(Collectors.toMap(
                arr -> (String) arr[0],
                arr -> arr[1] == null ? 0L : (Long) arr[1]
            ));

        Map<String, Long> parType = demandeRepo.countByTypeAutorisation()
            .stream()
            .collect(Collectors.toMap(
                arr -> (String) arr[0],
                arr -> (Long) arr[1]
            ));

        return new StatsResponse(
            demandeRepo.count(),
            demandeRepo.countByStatut(Status.AVIS_FAVORABLE),
            demandeRepo.countByStatut(Status.EN_COURS),
            demandeRepo.countByStatut(Status.ACCEPTEE),
            demandeRepo.countByStatut(Status.REJETE),
            parCommune,
            parType
        );
    }
}