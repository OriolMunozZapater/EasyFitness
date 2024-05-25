package com.uablis.easyfitness.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentario")
public class Comentario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer comentarioID;

  @Column(nullable = false)
  private String descripcion;

  @Column(nullable = false)
  private String userID;

  @Column(nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime fecha;

  @PrePersist
  protected void onCreate() {
    fecha = LocalDateTime.now();
  }

  public Integer getComentarioID() {
    return comentarioID;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public String getUserID() {
    return userID;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }
}
