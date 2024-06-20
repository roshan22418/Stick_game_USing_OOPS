package com.example.demo;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class Cherry implements displayable{

    private Rectangle rect;
    private boolean hidden;
    private RotateTransition idleanim;
    private Random random=new Random();
    private FadeTransition fadein;
    private FadeTransition fadeout;
    private Image cherryim=new Image("file:cherry.png");

    Cherry(){
        hidden=true;
        rect=new Rectangle(30,30);
        rect.setFill(new ImagePattern(cherryim));

        idleanim=new RotateTransition(Duration.millis(1000),rect);
        idleanim.setByAngle(10);
        idleanim.setAutoReverse(true);
        idleanim.setCycleCount(Animation.INDEFINITE);

        fadein=new FadeTransition(Duration.millis(500),rect);
        fadein.setFromValue(0);
        fadein.setToValue(100);

        fadeout=new FadeTransition(Duration.millis(500),rect);
        fadeout.setFromValue(100);
        fadeout.setToValue(0);


    }
    public void set(ArrayList<Platform> platforms,double minheight){
        double p1x=platforms.get(0).getRect().getX()+platforms.get(0).getRect().getWidth();
        double p2x=platforms.get(1).getRect().getX();
        rect.setX(p1x+ random.nextDouble(p2x-p1x-rect.getWidth()));
        rect.setY(minheight);
    }
    public boolean missed(hero h){
        return h.getRect().getX()>rect.getX();
    }
    public boolean isHidden(){
        return hidden;
    }
    public void hide(AnchorPane p){
        p.getChildren().remove(rect);
        hidden=true;
    }
    @Override
    public Rectangle getRect() {
        return this.rect;
    }

    @Override
    public void display(AnchorPane pane) {
        pane.getChildren().add(this.rect);
        hidden=false;
        fadein.play();

        idleanim.play();
    }

    @Override
    public void remove(AnchorPane pane) {
        fadeout.play();
        fadeout.setOnFinished(e->{
            pane.getChildren().remove(this.rect);
            hidden=true;});
        idleanim.pause();
    }
}
