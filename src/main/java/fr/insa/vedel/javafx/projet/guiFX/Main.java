package fr.insa.vedel.javafx.projet.guiFX;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    @Override
    public void start(Stage stage) {
        Scene sc = new Scene(new VuePrincipale());
//        Scene sc = new Scene(new TestFx());
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setScene(sc);
        stage.setTitle("Page Principale");
          stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}