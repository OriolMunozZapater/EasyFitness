package com.uablis.easyfitness.model;


import jakarta.persistence.*;

@Entity
@Table(name = "valoracion_ejercicio")
public class ValoracionEjercicio{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer valoracionID;

  @Column(nullable = false)
  private Integer userID;

  @Column(nullable = false)
  private Integer ejercicioID;

  private Integer valoracion;

  private char[] comentario = new char[255];

  // Getters y setters
  public Integer getValoracion() {
    return valoracion;
  }

  public void setValoracion(Integer valoracion) {
    this.valoracion = valoracion;
  }

  public char[] getComentario() {
    return comentario;
  }

  public void setComentario(char[] comentario) {
    this.comentario = comentario;
  }
}
