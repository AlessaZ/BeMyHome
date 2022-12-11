package com.pucp.bemyhome.Entity;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    @Exclude
    private String uid;
    private String nombre;
    private String correo;
    private String dni;
    private String permisos;
    private String avatarUrl;
    private List<String> searchKeywords;
    private String telefono;

    public User() {
    }

    public User(String nombre, String correo, String dni, String permisos, String avatarUrl, List<String> searchKeywords, String telefono) {
        this.nombre = nombre;
        this.correo = correo;
        this.dni = dni;
        this.permisos = permisos;
        this.avatarUrl = avatarUrl;
        this.searchKeywords = searchKeywords;
        this.telefono = telefono;
    }

    @Exclude
    public String getUid() {
        return uid;
    }

    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDNI() {
        return dni;
    }

    public void setDNI(String DNI) {
        this.dni = DNI;
    }

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<String> getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(List<String> searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
