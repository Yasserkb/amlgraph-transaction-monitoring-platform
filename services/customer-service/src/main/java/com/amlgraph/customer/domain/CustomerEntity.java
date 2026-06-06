package com.amlgraph.customer.domain;

import com.amlgraph.common.domain.RiskLevel;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class CustomerEntity {
    @Id
    private UUID id;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(nullable = false, length = 2)
    private String nationality;
    @Column(name = "country_of_residence", nullable = false, length = 2)
    private String countryOfResidence;
    @Column(name = "risk_score", nullable = false)
    private int riskScore;
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false)
    private KycStatus kycStatus;
    @Column(nullable = false)
    private boolean pep;
    @Column(nullable = false)
    private boolean sanctioned;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CustomerEntity() {}

    public CustomerEntity(UUID id, String fullName, String nationality, String countryOfResidence, int riskScore,
                          RiskLevel riskLevel, KycStatus kycStatus, boolean pep, boolean sanctioned,
                          Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.nationality = nationality;
        this.countryOfResidence = countryOfResidence;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.kycStatus = kycStatus;
        this.pep = pep;
        this.sanctioned = sanctioned;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void applyAlertImpact(int delta) {
        this.riskScore = Math.min(100, Math.max(0, this.riskScore + delta));
        this.riskLevel = CustomerRiskCalculator.toRiskLevel(this.riskScore);
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getNationality() { return nationality; }
    public String getCountryOfResidence() { return countryOfResidence; }
    public int getRiskScore() { return riskScore; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public KycStatus getKycStatus() { return kycStatus; }
    public boolean isPep() { return pep; }
    public boolean isSanctioned() { return sanctioned; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
