package com.pucp.bemyhome.Entity;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;

public class Pet implements Serializable {

    @Exclude
    private String key;
    private String genero;
    private String edad;
    private String categoria;
    private String peso;
    private String tamanio;
    private String nombre;
    private List<String> fotosUrl;
    private String descripcion;
    private List<String> caracteristicas;
    private String nivelActividad;
    private List<String> searchKeywords;
    private String searchCategoria;
    private String searchGenero;
    private String adoptado;

    public Pet() {
    }

    public Pet(String genero, String edad, String categoria, String peso, String tamanio, String nombre, List<String> fotosUrl, String descripcion, List<String> caracteristicas, String nivelActividad, List<String> searchKeywords, String searchCategoria, String searchGenero, String adoptado) {
        this.genero = genero;
        this.edad = edad;
        this.categoria = categoria;
        this.peso = peso;
        this.tamanio = tamanio;
        this.nombre = nombre;
        this.fotosUrl = fotosUrl;
        this.descripcion = descripcion;
        this.caracteristicas = caracteristicas;
        this.nivelActividad = nivelActividad;
        this.searchKeywords = searchKeywords;
        this.searchCategoria = searchCategoria;
        this.searchGenero = searchGenero;
        this.adoptado = adoptado;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getTamanio() {
        return tamanio;
    }

    public void setTamanio(String tamanio) {
        this.tamanio = tamanio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getFotosUrl() {
        return fotosUrl;
    }

    public void setFotosUrl(List<String> fotosUrl) {
        this.fotosUrl = fotosUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(List<String> caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public String getNivelActividad() {
        return nivelActividad;
    }

    public void setNivelActividad(String nivelActividad) {
        this.nivelActividad = nivelActividad;
    }

    public List<String> getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(List<String> searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public String getSearchCategoria() {
        return searchCategoria;
    }

    public void setSearchCategoria(String searchCategoria) {
        this.searchCategoria = searchCategoria;
    }

    public String getSearchGenero() {
        return searchGenero;
    }

    public void setSearchGenero(String searchGenero) {
        this.searchGenero = searchGenero;
    }

    public String getAdoptado() {
        return adoptado;
    }

    public void setAdoptado(String adoptado) {
        this.adoptado = adoptado;
    }
}
