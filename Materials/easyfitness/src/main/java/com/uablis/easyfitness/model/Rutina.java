package com.uablis.easyfitness.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rutina")
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rutinaID;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column(nullable = false)
    private Integer userID;

    @Column(nullable = false)
    private boolean publico;

    // Getters y setters
    public Integer getRutinaID() {
        return rutinaID;
    }

    public void setRutinaID(Integer rutinaID) {
        this.rutinaID = rutinaID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public boolean isPublico() {
        return publico;
    }

    public void setPublico(boolean publico) {
        this.publico = publico;
    }

    @Override
    public String toString() {
        return "Rutina{" +
                "rutinaID=" + rutinaID +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", userID=" + userID +
                ", publico=" + publico +
                '}';
    }
}
