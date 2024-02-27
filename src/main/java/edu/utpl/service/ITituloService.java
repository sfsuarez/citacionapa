package edu.utpl.service;

import edu.utpl.model.Resultados;
import edu.utpl.model.Titulo;

import java.util.List;

public interface ITituloService {

    public Resultados getTitulobyTitn(Object titn) throws Exception;

    public Resultados getTitulobycobarc(Object cobarc) throws Exception;

    public Resultados getTitulobyIsbn(Object isbn) throws Exception;

    public List<Titulo> buscarTitulobyKeyword(String keyword) throws Exception;

    public Resultados buscarTitulobyKeywordPaginado(String keyword, Integer page) throws Exception;

    public Resultados getAllTitulos(String command, Integer page) throws Exception;
}
