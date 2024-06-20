package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {



            Game game=new Game();

            Random random=new Random();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root= loader.load();
            HelloController a=loader.getController();
            Scene scene = new Scene(root);

            game.init(a.pane,scene,a.bg);

            stage.setTitle("Stick Hero game");
            stage.setScene(scene);

            game.startScreen();

            game.loop();







            stage.show();

        } catch (IOException e) {
            e.printStackTrace(); // Or handle the exception accordingly
        }
    }

    void AddtoAncorPane(AnchorPane anchorpane, Node o,double xpos,double ypos){
        anchorpane.getChildren().add(o);
        AnchorPane.setBottomAnchor(o,ypos);
        AnchorPane.setLeftAnchor(o,xpos);
    }

//    void transitionall(AnchorPane pane){
//        for(Node i:pane.getChildren()){
//            TranslateTransition t=new TranslateTransition(Duration.millis(1000),i);
//            t.setByX(-100);
//            t.play();
//        }
//
//    }


    public static void main(String[] args) {
        launch();
    }
}