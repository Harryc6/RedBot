package com.nco.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "NCO_IMPROVE", schema = "sql4404136")
public class NcoImproveEntity {
    private Long id;
    private String characterName;
    private String reason;
    private Integer influencePoints;
    private Timestamp createdOn;
    private String createdBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "CharacterName")
    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    @Basic
    @Column(name = "Reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Basic
    @Column(name = "InfluencePoints")
    public Integer getInfluencePoints() {
        return influencePoints;
    }

    public void setInfluencePoints(Integer influencePoints) {
        this.influencePoints = influencePoints;
    }

    @Basic
    @Column(name = "CreatedOn")
    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    @Basic
    @Column(name = "CreatedBy")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NcoImproveEntity that = (NcoImproveEntity) o;
        return Objects.equals(characterName, that.characterName) && Objects.equals(reason, that.reason) && Objects.equals(influencePoints, that.influencePoints) && Objects.equals(createdOn, that.createdOn) && Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterName, reason, influencePoints, createdOn, createdBy);
    }
}
