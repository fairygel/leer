package me.fairygel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import me.fairygel.enums.CardStatus;
import org.hibernate.annotations.*;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @NotBlank
    @Column(name = "question", nullable = false)
    private String question;

    @NotBlank
    @Column(name = "answer", nullable = false)
    private String answer;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", columnDefinition = "card_statuses", nullable = false)
    private CardStatus status = CardStatus.NEW;

    @Column(name = "last_viewed_at")
    private OffsetDateTime lastViewedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}