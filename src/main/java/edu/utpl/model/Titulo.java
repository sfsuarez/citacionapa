package edu.utpl.model;

import java.util.List;

public class Titulo {

    private int titn;
    private String titulo;
    private List<String> isbns;
    private String subtitulo;
    private String portada;
    private List<Autor> autores;
    private String yearEdicion;
    private String lugarEdicion;
    private String editorial;
    private String urlOPAC;
    private String edicion;
    private String citacionAPA;
    private String url;
    private String idioma;
    private String tipoMaterial;
    private String localizacion;
    private Integer noEjem;


    public Titulo() {
    }

    public Titulo(int titn, String titulo, List<String> isbns, String subtitulo, String portada, List<Autor> autores, String yearEdicion, String lugarEdicion, String editorial, String urlOPAC, String edicion, String citacionAPA, String url, String idioma, String tipoMaterial, String localizacion, Integer noEjem) {
        this.titn = titn;
        this.titulo = titulo;
        this.isbns = isbns;
        this.subtitulo = subtitulo;
        this.portada = portada;
        this.autores = autores;
        this.yearEdicion = yearEdicion;
        this.lugarEdicion = lugarEdicion;
        this.editorial = editorial;
        this.urlOPAC = urlOPAC;
        this.edicion = edicion;
        this.citacionAPA = citacionAPA;
        this.url = url;
        this.idioma = idioma;
        this.tipoMaterial = tipoMaterial;
        this.localizacion = localizacion;
        this.noEjem = noEjem;
    }

    public String getEdicion() {
        return edicion;
    }

    public void setEdicion(String edicion) {
        this.edicion = edicion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTitn() {
        return titn;
    }

    public void setTitn(int titn) {
        this.titn = titn;
    }


    public String getTitulo() {
        return titulo;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<String> getIsbns() {
        return isbns;
    }

    public void setIsbns(List<String> isbns) {
        this.isbns = isbns;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    public String getYearEdicion() {
        return yearEdicion;
    }

    public void setYearEdicion(String yearEdicion) {
        this.yearEdicion = yearEdicion;
    }

    public String getLugarEdicion() {
        return lugarEdicion;
    }

    public void setLugarEdicion(String lugarEdicion) {
        this.lugarEdicion = lugarEdicion;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getUrlOPAC() {
        return urlOPAC;
    }

    public void setUrlOPAC(String urlOPAC) {
        this.urlOPAC = urlOPAC;
    }

    public String getCitacionAPA() {
        return citacionAPA;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public Integer getNoEjem() {
        return noEjem;
    }

    public void setNoEjem(Integer noEjem) {
        this.noEjem = noEjem;
    }

    public void setCitacionAPA(String citacionAPA) {
        this.citacionAPA = citacionAPA;
    }
}
