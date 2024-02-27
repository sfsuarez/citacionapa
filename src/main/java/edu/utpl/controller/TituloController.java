package edu.utpl.controller;


import com.google.gson.Gson;
import edu.utpl.model.Resultados;
import edu.utpl.model.Titulo;
import edu.utpl.service.ITituloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET})
@RequestMapping("/catalogo/titulos")
public class TituloController {
    @Autowired
    private ITituloService service;
    private Gson gson = new Gson();
    private List<Titulo> titulos;


    @GetMapping("/{campo}/{dato}/{pagina}")
    public ResponseEntity getTitulobyID(@PathVariable("campo") String campo, @PathVariable("dato") Object dato, @PathVariable("pagina") Integer page ) throws Exception {


        titulos = new ArrayList<>();
        Titulo titulo = new Titulo();
        Resultados res = new Resultados();
        try {
            if( campo.equals("tintit")) {

                //titulos = new ArrayList<>();
                //titulos = service.getTitulobyTitn( (dato) );
                res = service.getTitulobyTitn( (dato) );

            }

            if(campo.equals("cobarc")){

                //titulos = new ArrayList<>();
                //titulos = service.getTitulobycobarc(dato);
                res = service.getTitulobycobarc(dato);

            }

            if( campo.equals("isbn") ) {

                //titulos = new ArrayList<>();
                //titulos = service.getTitulobyIsbn(dato);
                res = service.getTitulobyIsbn(dato);

            }

            if( campo.equals("search") ) {

                //titulos = new ArrayList<>();
                //titulos = service.buscarTitulobyKeyword(dato.toString());
                res = service.buscarTitulobyKeywordPaginado( dato.toString(), page);

            }

            if( campo.equals("listAll") && dato.equals("Libros" ) ) {

                //titulos = new ArrayList<>();
                //titulos = service.buscarTitulobyKeyword(dato.toString());

                res = service.getAllTitulos( dato.toString(), page);

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            //throw new Exception(  e.getLocalizedMessage());       }
            res.setTotalPages(0);
            res.setStatus(e.getLocalizedMessage());
            res.setPaginator("0/0");
            res.setPage(0);
        }

        return new ResponseEntity<Resultados>(res, HttpStatus.OK);

    }


}
