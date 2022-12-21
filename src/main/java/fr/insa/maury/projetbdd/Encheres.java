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
import static fr.insa.maury.projetbdd.Utilisateur.createUtilisateur;
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
    
     public static void creeCategorie(Connection con)// cree la table pour une ctageorie
            throws SQLException {
        con.setAutoCommit(false);
        try ( Statement st = con.createStatement()) {
            st.executeUpdate(
                    """
                    create table categorie (
                        id integer not null primary key
                        generated always as identity,
                        titre varchar(60) not null unique,
                        description varchar(500) not null
                    )
                    """);
            // trouver comment mettre des doubles dans la base de donnée
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            con.rollback();
            System.out.println("Erreur 1");
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }
     
     public static void deleteCategorie(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            try {
                st.executeUpdate(
                        """
                    drop table categorie
                    """);
                System.out.println("table categorie dropped");
            } catch (SQLException ex) {
            }
        }
    }
    
    public static class TitreExisteDejaException extends Exception {
    }
    
     public static int createCategorie(Connection con, String titre, String description)
            throws SQLException, TitreExisteDejaException {
        con.setAutoCommit(false);
        try ( PreparedStatement chercheNom = con.prepareStatement(
                "select id from categorie where titre = ?")) {
            chercheNom.setString(1, titre);
            ResultSet testNom = chercheNom.executeQuery();
            if (testNom.next()) {
                throw new TitreExisteDejaException();
            }
            try ( PreparedStatement pst = con.prepareStatement(
                    """
                insert into Categorie (titre,description) values (?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, titre);
                pst.setString(2, description);
                pst.executeUpdate();
                con.commit();
                try ( ResultSet rid = pst.getGeneratedKeys()) {
                    rid.next();
                    int id = rid.getInt(1);
                    return id;
                }
            }
        } catch (Exception ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }
     
     public static void demandenouvelcategorie(Connection con) throws SQLException{
        boolean existe = true; 
        String titre;
        String description;
        while (existe){
            try {
                System.out.println("Saisir un titre");
                titre = Lire.S();
                System.out.println("Saisir une description");
                description= Lire.S();
                createCategorie(con,titre,description);
                existe = false;
            } catch (TitreExisteDejaException ex) {
                System.out.println("ce titre existe deja, choisissez en un autre");
            }
        } 
    }
     public static void afficheTouteslesCategories(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from categorie")) {
                System.out.println("liste des categories :");
                System.out.println("------------------------");
                while (tlu.next()) {
                    int id = tlu.getInt("id");
                    String titre = tlu.getString(2);
                    String description = tlu.getString(3);
                    System.out.println(id + " : Categorie : " + titre + " , Description :" + description);
                }
            }
        }
    }
     
    public static boolean VerifieCategorie(Connection con,String titre) throws SQLException{
        boolean verif = false;
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from categorie")) {
                 while (tlu.next()) {
                     if(titre.equals(tlu.getString(2))){
                         verif =true;
                         System.out.println("La Categorie existe");
                     }
                 }
                 if(verif == false ){
                     System.out.println("La categorie n'existe pas");
                 }
            }
        }
        return verif;
     }

}



