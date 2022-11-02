/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.maury.projetbdd;

import static fr.insa.maury.projetbdd.Encheres.defautConnect;


/**
 *
 * @author adrie
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

public class Objet {
    
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
        return connectGeneralPostGres("localhost", 5439, "postgres", "postgres", "papate");
    }
    
    public static void creeObjet(Connection con)// cree la table pour un ojbet
            throws SQLException {
        con.setAutoCommit(false);
        try ( Statement st = con.createStatement()) {
            st.executeUpdate(
                    """
                    create table objet (
                        id integer not null primary key
                        generated always as identity,
                        titre varchar(60) not null unique,
                        quantite int not null,
                        lieu varchar(20) not null,
                        codepos varchar(5) not null,
                        description varchar(500) not null,
                        prixinit float not null,
                        prixactuel float not null,
                        acheteur varchar(30) not null
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
    
    public static void deleteObjet(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            try {
                st.executeUpdate(
                        """
                    alter table objet
                        drop constraint fk_objet_u1
                             """);
                System.out.println("constraint fk_objet_u1 dropped");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate(
                        """
                    alter table objet
                        drop constraint fk_objet_u2
                    """);
                System.out.println("constraint fk_objet_u2 dropped");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate(
                        """
                    drop table objet
                    """);
                System.out.println("dable objet dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
//            try {
//                st.executeUpdate(
//                        """
//                    drop table utilisateur
//                    """);
//                System.out.println("table utilisateur dropped");
//            } catch (SQLException ex) {
//                // nothing to do : maybe the table was not created
//            }
        }
    }
    
    public static class TitreExisteDejaException extends Exception {
    }

  
    public static int createObjet(Connection con, String titre, int quantite, String lieu, String codepos, String description, double prixinit , double prixactuel,String acheteur )
            throws SQLException, TitreExisteDejaException {
        con.setAutoCommit(false);
        try ( PreparedStatement chercheTitre = con.prepareStatement(
                "select id from objet where titre = ?")) {
            chercheTitre.setString(1, titre);
            ResultSet testTitre = chercheTitre.executeQuery();
            if (testTitre.next()) {
                throw new TitreExisteDejaException();
            }
            try ( PreparedStatement pst = con.prepareStatement(
                    """
                insert into objet (titre,quantite,lieu,codepos,description,prixinit,prixactuel,acheteur) values (?,?,?,?,?,?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, titre);
                pst.setInt(2, quantite);
                pst.setString(3, lieu);
                pst.setString(4, codepos);
                pst.setString(5, description);
                pst.setDouble(6, prixinit);
                pst.setDouble(7,prixactuel);
                pst.setString(8,acheteur);
                pst.executeUpdate();
                con.commit();

                // je peux alors rÃ©cupÃ©rer les clÃ©s crÃ©Ã©es comme un result set :
                try ( ResultSet rid = pst.getGeneratedKeys()) {
                    // et comme ici je suis sur qu'il y a une et une seule clÃ©, je
                    // fait un simple next 
                    rid.next();
                    // puis je rÃ©cupÃ¨re la valeur de la clÃ© crÃ©Ã© qui est dans la
                    // premiÃ¨re colonne du ResultSet
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
     
    public static boolean idobjetExiste(Connection con, int id) throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                "select id from objet where id = ?")) {
            pst.setInt(1, id);
            ResultSet res = pst.executeQuery();
            return res.next();
        }
    }
    
    public static int choisiObjet(Connection con) throws SQLException {
        boolean ok = false;
        int id = -1;
        while (!ok) {
            System.out.println("Saisir un ID d'objet");
            id = Lire.i();
            ok = idobjetExiste(con, id);
            System.out.println("Id trouvé");
            if (!ok) {
                System.out.println("id invalide");
            }
        }
        return id;
    }
    
    public static void demandenouvelobjet(Connection con) throws SQLException{
        boolean existe = true; 
        String titre;
        Integer quantite;
        String lieu;
        String codepostal;
        String description;
        double prixinit;
        double prixactuel;
        String acheteur;
        while (existe){
            try {
                System.out.println("Saisir un titre");
                titre = Lire.S();
                System.out.println("Saisir un quantite");
                quantite= Lire.i();
                System.out.println("Saisir un lieu");
                lieu = Lire.S();
                System.out.println("Saisir un code postal");
                codepostal = Lire.S();
                System.out.println("Saisir un description");
                description = Lire.S();
                System.out.println("Saisir un prix initial et sera celui initial");
                prixinit =prixactuel= Lire.d();
                System.out.println("Saisir l'acheteur");
                acheteur=Lire.S();
                createObjet(con,titre,quantite,lieu,codepostal,description,prixinit,prixactuel,acheteur);
                existe = false;
            } catch (TitreExisteDejaException ex) {
                System.out.println("Ce titre existe deja, choisissez en un autre.");
            }
        } 
    }
    
    public static void afficheTousLesObjets(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from objet")) {
                System.out.println("liste des objets :");
                System.out.println("------------------------");
                while (tlu.next()) {
                    int id = tlu.getInt("id");
                    String titre = tlu.getString(2);
                    String quantite = tlu.getString(3);
                    String Lieu = tlu.getString(4);
                    String cdp = tlu.getString(5);
                    String des = tlu.getString(6);
                    String pi = tlu.getString(7);
                    String pa = tlu.getString(8);
                    String acheteur = tlu.getString(9);
                    System.out.println(id + " : " + titre + "("+quantite+")"+ "Lieu: " + Lieu +" Code postal : " + cdp + " Description :"+ des + " Prix intial : " + pi + " Prix actuel: "+ pa + " Acheteur : "+acheteur);
                }
            }
        }
    }public static void afficheUnObjet(Connection con,int id1) throws SQLException {
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from objet where id = "+id1)) {
                System.out.println("Information sur l'objet:"+id1);
                System.out.println("------------------------");
                while (tlu.next()) {
                    int id = tlu.getInt("id");
                    String titre = tlu.getString(2);
                    String quantite = tlu.getString(3);
                    String Lieu = tlu.getString(4);
                    String cdp = tlu.getString(5);
                    String des = tlu.getString(6);
                    String pi = tlu.getString(7);
                    String pa = tlu.getString(8);
                    String acheteur = tlu.getString(9);
                    System.out.println(id + " : " + titre + " ("+quantite+")"+ " Lieu: " + Lieu +" Code postal : " + cdp + " Description :"+ des + " Prix intial : " + pi + " Prix actuel: "+ pa + " Acheteur : "+acheteur);
                }
            }
        }
    }
    public static double ObtenirprixObjet(Connection con,int id) throws SQLException{
        double prixactuelr;
        prixactuelr=0;
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from objet where id="+id)) {
//                System.out.println("recherche un prix de l'objet "+id);
                while (tlu.next()) {
                    double prixactuel = tlu.getDouble(8);
//                    System.out.println("Prix actuel : "+prixactuel);
                    prixactuelr=prixactuel;
            }
            }
        }
        System.out.println("Prix actuel retourné de l'objet"+id+" : "+prixactuelr);
        return prixactuelr;
    }
    
    public static void Updateprix (Connection con,double nouveauprix,String mail) throws SQLException{//id1 : id de l'utilisateur qui va acheter le produit
            int id = Objet.choisiObjet(con);
            while(Utilisateur.Verifiemail(con, mail)==false){
                System.out.println("Saisir un email valide");
                 mail=Lire.S();
            }
                if(nouveauprix > Objet.ObtenirprixObjet(con, id)){
                    System.out.println("Le nouveau prix est bien supérieur à l'ancien.");
                    try(PreparedStatement pst = con.prepareStatement(
                    "update Objet set prixactuel = ?, acheteur = ?  where id ="+id)){
                        pst.setDouble(1, nouveauprix);
                        pst.setString(2, mail);
                        pst.executeUpdate();
                    }
                    con.setAutoCommit(true);
                }
    }
}
