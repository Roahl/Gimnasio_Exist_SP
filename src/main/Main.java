package main;

import net.xqj.exist.ExistXQDataSource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XPathQueryService;


import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by a15carlosspb on 06/03/2017.
 */
public class Main
{
    public static void main(String [] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, XMLDBException, XQException {
        String driver = "org.exist.xmldb.DatabaseImpl";
        Class c = Class.forName(driver);
        Database db = (Database) c.newInstance();
        DatabaseManager.registerDatabase(db);
        Collection col = null;
        Resource newResource;
        byte op;

        String uri = "xmldb:exist://localhost:8080/exist/xmlrpc/db/Gimnasio";
        String user = "admin";
        String password = "admin";

        try {
            col = DatabaseManager.getCollection(uri,user,password);
        } catch (XMLDBException e) {
            System.err.println("Fallo al conectar con la base de datos");
        }
        System.out.printf("\nSubiendo documentos xml a la base de datos...");

        File socios = new File("xml/socios_gim.xml");
        File actividades = new File("xml/actividades_gim.xml");
        File usos = new File("xml/uso_gimnasio.xml");

        if(!socios.canRead()||!actividades.canRead()||!usos.canRead())
            System.err.println("Error al leer los ficheros");
        else{
            newResource = col.createResource(socios.getName(),"XMLResource");
            newResource.setContent(socios);
            col.storeResource(newResource);

            newResource = col.createResource(actividades.getName(),"XMLResource");
            newResource.setContent(actividades);
            col.storeResource(newResource);

            newResource = col.createResource(usos.getName(),"XMLResource");
            newResource.setContent(usos);
            col.storeResource(newResource);
        }

        System.out.printf("\nA continuación se obtendrá de cada socio su cuota a pagar");
        cuotas(col);

    }

    public static void cuotas(Collection col) throws XQException, XMLDBException {
        /*XQDataSource server = new ExistXQDataSource();
        server.setProperty("serverName","localhost");
        server.setProperty("port","8080");
        XQConnection*/

        XPathQueryService xpqs = (XPathQueryService)col.getService("XPathQueryService", "1.0");
        xpqs.setProperty("indent", "yes");
        ResourceSet result = xpqs.query("for $socio in /Socios/socio "+
                                        "let $cod := $socio/@codigo "+
                                        "for $uso in /USO_GIMNASIO/fila_uso[CODSOCIO = $cod] "+
                                        "let $codactividad := $uso/CODACTIV "+
                                        "for $actividad in /Actividades/actividad[@codigo=$codactividad] "+
                                        "let $cuota_adicional := $actividad/cuota_adicional "+
                                        "return <datos> "+
                                        "           <codigo_socio>{$cod}</codigo_socio> "+
                                        "           <nombre_socio>{string($socio/nombre)}</nombre_socio> "+
                                        "           <codigo_actividad>{string($codactividad)}</codigo_actividad> "+
                                        "           <nombre_actividad>{string($actividad/nombre)}</nombre_actividad> "+
                                        "           <duracion>{number($uso/HORAFINAL)-number($uso/HORAINICIO)}</duracion> "+
                                        "           <tipo_actividad>{($actividad/@tipo)}</tipo_actividad> "+
                                        "           <cuota_adicional>{number($cuota_adicional)}</cuota_adicional> "+
                                        "       </datos> ");
        ResourceIterator r = result.getIterator();
        File fichero = new File("xml/usuarios_cuota.xml");
        if(fichero.exists()){
            fichero.delete();
        }
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter("xml/usuarios_cuota.xml"));
            bw.write("<?xml version='1.0' encoding='UTF-8'?>" + "\n");
            bw.write("<usuarios_cuotas>"+"\n");

            while(r.hasMoreResources()){
                bw.write( r.nextResource().getContent()+"\n");
            }

            bw.write("</usuarios_cuotas>"+"\n");
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
