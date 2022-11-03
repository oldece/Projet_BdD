package fr.insa.vedel.javafx.projet.guiFX;

import fr.insa.vedel.javafx.projet.SessionInfo;
import fr.insa.vedel.javafx.projet.bdd.GestionBdD;
import fr.insa.vedel.javafx.projet.guiFX.vues.ConnectionBDDForm;
import fr.insa.vedel.javafx.projet.guiFX.vues.EnteteInitialLogin;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

public class VuePrincipale extends BorderPane {

    private SessionInfo sessionInfo;
    private ScrollPane mainContent;

    public void setEntete(Node c) {
        this.setTop(c);
    }

    public void setMainContent(Node c) {
        this.mainContent.setContent(c);
    }

    public VuePrincipale() {
        this.sessionInfo = new SessionInfo();
        this.mainContent = new ScrollPane();
        JavaFXUtils.addSimpleBorder(this.mainContent);
        this.setCenter(this.mainContent);
        try {
             Connection con = GestionBdD.defautConnect();
             this.sessionInfo.setConBdD(con);
             this.setMainContent(new Label("Please login"));
        } catch (SQLException | ClassNotFoundException ex) {
            this.setMainContent(new ConnectionBDDForm(this));
        }
        this.setEntete(new EnteteInitialLogin(this));
    }

    /**
     * @return the sessionInfo
     */
    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

}
