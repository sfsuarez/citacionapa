package edu.utpl.tools;

import edu.utpl.model.Autor;
import edu.utpl.model.Resultados;
import edu.utpl.model.Titulo;
import edu.utpl.repo.ConexAbnet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Component
public class CustomDeserialize2 {

    private String UrlBaseOpac= "https://bibliotecautpl.utpl.edu.ec/cgi-bin/abnetclwo?ACC=DOSEARCH&xsqf99=";
    private List<String> camposAutores = Arrays.asList("100","110","700","710");
    private List<String> SubcamposKeys = Arrays.asList("a","x","v");
    private static final Logger log = Logger.getLogger(CustomDeserialize.class.getName());
    private String portadaGenerica = "https://bibliotecautpl.utpl.edu.ec/abnetopac/imag/notavailable.png";
    private ConexAbnet conex ;
    private String urlMultimediaAbsysnet = "https://bibliotecautpl.utpl.edu.ec/cgi-bin/abnetclwo";

    public Titulo getTitulo(String json  ) throws Exception {


        Titulo mb;

        JSONObject respuesta = new JSONObject( json );

        JSONObject response = respuesta.getJSONObject("response");

        if( response.getString("code").equals("0") && !response.getString("count").equals("0")) {

            mb = new Titulo();

            JSONObject collection = response.getJSONObject("collection");


            if( esObject( collection ) ){

                //record = collection.getJSONObject("record");

                mb = leeRecord( collection.getJSONObject("record") );
               //titulo = leeRecord( collection.getJSONObject("record")  );

            }else{

                JSONArray records = collection.getJSONArray( "record" );

                for( int i=0; i <  records.length(); i++ ){

                    //JSONObject record = records.getJSONObject(i);

                   //titulos.add( leeRecord( records.getJSONObject(i) ) );

                    mb =  leeRecord( records.getJSONObject(i)   );

                }

            }


        }else {//No hya resultados

            mb = null;
        }

        return mb;

    }

    public Resultados getTitulo2(String json, Integer page  ) throws Exception {

        List<Titulo> titulos = new ArrayList<>();
        Titulo mb;

        JSONObject respuesta = new JSONObject( json );

        JSONObject response = respuesta.getJSONObject("response");

        Resultados res = new Resultados();
        res.setPage( page );

        res.setStatus( response.getString("description") );

        Integer totalPage = (int) Math.ceil(   Integer.parseInt( response.getString("count") )  / 10.0 );

        res.setTotalPages( totalPage  );
        res.setPaginator( page + "/" + totalPage);


        if( response.getString("code").equals("0") && !response.getString("count").equals("0")) {

            mb = new Titulo();

            JSONObject collection = response.getJSONObject("collection");


            if( esObject( collection ) ){

                //record = collection.getJSONObject("record");

                //mb = leeRecord( record );
                titulos.add( leeRecord( collection.getJSONObject("record") ) );

            }else{

                JSONArray records = collection.getJSONArray( "record" );

                for( int i=0; i <  records.length(); i++ ){

                    //JSONObject record = records.getJSONObject(i);
                    ;
                    titulos.add( leeRecord( records.getJSONObject(i) ) );

                }

            }


        }else {//No hya resultados

            throw new Exception( "No se encontraron resultados");
        }

        res.setData( titulos );

        return res;

    }


