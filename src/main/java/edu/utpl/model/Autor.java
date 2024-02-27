package edu.utpl.model;

public class Autor {

    private String nombres;
    private String campo;

    public Autor() {
    }

    public Autor(String nombres, String campo) {
        this.nombres = nombres;
        this.campo = campo;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }
}
