package app.fichier.Entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseAuditing {
    @CreatedDate
    @Column(name="creé_le", nullable=false, updatable=false)
    private LocalDateTime dateCreation;

    @LastModifiedDate
    @Column(name = "modifié")
    private LocalDateTime dateModification;
}
