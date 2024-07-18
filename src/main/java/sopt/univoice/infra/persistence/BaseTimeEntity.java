package sopt.univoice.infra.persistence;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = truncateToSeconds(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime());
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = truncateToSeconds(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime());
    }

    private LocalDateTime truncateToSeconds(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.truncatedTo(ChronoUnit.SECONDS) : null;
    }
}