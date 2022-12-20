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

public class Utilisateur{
    
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
    
    public static void creeUtilisateur(Connection con)// cree la table pour l'utilisateur 
            throws SQLException {
        con.setAutoCommit(false);
        try ( Statement st = con.createStatement()) {
            st.executeUpdate(
                    """
                    create table utilisateur (
                        id integer not null primary key
                        generated always as identity,
                        nom varchar(30) not null unique,
                        prenom varchar(30) not null,
                        pass varchar(20) not null,
                        codepos varchar(5) not null,
                        mail varchar(100) not null unique,
                        administrateur int not null
                    )
                    """);

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
    public static void deleteUtilisateur(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            try {
                st.executeUpdate(
                        """
                    drop table utilisateur
                    """);
                System.out.println("table utilisateur dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
        }
    }
    
    public static class NomExisteDejaException extends Exception {
    }

    
    public static int createUtilisateur(Connection con, String nom, String pass, String mail, String codepos, String prenom, int admin)
            throws SQLException, NomExisteDejaException {
        // je me place dans une transaction pour m'assurer que la sÃ©quence
        // test du nom - crÃ©ation est bien atomique et isolÃ©e
        con.setAutoCommit(false);
        try ( PreparedStatement chercheNom = con.prepareStatement(
                "select id from utilisateur where nom = ?")) {
            chercheNom.setString(1, nom);
            ResultSet testNom = chercheNom.executeQuery();
            if (testNom.next()) {
                throw new NomExisteDejaException();
            }
            try ( PreparedStatement pst = con.prepareStatement(
                    """
                insert into utilisateur (nom,prenom,pass,codepos,mail,administrateur) values (?,?,?,?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, nom);
                pst.setString(2, prenom);
                pst.setString(3, pass);
                pst.setString(4, codepos);
                pst.setString(5, mail);
                pst.setInt(6, admin);
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
    
    public static void demandenouvelutilisateur(Connection con) throws SQLException{
        boolean existe = true; 
        String nom;
        String prenom;
        String mdp;
        String codepostal;
        String mail;
        int admin;
        while (existe){
            try {
                System.out.println("Saisir un nom");
                nom = Lire.S();
                System.out.println("Saisir un prenom");
                prenom= Lire.S();
                System.out.println("Saisir un mdp");
                mdp = Lire.S();
                System.out.println("Saisir un mail");
                mail = Lire.S();
                System.out.println("Saisir un code postal");
                codepostal = Lire.S();
                System.out.println("Est-ce un administrateur 1/0 ? ");
                admin=Lire.i();
                System.out.println(admin);
                createUtilisateur(con,nom,mdp,mail,codepostal,prenom,admin);
                existe = false;
            } catch (NomExisteDejaException ex) {
                System.out.println("ce nom existe deja, choisissez en un autre");
            }
        } 
    }
    
     public static void afficheTousLesUtilisateur(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from utilisateur")) {
                System.out.println("liste des utilisateurs :");
                System.out.println("------------------------");
                while (tlu.next()) {
                    int id = tlu.getInt("id");
                    String nom = tlu.getString(2);
                    String prenom = tlu.getString(3);
                    String pass = tlu.getString(4);
                    String cdp = tlu.getString(5);
                    String mail = tlu.getString(6);
                    String administrateur = tlu.getString(7);
                    System.out.println(id + " : " + nom + " " + prenom + " mdp (" + pass +") , code postal: " + cdp + ", mail: " + mail + ", admin: " + administrateur );
                }
            }
        }
    }
     public static void afficheUnUtilisateur(Connection con, int id1) throws SQLException {
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from utilisateur where id = "+id1)) {
                System.out.println("Information sur l'utilisateur :"+id1);
                System.out.println("------------------------");
                while (tlu.next()) {
                    System.out.println("recolte des données");
                    int id = tlu.getInt("id");
                    String nom = tlu.getString(2);
                    String prenom = tlu.getString(3);
                    String pass = tlu.getString(4);
                    String cdp = tlu.getString(5);
                    String mail = tlu.getString(6);
                    String administrateur = tlu.getString(7);
                    System.out.println(id + " : " + nom + " " + prenom + " mdp (" + pass +") , code postal: " + cdp + ", mail: " + mail + ", admin: " + administrateur );
                }
            }
        }
    }
     
     public static String Obtenirmail(Connection con,int id) throws SQLException{
        String mail="init";
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from utilisateur where id="+id)) {
                while (tlu.next()) {
                    mail = tlu.getString(6);
            }
            }
        }
        return mail;
    }
    
    public static int Obtenirid(Connection con, String mail) throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                "select id from utilisateur where mail = ? ")) {
            pst.setString(1, mail);
            ResultSet res = pst.executeQuery();
            res.next();
            int id = res.getInt("id");
            System.out.println("IDtrouvé :"+id);
            return id;
            }
    }
     
     public static boolean Verifiemail(Connection con,String mail) throws SQLException{
        boolean verif = false;
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from utilisateur")) {
                 while (tlu.next()) {
                     if(mail.equals(tlu.getString(6))){
                         verif =true;
                         System.out.println("L'email existe");
                     }
                 }
                 if(verif == false ){
                     System.out.println("L'email saisi ne correspond à aucun utilisateur");
                 }
            }
        }
        return verif;
     }
     
     public static boolean Verifadmin(Connection con, String mail) throws SQLException{
         boolean verif = false;
         int admin=0;
         try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from utilisateur")) {
                 while (tlu.next()) {
                     if(mail.equals(tlu.getString(6))){
                         admin=tlu.getInt(7);
                            if(admin==1){
                                System.out.println("Vous etes administrateur");
                                verif=true;
                            }else{
                                System.out.println("Vous n'etes pas un administrateur");
                            }
                        }
                    }
            }
        }
        return verif;
     }
     
     public static boolean Verifmdp(Connection con, String mdp, String mail ) throws SQLException{
         boolean verif = false;
         String pass;
           try ( Statement st = con.createStatement()) {
                try (PreparedStatement pst = con.prepareStatement(
                    "select pass from utilisateur where mail = ? ")) {
                    pst.setString(1, mail);
                    ResultSet res = pst.executeQuery();
                    res.next();
                    pass = res.getString("pass");
                    if(mdp.equals(pass)){
                        verif = true;
                        System.out.println("Vous avez rentré le bon mot de pass");
                    }else{
                        System.out.println("Vous n'avez pas saisi le bon mot de passe");
                    }
                }
            }
           return verif;
    }
     
     public static void deleteUnUtilisateur(Connection con, int id) throws SQLException{
         try ( Statement st = con.createStatement()) {
             System.out.println("Confirmez la suppression de l'utilisateur suivant :");
             Utilisateur.afficheUnUtilisateur(con, id);
             System.out.println("-------------------");
             System.out.println("Tapez 1: confirmez la suppression, Tapez 2 : modifié l'ID");
             int a= Lire.i();
             if(a==1){
             }else if (a==2){
                 int b=0;
                 while(b==0){
                    System.out.println("Vous avez choisi de modifier l'ID de l'utilisateur à supprimer , tapez le nouvel ID");
                    id=Lire.i();
                    Utilisateur.afficheUnUtilisateur(con, id);
                    System.out.println("Si c'est le bon utilisateur taper 1 sinon 0");
                    b=Lire.i();
                 }
             }
            try {
                st.executeUpdate(
                        """
                    delete from utilisateur where id=
                    """+id);
                System.out.println("Utilisateur "+id+" supprimé");
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
        }
     }
     
     public static void UpdateUtilisateurEnAdmin(Connection con, int id) throws SQLException{
         try ( Statement st = con.createStatement()) {
             System.out.println("Saisir l'email de l'utilisateur à modifier le statut");
             String mail=Lire.S();
             if(true==Utilisateur.Verifadmin(con, mail)){
                 System.out.println("L'utilisateur est déjà un administrateur");
             }else{
                 try(PreparedStatement pst = con.prepareStatement(
                    "update utilisateur set administrateur = 1")){
                        pst.executeUpdate();
                    }
                    con.setAutoCommit(true);
                    System.out.println("Utilisateur "+id+" bien mis à jour en administrateur");
             }
             
         }
     }
     
    public static void UpdateCategorie(Connection con, String mail) throws SQLException{
       int b=0;
       String Cat;
        try ( Statement st = con.createStatement()) {
            if(true==Utilisateur.Verifadmin(con, mail)){
              System.out.println("Saisir votre mot de passse :");
              String mdp;
              mdp = Lire.S();
              while(b==0);
                if(true==Utilisateur.Verifmdp(con, mdp, mail)){
                    b=1;
                    System.out.println("Saisir une nouvelle categorie");
                    Cat = Lire.S();
                }else{
                    System.out.println("Vous n'avez pas saisi le bon mot de passe");
                    System.out.println("Saisir 1 pour retour, sinon retapez votre mot de passe");
                    mdp = Lire.S();
                    if(mdp == "1"){
                        b=1;
                    }
                }
            }else{
              System.out.println("Vous n'etes pas un administrateur");
            }
       }
        
    }
     
}


