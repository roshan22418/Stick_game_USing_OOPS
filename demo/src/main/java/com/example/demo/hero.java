package com.example.demo;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class hero implements displayable{
    private Image idle=new Image("file:idle.gif");
    private Image walkinggif=new Image("file:walking.gif");
    private boolean walking;
    private boolean falling;
    private Rectangle rect;
    private TranslateTransition walk;
    private TranslateTransition fall;
    private Rotate rot180;
    private Translate tr;

    private boolean flipped;

    hero(double x,double y){
        flipped=false;
        rect=new Rectangle(x,y,50,50);
        rect.setFill(Color.GREEN);
        rect.setFill(new ImagePattern(idle));
        tr= new Translate(0,-rect.getHeight());
        rot180=new Rotate(180,rect.getX()+rect.getWidth()/2,rect.getY()+rect.getHeight()/2,0,Rotate.X_AXIS);

    }

    public void WalkAnimate(double angle, double x_dist, double y_dist, Runnable onFinish) {
        rect.setFill(new ImagePattern(walkinggif));
        walking = true;
        double speed = 2;

        double xtarget = rect.getX() + x_dist;

        Thread walkthread = new Thread(() -> {
            double x = rect.getX();
            while (rect.getX() < xtarget) {
                x += speed;
                final double xPos = x; // Final variable for use in runLater
                Platform.runLater(() -> rect.setX(xPos));
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (!walking) {
                    break;
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> {
                if(rect.getX()>xtarget){
                    rect.setX(xtarget);
                }
                rect.setFill(new ImagePattern(idle));
                rect.setX(rect.getX() - rect.getTranslateX());
            });
            walking = false;

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(onFinish);
        });

        walkthread.start();
    }
    public void FallAnimate(double height,Runnable onfinish){
        falling=true;
        fall=new TranslateTransition(Duration.millis(500),rect);
        fall.setByY(height);
        fall.play();
        fall.setOnFinished(event->{
            rect.setX(rect.getX() + rect.getTranslateX());
            rect.setY(rect.getY() + rect.getTranslateY());
            rect.setTranslateX(0);
            rect.setTranslateY(0);
            falling = false;
            onfinish.run();
        });
    }
    public void flip(){
        if(rect.getTransforms().contains(rot180)){
            rect.getTransforms().remove(rot180);
            rect.getTransforms().remove(tr);
        }
        else{
            rect.getTransforms().add(rot180);
            rect.getTransforms().add(tr);
        }
        flipped=!flipped;


    }

    public void stopWalking(){
        walking=false;
    }

    public boolean isFlipped(){
        return flipped;
    }

    public Rectangle getRect(){
        return rect;
    }

    public boolean isWalking(){
        return walking;
    }
    public boolean isFalling(){
        return falling;
    }

    @Override
    public void display(AnchorPane pane) {
        pane.getChildren().add(rect);
    }

    @Override
    public void remove(AnchorPane pane) {
        pane.getChildren().remove(rect);
    }


}
