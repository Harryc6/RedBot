package com.nco.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "NCO_INSTALL", schema = "sql4404136")
public class NcoInstallEntity {
    private Long id;
    private String characterName;
    private String product;
    private String dice;
    private Integer humanityLoss;
    private String cyberOrBorg;
    private String amount;
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
    @Column(name = "Product")
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Basic
    @Column(name = "Dice")
    public String getDice() {
        return dice;
    }

    public void setDice(String dice) {
        this.dice = dice;
    }

    @Basic
    @Column(name = "HumanityLoss")
    public Integer getHumanityLoss() {
        return humanityLoss;
    }

    public void setHumanityLoss(Integer humanityLoss) {
        this.humanityLoss = humanityLoss;
    }

    @Basic
    @Column(name = "CyberOrBorg")
    public String getCyberOrBorg() {
        return cyberOrBorg;
    }

    public void setCyberOrBorg(String cyberOrBorg) {
        this.cyberOrBorg = cyberOrBorg;
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
        NcoInstallEntity that = (NcoInstallEntity) o;
        return Objects.equals(characterName, that.characterName) && Objects.equals(product, that.product) && Objects.equals(dice, that.dice) && Objects.equals(humanityLoss, that.humanityLoss) && Objects.equals(cyberOrBorg, that.cyberOrBorg) && Objects.equals(amount, that.amount) && Objects.equals(createdOn, that.createdOn) && Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterName, product, dice, humanityLoss, cyberOrBorg, amount, createdOn, createdBy);
    }
}
