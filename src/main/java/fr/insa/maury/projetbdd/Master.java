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

import fr.insa.vedel.javafx.projet.guiFX.Main;

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
        String pass;
        String categorie;
        String acheteur;
        boolean verif;
        double nouveauprix;
        System.out.println("Bienvenu dans le site d'enchere");
        System.out.println("-------------------------------");
        while(menu==0){
            System.out.println("Saisir 1 : menu des utilisateurs");
            System.out.println("Saisir 2 : menu des objets");
            System.out.println("Saisir 4 : menu des administrateurs");
            System.out.println("Saisir 5 : fin de programme");
            System.out.println("Saisir 6 : lancer l'interface graphique");
            System.out.println("Saisir 7 : fonction de programmation");
            a1=Lire.i();
            if(a1==1){
                System.out.println("Saisir 1 : Saisir un nouvel utilisateur");
                System.out.println("Saisir 2 : Information sur un utilisateur");
                System.out.println("Saisir 3 : Information sur tout les utilisateurs");
                a2=Lire.i();
                try ( Connection con = defautConnect()) {
                    if(a2==1){
                        Utilisateur.demandenouvelutilisateur(con);
                    }
                    if(a2==2){
                        System.out.println("Saisir le mail de l'utillisateur");
                        mail=Lire.S();
                        id=Utilisateur.Obtenirid(con, mail);
                        Utilisateur.afficheUnUtilisateur(con, id);
                    }
                    if(a2==3){
                        Utilisateur.afficheTousLesUtilisateur(con);
                    }
                } catch (Exception ex) {
                    throw new Error(ex);
                }
            }
            if(a1==2){
                System.out.println("Saisir 1 : Saisir un nouvel objet");
                System.out.println("Saisir 2 : Faire une offre sur un objet");
                System.out.println("Saisir 3 : Information sur des objets");
                a2=Lire.i();
                try ( Connection con = defautConnect()) {
                    if(a2==1){
                        Objet.demandenouvelobjet(con);
                    }
                    if(a2==2){
                        System.out.println("Saisir votre email d'utilisateur");
                        mail=Lire.S();
                        System.out.println("Saisir votre mot de passe");
                        pass= Lire.S();
                        if(true==Utilisateur.Verifmdp(con, pass, mail)){
                            System.out.println("Saisir votre offre pour l'objet");
                            nouveauprix=Lire.d();
                            Objet.Updateprix(con, nouveauprix, mail);
                        }else{
                            //ne fait rien si le mdp n'est pas bon
                        }
                    }
                    if(a2==3){
                        System.out.println("Tapez 1: recherche par categorie");
                        System.out.println("Tapez 2: recherche par ID");
                        System.out.println("Tapez 3 : recherche par Acheteur");
                        a3=Lire.i();
                        if(a3==2){
                            System.out.println("Saisir l'ID de l'objet dont vous voulez l'information");
                            id=Lire.i();
                            Objet.afficheUnObjet(con, id);
                        }
                        if(a3==1){
                            System.out.println("Saisir la categorie dont vous voulez tout les objets");
                            categorie = Lire.S();
                            System.out.println(Objet.afficheObjetparCategorie(con, categorie));
                        }
                        if(a3==3){
                            System.out.println("Saisir le mail de l'Utilisateur");
                            acheteur=Lire.S();
                            System.out.println(Objet.afficheObjetparAcheteur(con, acheteur));
                        }
                    }
                } catch (Exception ex) {
                    throw new Error(ex);
                }
            }
            if(a1==4){
                System.out.println("Saisir votre email pour verification du statut admnistrateur ");
                mail=Lire.S();
                try ( Connection con = defautConnect()) {
                    verif=Utilisateur.Verifadmin(con, mail);
                    System.out.println("Saisir votre mot de passe");
                    pass= Lire.S();
                if(true==Utilisateur.Verifmdp(con, pass, mail)){
                    if(verif ==true){
                        System.out.println("Saisir 1 : Supprimer un utilisateur");
                        System.out.println("Saisir 2 : Supprimer un objet");
                        System.out.println("Saisir 3 : Ajouter un utilisateur en administrateur");
                        System.out.println("Saisir 4 : Ajouter une categorie");
                        a2=Lire.i();
                        if(a2==1){
                            System.out.println("Taper l'ID de l'utilisateur à supprimer :");
                            id=Lire.i();
                            Utilisateur.deleteUnUtilisateur(con, id);
                        } 
                        if(a2==2){
                            System.out.println("Taper l'ID de l'objet à supprimer :");
                            id=Lire.i();
                            Objet.deleteUnObjet(con, id);
                        }
                        if(a2==3){
                            System.out.println("Taper l'ID de l'utilisateur à mettre à jour en administrateur");
                            Utilisateur.UpdateUtilisateurEnAdmin(con, id);
                        }
                        if(a2==4){
                            Encheres.demandenouvelcategorie(con);
                        }
                    }else{
                        System.out.println("Vous n'avez pas accès à cette partie du menu");
                    }
                }else{
                    //ne fais rien si le mdp n'est pas bon
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
            if(a1==6){
                Main.main(args);
            }
            if(a1==7){
                try ( Connection con = defautConnect()) {
                System.out.println("Vous entrez dans une partie du menu de dédié au developpement");
                System.out.println("Saisir 0 : Sortie de la partie de developpement");
                System.out.println("Saisir 1 : Suppression des objets");
                System.out.println("Saisir 2 : Suppression des utilisateurs");
                System.out.println("Saisir 3 : Créer BdD objet");
                System.out.println("Saisir 4 : Créer BdD Utilisateur");
                System.out.println("Saisir 5 : Créer BdD Categorie");
                System.out.println("Saisir 6 : Suppression des categories");
                a2=Lire.i();
                if(a2==0){
                }
                if(a2==1){
                    Objet.deleteObjet(con);
                }
                if(a3==2){
                    Utilisateur.deleteUtilisateur(con);
                }
                if(a2==3){
                    Objet.creeObjet(con);
                }
                if(a2==4){
                    Utilisateur.creeUtilisateur(con);
                }
                if(a2==5){
                    Encheres.creeCategorie(con);
                }
                if(a2==6){
                    Encheres.deleteCategorie(con);
                }
            }catch (Exception ex) {
                    throw new Error(ex);
                }
            }
        }
    }
}


  
