package fr.insa.vedel.javafx.projet.guiFX;

import fr.insa.vedel.javafx.projet.bdd.SQLUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;

public class JavaFXUtils {

    public static void addSimpleBorder(Region c) {
        c.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }

    public static WebView preparedStatementInWebView(PreparedStatement pst) {
        WebView view = new WebView();

        try ( ResultSet rs = pst.executeQuery()) {
            view.getEngine().loadContent(SQLUtils.formatResultSetAsHTMLTable(rs));
        } catch (SQLException ex) {
            view.getEngine().loadContent("<b> problem bdd : " + ex.getLocalizedMessage() + " </b>");
        }
        return view;
    }

    public static void showErrorInAlert(String titre, String message, String detail) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(message);
        alert.setContentText(detail);
        alert.showAndWait();

    }
}
