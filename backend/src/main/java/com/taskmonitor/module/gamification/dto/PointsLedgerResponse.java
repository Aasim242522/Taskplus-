package com.taskmonitor.module.gamification.dto;

import com.taskmonitor.module.gamification.domain.TransactionType;

import java.time.Instant;
import java.util.UUID;

public class PointsLedgerResponse {

    private UUID id;
    private int pointsDelta;
    private int balanceAfter;
    private TransactionType transactionType;
    private String referenceType;
    private UUID referenceId;
    private String description;
    private Instant createdAt;

    public PointsLedgerResponse() {
    }

    public PointsLedgerResponse(UUID id, int pointsDelta, int balanceAfter, TransactionType transactionType, String referenceType, UUID referenceId, String description, Instant createdAt) {
        this.id = id;
        this.pointsDelta = pointsDelta;
        this.balanceAfter = balanceAfter;
        this.transactionType = transactionType;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public int getPointsDelta() { return pointsDelta; }
    public void setPointsDelta(int pointsDelta) { this.pointsDelta = pointsDelta; }

    public int getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(int balanceAfter) { this.balanceAfter = balanceAfter; }

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
        private int pointsDelta;
        private int balanceAfter;
        private TransactionType transactionType;
        private String referenceType;
        private UUID referenceId;
        private String description;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder pointsDelta(int pointsDelta) { this.pointsDelta = pointsDelta; return this; }
        public Builder balanceAfter(int balanceAfter) { this.balanceAfter = balanceAfter; return this; }
        public Builder transactionType(TransactionType transactionType) { this.transactionType = transactionType; return this; }
        public Builder referenceType(String referenceType) { this.referenceType = referenceType; return this; }
        public Builder referenceId(UUID referenceId) { this.referenceId = referenceId; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public PointsLedgerResponse build() {
            return new PointsLedgerResponse(id, pointsDelta, balanceAfter, transactionType, referenceType, referenceId, description, createdAt);
        }
    }
}
