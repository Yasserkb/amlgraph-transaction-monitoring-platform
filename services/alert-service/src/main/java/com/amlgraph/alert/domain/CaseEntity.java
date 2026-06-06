package com.amlgraph.alert.domain;

import com.amlgraph.common.domain.CaseStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cases")
public class CaseEntity {
    @Id
    private UUID id;
    @Column(name = "alert_id", nullable = false)
    private UUID alertId;
    @Column(name = "assigned_analyst_id")
    private UUID assignedAnalystId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status;
    @Column(name = "str_required", nullable = false)
    private boolean strRequired;
    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;
    @Column(name = "closed_at")
    private Instant closedAt;

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CaseNoteEntity> notes = new ArrayList<>();

    protected CaseEntity() {}

    public CaseEntity(UUID id, UUID alertId, UUID assignedAnalystId, CaseStatus status,
                      boolean strRequired, Instant openedAt, Instant closedAt) {
        this.id = id;
        this.alertId = alertId;
        this.assignedAnalystId = assignedAnalystId;
        this.status = status;
        this.strRequired = strRequired;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
    }

    public void assign(UUID analystId) {
        this.assignedAnalystId = analystId;
        if (this.status == CaseStatus.NEW) {
            this.status = CaseStatus.IN_PROGRESS;
        }
    }

    public void updateStatus(CaseStatus status) {
        this.status = status;
        if (status == CaseStatus.CLOSED) {
            this.closedAt = Instant.now();
        }
    }

    public void addNote(UUID authorId, String content) {
        notes.add(new CaseNoteEntity(UUID.randomUUID(), this, authorId, content, Instant.now()));
    }

    public UUID getId() { return id; }
    public UUID getAlertId() { return alertId; }
    public UUID getAssignedAnalystId() { return assignedAnalystId; }
    public CaseStatus getStatus() { return status; }
    public boolean isStrRequired() { return strRequired; }
    public Instant getOpenedAt() { return openedAt; }
    public Instant getClosedAt() { return closedAt; }
    public List<CaseNoteEntity> getNotes() { return notes; }
}
