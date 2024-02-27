package edu.utpl.service.impl;

import edu.utpl.model.Resultados;
import edu.utpl.model.Titulo;
import edu.utpl.repo.ConexAbnet;
import edu.utpl.service.ITituloService;
import edu.utpl.tools.CustomDeserialize;
import edu.utpl.tools.CustomDeserialize2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


@Service
public class TituloServiceImpl implements ITituloService {

    private static final String URL_API_RELA = "http://172.16.80.15:8080/AbsysWebServiceRestful/webresources/service?operation=search&table=copias&cobarc=";
    private RestTemplate restTemplate;
    private ConexAbnet conex ;
    private int res_x_pag= 60;

    @Autowired
    private CustomDeserialize deserialized;
    @Autowired
    private CustomDeserialize2 deserialized2;


    @Override
    public Resultados getTitulobyTitn(Object titn) throws Exception {


        restTemplate = new RestTemplate();

        List<Titulo> titulos = new ArrayList<>();
        String jsonRes = restTemplate.getForObject(construyeURLRest("001",titn)+".titn.", String.class);
        //titulos = deserialized.getTitulo( jsonRes );
        Resultados res = deserialized.getTitulo2( jsonRes,1 );

        return res;
    }

    public List<Titulo> buscarTitulobyKeyword(String keyword) throws Exception{

        restTemplate = new RestTemplate();

        List<Titulo> titulos = new ArrayList<>();

        String jsonRes = restTemplate.getForObject(construyeURLRest("keyword",keyword), String.class);
        titulos = deserialized.getTitulo( jsonRes );

        return titulos;

    }

    @Override
    public Resultados buscarTitulobyKeywordPaginado(String keyword, Integer page) throws Exception {

        restTemplate = new RestTemplate();
        Resultados res = new Resultados();
        List<Titulo> titulos = new ArrayList<>();

        String jsonRes = restTemplate.getForObject(construyeURLRest("keyword",keyword)+"&_start_position="+page, String.class);
        //titulos = deserialized.getTitulo( jsonRes );
        res = deserialized.getTitulo2( jsonRes, page );

        return res;
    }

    @Override
    public Resultados getAllTitulos(String command, Integer page) throws Exception {

        restTemplate = new RestTemplate();
        Resultados res = new Resultados();
        List<Titulo> titulos = new ArrayList<>();
        Titulo titulo = null;
        int titn = 0;
        int conteo_res = 1;
        int limite= (res_x_pag * page);
        int inicio = (limite - res_x_pag+1);
        int total_resultados = 0;
        int total_pages=0;

        total_resultados = getNoTotalPages(  );
        int no_paginas = (int) Math.ceil( (total_resultados / Double.valueOf(res_x_pag) ) );

        try{

            conex = new ConexAbnet();

            conex.conectar();

            ResultSet resp = conex.consulta( getConsultaAllTitulos() );


            while( resp.next() ){

                try{

                    if( conteo_res >=  inicio ) {

                        titulo = new Titulo();
                        titn = resp.getInt("tintit");
                        String jsonRes = restTemplate.getForObject(construyeURLRest("001",titn)+".titn.", String.class);
                        titulos.add( deserialized2.getTitulo( jsonRes ) );

                        if(conteo_res == limite){

                            break;

                        }

                    }

                    conteo_res++;

                }catch (Exception ex){
                    System.out.println("ErrorConsulta: " + titn + " " +ex.getMessage());
                }

            }

        }catch( Exception ex ){

            System.out.println("ErrorConsulta: " + ex.getMessage());

            new Exception( ex.getMessage()  );

        }finally{

            conex.Cerrarconex();
        }

        res.setData( titulos );
        res.setPage( page );
        res.setTotalPages( no_paginas );
        res.setPaginator(page+"/"+no_paginas);
        res.setStatus("OK");


        return res;

    }

    private String getConsultaAllTitulos() {

        return "select distinct(t.tintit) from titulo t, copias c where t.tintit = c.contit and c.costat like 'C%' " +
                "and c.cococl not like 'C%'  and c.cococl not like 'O%' and c.cococp like 'LI%' order by t.tintit ASC";
    }


    @Override
    public Resultados getTitulobycobarc(Object cobarc) throws Exception {

        List<Titulo> titulos = new ArrayList<>();
        restTemplate = new RestTemplate();
        //titulos =     getTitulobyTitn (consultaTintitCobarc(cobarc) );

        Resultados res = getTitulobyTitn( consultaTintitCobarc(cobarc) );

        return res;

    }

    @Override
    public Resultados getTitulobyIsbn(Object isbn) throws Exception {
        restTemplate = new RestTemplate();

        List<Titulo> titulos = new ArrayList<>();

        String jsonRes = restTemplate.getForObject(construyeURLRest("020",isbn), String.class);

        //titulos = deserialized.getTitulo( jsonRes );

        Resultados res = deserialized.getTitulo2( jsonRes, 1 );

        return res;
    }

    public int  consultaTintitCobarc( Object cobarc ) throws Exception {

        restTemplate = new RestTemplate();

        int tintit = -1 ;

        String jsonRes = restTemplate.getForObject( URL_API_RELA+cobarc+"&_secondary=1" , String.class);

        tintit = deserialized.parseTintit( jsonRes );

        return tintit;

    }

    private String construyeURLRest(String id, Object dato) {

        String urlApi = "";
        if( id.equals("keyword")){
            urlApi = "http://172.16.80.15:8080/AbsysWebServiceRestful/webresources/service?operation=search&base=cata&search="+ dato;
        }else{
            urlApi = "http://172.16.80.15:8080/AbsysWebServiceRestful/webresources/service?operation=search&base=cata&_description=1&_secondary=1&_secondary_tables=copias%2Cpresta&_tertiary=1&_tertiary_tables=titulo&doc_fields="
                    +id +"%2Cleader&_doc_order=LIFO&_covers=1&search="+dato;
        }

        return urlApi;

    }

    private int getNoTotalPages() {

        int total_pages =0;
        try{

            conex = new ConexAbnet();

            conex.conectar();

            ResultSet resp = conex.consulta( getConsultaConteoAllTitulos() );

            while( resp.next() ){

                total_pages= resp.getInt("noresultados");

            }

        }catch( Exception ex ){

            ex.printStackTrace();

        }finally{

            conex.Cerrarconex();
        }

        return total_pages;

    }

    private String getConsultaConteoAllTitulos() {

        return "select count( distinct (t.tintit) ) as noresultados from titulo t, copias c where t.tintit = c.contit and c.costat like 'C%' " +
                "and c.cococl not like 'C%'  and c.cococl not like 'O%' and c.cococp like 'LI%' order by t.tintit ASC";
    }
}