    private Titulo leeRecord(JSONObject record) throws Exception {

        Titulo mb = new Titulo();

        try{

            mb.setTitn( String2Int( getObjectString(record,"titn")) );



            mb.setTitulo( getCampoDatafields( record.getJSONArray("datafield"), "245","a" ).replace("/", "").trim() );
            mb.setSubtitulo( getCampoDatafields( record.getJSONArray("datafield"), "245","b" ).replace("/", "").trim() );

            mb.setLugarEdicion( getCampoDatafields( record.getJSONArray("datafield"), "260","a" ).replace(":", "").trim());
            if( mb.getLugarEdicion().equals("N/D")) {
                mb.setLugarEdicion( getCampoDatafields( record.getJSONArray("datafield"), "264","a" ).replace(":", "").trim());
            }

            mb.setYearEdicion( getCampoDatafields( record.getJSONArray("datafield"), "260","c" ).replace(".", "").trim() );
            if( mb.getYearEdicion().equals("N/D")) {
                mb.setYearEdicion( getCampoDatafields( record.getJSONArray("datafield"), "264","c" ).replace(".", "").trim());
            }

            mb.setEditorial( getCampoDatafields( record.getJSONArray("datafield"), "260","b" ).replace(",", "").trim() );
            if( mb.getEditorial().equals("N/D")) {
                mb.setEditorial( getCampoDatafields( record.getJSONArray("datafield"), "264","b" ).replace(",", "").trim());
            }

            mb.setEditorial( getCampoDatafields( record.getJSONArray("datafield"), "260","b" ).replace(",", "").trim() );
            if( mb.getEditorial().equals("N/D")) {
                mb.setEditorial( getCampoDatafields( record.getJSONArray("datafield"), "264","b" ).replace(",", "").trim());
            }

            mb.setIdioma(getCampoDatafields( record.getJSONArray("datafield"), "040","b" ).trim()  );

            mb.setEdicion( getCampoDatafields( record.getJSONArray("datafield"), "250","a" ).trim() );

            mb.setPortada( getPortada2( mb.getTitn() ) );


            mb.setUrlOPAC(UrlBaseOpac+mb.getTitn()+".TITN.");


            JSONObject copias = getCopiasObject( record );


            mb.setAutores( get2Autores( record.getJSONArray("datafield") ));

            mb.setIsbns (getIsbns(record.getJSONArray("datafield"), "020","a" ) );

            mb.setUrl( getCampoDatafields( record.getJSONArray("datafield"), "856","u" ) );
            mb.setCitacionAPA( getCitacion( mb ) );

        }catch( Exception ex ){

            throw new Exception( "Error al leer record: " + ex.getMessage());
        }

        return getInfoBase(mb);

    }

    private String getPortada(JSONObject record) {

        String portada = portadaGenerica;

        try{

            portada = record.getString("cover");

        }catch( Exception ex ){

            portada = portadaGenerica;
        }

        return portada;
    }

    private String getPortada2( int titn ) throws Exception {

        String urlPortada = portadaGenerica;
        conex   = new ConexAbnet();
        String flhref = "";
        try{

            conex.conectar();


            ResultSet res = conex.consulta( getConsultaPortada(  titn )  );


            while( res.next() ){

                try{

                    flhref = res.getNString("flhref").trim();

                    if( flhref.contains("?METS=")  ) {//si la imagen de la portada esta subida en el Absysnet

                        urlPortada = urlMultimediaAbsysnet + flhref;

                    }else {//La portada es una URL de internet
                        /*
                         * Las portadas que no tienen en su URL's  certificado digital https
                         * se reemplaza la URL por la portada genérica
                         */
                        urlPortada = getPortadaExterna( flhref );

                        if(urlPortada.contains("http")){

                            urlPortada = portadaGenerica;

                        }


                    }



                }catch( Exception ex ){

                    urlPortada = portadaGenerica;
                    log.info( ex.getMessage() );

                }


            }


        }catch( Exception ex ){
            urlPortada = portadaGenerica;
            log.info( ex.getMessage() );

        }finally {
            conex.Cerrarconex();
        }


        return urlPortada;
    }

    private boolean esObject(JSONObject collection) {

        boolean ban = false;
        try{

            JSONObject record = collection.getJSONObject("record");

            ban = true;

        }catch( Exception ex ){
            ban = false;
        }

        return ban;
    }

    private Titulo getInfoBase(Titulo mb) {

        Titulo titulo = mb;

        try{
            conex = new ConexAbnet();

            conex.conectar();

            ResultSet res = conex.consulta("Select loc.cldesc, tip.cpdesc," +
                    "( select count(*) from copias where costat like 'C%' and cococl not like 'C%' and cococl not like 'O%' and contit = t.tintit  ) as noejem " +
                    "from titulo t, copias c, coploc loc, coptip tip where t.tintit = c.contit " +
                    "and c.cococl = loc.clcocl and c.cococp = tip.cpcocp and t.tintit =" + mb.getTitn());

            while( res.next() ){

                mb.setTipoMaterial( validaNull( res.getString( "cpdesc") ) );
                mb.setLocalizacion( validaNull( res.getString( "cldesc") ) );
                mb.setNoEjem( res.getInt( "noejem"));


                break;

            }


        }catch( Exception ex ){
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();

        }finally{

            conex.Cerrarconex();
        }

        return mb;
    }
    private String getPortadaExterna(String flhref) {
        // TODO Auto-generated method stub
        String portadaURL= portadaGenerica;

        try {


            for( int i=flhref.length()-1 ; i > 0 ; i-- ) {



                if( flhref.charAt(i) == '/'  ) {

                    portadaURL = flhref.substring(0, i);
                    break;
                }


            }


        }catch( Exception ex ) {

            log.info( ex.getLocalizedMessage());
        }




        return portadaURL;
    }

