package com.uablis.easyfitness.model;

import jakarta.persistence.*;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "rutina_ejercicio")
public class RutinaEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "rutinaID", nullable = false)
    private Integer rutinaID;

    @Column(name = "ejercicioID", nullable = false)
    private Integer ejercicioID;

    @Column(nullable = false)
    private Integer orden;

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRutinaId() {
        return rutinaID;
    }

    public void setRutinaId(Integer rutinaId) {
        this.rutinaID = rutinaId;
    }

    public Integer getEjercicioId() {
        return ejercicioID;
    }

    public void setEjercicioId(Integer ejercicioId) {
        this.ejercicioID = ejercicioId;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RutinaEjercicio)) return false;
        RutinaEjercicio that = (RutinaEjercicio) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(rutinaID, that.rutinaID) &&
            Objects.equals(ejercicioID, that.ejercicioID) &&
            Objects.equals(orden, that.orden);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rutinaID, ejercicioID, orden);
    }

    @Override
    public String toString() {
        return "RutinaEjercicio{" +
            "Id=" + id +
            ", rutinaId=" + rutinaID +
            ", ejercicioId=" + ejercicioID +
            ", orden=" + orden +
            '}';
    }

}