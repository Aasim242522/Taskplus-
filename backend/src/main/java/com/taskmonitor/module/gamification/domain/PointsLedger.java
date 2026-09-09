package com.taskmonitor.module.gamification.domain;

import com.taskmonitor.module.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "points_ledger", indexes = {
        @Index(name = "idx_points_ledger_user_created", columnList = "user_id, created_at"),
        @Index(name = "idx_points_ledger_ref", columnList = "reference_type, reference_id")
})
public class PointsLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer pointsDelta;

    @Column(nullable = false)
    private Integer balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TransactionType transactionType;

    @Column(name = "reference_type", nullable = false, length = 50)
    private String referenceType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(nullable = false, length = 255)
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public PointsLedger() {
    }

    public PointsLedger(UUID id, User user, Integer pointsDelta, Integer balanceAfter, TransactionType transactionType, String referenceType, UUID referenceId, String description) {
        this.id = id;
        this.user = user;
        this.pointsDelta = pointsDelta;
        this.balanceAfter = balanceAfter;
        this.transactionType = transactionType;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.description = description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getPointsDelta() { return pointsDelta; }
    public void setPointsDelta(Integer pointsDelta) { this.pointsDelta = pointsDelta; }

    public Integer getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(Integer balanceAfter) { this.balanceAfter = balanceAfter; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public UUID getReferenceId() { return referenceId; }
    public void setReferenceId(UUID referenceId) { this.referenceId = referenceId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class Builder {
        private UUID id;
        private User user;
        private Integer pointsDelta;
        private Integer balanceAfter;
        private TransactionType transactionType;
        private String referenceType;
        private UUID referenceId;
        private String description;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder pointsDelta(Integer pointsDelta) { this.pointsDelta = pointsDelta; return this; }
        public Builder balanceAfter(Integer balanceAfter) { this.balanceAfter = balanceAfter; return this; }
        public Builder transactionType(TransactionType transactionType) { this.transactionType = transactionType; return this; }
        public Builder referenceType(String referenceType) { this.referenceType = referenceType; return this; }
        public Builder referenceId(UUID referenceId) { this.referenceId = referenceId; return this; }
        public Builder description(String description) { this.description = description; return this; }

        public PointsLedger build() {
            return new PointsLedger(id, user, pointsDelta, balanceAfter, transactionType, referenceType, referenceId, description);
        }
    }
}