    private String getConsultaPortada(int titn) {

        return "select gridbd, obdesc, flhref from grupmm inner join objemm on obgrns=grnseq inner " +
                "join filele on feobsq=obnseq inner join flocat on flfesq=fenseq " +
                "where obdesc in ('portada','Portada') and gridbd LIKE 'ABTI"+ controlnoCaracteres(titn) +"%'";
    }

    private String controlnoCaracteres(int titn) {

        String abti = titn + "";

        int Iter = 9 - abti.length();
        abti = "";
        for(int x=0; x < Iter ;x++){

            abti +="0";

        }


        return abti+titn;
    }//end controlnoCaracteres

    private String validaNull (String dato) {

        String data = "N/D ";

        try {


            if( !dato.trim().equals("") && !dato.trim().isEmpty() && dato != null ) {

                data = dato;
            }

        }catch( Exception ex ) {
            log.info("Error parsear: "  + ex.getMessage() );
            data = "N/D";

        }

        return data;

    }

    private String getCitacion(Titulo titulo) {

        String citacion ="";

        try{

            citacion = formatAutores( titulo.getAutores() )
                    + " ("+titulo.getYearEdicion()+"). "
                    + titulo.getTitulo() + procesaSubtitulo( titulo.getSubtitulo())
                    + ". "+titulo.getEditorial()+"."
            ;


        }catch( Exception ex ){


        }


        return citacion;

    }

    private String procesaSubtitulo(String subtitulo) {
        String sub = "";
        try{

            if( !subtitulo.trim().equals("ND")){

                sub = ": " +subtitulo;
            }

        }catch( Exception ex ) {

        }

        return sub;

    }

    private String formatAutores(List<Autor> autores) {

        String autros = "";
        int no =0;
        for(Autor autor : autores){
            no ++;
            if( autores.size() > 1){
                if( no < autores.size()){
                    autros += autor.getNombres() + " & ";
                }else{
                    autros += autor.getNombres()+".";
                }
            }else{
                autros += autor.getNombres()+".";


            }



        }


        return autros;



    }


    public int parseTintit( String json  ) throws Exception {

        int tintit = -1;

        JSONObject respuesta = new JSONObject( json );

        JSONObject response = respuesta.getJSONObject("response");

        if( response.getString("code").equals("0") && !response.getString("count").equals("0")) {



            JSONObject copias = response.getJSONObject("copias");



            tintit=  String2Int( getObjectString(copias,"contit"));



        }else {//No hya resultados

            throw new Exception( "No se encontrar�n resultados");
        }



        return tintit;

    }



    private JSONObject getCopiasObject(JSONObject record) {
        // TODO Auto-generated method stub
        JSONObject copias = null;

        try {
            if( isJsonObject( record,"copias" ) ) {

                copias =  record.getJSONObject("copias");


            }else {

                JSONArray copiasArr = record.getJSONArray("copias");

                for( int i=0; i < copiasArr.length(); i++ ) {

                    copias = copiasArr.getJSONObject(i);

                    break;
                }


            }
        }catch( Exception  ex ) {

            log.info( ex.getLocalizedMessage());
        }



        return copias;
    }



