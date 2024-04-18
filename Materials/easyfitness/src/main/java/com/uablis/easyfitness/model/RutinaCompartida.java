package com.uablis.easyfitness.model;


import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "rutina_compartida")
public class RutinaCompartida {

    @EmbeddedId
    private RutinaCompartidaId id;



    // Constructor
    public RutinaCompartida() {
    }

    // Getters y setters
    public RutinaCompartidaId getId() {
        return id;
    }

    public void setId(RutinaCompartidaId id) {
        this.id = id;
    }
}


