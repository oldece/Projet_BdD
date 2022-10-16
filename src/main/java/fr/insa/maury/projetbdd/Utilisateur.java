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
        return connectGeneralPostGres("localhost", 5439, "postgres", "postgres", "pass");
    }
    
    public static void creeUtilisateur(Connection con)// cree la table pour l'utilisateur 
            throws SQLException {
        // je veux que le schema soit entierement crÃ©Ã© ou pas du tout
        // je vais donc gÃ©rer explicitement une transaction
        con.setAutoCommit(false);
        try ( Statement st = con.createStatement()) {
            // creation des tables
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
                    )
                    """);
            // si j'arrive jusqu'ici, c'est que tout s'est bien passÃ©
            // je confirme (commit) la transaction
            con.commit();
            // je retourne dans le mode par dÃ©faut de gestion des transaction :
            // chaque ordre au SGBD sera considÃ©rÃ© comme une transaction indÃ©pendante
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            // quelque chose s'est mal passÃ©
            // j'annule la transaction
            con.rollback();
            System.out.println("Erreur 1");
            // puis je renvoie l'exeption pour qu'elle puisse Ã©ventuellement
            // Ãªtre gÃ©rÃ©e (message Ã  l'utilisateur...)
            throw ex;
        } finally {
            // je reviens Ã  la gestion par dÃ©faut : une transaction pour
            // chaque ordre SQL
            con.setAutoCommit(true);
        }
    }
    // vous serez bien contents, en phase de dÃ©veloppement de pouvoir
    // "repartir de zero" : il est parfois plus facile de tout supprimer
    // et de tout recrÃ©er que d'essayer de modifier le schema et les donnÃ©es
    public static void deleteUtilisateur(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            // pour Ãªtre sÃ»r de pouvoir supprimer, il faut d'abord supprimer les liens
            // puis les tables
            // suppression des liens
            try {
                st.executeUpdate(
                        """
                    alter table aime
                        drop constraint fk_aime_u1
                             """);
                System.out.println("constraint fk_aime_u1 dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate(
                        """
                    alter table aime
                        drop constraint fk_aime_u2
                    """);
                System.out.println("constraint fk_aime_u2 dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            // je peux maintenant supprimer les tables
            try {
                st.executeUpdate(
                        """
                    drop table aime
                    """);
                System.out.println("dable aime dropped");
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
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

    
    public static int createUtilisateur(Connection con, String nom, String pass, String mail, String codepos, String prenom)
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
            // lors de la creation du PreparedStatement, il faut que je prÃ©cise
            // que je veux qu'il conserve les clÃ©s gÃ©nÃ©rÃ©es
            try ( PreparedStatement pst = con.prepareStatement(
                    """
                insert into utilisateur (nom,prenom,pass,codepos,mail) values (?,?,?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, nom);
                pst.setString(2, prenom);
                pst.setString(3, pass);
                pst.setString(4, codepos);
                pst.setString(5, mail);
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
    
    public static void demandenouvelutilisateur(Connection con) throws SQLException{
        boolean existe = true; 
        String nom;
        String prenom;
        String mdp;
        String codepostal;
        String mail;
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
                createUtilisateur(con,nom,prenom,mdp,codepostal,mail);
                existe = false;
            } catch (NomExisteDejaException ex) {
                System.out.println("ce nom existe deja, choisissez en un autre");
            }
        } 
    }
    
    public static void main(String[] args) {
        String a;
        try ( Connection con = defautConnect()) {
            System.out.println("connecté !!!");
            creeUtilisateur(con);
        } catch (Exception ex) {
            throw new Error(ex);
        }
//        System.out.println("Voulez vous ajoutez un utilisateur ? oui/non");
//        a=Lire.S();
//        if(a=="oui"){
//            try ( Connection con = defautConnect()) {
//                System.out.println("connecté !!!");
//                demandenouvelutilisateur(con);
//            } catch (Exception ex) {
//                throw new Error(ex);
//            }
//        }
//        try ( Connection con = defautConnect()){
//        deleteUtilisateur(con);
//        System.out.println("Schema bien detruit");
//        } catch (Exception ex) {
//            throw new Error(ex);
//        }
    }
}