    private List<String> getIsbns(JSONArray datafields, String campo, String subcampo) {
        // TODO Auto-generated method stub
        List<String> isbns = new ArrayList<String>();
        String tag = "";
        String isbn;
        try {

            for( int i=0; i < datafields.length(); i++ ) {

                JSONObject datafield = datafields.getJSONObject(i);

                tag = getObjectString(datafield, "tag").trim();

                if( tag.equals(campo)) {

                    isbn = getDatoJsonObject_Array(datafield,subcampo).replace(".", "").trim();

                    if (!isbn.equals("N/D") && keywordIsUnico(isbn, isbns) ) {

                        isbns.add( isbn);
                    }
                }
            }

        }catch( Exception msg ) {

            log.info(msg.getLocalizedMessage());
        }
        return isbns;
    }
    /*
        private List<String> getKeywordsArrays( JSONArray datafields, String campo ){

            // TODO Auto-generated method stub
                    List<String> keywords = new ArrayList<String>();
                    String tag = "N/D";
                    String dato = "";
                    String key = "";
                    try {

                        for( int i=0; i < datafields.length(); i++ ) {

                            JSONObject datafield = datafields.getJSONObject(i);

                            tag = getObjectString(datafield, "tag").trim();

                            if( tag.equals(campo)) {

                                for(String subcam: SubcamposKeys) {

                                    dato= getKeywordDato(datafield.getJSONArray("subfield"), subcam);
                                    key = getdatoArrayCampo( datafield.getJSONArray("subfield"), "a" );
                                    //log.info(   key );
                                    if (!key.equals("N/D") && keywordIsUnico(key, keywords) ) {
                                        //log.info(    dato );
                                        keywords.add( key);
                                    }

                                }//end for

                            }//end if
                        }//end for

                    }catch( Exception ex ) {

                        log.warning(    ex.getMessage() );
                    }



                    return keywords;


        }
        */
    private List<String> get2Keyword(  JSONArray datafields, String campo ) {

        List<String> keywords = new ArrayList<String>();
        String tag = "N/D";
        String dato = "";
        String key = "";
        try {



            for( int i=0; i < datafields.length(); i++ ) {

                JSONObject datafield = datafields.getJSONObject(i);

                tag = getObjectString(datafield, "tag").trim();


                if( tag.equals(campo)) {


                    for(String subcam: SubcamposKeys) {

                        //key = getCampoDatafields( datafields, campo,subcam );
                        key = getDatoJsonObject_Array(datafield,subcam).replace(".", "");


                        //log.info("KEY" +  key );
                        if (!key.equals("N/D") && keywordIsUnico(key, keywords) ) {

                            keywords.add( key);
                        }
                    }//end for
                }//end if


            }//end for

        }catch( Exception ex ) {

            log.info("ERROR: " +   ex.getMessage()  );
        }

        return keywords;
    }




    private boolean isJsonArray( JSONObject datafield ) {

        boolean ban = false;

        try {

            JSONArray datos = datafield.getJSONArray("subfield");

            ban = true;

        }catch( Exception ex ) {

            ban = false;
        }

        return ban;

    }

    private boolean isJsonObject (JSONObject datafield, String tag){

        boolean ban = false;

        try {

            JSONObject datos = datafield.getJSONObject(tag);

            ban = true;

        }catch( Exception ex ) {

            ban = false;
        }

        return ban;

    }


    private boolean keywordIsUnico( String key ,List<String> keywords ) {

        boolean ban = true;

        for(String Keyword: keywords) {

            if(key.equals(Keyword)) {

                ban = false;
                break;
            }

        }

        return ban;
    }


    private String getKeywordDato(JSONArray jsonArray, String subcampo) {
        // TODO Auto-generated method stub
        return getdatoArrayCampo(jsonArray,subcampo );
    }

    private List<Autor> get2Autores( JSONArray datafields ) {
        List<Autor> autores = new ArrayList<Autor>();
        String tag = "N/D";
        Autor autor;
        try {

            for(String camposA: camposAutores) {

                for( int i=0; i < datafields.length(); i++ ) {

                    JSONObject datafield = datafields.getJSONObject(i);

                    tag = getObjectString(datafield, "tag").trim();

                    if( tag.equals(camposA)) {

                        autor = new Autor();

                        //autor.setCalidad( getCampoDatafields( datafields, camposA,"e" ) );
                        String nombre =  getCampoDatafields( datafields, camposA,"a" ).endsWith(",")?getCampoDatafields( datafields, camposA,"a" ).substring(0, getCampoDatafields( datafields, camposA,"a" ).length() -1):getCampoDatafields( datafields, camposA,"a" );
                        autor.setNombres( nombre );
                        autor.setCampo(tag);

                        if(validAutorUnico(autores, autor.getNombres()) && !autor.getNombres().trim().equals("N/D") ) {//valida si el autor es unico

                            if( autor.getNombres().endsWith(",")) {


                            }

                            autores.add(autor);
                        }



                    }

                }//end for
            }//end for
        }catch(Exception ex ) {


            log.warning(    ex.getMessage() );


        }

        return autores;
    }









