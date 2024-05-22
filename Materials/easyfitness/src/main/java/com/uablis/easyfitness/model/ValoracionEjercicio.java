package com.uablis.easyfitness.model;

import jakarta.persistence.*;

@Entity
@Table(name = "valoracion_ejercicio")
public class ValoracionEjercicio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer valoracionID;

  @Column(nullable = false)
  private Integer userID;

  @Column(nullable = false)
  private Integer ejercicioID;

  private Boolean valoracion;

  @Column(length = 255)
  private String comentario;

  // Getters y setters
  public Integer getValoracionID() {
    return valoracionID;
  }

  public void setValoracionID(Integer valoracionID) {
    this.valoracionID = valoracionID;
  }

  public Integer getUserID() {
    return userID;
  }

  public void setUserID(Integer userID) {
    this.userID = userID;
  }

  public Integer getEjercicioID() {
    return ejercicioID;
  }

  public void setEjercicioID(Integer ejercicioID) {
    this.ejercicioID = ejercicioID;
  }

  public Boolean getValoracion() {
    return valoracion;
  }

  public void setValoracion(Boolean valoracion) {
    this.valoracion = valoracion;
  }

  public String getComentario() {
    return comentario;
  }

  public void setComentario(String comentario) {
    this.comentario = comentario;
  }
}