/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insa.maury.projetbdd;

/**
 *
 * @author Robin
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Encheres{
    
    public static Connection connectGeneralPostGres(String host,
            int port, String database,
            String user, String pass)
            throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:postgresql://" + host + ":" + port
                + "/" + database,
                user, pass);
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return con;
    }

    public static Connection defautConnect()
            throws ClassNotFoundException, SQLException {
        return connectGeneralPostGres("localhost", 5439, "postgres", "postgres", "pass");
    }
    
    public static void main(String[] args) {
        int a;
//        try ( Connection con = defautConnect()) {
//            System.out.println("connecté !!!");
//            Objet.creeObjet(con);
//        } catch (Exception ex) {
//            throw new Error(ex);
//        }
//        a=1;
//        if(a==1){
//            try ( Connection con = defautConnect()) {
//                System.out.println("connecté !!!");
//                Objet.demandenouvelobjet(con);
////                Utilisateur.afficheTousLesUtilisateur(con);
//            } catch (Exception ex) {
//                throw new Error(ex);
//            }
//        }
//        try ( Connection con = defautConnect()){
//        Objet.deleteObjet(con);
//        System.out.println("Schema bien detruit");
//        } catch (Exception ex) {
//            throw new Error(ex);
//        }
        try ( Connection con = defautConnect()) {
//            Objet.choisiObjet(con);
//              Utilisateur.demandenouvelutilisateur(con);
              int id=3;
              Objet.afficheTousLesObjets(con);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }
}


