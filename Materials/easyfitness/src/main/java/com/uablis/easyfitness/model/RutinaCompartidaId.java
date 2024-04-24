package com.uablis.easyfitness.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class RutinaCompartidaId implements Serializable {

    private Integer rutinaID;
    private Integer userID;

    public RutinaCompartidaId() {
    }

    public RutinaCompartidaId(Integer rutinaID, Integer userID) {
        this.rutinaID = rutinaID;
        this.userID = userID;
    }

    public Integer getRutinaID() {
        return rutinaID;
    }

    public void setRutinaID(Integer rutinaID) {
        this.rutinaID = rutinaID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }
}
