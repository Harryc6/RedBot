package com.nco.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "NCO_BUY_ARMOR", schema = "sql4404136")
public class NcoBuyArmorEntity {
    private Long id;
    private String characterName;
    private String headArmorType;
    private Integer headSp;
    private String bodyArmorType;
    private Integer bodySp;
    private String amount;
    private Timestamp creationOn;
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
    @Column(name = "HeadArmorType")
    public String getHeadArmorType() {
        return headArmorType;
    }

    public void setHeadArmorType(String headArmorType) {
        this.headArmorType = headArmorType;
    }

    @Basic
    @Column(name = "HeadSP")
    public Integer getHeadSp() {
        return headSp;
    }

    public void setHeadSp(Integer headSp) {
        this.headSp = headSp;
    }

    @Basic
    @Column(name = "BodyArmorType")
    public String getBodyArmorType() {
        return bodyArmorType;
    }

    public void setBodyArmorType(String bodyArmorType) {
        this.bodyArmorType = bodyArmorType;
    }

    @Basic
    @Column(name = "BodySP")
    public Integer getBodySp() {
        return bodySp;
    }

    public void setBodySp(Integer bodySp) {
        this.bodySp = bodySp;
    }

    @Basic
    @Column(name = "Amount")
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Basic
    @Column(name = "CreationOn")
    public Timestamp getCreationOn() {
        return creationOn;
    }

    public void setCreationOn(Timestamp creationOn) {
        this.creationOn = creationOn;
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
        NcoBuyArmorEntity that = (NcoBuyArmorEntity) o;
        return Objects.equals(characterName, that.characterName) && Objects.equals(headArmorType, that.headArmorType) && Objects.equals(headSp, that.headSp) && Objects.equals(bodyArmorType, that.bodyArmorType) && Objects.equals(bodySp, that.bodySp) && Objects.equals(amount, that.amount) && Objects.equals(creationOn, that.creationOn) && Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterName, headArmorType, headSp, bodyArmorType, bodySp, amount, creationOn, createdBy);
    }
}
