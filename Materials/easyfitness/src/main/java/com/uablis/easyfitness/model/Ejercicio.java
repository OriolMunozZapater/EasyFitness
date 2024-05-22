package com.uablis.easyfitness.model;


import jakarta.persistence.*;

@Entity
@Table(name = "ejercicio")
public class Ejercicio {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer ejercicioID;

  @Column
  private Integer userID;
  @Column
  private Integer rutinaID;

  @Column(nullable = false)
  private String tipo;

  @Column(nullable = false)
  private String descripcion;

  @Column(nullable = false)
  private String nombre;

  private Double valoracion;

  @Column(nullable = false)
  private String grupoMuscular;

  private byte[] video;

  //Setters
  public Integer getRutinaID() {
    return rutinaID;
  }

  public void setRutinaID(Integer rutinaID) {
    this.rutinaID = rutinaID;
  }

  public void setVideo(byte[] video) {
    this.video = video;
  }

  public void setEjercicioID(Integer ejercicioID) {
    this.ejercicioID = ejercicioID;
  }

  public void setUserID(Integer userID) {
    this.userID = userID;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void setValoracion(Double valoracion) {
    this.valoracion = valoracion;
  }

  public void setGrupoMuscular(String grupoMuscular) {
    this.grupoMuscular = grupoMuscular;
  }

  // Getters
  public byte[] getVideo() {
    return this.video;
  }

  public Integer getEjercicioID() {
    return ejercicioID;
  }

  public Integer getUserID() {
    return userID;
  }

  public String getTipo() {
    return tipo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public String getNombre() {
    return nombre;
  }

  public Double getValoracion() {
    return valoracion;
  }

  public String getGrupoMuscular() {
    return grupoMuscular;
  }

  @Override
  public String toString() {
    return "Ejercicio{" +
        "ejercicioID=" + ejercicioID +
        ", userID='" + userID + '\'' +
        ", nombre='" + nombre + '\'' +
        ", descripcion='" + descripcion + '\'' +
        ", grupo_muscular='" + grupoMuscular +
        '}';
  }
}