package com.nco.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "NCO_BODY_HP_UPDATE", schema = "sql4404136")
public class NcoBodyHpUpdateEntity {
    private Long id;
    private String characterName;
    private Integer oldBody;
    private Integer newBody;
    private Integer oldMaxHp;
    private Integer newMaxHp;
    private String reason;
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
    @Column(name = "OldBody")
    public Integer getOldBody() {
        return oldBody;
    }

    public void setOldBody(Integer oldBody) {
        this.oldBody = oldBody;
    }

    @Basic
    @Column(name = "NewBody")
    public Integer getNewBody() {
        return newBody;
    }

    public void setNewBody(Integer newBody) {
        this.newBody = newBody;
    }

    @Basic
    @Column(name = "OldMaxHP")
    public Integer getOldMaxHp() {
        return oldMaxHp;
    }

    public void setOldMaxHp(Integer oldMaxHp) {
        this.oldMaxHp = oldMaxHp;
    }

    @Basic
    @Column(name = "NewMaxHP")
    public Integer getNewMaxHp() {
        return newMaxHp;
    }

    public void setNewMaxHp(Integer newMaxHp) {
        this.newMaxHp = newMaxHp;
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
        NcoBodyHpUpdateEntity that = (NcoBodyHpUpdateEntity) o;
        return Objects.equals(characterName, that.characterName) && Objects.equals(oldBody, that.oldBody) && Objects.equals(newBody, that.newBody) && Objects.equals(oldMaxHp, that.oldMaxHp) && Objects.equals(newMaxHp, that.newMaxHp) && Objects.equals(reason, that.reason) && Objects.equals(createdOn, that.createdOn) && Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterName, oldBody, newBody, oldMaxHp, newMaxHp, reason, createdOn, createdBy);
    }
}
