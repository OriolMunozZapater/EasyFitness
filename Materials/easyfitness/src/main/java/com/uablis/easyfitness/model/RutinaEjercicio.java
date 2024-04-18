package com.uablis.easyfitness.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "rutina_ejercicio")
public class RutinaEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "rutinaID", nullable = false)
    private Integer rutinaId;

    @Column(name = "ejercicioID", nullable = false)
    private Integer ejercicioId;

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
        return rutinaId;
    }

    public void setRutinaId(Integer rutinaId) {
        this.rutinaId = rutinaId;
    }

    public Integer getEjercicioId() {
        return ejercicioId;
    }

    public void setEjercicioId(Integer ejercicioId) {
        this.ejercicioId = ejercicioId;
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
            Objects.equals(rutinaId, that.rutinaId) &&
            Objects.equals(ejercicioId, that.ejercicioId) &&
            Objects.equals(orden, that.orden);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rutinaId, ejercicioId, orden);
    }

    @Override
    public String toString() {
        return "RutinaEjercicio{" +
            "id=" + id +
            ", rutinaId=" + rutinaId +
            ", ejercicioId=" + ejercicioId +
            ", orden=" + orden +
            '}';
    }
}