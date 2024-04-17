package com.uablis.easyfitness.model;


import jakarta.persistence.*;

@Entity
@Table(name = "objetivo")
public class Objetivo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer objetivoID;

  @Column(nullable = false)
  private Double pesoObjetivo;

  private String descripcion;

  // Getters y setters
  public Integer getObjetivoID() {
    return objetivoID;
  }

  public void setObjetivoID(Integer objetivoID) {
    this.objetivoID = objetivoID;
  }

  public Double getPesoObjetivo() {
    return pesoObjetivo;
  }

  public void setPesoObjetivo(Double pesoObjetivo) {
    this.pesoObjetivo = pesoObjetivo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }
}
