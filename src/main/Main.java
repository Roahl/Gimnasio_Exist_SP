package main;

import net.xqj.exist.ExistXQDataSource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;


import javax.xml.xquery.XQDataSource;
import java.io.File;

/**
 * Created by a15carlosspb on 06/03/2017.
 */
public class Main
{
    public static void main(String [] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, XMLDBException {
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
    }

    public static void cuotas(){
        XQDataSource server = new ExistXQDataSource();
    }
}
