package app.fichier.DTO;

import java.util.Map;

public record StatsResponse(
    long total,
    long deposees,
    long enCours,
    long acceptees,
    long rejetees,
    Map<String, Long> parCommune,
    Map<String, Long> parType
) {

}
