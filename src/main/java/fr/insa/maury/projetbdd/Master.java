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


public class Master{
    
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
    
    public static void main(String[] args) throws SQLException {
        int a1=0,a2=0,a3=0;
        int id=0;
        int menu=0;
        String mail;
        String nom;
        boolean verif;
        double nouveauprix;
        System.out.println("Bienvenu dans le site d'enchere");
        System.out.println("-------------------------------");
        System.out.println("Saisir les numeros suivant pour vous déplacez dans les menus : 0 = retour menu génèrale");
        while(menu==0){
            System.out.println("Saisir 1 : menu des utilisateurs");
            System.out.println("Saisir 2 : menu des objets");
            System.out.println("Saisir 3 : menu des enchères");
            System.out.println("Saisir 4 : menu des administrateurs");
            System.out.println("Saisir 5 : fin de programme");
            a1=Lire.i();
            if(a1==1){
                System.out.println("Saisir 1 : Saisir un nouvel utilisateur");
                System.out.println("Saisir 2 : Information sur un utilisateur");
                a2=Lire.i();
                try ( Connection con = defautConnect()) {
                    if(a2==1){
                        Utilisateur.creeUtilisateur(con);
                    }
                    if(a2==2){
                        System.out.println("Saisir le mail de l'utillisateur");
                        mail=Lire.S();
                        id=Utilisateur.Obtenirid(con, mail);
                        Utilisateur.afficheUnUtilisateur(con, id);
                    }
                } catch (Exception ex) {
                    throw new Error(ex);
                }
            }
            if(a1==2){
                System.out.println("Saisir 1 : Saisir un nouvel objet");
                System.out.println("Saisir 2 : Faire une offre sur un objet");
                System.out.println("Saisir 3 : Information sur un objet");
                a2=Lire.i();
                try ( Connection con = defautConnect()) {
                    if(a2==1){
                        Objet.creeObjet(con);
                    }
                    if(a2==2){
                        System.out.println("Saisir votre email d'utilisateur");
                        mail=Lire.S();
                        System.out.println("Saisir votre offre pour l'objet");
                        nouveauprix=Lire.d();
                        Objet.Updateprix(con, nouveauprix, mail);
                    }
                } catch (Exception ex) {
                    throw new Error(ex);
                }
            }
            if(a1==3){
                System.out.println("Saisir 1 : Information sur une enchère");
            }
            if(a1==4){
                System.out.println("Saisir votre email pour verification du statut admnistrateur ");
                mail=Lire.S();
                try ( Connection con = defautConnect()) {
                    verif=Utilisateur.Verifadmin(con, mail);
                    if(verif ==true){
                        //a coder : supprimer un utilisateur , ajouter/supprimer un administrateur,cloturer une enchere 
                    }else{
                        System.out.println("Vous n'avez pas accès à cette partie du menu");
                    }
                } catch (Exception ex) {
                    throw new Error(ex);
                }
            }
            if(a1==5){
                System.out.println("Confirmez la sortie de programme, tapez 1");
                a2=Lire.i();
                if(a2==1){
                    menu=1;
                    System.out.println("Sortie de programme confirmé");
                }else{
                    System.out.println("Vous n'etes pas sorti, retour au menu");
                }
            }
        }
    }
}


  
