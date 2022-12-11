package com.pucp.bemyhome.Entity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class Solicitud implements Serializable {

    @Exclude
    private String key;
    private AdoptanteUser adoptanteUser;
    private AsistenteUser asistUser;
    private Pets pets;
    private String motivoReserva;
    private String domicilio;
    private String cantMascotas;
    private String cantPersonasCasa;
    private String tiempoDedicar;
    private String tiempoPaseo;
    private String dni;
    private String propioOalquilado;
    private String lugarParteTiempo;
    private String patioOjardin;
    private transient GeoPoint lugarRecojo;
    private String nombreLugarRecojo;
    private String motivoRechazo;
    private String estado;
    private String alergico;
    private String vacunasAlDia;
    private transient Timestamp horaReserva;
    private transient Timestamp horaRespuesta;

    public Solicitud() {
    }

    public Solicitud(AdoptanteUser adoptanteUser, AsistenteUser asistUser, Pets pets, String motivoReserva, String domicilio, String cantMascotas, String cantPersonasCasa, String tiempoDedicar, String tiempoPaseo, String dni, String propioOalquilado, String lugarParteTiempo, String patioOjardin, GeoPoint lugarRecojo, String nombreLugarRecojo, String motivoRechazo, String estado, String alergico, String vacunasAlDia, Timestamp horaReserva, Timestamp horaRespuesta) {
        this.adoptanteUser = adoptanteUser;
        this.asistUser = asistUser;
        this.pets = pets;
        this.motivoReserva = motivoReserva;
        this.domicilio = domicilio;
        this.cantMascotas = cantMascotas;
        this.cantPersonasCasa = cantPersonasCasa;
        this.tiempoDedicar = tiempoDedicar;
        this.tiempoPaseo = tiempoPaseo;
        this.dni = dni;
        this.propioOalquilado = propioOalquilado;
        this.lugarParteTiempo = lugarParteTiempo;
        this.patioOjardin = patioOjardin;
        this.lugarRecojo = lugarRecojo;
        this.nombreLugarRecojo = nombreLugarRecojo;
        this.motivoRechazo = motivoRechazo;
        this.estado = estado;
        this.alergico = alergico;
        this.vacunasAlDia = vacunasAlDia;
        this.horaReserva = horaReserva;
        this.horaRespuesta = horaRespuesta;
    }
    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public AdoptanteUser getAdoptanteUser() {
        return adoptanteUser;
    }

    public void setAdoptanteUser(AdoptanteUser adoptanteUser) {
        this.adoptanteUser = adoptanteUser;
    }

    public AsistenteUser getAsistUser() {
        return asistUser;
    }

    public void setAsistUser(AsistenteUser asistUser) {
        this.asistUser = asistUser;
    }

    public Pets getPets() {
        return pets;
    }

    public void setPets(Pets pets) {
        this.pets = pets;
    }

    public String getMotivoReserva() {
        return motivoReserva;
    }

    public void setMotivoReserva(String motivoReserva) {
        this.motivoReserva = motivoReserva;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getCantMascotas() {
        return cantMascotas;
    }

    public void setCantMascotas(String cantMascotas) {
        this.cantMascotas = cantMascotas;
    }

    public String getCantPersonasCasa() {
        return cantPersonasCasa;
    }

    public void setCantPersonasCasa(String cantPersonasCasa) {
        this.cantPersonasCasa = cantPersonasCasa;
    }

    public String getTiempoDedicar() {
        return tiempoDedicar;
    }

    public void setTiempoDedicar(String tiempoDedicar) {
        this.tiempoDedicar = tiempoDedicar;
    }

    public String getTiempoPaseo() {
        return tiempoPaseo;
    }

    public void setTiempoPaseo(String tiempoPaseo) {
        this.tiempoPaseo = tiempoPaseo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPropioOalquilado() {
        return propioOalquilado;
    }

    public void setPropioOalquilado(String propioOalquilado) {
        this.propioOalquilado = propioOalquilado;
    }

    public String getLugarParteTiempo() {
        return lugarParteTiempo;
    }

    public void setLugarParteTiempo(String lugarParteTiempo) {
        this.lugarParteTiempo = lugarParteTiempo;
    }

    public String getPatioOjardin() {
        return patioOjardin;
    }

    public void setPatioOjardin(String patioOjardin) {
        this.patioOjardin = patioOjardin;
    }

    public GeoPoint getLugarRecojo() {
        return lugarRecojo;
    }

    public void setLugarRecojo(GeoPoint lugarRecojo) {
        this.lugarRecojo = lugarRecojo;
    }

    public String getNombreLugarRecojo() {
        return nombreLugarRecojo;
    }

    public void setNombreLugarRecojo(String nombreLugarRecojo) {
        this.nombreLugarRecojo = nombreLugarRecojo;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAlergico() {
        return alergico;
    }

    public void setAlergico(String alergico) {
        this.alergico = alergico;
    }

    public String getVacunasAlDia() {
        return vacunasAlDia;
    }

    public void setVacunasAlDia(String vacunasAlDia) {
        this.vacunasAlDia = vacunasAlDia;
    }

    public Timestamp getHoraReserva() {
        return horaReserva;
    }

    public void setHoraReserva(Timestamp horaReserva) {
        this.horaReserva = horaReserva;
    }

    public Timestamp getHoraRespuesta() {
        return horaRespuesta;
    }

    public void setHoraRespuesta(Timestamp horaRespuesta) {
        this.horaRespuesta = horaRespuesta;
    }

    public static class AdoptanteUser implements Serializable {

        private String nombre;
        private String uid;
        private String avatarUrl;
        private String dni;

        public AdoptanteUser() {
        }

        public AdoptanteUser(String nombre, String uid, String avatarUrl, String dni) {
            this.nombre = nombre;
            this.uid = uid;
            this.avatarUrl = avatarUrl;
            this.dni = dni;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getDni() {
            return dni;
        }

        public void setDni(String dni) {
            this.dni = dni;
        }
    }

    public static class AsistenteUser implements Serializable {

        private String nombre;
        private String uid;
        private String avatarUrl;
        private String dni;

        public AsistenteUser() {
        }

        public AsistenteUser(String nombre, String uid, String avatarUrl, String dni) {
            this.nombre = nombre;
            this.uid = uid;
            this.avatarUrl = avatarUrl;
            this.dni = dni;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getDni() {
            return dni;
        }

        public void setDni(String dni) {
            this.dni = dni;
        }
    }

    public static class Pets implements Serializable {

        private String nombre;
        private String genero;
        private String edad;
        private String fotoUrl;
        private String categoria;
        private String uid;

        public Pets() {
        }

        public Pets(String nombre, String genero, String edad, String fotoUrl,String categoria, String uid) {
            this.nombre = nombre;
            this.genero = genero;
            this.edad = edad;
            this.fotoUrl = fotoUrl;
            this.categoria = categoria;
            this.uid = uid;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
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

        public String getFotoUrl() {
            return fotoUrl;
        }

        public void setFotoUrl(String fotoUrl) {
            this.fotoUrl = fotoUrl;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }
    }

}
