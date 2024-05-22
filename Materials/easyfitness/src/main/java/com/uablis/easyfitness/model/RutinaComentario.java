package com.uablis.easyfitness.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rutina_comentarios")
public class RutinaComentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_comentari;

    @Column(name = "id_rutina", nullable = false)
    private Long idRutina;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "comentario", length = 1000)
    private String comentario;

    // Getters and Setters
    public Long getId_comentari() {
        return id_comentari;
    }

    public void setId_comentari(Long id_comentari) {
        this.id_comentari = id_comentari;
    }

    public Long getIdRutina() {
        return idRutina;
    }

    public void setIdRutina(Long idRutina) {
        this.idRutina = idRutina;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
