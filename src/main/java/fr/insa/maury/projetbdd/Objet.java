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
import java.text.SimpleDateFormat;
import java.sql.Date;

public class Objet {
   
public int IdObjet;
public String titre; 
public String Categorie;
public String Mail_vendeur;
public String lieu;
public String codepos;
public String description;
public float prixinit;
public float prixactuel;
public String Date_de_fin;
public String acheteur;

public Objet(int id,String titre, String Categorie, String mail_vendeur, String lieu,String codepos,String description,float prixinit,float prixactuel,String Date_de_fin,String acheteur){
    this.IdObjet=id ;
    this.Categorie=Categorie;
    this.Mail_vendeur= mail_vendeur;
    this.Date_de_fin = Date_de_fin;
    this.lieu=lieu;
    this.prixactuel=prixactuel;
    this.prixinit = prixinit;
    this.codepos=codepos;
    this.titre = titre;
    this.acheteur=acheteur;
    this.description=description;
}
public int getIdObjet() {
        return IdObjet;
    }

public void setIdObjet(int id){
    this.IdObjet=id;
}

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String Categorie) {
        this.Categorie = Categorie;
    }

    public String getMail_vendeur() {
        return Mail_vendeur;
    }

    public void setMail_vendeur(String Mail_vendeur) {
        this.Mail_vendeur = Mail_vendeur;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getCodepos() {
        return codepos;
    }

    public void setCodepos(String codepos) {
        this.codepos = codepos;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrixinit() {
        return prixinit;
    }

    public void setPrixinit(float prixinit) {
        this.prixinit = prixinit;
    }

    public float getPrixactuel() {
        return prixactuel;
    }

    public void setPrixactuel(float prixactuel) {
        this.prixactuel = prixactuel;
    }

    public String getDate_de_fin() {
        return Date_de_fin;
    }

    public void setDate_de_fin(String Date_de_fin) {
        this.Date_de_fin = Date_de_fin;
    }

    public String getAcheteur() {
        return acheteur;
    }

    public void setAcheteur(String acheteur) {
        this.acheteur = acheteur;
    }

    @Override
    public String toString() {
        return "Objet{" + "IdObjet=" + IdObjet + ", titre=" + titre + ", Categorie=" + Categorie + ", Mail_vendeur=" + Mail_vendeur + ", lieu=" + lieu + ", codepos=" + codepos + ", description=" + description + ", prixinit=" + prixinit + ", prixactuel=" + prixactuel + ", Date_de_fin=" + Date_de_fin + ", acheteur=" + acheteur + '}';
    }


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
                        categorie varchar(30) not null,
                        mail_vendeur varchar(50) not null,
                        lieu varchar(20) not null,
                        codepos varchar(8) not null,
                        description varchar(500) not null,
                        prixinit float not null,
                        prixactuel float not null,
                        date_de_fin date not null,
                        acheteur varchar(60) not null
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
                    drop table objet
                    """);
                System.out.println("dable objet dropped");
            } catch (SQLException ex) {
            }
        }
    }
    
    public static class TitreExisteDejaException extends Exception {
    }

  
    public static int createObjet(Connection con, String titre,String Categorie,String mailvendeur, String lieu, String codepos, String description, double prixinit , double prixactuel , String datedefin , String acheteur)
            throws Exception, SQLException, TitreExisteDejaException {
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
                insert into objet (titre,categorie,mail_vendeur,lieu,codepos,description,prixinit,prixactuel,Date_de_fin,acheteur) values (?,?,?,?,?,?,?,?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, titre);
                pst.setString(2, Categorie);
                pst.setString(3,mailvendeur);
                pst.setString(4, lieu);
                pst.setString(5, codepos);
                pst.setString(6, description);
                pst.setDouble(7, prixinit);
                pst.setDouble(8,prixactuel);
                pst.setDate(9,Date.valueOf(datedefin));
                pst.setString(10,acheteur);
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
    
    public static void demandenouvelobjet(Connection con) throws SQLException, Exception{
        boolean existe = true; 
        String titre;
        Integer quantite;
        String lieu;
        String codepostal;
        String description;
        String categorie;
        double prixinit;
        double prixactuel;
        String acheteur;
        String datedefin;
        String mailvendeur;
        int b=0;
        int a=0;
        while (existe){
            try {
                System.out.println("Saisir un titre");
                titre = Lire.S();
                System.out.println("Saisir une categorie");
                categorie = Lire.S();
                while(Encheres.VerifieCategorie(con, categorie)==false){
                    System.out.println("Votre categorie saisi ne correspond pas au categore disponible sur le site");
                    System.out.println("Saisir une categorie existante tapez 1, tapez 0 voir les categories");
                    a=Lire.i();
                    if(a==1){
                        categorie=Lire.S();
                    }
                    if(a==0){
                        Encheres.afficheTouteslesCategories(con);
                    }
                }
                System.out.println("Saisir le mail du vendeur");
                mailvendeur = Lire.S();
                while(b==0){
                    if(true==Utilisateur.Verifiemail(con, mailvendeur)){
                        b=1;
                    }else{
                        System.out.println("L'email saisi n'existe pas, recommencez");
                    }
                }
                System.out.println("Saisir un lieu");
                lieu = Lire.S();
                System.out.println("Saisir un code postal");
                codepostal = Lire.S();
                System.out.println("Saisir un description");
                description = Lire.S();
                System.out.println("Saisir un prix initial et sera celui initial");
                prixinit =prixactuel= Lire.d();
                System.out.println("Il n'y a pas d'acheteur à la creation de l'enchère");
                acheteur = " ";
                System.out.println("Saisir la datedefin : aaaa-mm-jj");
                datedefin = Lire.S();
                createObjet(con , titre , categorie , mailvendeur, lieu , codepostal , description , prixinit , prixactuel , datedefin , acheteur);
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
                    String Categorie = tlu.getString(3);
                    String Vendeur = tlu.getString(4);
                    String quantite = tlu.getString(5);
                    String Lieu = tlu.getString(6);
                    String cdp = tlu.getString(7);
                    String des = tlu.getString(8);
                    String pi = tlu.getString(9);
                    String pa = tlu.getString(10);
                    String date = tlu.getString(11);
                    String acheteur = tlu.getString(12);
                    System.out.println(id + " : " + titre + " dans la categorie :" + Categorie + "("+quantite+")"+ "Lieu: " + Lieu +" Code postal : " + cdp + " Description :"+ des +" vendu par"+ Vendeur + " Prix intial : " + pi + " Prix actuel: "+ pa + " fin d'enchère : "+date +" Acheté par :"+acheteur);
                }
            }
        }
    }
    
    public static void afficheUnObjet(Connection con,int id1) throws SQLException {
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from objet where id = "+id1)) {
                System.out.println("Information sur l'objet:"+id1);
                System.out.println("------------------------");
                while (tlu.next()) {
                     int id = tlu.getInt("id");
                    String titre = tlu.getString(2);
                    String Categorie = tlu.getString(3);
                    String Vendeur = tlu.getString(4);
                    String quantite = tlu.getString(5);
                    String Lieu = tlu.getString(6);
                    String cdp = tlu.getString(7);
                    String des = tlu.getString(8);
                    String pi = tlu.getString(9);
                    String pa = tlu.getString(10);
                   String date = tlu.getString(11);
                    String acheteur = tlu.getString(12);
                    System.out.println(id + " : " + titre + " dans la categorie :" + Categorie + "("+quantite+")"+ "Lieu: " + Lieu +" Code postal : " + cdp + " Description :"+ des +" vendu par"+ Vendeur + " Prix intial : " + pi + " Prix actuel: "+ pa + " fin d'enchère : "+date +" Acheté par :"+acheteur);
                }
            }
        }
    }
    
    public static ArrayList<Objet> afficheObjetparCategorie(Connection con,String categorie) throws SQLException {
        ArrayList<Objet> res = new ArrayList<>();
        try ( Statement st = con.createStatement()) {
            try ( PreparedStatement pst = con.prepareStatement(
                "select * from objet where categorie = ?")) {
                pst.setString(1, categorie);
                try(ResultSet rs = pst.executeQuery()){
                    while(rs.next()){
                        res.add(new Objet(rs.getInt("id"),rs.getString("titre"),rs.getString("categorie"),rs.getString("mail_vendeur"),rs.getString("codepos"),rs.getString("lieu"),rs.getString("description"),rs.getFloat("prixinit"),rs.getFloat("prixactuel"),rs.getString("date_de_fin"),rs.getString("acheteur")));
                    }
                }
            }
        }
        return res;
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
    
    public static void deleteUnObjet(Connection con, int id) throws SQLException{
         try ( Statement st = con.createStatement()) {
             System.out.println("Confirmez la suppression de l'objet suivant :");
             Objet.afficheUnObjet(con, id);
             System.out.println("-------------------");
             System.out.println("Tapez 1: confirmez la suppression, Tapez 2 : modifié l'ID");
             int a= Lire.i();
             if(a==1){
             }else if (a==2){
                 int b=0;
                 while(b==0){
                    System.out.println("Vous avez choisi de modifier l'ID de l'objet à supprimer , tapez le nouvel ID");
                    id=Lire.i();
                    Objet.afficheUnObjet(con, id);
                    System.out.println("Si c'est le bon objet taper 1 sinon 0");
                    b=Lire.i();
                 }
             }
            try {
                st.executeUpdate(
                        """
                    delete from objet where id=
                    """+id);
                System.out.println("Objet "+id+" supprimé");
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
        }
     }
}
