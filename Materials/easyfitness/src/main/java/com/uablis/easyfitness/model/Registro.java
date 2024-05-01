<<<<<<< HEAD
package com.uablis.easyfitness.model;

import jakarta.persistence.*;

@Entity
@Table(name = "registro")
public class Registro {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer registroID;

  private String nombreRutina;

  private java.sql.Time tiempoTardado;

  // Setters
  public void setRegistroID(Integer registroID) {
    this.registroID = registroID;
  }

  public void setNombreRutina(String nombreRutina) {
    this.nombreRutina = nombreRutina;
  }

  public void setTiempoTardado(java.sql.Time tiempoTardado) {
    this.tiempoTardado = tiempoTardado;
  }

  // Getters
  public Integer getRegistroID() {
    return registroID;
  }

  public String getNombreRutina() {
    return nombreRutina;
  }

  public java.sql.Time getTiempoTardado() {
    return tiempoTardado;
  }

  @Column(name = "userID")
  private Integer userID;

  public void setUserID(Integer userID) {
    this.userID = userID;
  }

  public Integer getUserID() {
    return userID;
  }
}
=======
package com.uablis.easyfitness.model;

import jakarta.persistence.*;

@Entity
@Table(name = "registro")
public class Registro {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer registroID;

  private String nombreRutina;

  private java.sql.Time tiempoTardado;

  // Setters
  public void setRegistroID(Integer registroID) {
    this.registroID = registroID;
  }

  public void setNombreRutina(String nombreRutina) {
    this.nombreRutina = nombreRutina;
  }

  public void setTiempoTardado(java.sql.Time tiempoTardado) {
    this.tiempoTardado = tiempoTardado;
  }

  // Getters
  public Integer getRegistroID() {
    return registroID;
  }

  public String getNombreRutina() {
    return nombreRutina;
  }

  public java.sql.Time getTiempoTardado() {
    return tiempoTardado;
  }

  @Column(name = "userID")
  private Integer userID;

  public void setUserID(Integer userID) {
    this.userID = userID;
  }

  public Integer getUserID() {
    return userID;
  }
}
>>>>>>> a897230213c6b8d9bf5816a808364420c2e13928