    private boolean validAutorUnico(List<Autor> autores, String name_autor) {
        // TODO Auto-generated method stub
        boolean es_unico = true;

        for(Autor autor: autores) {

            if( autor.getNombres().toLowerCase().trim().equals(name_autor.toLowerCase().trim())) {
                es_unico = false;
                break;
            }

        }


        return es_unico;

    }
    private Date getObjectDate(JSONObject jsonObject, String key) {
        // TODO Auto-generated method stub
        Date dato;
        try {

            dato = (Date) jsonObject.get(key);

        }catch( Exception ex ) {

            log.warning( ex.getMessage());
        }
        return null;
    }

    private int getDatoBD(JSONObject jsonObject, String key) {
        // TODO Auto-generated method stub

        try {



        }catch( Exception ex ) {

            log.warning( ex.getMessage());

        }


        return 0;
    }

    private int String2Int(String objectString) {
        // TODO Auto-generated method stub

        int no = 0;

        try {

            no = Integer.parseInt( objectString );

        }catch( Exception ex  ) {

            no = -1;
        }
        return no;
    }

    private String getCampoDatafields(JSONArray datafields, String campo, String subcampo) {
        // TODO Auto-generated method stub
        String dato = "N/D";
        String tag = "N/D";

        try {

            for( int i=0; i < datafields.length(); i++ ) {

                JSONObject datafield = datafields.getJSONObject(i);

                tag = getObjectString(datafield, "tag").trim();

                if( tag.equals(campo)) {

                    dato = getDatoJsonObject_Array(datafield,subcampo);


                    //JSONObject subfield =  datafield.getJSONObject("subfield");
                    //dato = getdatoArrayCampo( datafield.getJSONArray("subfield"), subcampo );
                    //dato = getdatoArrayCampo( subfield.getJSONArray("subfield"), subcampo );



                }

            }//end for

        }catch(Exception ex ) {


            log.warning(   campo + subcampo+ ex.getMessage() );
            dato = "N/D";

        }

        return dato;
    }



    private String getDatoJsonObject_Array(JSONObject datafield, String subcampo) {
        // TODO Auto-generated method stub
        String dato= "";

        try {
            if( isJsonObject( datafield,"subfield" ) ) {

                JSONObject subfield =  datafield.getJSONObject("subfield");
                dato = getdatoSubField( subfield , subcampo );

            }else {

                dato = getdatoArrayCampo( datafield.getJSONArray("subfield"), subcampo );
            }
        }catch( Exception  ex ) {

            dato = "N/D";
        }

        return dato;
    }

    private String getdatoArrayCampo(JSONArray subfields , String subcampo) {

        String dato = "N/D";
        String code = "N/D";

        try {
            //log.info("Cuantos SUbfield: " + subfields.length() );
            for( int i=0; i < subfields.length(); i++ ) {

                JSONObject subfield = subfields.getJSONObject(i);

                code = getObjectString(subfield, "code").trim();

                if(code.equals(subcampo)) {

                    dato = getObjectString(subfield, "content");

                }
            }

        }catch( Exception ex ) {

            log.info("CAPA: " + ex.getLocalizedMessage());
        }

        return dato;

    }

    private String  getdatoSubField( JSONObject subfield,String subcampo ){

        String dato= "N/D";

        String code = getObjectString(subfield, "code").trim();



        if(code.equals(subcampo)) {

            dato = getObjectString(subfield, "content");


        }

        return dato;

    }

    private JSONObject getObjectJson (JSONObject jsonObject, String key) {

        return jsonObject.getJSONObject(key);

    }


    private String getObjectString (JSONObject jsonObject,String key) {

        String data = "N/D ";

        try {


            if( !jsonObject.getString(key).trim().equals("") && !jsonObject.getString(key).trim().isEmpty()) {

                data = jsonObject.getString(key).trim();
            }

        }catch( Exception ex ) {
            log.info(   ex.getMessage() );
            data = "N/D";

        }

        return data;

    }
}
