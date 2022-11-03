package fr.insa.vedel.javafx.projet.classes;

public class Role {
    
    private int id;
    private String nrole;

    public Role(int id, String nrole) {
        this.id = id;
        this.nrole = nrole;
    }
    
    @Override
    public String toString() {
        return this.nrole;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the nrole
     */
    public String getNrole() {
        return nrole;
    }

    /**
     * @param nrole the nrole to set
     */
    public void setNrole(String nrole) {
        this.nrole = nrole;
    }
    
    
    
}
