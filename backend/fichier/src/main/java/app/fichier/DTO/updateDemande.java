package app.fichier.DTO;

import app.fichier.Entity.Status;

public record updateDemande(Status status, String motifRejet) {

}
