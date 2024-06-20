package com.example.demo;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

public class Platform implements displayable{
    private Rectangle rect;
    private Rectangle middle;
    private Random r = new Random();
    private double width;
    private double height;
    private Image texture=new Image("file:platformtexture.png");

    Platform(double x,double y) {

        width = r.nextDouble(200 + 1) + 30;
        height = 300;
        this.rect = new Rectangle(x,y,width,height);
        rect.setFill(new ImagePattern(texture,0,0,100,100,false));
        rect.setStroke(Color.WHITE);
        rect.setStrokeWidth(2);
        this.middle = new Rectangle(x+width/2-5,y,10, 10);
        middle.setFill(Color.RED);
        //rect.setFill(Color.GREEN);
    }
    public void display(AnchorPane pane){
        pane.getChildren().add(rect);
        pane.getChildren().add(middle);

    }
    public void distance_X(Platform p){
        double d=p.rect.getX()-this.rect.getX();
    }
    public void correctmiddlepos(){
        middle.setTranslateX(0);
        middle.setTranslateY(0);
        this.middle.setX(rect.getX()+width/2-10);
        this.middle.setY(rect.getY());
    }

    public void apperAnimation( double to_Y){
        TranslateTransition t1=new TranslateTransition(Duration.millis(400),rect);
        TranslateTransition t2=new TranslateTransition(Duration.millis(400),middle);
        t1.setByY(-to_Y);
        t2.setByY(-to_Y);
        t1.play();
        t2.play();
        t1.setOnFinished(actionEvent -> {
            rect.setY(rect.getY() + rect.getTranslateY());
            rect.setTranslateY(0);
        });
        t2.setOnFinished(actionEvent -> {
            middle.setY(middle.getY() + middle.getTranslateY());
            middle.setTranslateY(0);
        });
    }
    public boolean inZone(double X){

        return (X>middle.getX()&&X<(middle.getX()+10));
    }

    @Override
    public void remove(AnchorPane pane) {
        pane.getChildren().remove(rect);
        pane.getChildren().remove(middle);
    }


    public double getHeight(){
        return this.height;
    }

    public double getWidth(){
        return this.width;
    }

    public Rectangle getRect(){
        return rect;
    }
}
