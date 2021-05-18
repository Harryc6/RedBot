package com.nco.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "NCO_CRITICAL_INJURIES", schema = "sql4404136")
public class NcoCriticalInjuriesEntity {
    private Long id;
    private String characterName;
    private String injury;

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
    @Column(name = "Injury")
    public String getInjury() {
        return injury;
    }

    public void setInjury(String injury) {
        this.injury = injury;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NcoCriticalInjuriesEntity that = (NcoCriticalInjuriesEntity) o;
        return Objects.equals(characterName, that.characterName) && Objects.equals(injury, that.injury);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterName, injury);
    }
}
