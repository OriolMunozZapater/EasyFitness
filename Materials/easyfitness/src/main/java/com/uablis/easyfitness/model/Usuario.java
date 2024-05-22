package com.uablis.easyfitness.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "usuario")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer userID;

  @Column(name = "is_first_login")
  private boolean isFirstLogin = true;

  private String nombre;

  private String apellido;

  @Column(unique = true)
  private String correo;

  private String password;

  private String sexo;

  private Double peso_actual;

  private Integer altura;

  @Column(name = "foto")
  private String foto;

  @Column
  private String descripcion;

  @Column
  private String redes_sociales;

  @Column
  private String tiempo_entrenamiento;

  private String gimnasio;

  @JsonIgnore
  @ManyToMany(mappedBy = "seguidos")
  private Set<Usuario> seguidores;

  @JsonIgnore
  @ManyToMany
  @JoinTable(
          name = "usuarios_seguidos",
          joinColumns = @JoinColumn(name = "seguidoID"),
          inverseJoinColumns = @JoinColumn(name = "usuarioID")
  )
  private Set<Usuario> seguidos;

  @OneToOne
  @JoinColumn(name = "objetivoID")
  private Objetivo objetivo;

  @Column(name = "fecha_nacimiento")
  @Temporal(TemporalType.DATE)
  private Date fechaNacimiento;

  // Getters y setters
  public void setFirstLogin(boolean firstLogin) {
    isFirstLogin = firstLogin;
  }

  public Date getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(Date fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }

  public boolean getFirstLogin() {
    return isFirstLogin;
  }
  public Set<Usuario> getSeguidos() {
    return seguidos;
  }

  public void setSeguidos(Set<Usuario> seguidos) {
    this.seguidos = seguidos;
  }

  public Set<Usuario> getSeguidores() {
    return seguidores;
  }

  public void setSeguidores(Set<Usuario> seguidores) {
    this.seguidores = seguidores;
  }

  public Integer getUserID() {
    return userID;
  }

  public void setUserID(Integer userID) {
    this.userID = userID;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getApellido() {
    return apellido;
  }

  public void setApellido(String apellido) {
    this.apellido = apellido;
  }

  public String getCorreo() {
    return correo;
  }

  public void setCorreo(String correo) {
    this.correo = correo;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSexo() {
    return sexo;
  }

  public void setSexo(String sexo) {
    this.sexo = sexo;
  }

  public Double getPeso_actual() {
    return peso_actual;
  }

  public void setPeso_actual(Double peso_actual) {
    this.peso_actual = peso_actual;
  }

  public Integer getAltura() {
    return altura;
  }

  public void setAltura(Integer altura) {
    this.altura = altura;
  }

  public String getFotoUrl() {
    return foto;
  }

  public void setFotoUrl(String fotoUrl) {
    this.foto = fotoUrl;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getRedes_sociales() {
    return redes_sociales;
  }

  public void setRedes_sociales(String redes_sociales) {
    this.redes_sociales = redes_sociales;
  }

  public String getTiempo_entrenamiento() {
    return tiempo_entrenamiento;
  }

  public void setTiempo_entrenamiento(String tiempo_entrenamiento) {
    this.tiempo_entrenamiento = tiempo_entrenamiento;
  }

  public String getGimnasio() {return gimnasio;}
  public void setGimnasio(String gimnasio) { this.gimnasio = gimnasio; }

  public Objetivo getObjetivo() {
    return objetivo;
  }

  public void setObjetivo(Objetivo objetivo) {
    this.objetivo = objetivo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Usuario)) return false;
    Usuario usuario = (Usuario) o;
    return getUserID().equals(usuario.getUserID());
  }

  @Override
  public int hashCode() {
    return getUserID().hashCode();
  }

  @Override
  public String toString() {
    return "Usuario{" +
            "userID=" + userID +
            ", nombre='" + nombre + '\'' +
            ", apellido='" + apellido + '\'' +
            ", correo='" + correo + '\'' +
            ", password='" + password + '\'' +
            ", sexo='" + sexo + '\'' +
            ", peso_actual=" + peso_actual +
            ", altura=" + altura +
            ", foto=" + foto +
            ", descripcion='" + descripcion + '\'' +
            ", redes_sociales='" + redes_sociales + '\'' +
            ", gimnasio='" + gimnasio + '\'' +
            ", tiempo_entrenamiento='" + tiempo_entrenamiento + '\'' +
            ", objetivo=" + objetivo +
            '}';
  }
}