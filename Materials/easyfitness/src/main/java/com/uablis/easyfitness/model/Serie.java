package com.uablis.easyfitness.model;


import jakarta.persistence.*;

@Entity
@Table(name = "serie")
public class Serie {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer serieID;

  @Column(nullable = false)
  private Integer ejercicioID;

  private Integer nRepeticiones;

  private Double peso;

  private char[] comentarioSerie = new char[255];

  private char[] tipo = new char[50];

  // Getters y setters
  public Integer getEjercicioID() {
    return ejercicioID;
  }

  public void setEjercicioID(Integer ejercicioID) {
    this.ejercicioID = ejercicioID;
  }

  public Integer getNRepeticiones() {
    return nRepeticiones;
  }

  public void setNRepeticiones(Integer nRepeticiones) {
    this.nRepeticiones = nRepeticiones;
  }

  public Double getPeso() {
    return peso;
  }

  public void setPeso(Double peso) {
    this.peso = peso;
  }

  public char[] getComentarioSerie() {
    return comentarioSerie;
  }

  public void setComentarioSerie(char[] comentarioSerie) {
    this.comentarioSerie = comentarioSerie;
  }

  public char[] getTipo() {
    return tipo;
  }

  public void setTipo(char[] tipo) {
    this.tipo = tipo;
  }
}
