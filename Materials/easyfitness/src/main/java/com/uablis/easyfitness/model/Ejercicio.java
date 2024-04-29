package com.uablis.easyfitness.model;


import jakarta.persistence.*;

@Entity
@Table(name = "ejercicio")
public class Ejercicio {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer ejercicioID;

  @Column(nullable = false)
  private Integer rutinaID;

  @Column(nullable = false)
  private String tipo; //normal, calentamiento, serie descendente y al fallo.

  @Column(nullable = false)
  private String descripcion;

  @Column(nullable = false)
  private String nombre;

  private Double valoracion;

  @Column(nullable = false)
  private String grupoMuscular;

  private byte[] video;

  //Setters

  public void setVideo(byte[] video) {
    this.video = video;
  }

  public void setEjercicioID(Integer ejercicioID) {
    this.ejercicioID = ejercicioID;
  }

  public void setRutinaID(Integer rutinaID) {
    this.rutinaID = rutinaID;
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

  public Integer getRutinaID() {
    return rutinaID;
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
}
